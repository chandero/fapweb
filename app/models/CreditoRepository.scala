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

class CreditoRepository @Inject()(dbapi: DBApi, _g: GlobalesCol)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  def liquidar(id_colocacion: String, cuotas: Int, fecha_corte: Long, es_web: Boolean): Future[Liquidacion] = {
    var dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    var df = DateTimeFormat.forPattern("yyyy-MM-dd");
    var id_tipo_cuota = db.withConnection { implicit connection =>
      SQL("""SELECT c.ID_TIPO_CUOTA FROM "col$colocacion" c WHERE c.ID_COLOCACION = {id_colocacion}""").
      on(
        'id_colocacion -> id_colocacion
      ).as(SqlParser.scalar[Long].single)
    }
    val liquidacion = id_tipo_cuota match {
      case 1 => liquidacionCuotaFija(id_colocacion, cuotas, fecha_corte)
      case 2 => liquidacionCuotaVariableAnticipada(id_colocacion, cuotas, fecha_corte)
      case 3 => liquidacionCuotaVariableVencida(id_colocacion, cuotas, fecha_corte)
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

  def liquidacionCuotaFija(id_colocacion: String, cuotas: Int, fecha_corte: Long): Future[Liquidacion] = {
    val _fecha_corte = new DateTime(fecha_corte)
    var uuid: String = randomUUID().toString()
    _g.liquidarCuotaFija(id_colocacion, cuotas, _fecha_corte).map { _l =>
        if (_l._6) {
            val result = new Liquidacion(Some(_l._1), Some(_l._2), Some(_l._3), Some(_l._4), Some(_l._5), _l._6, uuid)
            result
        } else {
            val result = new Liquidacion(None, None, None, None, None, _l._6, uuid)
            result
        }
      }
  }

  def liquidacionCuotaVariableAnticipada(id_colocacion: String, cuotas: Int, fecha_corte: Long): Future[Liquidacion] = {
    val _fecha_corte = new DateTime(fecha_corte)
    var uuid: String = randomUUID().toString()
    _g.liquidarCuotaVariableAnticipada(id_colocacion, cuotas, _fecha_corte).map { _l =>
        if (_l._6) {
            val result = new Liquidacion(Some(_l._1), Some(_l._2), Some(_l._3), Some(_l._4), Some(_l._5), _l._6, uuid)
            result
        } else {
            val result = new Liquidacion(None, None, None, None, None, _l._6, uuid)
            result
        }
      }
  }

  def liquidacionCuotaVariableVencida(id_colocacion: String, cuotas: Int, fecha_corte: Long): Future[Liquidacion] = {
    val _fecha_corte = new DateTime(fecha_corte)
    var uuid: String = randomUUID().toString()
    _g.liquidarCuotaVariableVencida(id_colocacion, cuotas, _fecha_corte).map { _l =>
        if (_l._6) {
            val result = new Liquidacion(Some(_l._1), Some(_l._2), Some(_l._3), Some(_l._4), Some(_l._5), _l._6, uuid)
            result
        } else {
            val result = new Liquidacion(None, None, None, None, None, _l._6, uuid)
            result
        }
      }
  }    

}