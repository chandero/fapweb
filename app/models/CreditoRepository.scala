package models

import javax.inject.Inject
import java.util.Calendar

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
import scala.collection.mutable

import scala.collection.mutable.ListBuffer
import scala.math.BigDecimal

import utilities._
import java.time.DateTimeException

class CreditoRepository @Inject()(dbapi: DBApi, _g: GlobalesCol)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  def liquidar(id_colocacion: String, cuotas: Int, fecha_corte: Long): Future[Liquidacion] = {
    var id_tipo_cuota = db.withConnection { implicit connection =>
      SQL("""SELECT c.ID_TIPO_CUOTA FROM "col$colocacion" c WHERE c.ID_COLOCACION = {id_colocacion}""").
      on(
        'id_colocacion -> id_colocacion
      ).as(SqlParser.scalar[Long].single)
    }
    id_tipo_cuota match {
      case 1 => liquidacionCuotaFija(id_colocacion, cuotas, fecha_corte)
      case 2 => liquidacionCuotaVariableAnticipada(id_colocacion, cuotas, fecha_corte)
      case 3 => liquidacionCuotaVariableVencida(id_colocacion, cuotas, fecha_corte)
    }
  } 

  def liquidacionCuotaFija(id_colocacion: String, cuotas: Int, fecha_corte: Long): Future[Liquidacion] = {
    val _fecha_corte = new DateTime(fecha_corte)
    _g.liquidarCuotaFija(id_colocacion, cuotas, _fecha_corte).map { _l =>
        if (_l._6) {
            val result = new Liquidacion(Some(_l._1), Some(_l._2), Some(_l._3), Some(_l._4), Some(_l._5), _l._6)
            result
        } else {
            val result = new Liquidacion(None, None, None, None, None, _l._6)
            result
        }
      }
  }

  def liquidacionCuotaVariableAnticipada(id_colocacion: String, cuotas: Int, fecha_corte: Long): Future[Liquidacion] = {
    val _fecha_corte = new DateTime(fecha_corte)
    _g.liquidarCuotaVariableAnticipada(id_colocacion, cuotas, _fecha_corte).map { _l =>
        if (_l._6) {
            val result = new Liquidacion(Some(_l._1), Some(_l._2), Some(_l._3), Some(_l._4), Some(_l._5), _l._6)
            result
        } else {
            val result = new Liquidacion(None, None, None, None, None, _l._6)
            result
        }
      }
  }

  def liquidacionCuotaVariableVencida(id_colocacion: String, cuotas: Int, fecha_corte: Long): Future[Liquidacion] = {
    val _fecha_corte = new DateTime(fecha_corte)
    _g.liquidarCuotaVariableVencida(id_colocacion, cuotas, _fecha_corte).map { _l =>
        if (_l._6) {
            val result = new Liquidacion(Some(_l._1), Some(_l._2), Some(_l._3), Some(_l._4), Some(_l._5), _l._6)
            result
        } else {
            val result = new Liquidacion(None, None, None, None, None, _l._6)
            result
        }
      }
  }    

}