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

class TipoViviendaRepository @Inject()(dbapi: DBApi)(
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
            get[Option[Int]]("id_tipovivienda") ~
              get[Option[String]]("descripcion_tipo") map {
              case id ~ descripcion =>
                Tipo(id, descripcion)
            }
          }
        SQL("""SELECT * FROM \"gen$tipovivienda\"""").as(
          _set *
        )
      }
    }
}
