package utilities

import javax.inject.Inject
import java.util.Calendar

import play.api.db.DBApi

import anorm._
import anorm.SqlParser.{ get, str }
import anorm.JodaParameterMetaData._

import org.joda.time.DateTime

import scala.collection.mutable.ListBuffer
import scala.math.BigDecimal

import models._

case class CuotasLiq(
    cuotaNumero: Int,
    codigoPuc: String,
    fechaInicial: DateTime,
    fechaFinal: DateTime,
    dias: Int,
    tasa: Double,
    debito: BigDecimal,
    credito:  BigDecimal,
    esCapital: Boolean,
    esCausado: Boolean,
    esCorriente: Boolean,
    esVencido: Boolean,
    esAnticipado: Boolean,
    esDevuelto: Boolean,
    esOtros: Boolean,
    idClaseOperacion: String    
)

class GlobalesCol @Inject()(dbapi: DBApi, _funcion: Funcion, _colocacionService: ColocacionRepository, _codigopuccolocacionService: CodigoPucColocacionRepository)(implicit ec: DatabaseExecutionContext) {
    private val db = dbapi.database("default")

    def CalcularDescuentoPorCuota(_cuota: Seq[Tabla], _descuento: Seq[DescuentoColocacion], _valorDesembolso: BigDecimal, _amortizaCapital: Int, _saldoActual: BigDecimal): ListBuffer[ADescontar] = {
        var _descontar = new ListBuffer[ADescontar]
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
                SQL("""SELECT * FROM "col$descuentos" WHERE ID_DESCUENTO = {id_descuento}""").
                on(
                    'id_descuento -> _d.id_descuento
                ).as(Descuento._set.single)
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

            if (nValor > 0 ) {
                if (bEnDesembolso) { return _descontar }
                if (bEsDistribuido) { nCobro = math.round(nValor.doubleValue / _cuota.length) }
                if (bEnCuota) {
                    nCobro = nValor
                    _cuota.foreach { _c =>
                        var _ad = new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
                        _descontar += _ad
                    }
                }
            } else if (nPorcColocacion > 0) {
                if (bEnDesembolso) { return _descontar }
                nCobro = math.round(_valorDesembolso.doubleValue * nPorcColocacion/100)*(_amortizaCapital/nAmortiza);  
                if (bEsDistribuido) { nCobro = math.round(nCobro.doubleValue / _cuota.length) }
                if (bEnCuota) {
                    _cuota.foreach { _c =>
                        var _ad = new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
                        _descontar += _ad
                    }
                }              
            } else if (nPorcSaldo > 0) {
                if (bEnDesembolso) { return _descontar }
                if (bEnCuota && !bEsDistribuido) {
                    nValor = _valorDesembolso
                    _cuota.foreach { _c =>
                        nCobro = math.round(nValor.doubleValue * nPorcSaldo/100)*(_amortizaCapital/nAmortiza)
                        nValor = nValor - _c.cuot_capital
                        var _ad = new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
                        _descontar += _ad
                    }
                } else if (bEnCuota && bEsDistribuido) {
                    nValor = _valorDesembolso
                    nCobro = 0
                    _cuota.foreach { _c =>
                        nCobro = nCobro + math.round(nValor.doubleValue * nPorcSaldo/100)*(_amortizaCapital/nAmortiza)
                        nValor = nValor - _c.cuot_capital
                    }
                    nCobro = math.round(nCobro.doubleValue / _cuota.length)
                    _cuota.foreach { _c =>
                        var _ad = new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
                        _descontar += _ad
                    }
                }
            } else if (nPorcCuota > 0) {
                if (bEnDesembolso) { return _descontar }
                if (bEnCuota && !bEsDistribuido){
                    _cuota.foreach { _c => 
                        nCobro = math.round(_c.cuot_capital.doubleValue * nPorcCuota)
                        var _ad = new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
                        _descontar += _ad                        
                    }
                }
            }
          }
        }
        _descontar
    }

    def LiquidarCuotaFija(id_colocacion: String, cuotas_a_liquidar: Int, fecha_corte: DateTime) = {

        _colocacionService.buscarColocacion(id_colocacion).map { colocacion =>
            colocacion match {
                case Some(c) => 
                    var _lista = new ListBuffer[DateTime]
                    var _fecha_prox = new DateTime()
                    var _nuevo_saldo = BigDecimal(0)
                    var _interes_hasta = new DateTime()
                    var _capital_hasta = new DateTime()
                    var _liquidado: Boolean = false

                    val _descuento = db.withConnection { implicit connection =>
                        SQL("""SELECT
                        cd.ID_DESCUENTO, 
                        cd.DESCRIPCION_DESCUENTO FROM "col$colocaciondescuento" cd
                        INNER JOIN "col$descuentos" d ON (d.ID_DESCUENTO = cd.ID_DESCUENTO)
                        WHERE (ID_COLOCACION = {id_colocacion})""").
                        on(
                            'id_colocacion -> id_colocacion
                        ).as(Descuento._set *)
                    }

                    val _cdsDescuento = new ListBuffer[DescuentoColocacion]()
                    _descuento.foreach { d =>
                        val _dc = new DescuentoColocacion(d.id_descuento, d.descripcion_descuento, true)
                        _cdsDescuento += _dc
                    }

                    val _tablaLiquidacion = db.withConnection { implicit connection => 
                        SQL("""select * from "col$tablaliquidacion" where ID_COLOCACION = {id_colocacion}""").
                        on(
                            'id_colocacion -> id_colocacion
                        ).as(Tabla._set *)
                    }

                    val _total_cuotas = _tablaLiquidacion.length

                    val _tablaLiquidacionNoPagada = db.withConnection { implicit connection => 
                        SQL("""select * from "col$tablaliquidacion" where ID_COLOCACION = {id_colocacion} and PAGADA = 0
                               ORDER BY ID_COLOCACION, CUOTA_NUMERO""").
                        on(
                            'id_colocacion -> id_colocacion
                        ).as(Tabla._set *)
                    }

                    val _costas = db.withConnection { implicit connection =>
                        SQL("""SELECT SUM(VALOR_COSTAS) AS VALOR_COSTAS from "col$costas" where ID_COLOCACION = {id_colocacion}""").
                        on(
                            'id_colocacion -> id_colocacion
                        ).as(SqlParser.scalar[BigDecimal].single)

                    }

                    var _cdsCuotas = new ListBuffer[Tabla]
                    var _amortizacion:Int = 0
                    val _valor_desembolso = c.a.valor_desembolso.get
                    val _amortiza_capital = c.b.amortiza_capital.get
                    val _amortiza_interes = c.b.amortiza_interes.get
                    val _puntos_interes = c.a.puntos_interes.get
                    val _linea = c.a.id_linea.get
                    val _fecha_desembolso = c.a.fecha_desembolso.get
                    val _saldo_actual = c.a.valor_desembolso.get - c.b.abonos_capital.get
                    val _tasa_mora = c.a.tasa_interes_mora.get
                    val _valor_cuota = c.b.valor_cuota.get
                    val _dias_prorroga = c.b.dias_prorrogados.get
                    var _proximo_pago = new DateTime()

                    var _saldo = _valor_desembolso
                    _tablaLiquidacion.foreach { t =>
                        val _c = new Tabla(t.cuot_num, t.cuot_fecha, _saldo, t.cuot_capital, t.cuot_interes, t.cuot_otros)
                        _cdsCuotas += _c
                        _saldo = _saldo - t.cuot_capital
                    }

                    val _adescontar = CalcularDescuentoPorCuota(_cdsCuotas, _cdsDescuento, _valor_desembolso, _amortiza_capital, _saldo_actual)

                    val i = 0
                    var _cuotas = new ListBuffer[Tabla]
                    for( i <- 1 to _cdsCuotas.length){
                        _cuotas += new Tabla(_cdsCuotas(i).cuot_num, _cdsCuotas(i).cuot_fecha, _cdsCuotas(i).cuot_saldo, _cdsCuotas(i).cuot_capital, _cdsCuotas(i).cuot_interes, _adescontar(i).valor)
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
                        SQL("""SELECT CODIGO FROM "col$codigospucbasicos" WHERE ID_CODIGOPUCBASICO = 1""").as(SqlParser.scalar[String].single)
                    }

                    val _codigo_gestion_cartera = db.withConnection { implicit connection => 
                        SQL("""SELECT CODIGO FROM "col$codigospucbasicos" WHERE ID_CODIGOPUCBASICO = 62""").as(SqlParser.scalar[String].single)
                    }

                    val _tasa_usura = db.withConnection { implicit connection =>
                        SQL("""SELECT VALOR_MINIMO FROM "gen$minimos" WHERE ID_MINIMO = 100""").as(SqlParser.scalar[Double].single)
                    }

                    var _porcentaje_gestion = 0
                    val _valor_tasa_efectiva = c.a.tasa_interes_corriente.get
                    if (_tasa_usura < _valor_tasa_efectiva) {
                        _porcentaje_gestion = math.round(((_valor_tasa_efectiva - _tasa_usura) * 100).toFloat / _valor_tasa_efectiva.toFloat)
                    }
                    val _codigos = _codigopuccolocacionService.obtener(c.a.id_clasificacion.get, c.a.id_garantia.get, c.a.id_categoria.get)

                    var _cuota_liq = new ListBuffer[CuotasLiq]()
                    // Evaluar Costas
                    if (_costas > 0) {
                        var _cl = new CuotasLiq(
                            0,
                            _codigos.cod_costas.get,
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
                            ""
                        )
                        _cuota_liq += _cl
                        _total_debito += _cl.debito
                        _total_credito += _cl.credito
                    }
                    // Fin Evaluar Costas
                    // Evaluar Cuota a Cuota a Liquidar
                    var cuota = 0
                    var _fecha_prox_capital = new DateTime()
                    var _fecha_prox_nueva = new DateTime()
                    for (cuota <- 0 to (cuotas_a_liquidar - 1)) {
                        val _ncuota = _tablaLiquidacionNoPagada(cuota).cuot_num
                        var _interes_cuota = _funcion.round0(((_saldo_actual * (_funcion.tasaNominalVencida(_valor_tasa_efectiva, _amortizacion) + _puntos_interes) / 100 * _amortizacion ) / 360).toDouble)
                        var _capital = _valor_cuota - _interes_cuota
                        _fecha_prox = _tablaLiquidacionNoPagada(cuota).cuot_fecha
                        _fecha_prox = _funcion.calculoFecha(_fecha_prox, _dias_prorroga)
                        _fecha_prox_capital = _fecha_prox
                        _fecha_prox_nueva = _fecha_prox
                        _capital_hasta = _fecha_prox

                        if (_saldo_actual < _capital) {
                            _capital = _saldo_actual
                        }

                        if (_ncuota == _total_cuotas) {
                            _capital = _saldo_actual
                            _fecha_prox = new DateTime(0)
                        } else {
                            _proximo_pago = _funcion.calculoFecha(_fecha_prox, _amortizacion)
                            var (_a, _m, _d) = _funcion.decodeDate(_fecha_desembolso)
                            var (_aa, _mm, _dd) = _funcion.decodeDate(_proximo_pago)

                            if ((_mm == 2) && ( _d > _proximo_pago.dayOfMonth().get)) { _d = _dd }
                            _proximo_pago = new DateTime(_aa,_mm, _d)
                            _fecha_prox = _proximo_pago
                        }

                    }
                    // Fin Evaluar Cuota a Cuota a Liquidar

                case None => None
            }
        }
    }
}