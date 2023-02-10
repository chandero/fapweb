package models

import javax.inject.Inject
import java.util.Calendar
import java.util.UUID.randomUUID

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

import play.api.db.DBApi

import anorm._
import anorm.SqlParser.{get, str}
import anorm.JodaParameterMetaData._

import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import scala.collection.mutable

import scala.collection.mutable.ListBuffer
import scala.math.BigDecimal

import utilities._
import java.time.DateTimeException
import java.sql.Connection

class CreditoRepository @Inject()(dbapi: DBApi, _g: GlobalesCol, _gd: GlobalesCon)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  def liquidar(id_agencia:Int, id_colocacion: String, cuotas: Int, fecha_corte: Long, es_web: Boolean): Future[Liquidacion] = {
    var dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    var df = DateTimeFormat.forPattern("yyyy-MM-dd");
    var id_tipo_cuota = db.withConnection { implicit connection =>
      SQL("""SELECT c.ID_TIPO_CUOTA FROM "col$colocacion" c WHERE c.ID_COLOCACION = {id_colocacion}""").
      on(
        'id_colocacion -> id_colocacion
      ).as(SqlParser.scalar[Long].single)
    }
    val liquidacion = id_tipo_cuota match {
      case 1 => liquidacionCuotaFija(id_agencia, id_colocacion, cuotas, fecha_corte)
      case 2 => liquidacionCuotaVariableAnticipada(id_agencia, id_colocacion, cuotas, fecha_corte)
      case 3 => liquidacionCuotaVariableVencida(id_agencia, id_colocacion, cuotas, fecha_corte)
    }

    if (es_web) {
      liquidacion.map { _l =>
        db.withTransaction { implicit transaction =>
        // guardar liquidacion
          SQL("""INSERT INTO LIQUIDACION VALUES({referencia}, {fecha}, {id_colocacion}, {saldo}, {fecha_capital}, {fecha_interes}, {fecha_proxima}, {aplicada})""").
            on(
              'referencia -> _l.referencia,
              'fecha -> dtf.print(DateTime.now()),
              'id_colocacion -> id_colocacion,
              'saldo -> _l.saldo,
              'fecha_capital -> df.print(_l.fecha_capital.get),
              'fecha_interes -> df.print(_l.fecha_interes.get),
              'fecha_proxima -> df.print(_l.fecha_proxima.get),
              'aplicada -> 0
            ).executeUpdate()
          println("Items: " + _l.items)
          _l.items match {
            case Some(items) => items.map { item =>
              println("item: " + item)
              SQL("""INSERT INTO LIQUIDACION_DETALLE VALUES (
                {referencia},
                {cuotaNumero},
                {codigoPuc},
                {codigoNombre},
                {fechaInicial},
                {fechaFinal},
                {dias},
                {tasa},
                {debito},
                {credito},
                {esCapital},
                {esCausado},
                {esCorriente},
                {esVencido},
                {esAnticipado},
                {esDevuelto},
                {esOtros},
                {esCajaBanco},
                {esCostas},
                {idClaseOperacion})""").
              on(
                'referencia -> _l.referencia,
                'cuotaNumero -> item.cuotaNumero,
                'codigoPuc -> item.codigoPuc,
                'codigoNombre -> item.codigoNombre,
                'fechaInicial -> df.print(item.fechaInicial),
                'fechaFinal -> df.print(item.fechaFinal),
                'dias -> item.dias,
                'tasa -> item.tasa,
                'debito -> item.debito,
                'credito -> item.credito,
                'esCapital -> item.esCapital,
                'esCausado -> item.esCausado,
                'esCorriente -> item.esCorriente,
                'esVencido -> item.esVencido,
                'esAnticipado -> item.esAnticipado,
                'esDevuelto -> item.esDevuelto,
                'esOtros -> item.esOtros,
                'esCajaBanco -> item.esCajaBanco,
                'esCostas -> item.esCostas,
                'idClaseOperacion -> item.idClaseOperacion
              ).executeUpdate()
           }
            case None => None
          }
        }
      }
    } // cierre if es_web

    liquidacion
  } 

  def liquidacionCuotaFija(id_agencia: Int, id_colocacion: String, cuotas: Int, fecha_corte: Long): Future[Liquidacion] = {
    val _fecha_corte = new DateTime(fecha_corte)
    val _fecha = new DateTime()
    var uuid: String = randomUUID().toString()
    _g.liquidarCuotaFija(id_colocacion, cuotas, _fecha_corte).map { _l =>
        if (_l._6) {
            val result = new Liquidacion(uuid, _fecha, id_colocacion,  Some(_l._2), Some(_l._3), Some(_l._4), Some(_l._5), _l._6, id_agencia, Some(_l._1))
            result
        } else {
            val result = new Liquidacion(uuid, _fecha, id_colocacion, None, None, None, None, _l._6, id_agencia, None)
            result
        }
      }
  }

  def liquidacionCuotaVariableAnticipada(id_agencia: Int, id_colocacion: String, cuotas: Int, fecha_corte: Long): Future[Liquidacion] = {
    val _fecha_corte = new DateTime(fecha_corte)
    val _fecha = new DateTime()
    var uuid: String = randomUUID().toString()
    _g.liquidarCuotaVariableAnticipada(id_colocacion, cuotas, _fecha_corte).map { _l =>
        if (_l._6) {
            val result = new Liquidacion(uuid, _fecha, id_colocacion,  Some(_l._2), Some(_l._3), Some(_l._4), Some(_l._5), _l._6, id_agencia, Some(_l._1))
            result
        } else {
            val result = new Liquidacion(uuid, _fecha, id_colocacion, None, None, None, None, _l._6, id_agencia, None)
            result
        }
      }
  }

  def liquidacionCuotaVariableVencida(id_agencia: Int, id_colocacion: String, cuotas: Int, fecha_corte: Long): Future[Liquidacion] = {
    val _fecha_corte = new DateTime(fecha_corte)
    val _fecha = new DateTime()
    var uuid: String = randomUUID().toString()
    _g.liquidarCuotaVariableVencida(id_colocacion, cuotas, _fecha_corte).map { _l =>
        if (_l._6) {
            val result = new Liquidacion(uuid, _fecha, id_colocacion,  Some(_l._2), Some(_l._3), Some(_l._4), Some(_l._5), _l._6, id_agencia, Some(_l._1))
            result
        } else {
            val result = new Liquidacion(uuid, _fecha, id_colocacion, None, None, None, None, _l._6, id_agencia, None)
            result
        }
      }
  }

  def aplicarLiquidacionWompi(referencia: String) = {
    var _abonoCapital: BigDecimal = 0
    var _descuentoCapital: BigDecimal = 0
    var _causado: BigDecimal = 0
    var _corriente: BigDecimal = 0
    var _vencido: BigDecimal = 0
    var _anticipado: BigDecimal = 0
    var _devuelto: BigDecimal = 0
    var _otros: BigDecimal = 0
    var _seguro: BigDecimal = 0
    var _pago_x_cliente: BigDecimal = 0
    var _honorarios: BigDecimal = 0
    var _caja_banco: BigDecimal = 0
    var _codigoBanco: String = ""
    var _cuotaAnterior: Int = 0
    var _esCambio: Boolean = false
    var _valorFacturarCXC: BigDecimal = 0
    var _valorFacturarMES: BigDecimal = 0
    var _valorFacturarMORA : BigDecimal = 0
    var _valorFacturarANT: BigDecimal = 0
    var _valorFacturarDEV: BigDecimal = 0
    var _abonoSobrante: BigDecimal = 0
    var _valorRecibido: BigDecimal = 0

    db.withTransaction { implicit transaction =>
      _codigoBanco = SQL("""SELECT CODIGO FROM "gen$bancosconnal" WHERE ID_BANCO = 71""").as(SqlParser.str("CODIGO").single)
      val _transaccion = SQL("""SELECT * FROM TRAN_TRANSACCION WHERE referencia = {referencia}""").on('referencia -> referencia).as(Transaccion._set.single)
      val _liquidacion = SQL("""SELECT * FROM LIQUIDACION WHERE referencia = {referencia}""").on('referencia -> referencia).as(Liquidacion._set.single)
      val _liquidacion_detalle = SQL("""SELECT * FROM LIQUIDACION_DETALLE WHERE referencia = {referencia}""").on('referencia -> referencia).as(CuotasLiq._set *)
              /// Consecutivo Comprobante
      var _comprobante = SQL("""SELECT LLAVECSC FROM "con$tipocomprobante" WHERE ID = 1""").as(SqlParser.int("LLAVECSC").single)
      _comprobante = _comprobante + 1
      SQL("""UPDATE "con$tipocomprobante" SET LLAVECSC = {comprobante} WHERE ID = 1""").on('comprobante -> _comprobante).executeUpdate()

        _valorRecibido = _transaccion.tran_tran_amount_in_cents match {
          case Some(x) => x / 100
          case None => 0
        }

      _liquidacion_detalle.map { _r =>
        if (_r.cuotaNumero != _cuotaAnterior) {
          if (_esCambio) {
            _esCambio = true
          } else {
            // Grabar Extracto
            guardarExtracto( _liquidacion,
              _liquidacion_detalle,
              _comprobante,
              _r.cuotaNumero,
              _abonoCapital,
              _causado,
              _anticipado,
              _corriente,
              _vencido,
              _seguro,
              _pago_x_cliente,
              _honorarios,
              _otros,
              "Wompi",
              _liquidacion.fecha_interes.get,
              _liquidacion.fecha_capital.get,
              1)
            guardarTablaLiquidacion()
            // Grabar Tabla de Liquidación
          }
        }
        _cuotaAnterior = _r.cuotaNumero
        if (_r.esCapital) {
          _abonoCapital = _abonoCapital + _r.credito
          _descuentoCapital = _descuentoCapital + _r.debito
        }
        if (_r.esCausado) {
          _causado = _causado + _r.credito
          _valorFacturarCXC = _valorFacturarCXC + _r.credito
        }
        if (_r.esCorriente) {
          _corriente = _corriente + _r.credito
          _valorFacturarMES = _valorFacturarMES + _r.credito
        }
        if (_r.esVencido) {
          _vencido = _vencido + _r.credito
          _valorFacturarMORA = _valorFacturarMORA + _r.credito
        }
        if (_r.esAnticipado) {
          _anticipado = _anticipado + _r.credito
          _valorFacturarANT = _valorFacturarANT + _r.credito
        }
        if (_r.esDevuelto) {
          _valorFacturarDEV = _valorFacturarDEV + _r.debito
        }
        if (_r.esOtros) {
          _otros = _otros + _r.credito
        }
        if (_r.esCajaBanco) {
          _caja_banco = _caja_banco + _r.debito
        }
        // Insertar en Extracto Detallado
        SQL("""INSERT INTO "col$extractodet" (ID_AGENCIA, ID_CBTE_COLOCACION, ID_COLOCACION, FECHA_EXTRACTO, HORA_EXTRACTO, CODIGO_PUC, FECHA_INICIAL, FECHA_FINAL, DIAS_APLICADOS, TASA_LIQUIDACION, VALOR_DEBITO, VALOR_CREDITO)
        VALUES ({id_agencia}, {id_cbte_colocacion}, {id_colocacion}, {fecha_extracto}, {hora_extracto}, {codigo_puc}, {fecha_inicial}, {fecha_final}, {dias_aplicados}, {tasa_liquidacion}, {valor_debito}, {valor_credito})""")
        on(
          'id_agencia -> _liquidacion.id_agencia,
          'id_cbte_colocacion -> _comprobante,
          'id_colocacion -> _liquidacion.id_colocacion,
          'fecha_extracto -> _liquidacion.fecha,
          'hora_extracto -> new DateTime(),
          'codigo_puc -> _r.codigoPuc,
          'fecha_inicial -> _r.fecha_inicial,
          'fecha_final -> _r.fecha_final,
          'dias_aplicados -> _r.dias_aplicados,
          'tasa_liquidacion -> _r.tasa_liquidacion,
          'valor_debito -> _r.debito,
          'valor_credito -> _r.credito
        ).executeInsert()
      }
      guardarExtracto( _liquidacion,
              _liquidacion_detalle,
              _comprobante,
              _r.cuotaNumero,
              _abonoCapital,
              _causado,
              _anticipado,
              _corriente,
              _vencido,
              _seguro,
              _pago_x_cliente,
              _honorarios,
              _otros,
              "Wompi",
              _liquidacion.fecha_interes.get,
              _liquidacion.fecha_capital.get,
              1)
      guardarTablaLiquidacion()

      _abonoSobrante = _valorRecibido - _caja_banco
      // Actualizar Colocacion
      SQL("""UPDATE "col$colocacion" SET
        ABONOS_CAPITAL = ABONOS_CAPITAL + {capital},
        FECHA_CAPITAL = {fecha_capital},
        FECHA_INTERES = {fecha_interes},
        DIAS_PRORROGADOS = 0
        WHERE ID_AGENCIA = {id_agencia} AND ID_COLOCACION = {id_colocacion}""").on(
          'capital -> (_abonoCapital + _abonoSobrante),
          'fecha_capital -> _liquidacion.fecha_capital,
          'fecha_interes -> _liquidacion.fecha_interes,
          'id_agencia -> _liquidacion.id_agencia,
          'id_colocacion -> _liquidacion.id_colocacion
        ).executeUpdate()

      // Generar Comprobante
      SQL("""INSERT INTO "con$comprobante" (ID_COMPROBANTE, ID_AGENCIA, TIPO_COMPROBANTE, FECHADIA, DESCRIPCION, TOTAL_DEBITO, TOTAL_CREDITO, ESTADO, IMPRESO, ID_EMPLEADO)
        VALUES ({id_comprobante}, {id_agencia}, {tipo_comprobante}, {fechadia}, {descripcion}, {total_debito}, {total_credito}, {estado}, {impreso}, {id_empleado})""")
        .on(
          'id_agencia -> _liquidacion.id_agencia,
          'tipo_comprobante -> 1,
          'id_comprobante -> _comprobante,
          'fecha -> _liquidacion.fecha_liquidacion,
          'descripcion -> "Abono de Cartera via Wompi, Colocación No. " + _liquidacion.id_colocacion,
          'total_debito -> _caja_banco,
          'total_credito -> _caja_banco,
          'estado -> "O",
          'id_empleado -> "WOMPI"
        ).executeInsert()

      // Generar Comprobante Detalle
      _liquidacion_detalle.map { _r =>
        SQL("""INSERT INTO "con$auxiliar" (ID_COMPROBANTE, ID_AGENCIA, TIPO_COMPROBANTE, FECHA, CODIGO, DEBITO, CREDITO, ID_COLOCACION, ID_IDENTIFICACION, ID_PERSONA, ESTADOAUX, ID, ID_CLASE_OPERACION) VALUES (
          {id_comprobante}, {id_agencia}, {tipo_comprobante}, {fecha}, {codigo}, {debito}, {credito}, {id_colocacion}, {id_identificacion}, {id_persona}, {estadoaux}, {id}, {id_clase_operacion})""")
          .on(
            'id_comprobante -> _comprobante,
            'id_agencia -> _liquidacion.id_agencia,
            'tipo_comprobante -> 1,
            'fecha -> _liquidacion.fecha_liquidacion,
            'codigo -> _r.codigo_puc,
            'debito -> _r.debito,
            'credito -> _r.credito,
            'id_colocacion -> _liquidacion.id_colocacion,
            'id_identificacion -> _liquidacion.id_identificacion,
            'id_persona -> _liquidacion.id_persona,
            'estadoaux -> "O",
            'id -> _r.id,
            'id_clase_operacion -> _r.id_clase_operacion
          ).executeInsert()
        val _idAux = _gd.obtenerIdAuxiliar()
        // Actualizar Auxiliar Anexo
        SQL("""INSERT INTO "con$auxiliarext" (ID, DETALLE, ID_COMPROBANTE, TIPO_COMPROBANTE, ID_AGENCIA) VALUES (
          {id}, {detalle}, {id_comprobante}, {tipo_comprobante}, {id_agencia})""")
          .on(
            'id -> _idAux,
            'detalle -> "Abono de Cartera via Wompi, Colocación No. " + _liquidacion.id_colocacion,
            'id_comprobante -> _comprobante,
            'tipo_comprobante -> 1,
            'id_agencia -> _liquidacion.id_agencia
          ).executeInsert()
      }

      // Crear Factura si es necesario

      if ((_valorFacturarCXC > 0) || (_valorFacturarMES > 0) || (_valorFacturarMORA > 0) || ((_valorFacturarANT - _valorFacturarDEV) > 0) ) {
        val _vFacturaConsecutivo = _gd.obtenerConsecutivoFactura()
        val _vFechaComprobante = new DateTime()
        SQL("""INSERT INTO FACTURA (FACT_NUMERO, FACT_FECHA, FACT_DESCRIPCION, TIPO_COMPROBANTE, ID_COMPROBANTE, FECHA_COMPROBANTE, ID_IDENTIFICACION, ID_PERSONA, ID_EMPLEADO, FACT_ESTADO) VALUES (
          {fact_numero}, {fact_fecha}, {fact_descripcion}, {tipo_comprobante}, {id_comprobante}, {fecha_comprobante}, {id_identificacion}, {id_persona}, {id_empleado}, {fact_estado})""")
          .on(
            'fact_numero -> _vFacturaConsecutivo,
            'fact_fecha -> _vFechaComprobante,
            'fact_descripcion -> "Recaudo Por Cartera de Crédito, Colocación No. " + _liquidacion.id_colocacion,
            'tipo_comprobante -> 1,
            'id_comprobante -> _comprobante,
            'fecha_comprobante -> _liquidacion.fecha_liquidacion,
            'id_identificacion -> _liquidacion.id_identificacion,
            'id_persona -> _liquidacion.id_persona,
            'id_empleado -> "WOMPI",
            'fact_estado -> 1
          ).executeInsert()
        val _query = """INSERT INTO FACTURA_ITEM (FACT_NUMERO, FAIT_DETALLE, FAIT_CANTIDAD, FAIT_VALORUNITARIO, FAIT_TASAIVA, FAIT_VALORIVA, FAIT_TOTAL) VALUES (
          {fact_numero}, {fait_detalle}, {fait_cantidad}, {fait_valorunitario}, {fait_tasaiva}, {fait_valoriva}, {fait_total})"""
        if (_valorFacturarANT > 0) {
          var _valor = _valorFacturarANT - _valorFacturarDEV
          if (_valor < 0) {
            _valor = 0
          }
          if (valor > 0) {
            SQL(_query).on(
              'fact_numero -> _vFacturaConsecutivo,
              'fait_detalle -> "Abono de Interés Anticipado",
              'fait_cantidad -> 1,
              'fait_valorunitario -> _valor ,
              'fait_tasaiva -> 0,
              'fait_valoriva -> 0,
              'fait_total -> _valor
            ).executeInsert()
          }
        }
        if (_valorFacturarCXC > 0) {
            SQL(_query).on(
              'fact_numero -> _vFacturaConsecutivo,
              'fait_detalle -> "Abono de Interés Causado",
              'fait_cantidad -> 1,
              'fait_valorunitario -> _valorFacturarCXC ,
              'fait_tasaiva -> 0,
              'fait_valoriva -> 0,
              'fait_total -> _valorFacturarCXC
            ).executeInsert()
        }
        if (_valorFacturarMES > 0) {
            SQL(_query).on(
              'fact_numero -> _vFacturaConsecutivo,
              'fait_detalle -> "Abono de Interés Servicio",
              'fait_cantidad -> 1,
              'fait_valorunitario -> _valorFacturarMES ,
              'fait_tasaiva -> 0,
              'fait_valoriva -> 0,
              'fait_total -> _valorFacturarMES
            ).executeInsert()
        }
        if (_valorFacturarMORA > 0) {
            SQL(_query).on(
              'fact_numero -> _vFacturaConsecutivo,
              'fait_detalle -> "Abono de Interés Moratorio",
              'fait_cantidad -> 1,
              'fait_valorunitario -> _valorFacturarMORA ,
              'fait_tasaiva -> 0,
              'fait_valoriva -> 0,
              'fait_total -> _valorFacturarMORA
            ).executeInsert()
        }
        // Insertar Nota a la Factura de Pago por pasarela electrónica
        SQL("""INSERT INTO FACTURA_LSNOTA (FACT_NUMERO, FALN_CONSECUTIVO, FALN_DESCRIPCION) VALUES (
          {fact_numero}, {faln_consecutivo}, {faln_descripcion})""")
          .on(
            'fact_numero -> _vFacturaConsecutivo,
            'faln_consecutivo -> 1,
            'faln_descripcion -> "Pago por pasarela electrónica"
          ).executeInsert()
      }


      SQL("""UPDATE LIQUIDACION SET aplicada = 1 WHERE referencia = {referencia}""").on('referencia -> referencia).executeUpdate()
    }
  }

  def guardarExtracto(
        _liquidacion: Liquidacion,
        _liquidacion_detalle: List[CuotasLiq],
        _comprobante: Int,
        _cuotaNumero: Int,
        _capital: BigDecimal,
        _causado: BigDecimal,
        _anticipado: BigDecimal,
        _corriente: BigDecimal,
        _vencido: BigDecimal,
        _seguro: BigDecimal,
        _pago_x_cliente: BigDecimal,
        _honorarios: BigDecimal,
        _otros: BigDecimal,
        _id_empleado: String,
        _interes_pago_hasta: DateTime,
        _capital_pago_hasta: DateTime,
        _tipo_abono: Int)(implicit connection: Connection) = {
      val _registro_interes = _liquidacion_detalle.filter(x => (x.cuotaNumero == _cuotaNumero && (x.esCausado == true || x.esCorriente == true))).head
      var _saldo_anterior = 0
      var _tasa_interes = _registro_interes != null match {
        case true => _registro_interes.tasa
        case false => 0
      }

      SQL("""INSERT INTO "col$extracto" (ID_AGENCIA, ID_CBTE_COLOCACION, ID_COLOCACION, FECHA_EXTRACTO, HORA_EXTRACTO, CUOTA_EXTRACTO, TIPO_OPERACION, SALDO_ANTERIOR_EXTRACTO, ABONO_CAPITAL, ABONO_CXC, ABONO_ANTICIPADO, ABONO_SERVICIOS, ABONO_MORA, ABONO_SEGURO, ABONO_PAGXCLI, ABONO_HONORARIO, ABONO_OTROS, TASA_INTERES_LIQUIDACION, ID_EMPLEADO, INTERES_PAGO_HASTA, CAPITAL_PAGO_HASTA, TIPO_ABONO)
      VALUES ({id_agencia}, {id_cbte_colocacion}, {id_colocacion}, {fecha_extracto}, {hora_extracto}, {cuota_extracto}, {tipo_operacion}, {saldo_anterior_extracto}, {abono_capital}, {abono_cxc}, {abono_anticipado}, {abono_servicios}, {abono_mora}, {abono_seguro}, {abono_pagxcli}, {abono_honorario}, {abono_otros}, {tasa_interes_liquidacion}, {id_empleado}, {interes_pago_hasta}, {capital_pago_hasta}, {tipo_abono})""")
      .on(
        'id_agencia -> _liquidacion.id_agencia,
        'id_cbte_colocacion -> _comprobante,
        'id_colocacion -> _liquidacion.id_colocacion,
        'fecha_extracto -> _liquidacion.fecha,
        'hora_extracto -> new DateTime(),
        'cuota_extracto -> _cuotaNumero,
        'tipo_operacion -> 1,
        'saldo_anterior_extracto -> _saldo_anterior,
        'abono_capital -> _capital,
        'abono_cxc -> _causado,
        'abono_anticipado -> _anticipado,
        'abono_servicios -> _corriente,
        'abono_mora -> _vencido,
        'abono_seguro -> _seguro,
        'abono_pagxcli -> _pago_x_cliente,
        'abono_honorario -> _honorarios,
        'abono_otros -> _otros,
        'tasa_interes_liquidacion -> _tasa_interes,
        'id_empleado -> _id_empleado,
        'interes_pago_hasta -> _interes_pago_hasta,
        'capital_pago_hasta -> _capital_pago_hasta,
        'tipo_abono -> _tipo_abono
      ).executeInsert()
  }

  def guardarTablaLiquidacion(_id_colocacion: String, _cuota_numero: Int)(implicit connection: Connection){

  }

}