package utilities

import javax.inject.Inject
import java.util.Calendar

import play.api.db.DBApi

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

import anorm._
import anorm.SqlParser.{get, str, int, date}
import anorm.JodaParameterMetaData._

import org.joda.time.DateTime

import scala.collection.mutable.ListBuffer
import scala.math.BigDecimal
import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}

import models._

class GlobalesCol @Inject()(
    dbapi: DBApi,
    _funcion: Funcion,
    _colocacionService: ColocacionRepository,
    _codigopuccolocacionService: CodigoPucColocacionRepository
)(implicit ec: DatabaseExecutionContext) {
  private val db = dbapi.database("default")

  def buscoTasaEfectivaMaximaIpc: Double = {
    var _tm = 99.99
    db.withConnection { implicit connection =>
      val _tasa = SQL(
        """SELECT VALOR_ACTUAL_TASA FROM "col$tasasvariables" WHERE ID_INTERES = 2"""
      ).as(SqlParser.scalar[Double].singleOpt)
      _tasa match {
        case Some(t) => _tm = t
        case None    => _tm = 99.99
      }
    }
    _tm
  }

  def buscoTasaEfectivaMaximaDtf(_fecha: DateTime): Double = {
    var _tm: Double = 99.99
    db.withConnection { implicit connection =>
      val _tasa = SQL(
        """SELECT VALOR_TASA_EFECTIVA FROM 
                         "col$tasadtf" WHERE {fecha} BETWEEN FECHA_INICIAL AND FECHA_FINAL """
      ).on(
          'fecha -> _fecha
        )
        .as(SqlParser.scalar[Double].singleOpt)

      _tasa match {
        case Some(t) => _tm = t
        case None =>
          val _tasa = SQL(
            """SELECT FIRST 1 VALOR_TASA_EFECTIVA FROM"
                                             "col$tasadtf" ORDER BY ID_TASA_DTF DESC """
          ).as(SqlParser.scalar[Double].singleOpt)
          _tasa match {
            case Some(t) => _tm = t
            case None    => _tm = 99.99
          }
      }
    }
    _tm
  }

  def buscoTasaEfectivaMaxima(
      _fecha: DateTime,
      _clasificacion: Int,
      _edad: String
  ): Double = {
    var _tm: Double = 99.99
    db.withConnection { implicit connection =>
      val _tasa = SQL(
        """select FIRST 1 VALOR_TASA_EFECTIVA from
                            "col$tasaclasificacion" where ({fecha} between FECHA_INICIAL and FECHA_FINAL) and
                            ID_CLASIFICACION = {clasificacion} AND ID_EDAD = {edad} ORDER BY FECHA_INICIAL DESC"""
      ).on(
          'fecha -> _fecha,
          'clasificacion -> _clasificacion,
          'edad -> _edad
        )
        .as(SqlParser.scalar[Double].singleOpt)

      _tasa match {
        case Some(t) => _tm = t
        case None =>
          val _tasa = SQL(
            """SELECT First 1 VALOR_TASA_EFECTIVA from "col$tasaclasificacion"
                                            WHERE ID_CLASIFICACION = {clasificacion}
                                            ORDER BY FECHA_INICIAL desc, VALOR_TASA_EFECTIVA ASC"""
          ).on(
              'clasificacion -> _clasificacion
            )
            .as(SqlParser.scalar[Double].singleOpt)
          _tasa match {
            case Some(t) => _tm = t
            case None    => _tm = 99.99
          }
      }
    }
    _tm
  }

  def obtenerDiasMora(id_colocacion: String): Int = {
    var dias = 0
    val _fechaHoy = new DateTime()
    var _fechaDesembolso = new DateTime()
    val parser =
      int("ID_ESTADO_COLOCACION") ~
        date("FECHA_DESEMBOLSO") ~
        date("FECHA_INTERES") ~
        int("ID_LINEA") ~
        int("AMORTIZA_INTERES") ~
        int("DIAS_PAGO") ~
        str("INTERES") map {
        case a ~ b ~ c ~ d ~ e ~ f ~ g => (a, b, c, d, e, f, g)
      }
    val c = db.withConnection { implicit connection =>
      SQL("""
           SELECT 
                c.ID_ESTADO_COLOCACION,
                c.FECHA_DESEMBOLSO,
                c.FECHA_INTERES,
                c.ID_LINEA,
                c.AMORTIZA_INTERES,
                c.DIAS_PAGO,
                t.INTERES
           FROM
                "col$colocacion" c
                INNER JOIN "col$tiposcuota" t ON (t.ID_TIPOS_CUOTA = c.ID_TIPO_CUOTA)
           WHERE 
                c.ID_COLOCACION = {id_colocacion}""")
        .on(
          'id_colocacion -> id_colocacion
        )
        .as(parser.single)
    }

    var fecha = c._3
    val tipo = c._7
    val amortiza = c._5

    var _fecha = new DateTime()
    if (tipo.equals("V")) {
      _fecha = _funcion.calculoFecha(new DateTime(fecha), amortiza)
    }

    _fecha = _fecha.plusDays(1)
    _fechaDesembolso = new DateTime(c._2)
    _fechaDesembolso = _fechaDesembolso.plusDays(c._6)
    dias = _funcion.diasEntreFechas(_fecha, _fechaHoy, _fechaDesembolso)

    // validar si aplica periodo de gracia
    val _parseGracia = str("ID_COLOCACION") ~ date("FECHA_REGISTRO") ~ int(
      "DIAS"
    ) map { case a ~ b ~ c => (a, b, c) }
    val gracia = db.withConnection { implicit connection =>
      SQL(
        """SELECT FIRST 1 ID_COLOCACION, FECHA_REGISTRO, DIAS FROM COL_PERIODO_GRACIA WHERE ID_COLOCACION = {id_colocacion} and ESTADO = 0 ORDER BY FECHA_REGISTRO DESC"""
      ).on(
          'id_colocacion -> id_colocacion
        )
        .as(_parseGracia.singleOpt)
    }

    gracia match {
      case Some(g) => {
        val _fi = new DateTime(g._2)
        val _ff = _funcion.calculoFecha(new DateTime(g._2), g._3)
        if (_fi.getMillis() <= _fechaHoy.getMillis() && _ff
              .getMillis() >= _fechaHoy.getMillis()) {
          dias = 0
        }
      }
      case None => None
    }

    dias
  }

  def calcularDescuentoPorCuota(
      _cuota: Seq[Tabla],
      _descuento: Seq[DescuentoColocacion],
      _valorDesembolso: BigDecimal,
      _amortizaCapital: Int,
      _saldoActual: BigDecimal
  ): Seq[ADescontar] = {
    var _descontar = new ListBuffer[ADescontar]()
    var nValor: BigDecimal = 0
    var nPorcColocacion: Double = 0
    var nPorcSaldo: Double = 0
    var nPorcCuota: Double = 0
    var bEnDesembolso: Boolean = false
    var bEnCuota: Boolean = false
    var bEsDistribuido: Boolean = false
    var sCodigo: String = ""
    var nCobro: BigDecimal = 0
    var idDescuento: Int = 0
    var nAmortiza: Int = 0

    _descuento.foreach { _d =>
      if (_d.descontar) {
        val descuento = db.withConnection { implicit connection =>
          SQL(
            """SELECT * FROM "col$descuentos" WHERE ID_DESCUENTO = {id_descuento}"""
          ).on(
              'id_descuento -> _d.id_descuento
            )
            .as(Descuento._set.single)
        }

        nValor = descuento.valor_descuento
        nPorcColocacion = descuento.porcentaje_colocacion
        nPorcSaldo = descuento.porcentaje_saldo
        nPorcCuota = descuento.porcentaje_cuota
        bEnDesembolso = Conversion.entero_a_boolean(descuento.en_desembolso)
        bEnCuota = Conversion.entero_a_boolean(descuento.en_cuota)
        bEsDistribuido = Conversion.entero_a_boolean(descuento.es_distribuido)
        sCodigo = descuento.codigo
        nAmortiza = descuento.amortizacion

        if (nValor > 0) {
          if (bEnDesembolso) {
            return _descontar.toSeq
          }
          if (bEsDistribuido) {
            nCobro = math.round(nValor.doubleValue / _cuota.length)
          }
          if (bEnCuota) {
            nCobro = nValor
            _cuota.foreach { _c =>
              var _ad =
                new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
              _descontar += _ad
            }
          }
        } else if (nPorcColocacion > 0) {
          if (bEnDesembolso) {
            return _descontar.toSeq
          }
          nCobro = math.round(
            _valorDesembolso.doubleValue * nPorcColocacion / 100
          ) * (_amortizaCapital / nAmortiza);
          if (bEsDistribuido) {
            nCobro = math.round(nCobro.doubleValue / _cuota.length)
          }
          if (bEnCuota) {
            _cuota.foreach { _c =>
              var _ad =
                new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
              _descontar += _ad
            }
          }
        } else if (nPorcSaldo > 0) {
          if (bEnDesembolso) {
            return _descontar.toSeq
          }
          if (bEnCuota && !bEsDistribuido) {
            nValor = _valorDesembolso
            _cuota.foreach { _c =>
              nCobro = math.round(nValor.doubleValue * nPorcSaldo / 100) * (_amortizaCapital / nAmortiza)
              nValor = nValor - _c.cuot_capital
              var _ad =
                new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
              _descontar += _ad
            }
          } else if (bEnCuota && bEsDistribuido) {
            nValor = _valorDesembolso
            nCobro = 0
            _cuota.foreach { _c =>
              nCobro = nCobro + math.round(
                nValor.doubleValue * nPorcSaldo / 100
              ) * (_amortizaCapital / nAmortiza)
              nValor = nValor - _c.cuot_capital
            }
            nCobro = math.round(nCobro.doubleValue / _cuota.length)
            _cuota.foreach { _c =>
              var _ad =
                new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
              _descontar += _ad
            }
          }
        } else if (nPorcCuota > 0) {
          if (bEnDesembolso) {
            return _descontar.toSeq
          }
          if (bEnCuota && !bEsDistribuido) {
            _cuota.foreach { _c =>
              nCobro = math.round(_c.cuot_capital.doubleValue * nPorcCuota)
              var _ad =
                new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
              _descontar += _ad
            }
          }
        }
      }
    }
    _descontar.toList
  }

  def calcularFechasMora(
      fecha_corte: DateTime,
      fecha_proxima: DateTime
  ): List[FechaLiq] = {
    var _list = new ListBuffer[FechaLiq]()

    var _fechaf2 = fecha_corte
    var _fechaf3 = fecha_proxima.plusDays(1)

    _fechaf2 = _fechaf2
      .minusMinutes(_fechaf2.minuteOfHour().get())
      .minusSeconds(_fechaf2.secondOfMinute().get())
    _fechaf3 = _fechaf3
      .minusMinutes(_fechaf3.minuteOfHour().get())
      .minusSeconds(_fechaf3.secondOfMinute().get())

    var _fechaX = _fechaf3
    var _fechaY = _fechaf2
    var _fechaf1 = _fechaX
    _fechaf2 = _fechaY

    while (_fechaf1.getMillis() <= _fechaf2.getMillis()) {
      var _anticipado = false
      var _causado = false
      var _corriente = false
      var _vencido = true
      var _devuelto = false

      var _fecha = _fechaf1.dayOfMonth().withMaximumValue()
      if (_fecha.isAfter(_fechaf2)) {
        _fecha = _fechaf2
      }
      var _fecha_inicial = _fechaf1
      var _fecha_final = _fecha

      val (_a, _m, _d) = _funcion.decodeDate(_fecha_inicial)
      val (_aa, _mm, _dd) = _funcion.decodeDate(_fecha_final)
      val (_aaa, _mmm, _ddd) = _funcion.decodeDate(fecha_proxima)
      _fechaf1 = _fecha

      if (!((_m == _mm) && (_d == 31) && (_dd == 31)) && !((_m == _mm) && (_d == 30) && (_dd == 30) && (_dd == _ddd))) {
        _list += new FechaLiq(
          _fecha_inicial,
          _fecha_final,
          _anticipado,
          _causado,
          _corriente,
          _vencido,
          _devuelto
        )
      }
      _fechaf1 = _fecha.plusDays(1)
    }
    _list.toList
  }

  def calcularFechasDevolucion(
      fecha_desembolso: DateTime,
      fecha_corte: DateTime,
      fecha_proxima: DateTime
  ): List[FechaLiq] = {

    var _list = new ListBuffer[FechaLiq]()

    var _fechaf2 = DateTime.now()

    var _fecha_corte = _funcion.limpiarFecha(fecha_corte)
    var _fecha_prox = _funcion.limpiarFecha(fecha_proxima)

    var _fechaf1 = _fecha_corte

    val (_aaa, _mmm, _ddd) = _funcion.decodeDate(fecha_proxima)
    val (_aaaa, _mmmm, _dddd) = _funcion.decodeDate(fecha_desembolso)

    if ((_mmm == 2) && (_ddd == 28) && (_ddd != _dddd)) {
      _fechaf2 = _fecha_prox
      // _fechaf2 = _fechaf2.minusMinutes(_fechaf2.minuteOfHour().get()).minusSeconds(_fechaf2.secondOfMinute().get())
    } else {
      _fechaf2 = _fecha_prox.plusDays(-1)
      // _fechaf2 = _fechaf2.minusMinutes(_fechaf2.minuteOfHour().get()).minusSeconds(_fechaf2.secondOfMinute().get())
    }

    println("Fecha Final: " + _fechaf2)
    println("")
    println("Fecha En Que Inicia: " + _fechaf1)

    while (_fechaf1.getMillis() <= _fechaf2.getMillis()) {
      var _anticipado = false
      var _causado = false
      var _corriente = false
      var _vencido = false
      var _devuelto = false

      var _fecha = _fechaf1.dayOfMonth().withMaximumValue()
      if (_fecha.getMillis() > _fechaf2.getMillis()) {
        _fecha = _fechaf2
      }

      _devuelto = true
      val _fecha_inicial = _fechaf1
      val _fecha_final = _fecha

      println("Fecha Inicial :" + _fecha_inicial)
      println("Fecha Final: " + _fecha_final)

      val (_a, _m, _d) = _funcion.decodeDate(_fecha_inicial)
      val (_aa, _mm, _dd) = _funcion.decodeDate(_fecha_final)
      if (!(_m == _mm && _d == 31 && _dd == 31)) {
        _list += new FechaLiq(
          _fecha_inicial,
          _fecha_final,
          _anticipado,
          _causado,
          _corriente,
          _vencido,
          _devuelto
        )
      }
      _fechaf1 = _fecha.plusDays(1)
    }

    _list.toList

  }

  def calcularFechasALiquidarFija(
      fecha_inicial: DateTime,
      fecha_corte: DateTime,
      fecha_proxima: DateTime
  ): List[FechaLiq] = {

    var _list = new ListBuffer[FechaLiq]()
    var _fechaf1 = fecha_inicial
    var _fechaf2 = fecha_corte

    var _fechaf3 = fecha_proxima
    var _fechaf4 = DateTime.now()

    _fechaf1 = _fechaf1
      .minusMinutes(_fechaf1.minuteOfHour().get())
      .minusSeconds(_fechaf1.secondOfMinute().get())
    _fechaf2 = _fechaf2
      .minusMinutes(_fechaf2.minuteOfHour().get())
      .minusSeconds(_fechaf2.secondOfMinute().get())
    _fechaf3 = _fechaf3
      .minusMinutes(_fechaf3.minuteOfHour().get())
      .minusSeconds(_fechaf3.secondOfMinute().get())
    _fechaf4 = _fechaf4
      .minusMinutes(_fechaf4.minuteOfHour().get())
      .minusSeconds(_fechaf4.secondOfMinute().get())

    var _paso = false

    // Inicio While 1
    while (_fechaf1.getMillis() <= _fechaf3.getMillis()) {
      var _fecha = _fechaf1.dayOfMonth().withMaximumValue()
      if (_fecha.isAfter(_fechaf3)) {
        _fecha = _fechaf3
      }
      if (_fecha.isAfter(_fechaf4) && _paso == false && _fechaf1.isBefore(
            _fechaf4
          )) {
        _fecha = _fechaf4
        _paso = true
      }
      val _fecha_inicial = _fechaf1
      val _fecha_final = _fecha

      var _anticipado = false
      var _causado = false
      var _corriente = false
      var _vencido = false
      var _devuelto = false

      val (_a, _m, _d) = _funcion.decodeDate(_fecha_inicial)
      val (_aa, _mm, _dd) = _funcion.decodeDate(_fecha_final)

      val _fechaA = _fecha_final.dayOfMonth().withMinimumValue()
      val _fechaB = _fechaf4.dayOfMonth().withMinimumValue()

      if (_fecha.getMillis() <= _fechaf4.getMillis() && _fechaA
            .getMillis() == _fechaB.getMillis) {
        _anticipado = false
        _causado = false
        _corriente = true
        _vencido = false
        _devuelto = false
      } else if (_fecha.getMillis() <= _fechaf4.getMillis()) {
        _anticipado = false
        _causado = true
        _corriente = false
        _vencido = false
        _devuelto = false
      } else {
        _anticipado = true
        _causado = false
        _corriente = false
        _vencido = false
        _devuelto = false
      }
      _fechaf1 = _fecha
      if (!((_m == _mm) && (_d == 31) && (_dd == 31))) {
        _list += new FechaLiq(
          _fecha_inicial,
          _fecha_final,
          _anticipado,
          _causado,
          _corriente,
          _vencido,
          _devuelto
        )
      }
      _fechaf1 = _fecha.plusDays(1)
    }
    _list.toList
  }

  def calcularFechasALiquidarVarAnticipada(
      fecha_inicial: DateTime,
      fecha_corte: DateTime,
      fecha_proxima: DateTime
  ): List[FechaLiq] = {
    var _list = new ListBuffer[FechaLiq]()
    var _fechaf1 = fecha_inicial
    var _fechaf2 = fecha_corte

    var _fechaf3 = fecha_proxima
    var _fechaf4 = DateTime.now()
    var _fecha = DateTime.now()

    _fechaf1 = _fechaf1
      .minusMinutes(_fechaf1.minuteOfHour().get())
      .minusSeconds(_fechaf1.secondOfMinute().get())
    _fechaf2 = _fechaf2
      .minusMinutes(_fechaf2.minuteOfHour().get())
      .minusSeconds(_fechaf2.secondOfMinute().get())
    _fechaf3 = _fechaf3
      .minusMinutes(_fechaf3.minuteOfHour().get())
      .minusSeconds(_fechaf3.secondOfMinute().get())
    _fechaf4 = _fechaf4
      .minusMinutes(_fechaf4.minuteOfHour().get())
      .minusSeconds(_fechaf4.secondOfMinute().get())

    var _paso = false

    // Inicio While 1
    while (_fechaf1.getMillis() <= _fechaf3.getMillis()) {
      _fecha = _fechaf3
      if ((_fecha.getMillis() > _fechaf4
            .getMillis()) && (_paso == false) && (_fechaf1
            .getMillis() < _fechaf4.getMillis())) {
        _fecha = _fechaf4
        _paso = true
      }

      val _fecha_inicial = _fechaf1
      val _fecha_final = _fecha

      var _anticipado = false
      var _causado = false
      var _corriente = false
      var _vencido = false
      var _devuelto = false

      val (_a, _m, _d) = _funcion.decodeDate(_fecha_inicial)
      val (_aa, _mm, _dd) = _funcion.decodeDate(_fecha_final)

      val _fechaA = _fecha.dayOfMonth().withMinimumValue()
      val _fechaB = _fechaf4.dayOfMonth().withMinimumValue()

      if ((_fecha.getMillis() <= _fechaf4
            .getMillis()) && (_fechaA == _fechaB)) {
        _anticipado = false
        _causado = false
        _corriente = true
        _vencido = false
        _devuelto = false
      } else if (_fecha.getMillis() <= _fechaf4.getMillis()) {
        _anticipado = false
        _causado = true
        _corriente = false
        _vencido = false
        _devuelto = false
      } else {
        _anticipado = true
        _causado = false
        _corriente = false
        _vencido = false
        _devuelto = false
      }

      _fechaf1 = _fecha
      if (!(_m == _mm) && (_d == 31) && (_dd == 31)) {
        _list += new FechaLiq(
          _fecha_inicial,
          _fecha_final,
          _anticipado,
          _causado,
          _corriente,
          _vencido,
          _devuelto
        )
      }

      _fechaf1 = _fecha.plusDays(1)

    }

    _list.toList
  }

  def liquidarCuotaFija(
      id_colocacion: String,
      cuotas_a_liquidar: Int,
      fecha_corte: DateTime
  ) = {

    _colocacionService.buscarColocacion(id_colocacion).map { colocacion =>
      colocacion match {
        case Some(c) =>
          var _lista = new ListBuffer[DateTime]
          var _fecha_prox = new DateTime()
          var _nuevo_saldo = BigDecimal(0)
          var _liquidado: Boolean = false
          var _my_cuotas_liq = new ListBuffer[CuotasLiq]()
          var _m_saldo_actual = BigDecimal(0)
          var _m_interes_hasta = new DateTime()
          var _m_capital_hasta = new DateTime()
          var _m_fecha_prox = new DateTime()

          val _descuento = db.withConnection { implicit connection =>
            SQL("""SELECT
                        d.*,
                        cd.ID_DESCUENTO
                         FROM "col$colocaciondescuento" cd
                        INNER JOIN "col$descuentos" d ON (d.ID_DESCUENTO = cd.ID_DESCUENTO)
                        WHERE (ID_COLOCACION = {id_colocacion})""")
              .on(
                'id_colocacion -> id_colocacion
              )
              .as(Descuento._set *)
          }

          val _cdsDescuento = new ListBuffer[DescuentoColocacion]()
          _descuento.foreach { d =>
            val _dc = new DescuentoColocacion(
              d.id_descuento,
              d.descripcion_descuento,
              true
            )
            _cdsDescuento += _dc
          }

          val _tablaLiquidacion = db.withConnection { implicit connection =>
            SQL(
              """select * from "col$tablaliquidacion" where ID_COLOCACION = {id_colocacion}"""
            ).on(
                'id_colocacion -> id_colocacion
              )
              .as(Tabla._set *)
          }

          val _total_cuotas = _tablaLiquidacion.length

          val _tablaLiquidacionNoPagada = db.withConnection {
            implicit connection =>
              SQL("""select * from "col$tablaliquidacion" where ID_COLOCACION = {id_colocacion} and PAGADA = 0
                               ORDER BY ID_COLOCACION, CUOTA_NUMERO""")
                .on(
                  'id_colocacion -> id_colocacion
                )
                .as(Tabla._set *)
          }

          val _costas = db.withConnection { implicit connection =>
            SQL(
              """SELECT SUM(VALOR_COSTAS) AS VALOR_COSTAS from "col$costas" where ID_COLOCACION = {id_colocacion}"""
            ).on(
                'id_colocacion -> id_colocacion
              )
              .as(SqlParser.scalar[BigDecimal].singleOpt)

          }

          var _cdsCuotas = new ListBuffer[Tabla]
          var _amortizacion: Int = 0
          val _valor_desembolso = c.a.valor_desembolso.get
          val _amortiza_capital = c.b.amortiza_capital.get
          val _amortiza_interes = c.b.amortiza_interes.get
          val _clasificacion = c.a.id_clasificacion.get
          val _categoria = c.a.id_categoria.get
          val _fecha_pago_capital = _funcion.limpiarFecha(c.b.fecha_capital.get)
          val _fecha_pago_interes = _funcion.limpiarFecha(c.b.fecha_interes.get)
          val _puntos_interes = c.a.puntos_interes.get
          val _tipo_interes = c.a.id_interes.get
          val _linea = c.a.id_linea.get
          val _fecha_desembolso =
            _funcion.limpiarFecha(c.a.fecha_desembolso.get)
          var _saldo_actual = c.a.valor_desembolso.get - c.b.abonos_capital.get
          var _tasa_mora = c.a.tasa_interes_mora.get
          val _valor_cuota = c.b.valor_cuota.get
          val _dias_prorroga = c.b.dias_prorrogados.get
          var _proximo_pago = new DateTime()

          var _saldo = _valor_desembolso
          _tablaLiquidacion.foreach { t =>
            val _c = new Tabla(
              t.cuot_num,
              t.cuot_fecha,
              _saldo,
              t.cuot_capital,
              t.cuot_interes,
              t.cuot_otros
            )
            _cdsCuotas += _c
            _saldo = _saldo - t.cuot_capital
          }

          val _adescontar = calcularDescuentoPorCuota(
            _cdsCuotas.toSeq,
            _cdsDescuento.toSeq,
            _valor_desembolso,
            _amortiza_capital,
            _saldo_actual
          )

          val i = 0
          var _cuotas = new ListBuffer[Tabla]
          for (i <- 0 to _cdsCuotas.length - 1) {
            _cuotas += new Tabla(
              _cdsCuotas(i).cuot_num,
              _cdsCuotas(i).cuot_fecha,
              _cdsCuotas(i).cuot_saldo,
              _cdsCuotas(i).cuot_capital,
              _cdsCuotas(i).cuot_interes,
              _adescontar(i).valor
            )
          }

          // Proceso de Liquidacion
          var _total_debito: BigDecimal = BigDecimal(0)
          var _total_credito: BigDecimal = BigDecimal(0)

          var _fecha_corte = fecha_corte

          if (_amortiza_capital < _amortiza_interes) {
            _amortizacion = _amortiza_capital
          } else {
            _amortizacion = _amortiza_interes
          }

          val _codigo_caja = db.withConnection { implicit connection =>
            SQL(
              """SELECT CODIGO FROM "col$codigospucbasicos" WHERE ID_CODIGOPUCBASICO = 1"""
            ).as(SqlParser.scalar[String].single)
          }

          val _codigo_gestion_cartera = db.withConnection {
            implicit connection =>
              SQL(
                """SELECT CODIGO FROM "col$codigospucbasicos" WHERE ID_CODIGOPUCBASICO = 62"""
              ).as(SqlParser.scalar[String].single)
          }

          val _tasa_usura = db.withConnection { implicit connection =>
            SQL(
              """SELECT VALOR_MINIMO FROM "gen$minimos" WHERE ID_MINIMO = 100"""
            ).as(SqlParser.scalar[Double].single)
          }

          var _porcentaje_gestion = 0
          val _valor_tasa_efectiva = c.a.tasa_interes_corriente.get
          if (_tasa_usura < _valor_tasa_efectiva) {
            _porcentaje_gestion = math.round(
              ((_valor_tasa_efectiva - _tasa_usura) * 100).toFloat / _valor_tasa_efectiva.toFloat
            )
          }
          val _codigos = _codigopuccolocacionService.obtener(
            c.a.id_clasificacion.get,
            c.a.id_garantia.get,
            c.a.id_categoria.get
          )
          val _codigosdup = _codigopuccolocacionService.obtenerFacturado(
            c.a.id_clasificacion.get,
            c.a.id_garantia.get,
            c.a.id_categoria.get
          )
          var _cuota_liq = new ListBuffer[CuotasLiq]()
          var _codigo_puc = ""
          var _codigo_nombre = ""
          // Evaluar Costas
          _costas match {
            case Some(_costas) =>
              if (_costas > 0) {
                _codigo_puc = _codigos.cod_costas match {
                  case Some(c) => c
                  case None    => "No Encontrado"
                }
                _codigo_nombre = codigoNombre(_codigo_puc)
                var _cl = new CuotasLiq(
                  0,
                  _codigos.cod_costas.get,
                  _codigo_nombre,
                  _fecha_corte,
                  _fecha_corte,
                  0,
                  0,
                  0,
                  _costas,
                  false,
                  false,
                  false,
                  false,
                  false,
                  false,
                  false,
                  false,
                  true,
                  ""
                )
                if (_cl.debito != 0 || _cl.credito != 0) {
                  _cuota_liq += _cl
                  _total_debito += _cl.debito
                  _total_credito += _cl.credito
                }
              }
            case None => None
          }
          // Fin Evaluar Costas
          // Evaluar Cuota a Cuota a Liquidar
          var cuota = 0
          var _fecha_prox_capital = new DateTime()
          var _fecha_prox_nueva = new DateTime()
          for (cuota <- 0 to (cuotas_a_liquidar - 1)) {
            val _ncuota = _tablaLiquidacionNoPagada(cuota).cuot_num
            var _interes_cuota = _funcion.round0(
              ((_saldo_actual * (_funcion.tasaNominalVencida(
                _valor_tasa_efectiva,
                _amortizacion
              ) + _puntos_interes) / 100 * _amortizacion) / 360).toDouble
            )
            var _capital = _valor_cuota - _interes_cuota
            var _cuota_numero = _ncuota
            var _fecha1 = _fecha_pago_interes
            _fecha_prox = _tablaLiquidacionNoPagada(cuota).cuot_fecha
            _fecha_prox = _funcion.limpiarFecha(_fecha_prox)
            _fecha_prox = _funcion.calculoFecha(_fecha_prox, _dias_prorroga)
            _fecha_prox_capital = _fecha_prox
            _fecha_prox_nueva = _fecha_prox
            _m_capital_hasta = _fecha_prox_capital

            if (_saldo_actual < _capital) {
              _capital = _saldo_actual
            }

            if (_ncuota == _total_cuotas) {
              _capital = _saldo_actual
              _m_fecha_prox = new DateTime(0)
            } else {
              _proximo_pago = _funcion.calculoFecha(_fecha_prox, _amortizacion)
              var (_a, _m, _d) = _funcion.decodeDate(_fecha_desembolso)
              var (_aa, _mm, _dd) = _funcion.decodeDate(_proximo_pago)

              if ((_mm == 2) && (_d > _proximo_pago.dayOfMonth().get)) {
                _d = _dd
              }
              _proximo_pago = new DateTime(_aa, _mm, _d, 0, 0)
              _m_fecha_prox = _proximo_pago
            }

            var _fecha_arranque = _fecha1.plusDays(1)

            val _l1 = calcularFechasALiquidarFija(
              _fecha_arranque,
              _fecha_corte,
              _fecha_prox
            )
            var _l2 = List.empty[FechaLiq]
            var _l3 = List.empty[FechaLiq]
            if (_fecha_prox_nueva.getMillis() > _fecha_corte.getMillis()) {
              _l2 = calcularFechasDevolucion(
                _fecha_desembolso,
                _fecha_corte,
                _fecha_prox_nueva
              )
            } else {
              _l3 = calcularFechasMora(_fecha_corte, _fecha_prox_nueva)
            }
            val _fechas_liq = List.concat(_l1, _l2, _l3)

            var _total_interes = BigDecimal(0)
            var _total_interes_credito = BigDecimal(0)
            var _a_cobrar_gestion = BigDecimal(0)
            var _tasa_liquidar = 0.00
            var _tasa_efectiva = 0.00
            _fechas_liq.foreach { _af =>
              val _tasa_maxima =
                buscoTasaEfectivaMaxima(_af.fecha_final, _clasificacion, "A")
              _tipo_interes match {
                case 0 =>
                  _tasa_efectiva = _tasa_maxima
                  if (_valor_tasa_efectiva > _tasa_efectiva) {
                    _tasa_liquidar = _tasa_efectiva
                  } else {
                    _tasa_liquidar = _valor_tasa_efectiva
                  }
                  _tasa_liquidar =
                    _funcion.tasaNominalVencida(_tasa_liquidar, _amortizacion)
                case 1 =>
                  _tasa_efectiva =
                    buscoTasaEfectivaMaximaDtf(_fecha_pago_interes)
                  _tasa_liquidar =
                    _funcion.tasaNominalVencida(_tasa_efectiva, _amortizacion)
                case _ =>
                  _tasa_efectiva = buscoTasaEfectivaMaximaIpc
                  _tasa_liquidar =
                    _funcion.tasaNominalVencida(_tasa_efectiva, _amortizacion)
              }
              var _tasa_liquidar_mora =
                _funcion.tasaNominalVencida(_tasa_mora, _amortizacion)
              var _vtasa = _tasa_liquidar
              var _duplicar = false
              var _a_cobrar = BigDecimal(0)
              // inicio variables cuotas liq
              _cuota_numero = 0
              _codigo_puc = ""
              _codigo_nombre = ""
              var _fecha_inicial = new DateTime()
              var _fecha_final = new DateTime()
              var _dias = 0
              var _tasa = 0.00
              var _debito = BigDecimal(0)
              var _credito = BigDecimal(0)
              var _es_capital = false
              var _es_causado = false
              var _es_corriente = false
              var _es_vencido = false
              var _es_anticipado = false
              var _es_devuelto = false
              var _es_otros = false
              var _es_cajabanco = false
              var _es_costas = false
              var _id_clase_operacion = false
              // fin variables para cuotasliq
              var _tasa_dev = 0.00
              if (_af.anticipado || _af.devuelto) {
                _codigo_puc = _codigosdup.cod_int_ant match {
                  case Some(c) => c
                  case None    => "No Encontrado"
                }
                _codigo_nombre = codigoNombre(_codigo_puc)
              } else if (_af.causado) {
                _codigo_puc = _codigos.cod_cxc match {
                  case Some(c) => c
                  case None    => "No Encontrado"
                }
                _codigo_nombre = codigoNombre(_codigo_puc)
              } else if (_af.corrientes) {
                _codigo_puc = _codigos.cod_int_mes match {
                  case Some(c) => c
                  case None    => "No Encontrado"
                }
                _codigo_nombre = codigoNombre(_codigo_puc)
              } else if (_af.vencida) {
                _codigo_puc = _codigos.cod_int_mora match {
                  case Some(c) => c
                  case None    => "No Encontrado"
                }
                _codigo_nombre = codigoNombre(_codigo_puc)
              } else if (_af.devuelto) {
                println("Buscando Tasa Interes Devuelto")
                val _tasa = db.withConnection { implicit connection =>
                  SQL("""SELECT DISTINCT TASA_LIQUIDACION FROM "col$extracto" e
                                    INNER JOIN "col$extractodet" d ON (d.ID_COLOCACION = e.ID_COLOCACION) 
                                    WHERE e.ID_COLOCACION = {id_colocacion} AND e.TIPO_OPERACION = 1 AND
                                    {fecha} BETWEEN d.FECHA_INICIAL AND d.FECHA_FINAL AND d.CODIGO_PUC = {codigo_puc}
                                    AND d.VALOR_CREDITO > 0""")
                    .on(
                      'id_colocacion -> id_colocacion,
                      'fecha -> _af.fecha_final,
                      'codigo_puc -> _codigo_puc
                    )
                    .as(SqlParser.scalar[Double].singleOpt)
                }
                _tasa_dev = _tasa match {
                  case Some(t) => _funcion.tasaEfectivaVencida(t, _amortizacion)
                  case None    => 0.00
                }
              }
              _cuota_numero = _ncuota
              _fecha_inicial = _af.fecha_inicial
              _fecha_final = _af.fecha_final
              val (_ano, _mes, _dia) = _funcion.decodeDate(_af.fecha_inicial)
              var _biciesto = false
              var _devuelto = false
              if ((_fecha_arranque == _fecha_inicial) && _mes == 3 && _dia == 1 && _af.vencida == false) {
                _biciesto = true
              }
              if (_af.devuelto) {
                _devuelto = true
              }
              _dias = _funcion.diasEntreFechas(
                _af.fecha_inicial,
                _af.fecha_final,
                _fecha_desembolso
              )
              if (_af.vencida) {
                _tasa = _tasa_liquidar_mora
              } else {
                _tasa = _tasa_liquidar
              }
              if (_af.devuelto) {
                _credito = BigDecimal(0)
                _debito = _funcion.round0(
                  ((_capital * _tasa_liquidar / 100 * _dias) / 360).toDouble
                )
                _es_devuelto = true
              } else if (_af.vencida) {
                _debito = BigDecimal(0)
                _credito = _funcion.round0(
                  ((_capital * _tasa_liquidar_mora / 100 * _dias) / 360).toDouble
                )
                _es_devuelto = false
              } else {
                _debito = BigDecimal(0)
                _credito = _funcion.round0(
                  ((_saldo_actual * _tasa_liquidar / 100 * _dias) / 360).toDouble
                )
              }

              if (_af.causado) {
                _duplicar = true
                _es_causado = true
                _total_interes_credito += _credito
              } else {
                _es_causado = false
                _duplicar = false
              }
              if (_af.corrientes) {
                _es_corriente = true
                _total_interes_credito += _credito
              } else {
                _es_corriente = false
              }
              if (_af.vencida) {
                _es_vencido = true
              } else {
                _es_vencido = false
              }
              if (_af.anticipado) {
                _es_anticipado = true
                _total_interes_credito += _credito
              } else {
                _es_anticipado = false
              }

              if (_af.devuelto) {
                _es_devuelto = true
              } else {
                _es_devuelto = false
              }

              _es_otros = false
              _a_cobrar = BigDecimal(0)

              if (_es_causado || _es_corriente || _es_vencido || _es_anticipado) {
                _a_cobrar = _funcion
                  .round0((_credito * _porcentaje_gestion).toDouble / 100)
                _a_cobrar_gestion = _a_cobrar_gestion + _a_cobrar
                _credito = _credito - _a_cobrar
              }

              if (_credito != 0 || _debito != 0) {
                var _cl = new CuotasLiq(
                  _cuota_numero,
                  _codigo_puc,
                  _codigo_nombre,
                  _fecha_inicial,
                  _fecha_final,
                  _dias,
                  _tasa,
                  _debito,
                  _credito,
                  _es_capital,
                  _es_causado,
                  _es_corriente,
                  _es_vencido,
                  _es_anticipado,
                  _es_devuelto,
                  _es_otros,
                  _es_cajabanco,
                  _es_costas,
                  ""
                )
                if (_cl.debito != 0 || _cl.credito != 0) {
                  _my_cuotas_liq += _cl
                  _total_credito += _credito
                  _total_debito += _debito
                  _total_interes += _credito
                }
              }

              if (_duplicar) {
                _codigo_puc = _codigosdup.cod_int_mes match {
                  case Some(c) => c
                  case None    => "No Encontrado"
                }
                _codigo_nombre = codigoNombre(_codigo_puc)
                var _cl = new CuotasLiq(
                  _cuota_numero,
                  _codigo_puc,
                  _codigo_nombre,
                  _fecha_inicial,
                  _fecha_final,
                  _dias,
                  _tasa,
                  _debito,
                  _credito,
                  _es_capital,
                  _es_causado,
                  _es_corriente,
                  _es_vencido,
                  _es_anticipado,
                  _es_devuelto,
                  _es_otros,
                  _es_cajabanco,
                  _es_costas,
                  ""
                )
                if (_cl.debito != 0 || _cl.credito != 0) {
                  _my_cuotas_liq += _cl
                  _total_credito += _credito
                  _total_debito += _debito
                }

                _cl = new CuotasLiq(
                  _cuota_numero,
                  _codigo_puc,
                  _codigo_nombre,
                  _fecha_inicial,
                  _fecha_final,
                  _dias,
                  _tasa,
                  _credito,
                  _debito,
                  _es_capital,
                  _es_causado,
                  _es_corriente,
                  _es_vencido,
                  _es_anticipado,
                  _es_devuelto,
                  _es_otros,
                  _es_cajabanco,
                  _es_costas,
                  ""
                )
                if (_cl.debito != 0 || _cl.credito != 0) {
                  _my_cuotas_liq += _cl
                  _total_credito += _debito
                  _total_debito += _credito
                }

              }
            }
            // Agregar costo gestion de cartera
            _codigo_nombre = codigoNombre(_codigo_gestion_cartera)
            var _cl = new CuotasLiq(
              _cuota_numero,
              _codigo_gestion_cartera,
              _codigo_nombre,
              _fecha_corte,
              _fecha_corte,
              0,
              0.00,
              0.00,
              _a_cobrar_gestion,
              false,
              false,
              false,
              false,
              false,
              false,
              true,
              false,
              false,
              ""
            )
            if (_cl.debito != 0 || _cl.credito != 0) {
              _my_cuotas_liq += _cl
              _total_credito += _a_cobrar_gestion
              _total_interes += _a_cobrar_gestion
            }

            // Agregar Capital
            _codigo_puc = _codigos.cod_capital_cp match {
              case Some(c) => c
              case None    => "No Encontrado"
            }
            _codigo_nombre = codigoNombre(_codigo_puc)
            _cl = new CuotasLiq(
              _cuota_numero,
              _codigo_puc,
              _codigo_nombre,
              _fecha_corte,
              _fecha_corte,
              0,
              0.00,
              0.00,
              _capital,
              true,
              false,
              false,
              false,
              false,
              false,
              false,
              false,
              false,
              ""
            )
            if (_cl.debito != 0 || _cl.credito != 0) {
              _my_cuotas_liq += _cl
              _total_credito += _capital
            }
            _adescontar.foreach { _d =>
              if (_d.cuota_numero == _cuota_numero) {
                _codigo_nombre = codigoNombre(_d.codigo)
                _cl = new CuotasLiq(
                  _cuota_numero,
                  _d.codigo,
                  _codigo_nombre,
                  _fecha_corte,
                  _fecha_corte,
                  0,
                  0.00,
                  0.00,
                  _d.valor,
                  false,
                  false,
                  false,
                  false,
                  false,
                  false,
                  true,
                  false,
                  false,
                  ""
                )
                if (_cl.debito != 0 || _cl.credito != 0) {
                  _my_cuotas_liq += _cl
                  _total_credito += _d.valor
                }
              }
            }

            // Agregar Caja
            _codigo_nombre = codigoNombre(_codigo_caja)
            _cl = new CuotasLiq(
              _cuota_numero,
              _codigo_caja,
              _codigo_nombre,
              _fecha_corte,
              _fecha_corte,
              0,
              0.00,
              _total_credito - _total_debito,
              0.00,
              false,
              false,
              false,
              false,
              false,
              false,
              false,
              true,
              false,
              ""
            )
            if (_cl.debito != 0 || _cl.credito != 0) {
              _my_cuotas_liq += _cl
            }
            _saldo_actual = _saldo_actual - _capital
            _m_saldo_actual = _saldo_actual
            _m_interes_hasta = _fecha_pago_interes
          }

          _liquidado = true
          (
            _my_cuotas_liq.toList,
            _m_saldo_actual,
            _m_capital_hasta,
            _m_interes_hasta,
            _m_fecha_prox,
            _liquidado
          )
        // Fin Evaluar Cuota a Cuota a Liquidar

        case None =>
          var _liquidado: Boolean = false
          var _my_cuotas_liq = new ListBuffer[CuotasLiq]()
          var _m_saldo_actual = BigDecimal(0)
          var _m_interes_hasta = new DateTime()
          var _m_capital_hasta = new DateTime()
          var _m_fecha_prox = new DateTime()
          (
            _my_cuotas_liq.toList,
            _m_saldo_actual,
            _m_capital_hasta,
            _m_interes_hasta,
            _m_fecha_prox,
            _liquidado
          )
      }
    }
  }

  def liquidarCuotaVariableAnticipada(
      id_colocacion: String,
      cuotas_a_liquidar: Int,
      fecha_corte: DateTime
  ) = {
    _colocacionService.buscarColocacion(id_colocacion).map { colocacion =>
      colocacion match {
        case Some(c) =>
          var _lista = new ListBuffer[DateTime]
          var _fecha_prox = new DateTime()
          var _nuevo_saldo = BigDecimal(0)
          var _liquidado: Boolean = false
          var _my_cuotas_liq = new ListBuffer[CuotasLiq]()
          var _m_saldo_actual = BigDecimal(0)
          var _m_interes_hasta = new DateTime()
          var _m_capital_hasta = new DateTime()
          var _m_fecha_prox = new DateTime()

          val _descuento = db.withConnection { implicit connection =>
            SQL("""SELECT
                        d.*,
                        cd.ID_DESCUENTO
                         FROM "col$colocaciondescuento" cd
                        INNER JOIN "col$descuentos" d ON (d.ID_DESCUENTO = cd.ID_DESCUENTO)
                        WHERE (ID_COLOCACION = {id_colocacion})""")
              .on(
                'id_colocacion -> id_colocacion
              )
              .as(Descuento._set *)
          }

          val _cdsDescuento = new ListBuffer[DescuentoColocacion]()
          _descuento.foreach { d =>
            val _dc = new DescuentoColocacion(
              d.id_descuento,
              d.descripcion_descuento,
              true
            )
            _cdsDescuento += _dc
          }

          val _tablaLiquidacion = db.withConnection { implicit connection =>
            SQL(
              """select * from "col$tablaliquidacion" where ID_COLOCACION = {id_colocacion}"""
            ).on(
                'id_colocacion -> id_colocacion
              )
              .as(Tabla._set *)
          }

          val _total_cuotas = _tablaLiquidacion.length

          val _tablaLiquidacionNoPagada = db.withConnection {
            implicit connection =>
              SQL("""select * from "col$tablaliquidacion" where ID_COLOCACION = {id_colocacion} and PAGADA = 0
                               ORDER BY ID_COLOCACION, CUOTA_NUMERO""")
                .on(
                  'id_colocacion -> id_colocacion
                )
                .as(Tabla._set *)
          }

          val _costas = db.withConnection { implicit connection =>
            SQL(
              """SELECT SUM(VALOR_COSTAS) AS VALOR_COSTAS from "col$costas" where ID_COLOCACION = {id_colocacion}"""
            ).on(
                'id_colocacion -> id_colocacion
              )
              .as(SqlParser.scalar[BigDecimal].singleOpt)

          }

          var _cdsCuotas = new ListBuffer[Tabla]
          var _amortizacion: Int = 0
          val _valor_desembolso = c.a.valor_desembolso.get
          val _amortiza_capital = c.b.amortiza_capital.get
          val _amortiza_interes = c.b.amortiza_interes.get
          val _clasificacion = c.a.id_clasificacion.get
          val _categoria = c.a.id_categoria.get
          val _fecha_pago_capital = _funcion.limpiarFecha(c.b.fecha_capital.get)
          val _fecha_pago_interes = _funcion.limpiarFecha(c.b.fecha_interes.get)
          val _puntos_interes = c.a.puntos_interes.get
          val _tipo_interes = c.a.id_interes.get
          val _linea = c.a.id_linea.get
          val _fecha_desembolso =
            _funcion.limpiarFecha(c.a.fecha_desembolso.get)
          var _saldo_actual = c.a.valor_desembolso.get - c.b.abonos_capital.get
          var _tasa_mora = c.a.tasa_interes_mora.get
          val _valor_cuota = c.b.valor_cuota.get
          val _dias_prorroga = c.b.dias_prorrogados.get
          var _proximo_pago = new DateTime()

          var _saldo = _valor_desembolso
          _tablaLiquidacion.foreach { t =>
            val _c = new Tabla(
              t.cuot_num,
              t.cuot_fecha,
              _saldo,
              t.cuot_capital,
              t.cuot_interes,
              t.cuot_otros
            )
            _cdsCuotas += _c
            _saldo = _saldo - t.cuot_capital
          }

          val _adescontar = calcularDescuentoPorCuota(
            _cdsCuotas.toSeq,
            _cdsDescuento.toSeq,
            _valor_desembolso,
            _amortiza_capital,
            _saldo_actual
          )

          val i = 0
          var _cuotas = new ListBuffer[Tabla]
          for (i <- 0 to _cdsCuotas.length - 1) {
            _cuotas += new Tabla(
              _cdsCuotas(i).cuot_num,
              _cdsCuotas(i).cuot_fecha,
              _cdsCuotas(i).cuot_saldo,
              _cdsCuotas(i).cuot_capital,
              _cdsCuotas(i).cuot_interes,
              _adescontar(i).valor
            )
          }

          // Proceso de Liquidacion
          var _total_debito: BigDecimal = BigDecimal(0)
          var _total_credito: BigDecimal = BigDecimal(0)

          var _fecha_corte = fecha_corte

          if (_amortiza_capital < _amortiza_interes) {
            _amortizacion = _amortiza_capital
          } else {
            _amortizacion = _amortiza_interes
          }

          val _codigo_caja = db.withConnection { implicit connection =>
            SQL(
              """SELECT CODIGO FROM "col$codigospucbasicos" WHERE ID_CODIGOPUCBASICO = 1"""
            ).as(SqlParser.scalar[String].single)
          }

          val _codigo_gestion_cartera = db.withConnection {
            implicit connection =>
              SQL(
                """SELECT CODIGO FROM "col$codigospucbasicos" WHERE ID_CODIGOPUCBASICO = 62"""
              ).as(SqlParser.scalar[String].single)
          }

          val _tasa_usura = db.withConnection { implicit connection =>
            SQL(
              """SELECT VALOR_MINIMO FROM "gen$minimos" WHERE ID_MINIMO = 100"""
            ).as(SqlParser.scalar[Double].single)
          }

          var _porcentaje_gestion = 0
          val _valor_tasa_efectiva = c.a.tasa_interes_corriente.get
          if (_tasa_usura < _valor_tasa_efectiva) {
            _porcentaje_gestion = math.round(
              ((_valor_tasa_efectiva - _tasa_usura) * 100).toFloat / _valor_tasa_efectiva.toFloat
            )
          }
          val _codigos = _codigopuccolocacionService.obtener(
            c.a.id_clasificacion.get,
            c.a.id_garantia.get,
            c.a.id_categoria.get
          )
          val _codigosdup = _codigopuccolocacionService.obtenerFacturado(
            c.a.id_clasificacion.get,
            c.a.id_garantia.get,
            c.a.id_categoria.get
          )
          var _cuota_liq = new ListBuffer[CuotasLiq]()
          var _codigo_puc = ""
          var _codigo_nombre = ""
          // Evaluar Costas
          _costas match {
            case Some(_costas) =>
              if (_costas > 0) {
                _codigo_puc = _codigos.cod_costas match {
                  case Some(c) => c
                  case None    => "No Encontrado"
                }
                _codigo_nombre = codigoNombre(_codigo_puc)
                var _cl = new CuotasLiq(
                  0,
                  _codigos.cod_costas.get,
                  _codigo_nombre,
                  _fecha_corte,
                  _fecha_corte,
                  0,
                  0,
                  0,
                  _costas,
                  false,
                  false,
                  false,
                  false,
                  false,
                  false,
                  false,
                  false,
                  true,
                  ""
                )
                if (_cl.debito != 0 || _cl.credito != 0) {
                  _cuota_liq += _cl
                  _total_debito += _cl.debito
                  _total_credito += _cl.credito
                }
              }
            case None => None
          }
          // Fin Evaluar Costas
          // Evaluar Cuota a Cuota a Liquidar
          var cuota = 0
          var _fecha_prox_capital = new DateTime()
          var _fecha_prox_nueva = new DateTime()
          for (cuota <- 0 to (cuotas_a_liquidar - 1)) {
            val _ncuota = _tablaLiquidacionNoPagada(cuota).cuot_num
            var _interes_cuota = _funcion.round0(
              ((_saldo_actual * (_funcion.tasaNominalVencida(
                _valor_tasa_efectiva,
                _amortizacion
              ) + _puntos_interes) / 100 * _amortizacion) / 360).toDouble
            )
            var _capital = _valor_cuota - _interes_cuota
            var _cuota_numero = _ncuota
            var _fecha1 = _fecha_pago_interes
            _fecha_prox = _tablaLiquidacionNoPagada(cuota).cuot_fecha
            _fecha_prox = _funcion.limpiarFecha(_fecha_prox)
            _fecha_prox = _funcion.calculoFecha(_fecha_prox, _dias_prorroga)
            _fecha_prox_capital = _fecha_prox
            _fecha_prox_nueva = _fecha_prox
            _m_capital_hasta = _fecha_prox_capital

            if (_saldo_actual < _capital) {
              _capital = _saldo_actual
            }

            if (_ncuota == _total_cuotas) {
              _capital = _saldo_actual
              _m_fecha_prox = new DateTime(0)
            } else {
              _proximo_pago = _funcion.calculoFecha(_fecha_prox, _amortizacion)
              var (_a, _m, _d) = _funcion.decodeDate(_fecha_desembolso)
              var (_aa, _mm, _dd) = _funcion.decodeDate(_proximo_pago)

              if ((_mm == 2) && (_d > _proximo_pago.dayOfMonth().get)) {
                _d = _dd
              }
              _proximo_pago = new DateTime(_aa, _mm, _d, 0, 0)
              _m_fecha_prox = _proximo_pago
            }

            var _fecha_arranque = _fecha1.plusDays(1)

            val _l1 = calcularFechasALiquidarFija(
              _fecha_arranque,
              _fecha_corte,
              _fecha_prox
            )
            var _l2 = List.empty[FechaLiq]
            var _l3 = List.empty[FechaLiq]
            if (_fecha_prox_nueva.getMillis() > _fecha_corte.getMillis()) {
              _l2 = calcularFechasDevolucion(
                _fecha_desembolso,
                _fecha_corte,
                _fecha_prox_nueva
              )
            } else {
              _l3 = calcularFechasMora(_fecha_corte, _fecha_prox_nueva)
            }
            val _fechas_liq = List.concat(_l1, _l2, _l3)

            var _total_interes = BigDecimal(0)
            var _total_interes_credito = BigDecimal(0)
            var _a_cobrar_gestion = BigDecimal(0)
            var _tasa_liquidar = 0.00
            var _tasa_efectiva = 0.00
            _fechas_liq.foreach { _af =>
              val _tasa_maxima =
                buscoTasaEfectivaMaxima(_af.fecha_final, _clasificacion, "A")
              _tipo_interes match {
                case 0 =>
                  _tasa_efectiva = _tasa_maxima
                  if (_valor_tasa_efectiva > _tasa_efectiva) {
                    _tasa_liquidar = _tasa_efectiva
                  } else {
                    _tasa_liquidar = _valor_tasa_efectiva
                  }
                  _tasa_liquidar =
                    _funcion.tasaNominalVencida(_tasa_liquidar, _amortizacion)
                case 1 =>
                  _tasa_efectiva =
                    buscoTasaEfectivaMaximaDtf(_fecha_pago_interes)
                  _tasa_liquidar =
                    _funcion.tasaNominalVencida(_tasa_efectiva, _amortizacion)
                case _ =>
                  _tasa_efectiva = buscoTasaEfectivaMaximaIpc
                  _tasa_liquidar =
                    _funcion.tasaNominalVencida(_tasa_efectiva, _amortizacion)
              }
              var _tasa_liquidar_mora =
                _funcion.tasaNominalVencida(_tasa_mora, _amortizacion)
              var _vtasa = _tasa_liquidar
              var _duplicar = false
              var _a_cobrar = BigDecimal(0)
              // inicio variables cuotas liq
              _cuota_numero = 0
              _codigo_puc = ""
              _codigo_nombre = ""
              var _fecha_inicial = new DateTime()
              var _fecha_final = new DateTime()
              var _dias = 0
              var _tasa = 0.00
              var _debito = BigDecimal(0)
              var _credito = BigDecimal(0)
              var _es_capital = false
              var _es_causado = false
              var _es_corriente = false
              var _es_vencido = false
              var _es_anticipado = false
              var _es_devuelto = false
              var _es_otros = false
              var _es_cajabanco = false
              var _es_costas = false
              var _id_clase_operacion = false
              // fin variables para cuotasliq
              var _tasa_dev = 0.00
              if (_af.anticipado || _af.devuelto) {
                _codigo_puc = _codigosdup.cod_int_ant match {
                  case Some(c) => c
                  case None    => "No Encontrado"
                }
                _codigo_nombre = codigoNombre(_codigo_puc)
              } else if (_af.causado) {
                _codigo_puc = _codigos.cod_cxc match {
                  case Some(c) => c
                  case None    => "No Encontrado"
                }
                _codigo_nombre = codigoNombre(_codigo_puc)
              } else if (_af.corrientes) {
                _codigo_puc = _codigos.cod_int_mes match {
                  case Some(c) => c
                  case None    => "No Encontrado"
                }
                _codigo_nombre = codigoNombre(_codigo_puc)
              } else if (_af.vencida) {
                _codigo_puc = _codigos.cod_int_mora match {
                  case Some(c) => c
                  case None    => "No Encontrado"
                }
                _codigo_nombre = codigoNombre(_codigo_puc)
              } else if (_af.devuelto) {
                println("Buscando Tasa Interes Devuelto")
                val _tasa = db.withConnection { implicit connection =>
                  SQL("""SELECT DISTINCT TASA_LIQUIDACION FROM "col$extracto" e
                                    INNER JOIN "col$extractodet" d ON (d.ID_COLOCACION = e.ID_COLOCACION) 
                                    WHERE e.ID_COLOCACION = {id_colocacion} AND e.TIPO_OPERACION = 1 AND
                                    {fecha} BETWEEN d.FECHA_INICIAL AND d.FECHA_FINAL AND d.CODIGO_PUC = {codigo_puc}
                                    AND d.VALOR_CREDITO > 0""")
                    .on(
                      'id_colocacion -> id_colocacion,
                      'fecha -> _af.fecha_final,
                      'codigo_puc -> _codigo_puc
                    )
                    .as(SqlParser.scalar[Double].singleOpt)
                }
                _tasa_dev = _tasa match {
                  case Some(t) => _funcion.tasaEfectivaVencida(t, _amortizacion)
                  case None    => 0.00
                }
              }
              _cuota_numero = _ncuota
              _fecha_inicial = _af.fecha_inicial
              _fecha_final = _af.fecha_final
              val (_ano, _mes, _dia) = _funcion.decodeDate(_af.fecha_inicial)
              var _biciesto = false
              var _devuelto = false
              if ((_fecha_arranque == _fecha_inicial) && _mes == 3 && _dia == 1 && _af.vencida == false) {
                _biciesto = true
              }
              if (_af.devuelto) {
                _devuelto = true
              }
              _dias = _funcion.diasEntreFechas(
                _af.fecha_inicial,
                _af.fecha_final,
                _fecha_desembolso
              )
              if (_af.vencida) {
                _tasa = _tasa_liquidar_mora
              } else {
                _tasa = _tasa_liquidar
              }
              if (_af.devuelto) {
                _credito = BigDecimal(0)
                _debito = _funcion.round0(
                  ((_capital * _tasa_liquidar / 100 * _dias) / 360).toDouble
                )
                _es_devuelto = true
              } else if (_af.vencida) {
                _debito = BigDecimal(0)
                _credito = _funcion.round0(
                  ((_capital * _tasa_liquidar_mora / 100 * _dias) / 360).toDouble
                )
                _es_devuelto = false
              } else {
                _debito = BigDecimal(0)
                _credito = _funcion.round0(
                  ((_saldo_actual * _tasa_liquidar / 100 * _dias) / 360).toDouble
                )
              }

              if (_af.causado) {
                _duplicar = true
                _es_causado = true
                _total_interes_credito += _credito
              } else {
                _es_causado = false
                _duplicar = false
              }
              if (_af.corrientes) {
                _es_corriente = true
                _total_interes_credito += _credito
              } else {
                _es_corriente = false
              }
              if (_af.vencida) {
                _es_vencido = true
              } else {
                _es_vencido = false
              }
              if (_af.anticipado) {
                _es_anticipado = true
                _total_interes_credito += _credito
              } else {
                _es_anticipado = false
              }

              if (_af.devuelto) {
                _es_devuelto = true
              } else {
                _es_devuelto = false
              }

              _es_otros = false
              _a_cobrar = BigDecimal(0)

              if (_es_causado || _es_corriente || _es_vencido || _es_anticipado) {
                _a_cobrar = _funcion
                  .round0((_credito * _porcentaje_gestion).toDouble / 100)
                _a_cobrar_gestion = _a_cobrar_gestion + _a_cobrar
                _credito = _credito - _a_cobrar
              }

              if (_credito != 0 || _debito != 0) {
                var _cl = new CuotasLiq(
                  _cuota_numero,
                  _codigo_puc,
                  _codigo_nombre,
                  _fecha_inicial,
                  _fecha_final,
                  _dias,
                  _tasa,
                  _debito,
                  _credito,
                  _es_capital,
                  _es_causado,
                  _es_corriente,
                  _es_vencido,
                  _es_anticipado,
                  _es_devuelto,
                  _es_otros,
                  _es_cajabanco,
                  _es_costas,
                  ""
                )
                if (_cl.debito != 0 || _cl.credito != 0) {
                  _my_cuotas_liq += _cl
                  _total_credito += _credito
                  _total_debito += _debito
                  _total_interes += _credito
                }
              }

              if (_duplicar) {
                _codigo_puc = _codigosdup.cod_int_mes match {
                  case Some(c) => c
                  case None    => "No Encontrado"
                }
                _codigo_nombre = codigoNombre(_codigo_puc)
                var _cl = new CuotasLiq(
                  _cuota_numero,
                  _codigo_puc,
                  _codigo_nombre,
                  _fecha_inicial,
                  _fecha_final,
                  _dias,
                  _tasa,
                  _debito,
                  _credito,
                  _es_capital,
                  _es_causado,
                  _es_corriente,
                  _es_vencido,
                  _es_anticipado,
                  _es_devuelto,
                  _es_otros,
                  _es_cajabanco,
                  _es_costas,
                  ""
                )
                if (_cl.debito != 0 || _cl.credito != 0) {
                  _my_cuotas_liq += _cl
                  _total_credito += _credito
                  _total_debito += _debito
                }

                _cl = new CuotasLiq(
                  _cuota_numero,
                  _codigo_puc,
                  _codigo_nombre,
                  _fecha_inicial,
                  _fecha_final,
                  _dias,
                  _tasa,
                  _credito,
                  _debito,
                  _es_capital,
                  _es_causado,
                  _es_corriente,
                  _es_vencido,
                  _es_anticipado,
                  _es_devuelto,
                  _es_otros,
                  _es_cajabanco,
                  _es_costas,
                  ""
                )
                if (_cl.debito != 0 || _cl.credito != 0) {
                  _my_cuotas_liq += _cl
                  _total_credito += _debito
                  _total_debito += _credito
                }

              }
            }
            // Agregar costo gestion de cartera
            _codigo_nombre = codigoNombre(_codigo_gestion_cartera)
            var _cl = new CuotasLiq(
              _cuota_numero,
              _codigo_gestion_cartera,
              _codigo_nombre,
              _fecha_corte,
              _fecha_corte,
              0,
              0.00,
              0.00,
              _a_cobrar_gestion,
              false,
              false,
              false,
              false,
              false,
              false,
              true,
              false,
              false,
              ""
            )
            if (_cl.debito != 0 || _cl.credito != 0) {
              _my_cuotas_liq += _cl
              _total_credito += _a_cobrar_gestion
              _total_interes += _a_cobrar_gestion
            }

            // Agregar Capital
            _codigo_puc = _codigos.cod_capital_cp match {
              case Some(c) => c
              case None    => "No Encontrado"
            }
            _codigo_nombre = codigoNombre(_codigo_puc)
            _cl = new CuotasLiq(
              _cuota_numero,
              _codigo_puc,
              _codigo_nombre,
              _fecha_corte,
              _fecha_corte,
              0,
              0.00,
              0.00,
              _capital,
              true,
              false,
              false,
              false,
              false,
              false,
              false,
              false,
              false,
              ""
            )
            if (_cl.debito != 0 || _cl.credito != 0) {
              _my_cuotas_liq += _cl
              _total_credito += _capital
            }
            _adescontar.foreach { _d =>
              if (_d.cuota_numero == _cuota_numero) {
                _codigo_nombre = codigoNombre(_d.codigo)
                _cl = new CuotasLiq(
                  _cuota_numero,
                  _d.codigo,
                  _codigo_nombre,
                  _fecha_corte,
                  _fecha_corte,
                  0,
                  0.00,
                  0.00,
                  _d.valor,
                  false,
                  false,
                  false,
                  false,
                  false,
                  false,
                  true,
                  false,
                  false,
                  ""
                )
                if (_cl.debito != 0 || _cl.credito != 0) {
                  _my_cuotas_liq += _cl
                  _total_credito += _d.valor
                }
              }
            }

            // Agregar Caja
            _codigo_nombre = codigoNombre(_codigo_caja)
            _cl = new CuotasLiq(
              _cuota_numero,
              _codigo_caja,
              _codigo_nombre,
              _fecha_corte,
              _fecha_corte,
              0,
              0.00,
              _total_credito - _total_debito,
              0.00,
              false,
              false,
              false,
              false,
              false,
              false,
              false,
              true,
              false,
              ""
            )
            if (_cl.debito != 0 || _cl.credito != 0) {
              _my_cuotas_liq += _cl
            }
            _saldo_actual = _saldo_actual - _capital
            _m_saldo_actual = _saldo_actual
            _m_interes_hasta = _fecha_pago_interes
          }

          _liquidado = true
          (
            _my_cuotas_liq.toList,
            _m_saldo_actual,
            _m_capital_hasta,
            _m_interes_hasta,
            _m_fecha_prox,
            _liquidado
          )
        // Fin Evaluar Cuota a Cuota a Liquidar

        case None =>
          var _liquidado: Boolean = false
          var _my_cuotas_liq = new ListBuffer[CuotasLiq]()
          var _m_saldo_actual = BigDecimal(0)
          var _m_interes_hasta = new DateTime()
          var _m_capital_hasta = new DateTime()
          var _m_fecha_prox = new DateTime()
          (
            _my_cuotas_liq.toList,
            _m_saldo_actual,
            _m_capital_hasta,
            _m_interes_hasta,
            _m_fecha_prox,
            _liquidado
          )
      }
    }
  }

  def liquidarCuotaVariableVencida(
      id_colocacion: String,
      cuotas_a_liquidar: Int,
      fecha_corte: DateTime
  ) = {
    _colocacionService.buscarColocacion(id_colocacion).map { colocacion =>
      colocacion match {
        case Some(c) =>
          var _liquidado: Boolean = false
          var _my_cuotas_liq = new ListBuffer[CuotasLiq]()
          var _m_saldo_actual = BigDecimal(0)
          var _m_interes_hasta = new DateTime()
          var _m_capital_hasta = new DateTime()
          var _m_fecha_prox = new DateTime()
          (
            _my_cuotas_liq.toList,
            _m_saldo_actual,
            _m_capital_hasta,
            _m_interes_hasta,
            _m_fecha_prox,
            _liquidado
          )
        case None =>
          var _liquidado: Boolean = false
          var _my_cuotas_liq = new ListBuffer[CuotasLiq]()
          var _m_saldo_actual = BigDecimal(0)
          var _m_interes_hasta = new DateTime()
          var _m_capital_hasta = new DateTime()
          var _m_fecha_prox = new DateTime()
          (
            _my_cuotas_liq.toList,
            _m_saldo_actual,
            _m_capital_hasta,
            _m_interes_hasta,
            _m_fecha_prox,
            _liquidado
          )
      }
    }
  }

  def reajustarTablaPeriodoGracia(id_colocacion: String, dias: Int): Boolean = {
    var _result: Boolean = true
    var _nueva_fecha = new DateTime()
    val _tabla = db.withTransaction { implicit connection =>
      SQL(
        """SELECT t.* FROM "col$tablaliquidacion" t WHERE t.ID_COLOCACION = {id_colocacion}  AND t.PAGADA = 0"""
      ).on(
          'id_colocacion -> id_colocacion
        )
        .as(Tabla._set *)
    }

    _tabla.foreach { t =>
      println("fila: " + t.toString)
      _nueva_fecha = _funcion.calculoFecha(t.cuot_fecha, dias)
      val _updresult = _result && db.withTransaction { implicit connection =>
        SQL(
          """UPDATE "col$tablaliquidacion" 
                       SET FECHA_A_PAGAR = {nueva_fecha_a_pagar} 
                       WHERE ID_COLOCACION = {id_colocacion} and CUOTA_NUMERO = {cuota} """
        ).on(
            'id_colocacion -> id_colocacion,
            'cuota -> t.cuot_num,
            'nueva_fecha_a_pagar -> _nueva_fecha
          )
          .executeUpdate() > 0
      }
      println("actualicé fila en tabla liquidacion: " + _updresult)
      _result = _result && _updresult
    }
    _result
  }

  def codigoNombre(codigo: String) = {
    if (codigo.equals("No Encontrado")) {
      "No Encontrado"
    } else {
      val _nombre = db.withConnection { implicit connection =>
        SQL("""SELECT NOMBRE FROM "con$puc" WHERE CODIGO = {codigo}""")
          .on(
            'codigo -> codigo
          )
          .as(SqlParser.scalar[String].singleOpt)
      }
      _nombre match {
        case Some(n) => n
        case None    => "No Encontrado"
      }
    }
  }
}
