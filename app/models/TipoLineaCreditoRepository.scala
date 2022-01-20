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

case class TipoLineaCredito (linea_id: Int, linea_descripcion: String, linea_tasa_efectiva: Double, linea_comision: Double)
object TipoLineaCredito {
  var _set = {
    get[Int]("linea_id") ~
    get[String]("linea_descripcion") ~
    get[Double]("linea_tasa_efectiva") ~ 
    get[Double]("linea_comision")  map {
      case linea_id ~ linea_descripcion ~ linea_tasa_efectiva ~ linea_comision => TipoLineaCredito(linea_id, linea_descripcion, linea_tasa_efectiva, linea_comision)
    }
  }
}


class TipoLineaCreditoRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  /**
    * obtenerLista
    * @return Future[Iterable[Tipo]]
    */
  def obtenerLista(): Future[Iterable[Tipo]] =
    Future[Iterable[Tipo]] {
      db.withConnection { implicit connection =>
        val _set = {
            get[Option[Int]]("id_linea") ~
              get[Option[String]]("descripcion_linea") map {
              case id ~ descripcion =>
                Tipo(id, descripcion)
            }
          }
        SQL("""SELECT * FROM \"col$lineas\"""").as(
          _set *
        )
      }
    }

  /**
    * obtenerLista
    * @return Future[Iterable[Tipo]]
    */
  def obtenerListaApiRest(): Future[Iterable[TipoLineaCredito]] =
    Future[Iterable[TipoLineaCredito]] {
      db.withConnection { implicit connection =>
        val _lineasResult = SQL("""SELECT l1.id_linea as linea_id, l1.descripcion_linea as linea_descripcion, l1.tasa as linea_tasa_efectiva, d1.valor_descuento as linea_comision FROM \"col$lineas\" l1
                                   LEFT JOIN \"col$descuentos\" d1 ON d1.id_descuento = 4
                                   WHERE ESTADO = 1""")
        .as(TipoLineaCredito._set *)
        _lineasResult
      }
    }    
    
}
