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

class AsesorRepository @Inject()(dbapi: DBApi)(
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
            get[Option[Int]]("ases_id") ~
              get[Option[String]]("nombre") map {
              case id ~ descripcion =>
                Tipo(id, descripcion)
            }
          }
        SQL("""SELECT a.ASES_ID, e.NOMBRE || ' ' || e.PRIMER_APELLIDO || ' ' || e.SEGUNDO_APELLIDO AS NOMBRE
        FROM ASESOR a
        LEFT JOIN "gen$empleado" e ON e.ID_EMPLEADO = a.ID_EMPLEADO
        WHERE e.TIPO IN (1,2,3)
        ORDER BY a.ASES_ID""").as(
          _set *
        )
      }
    }
}
