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

class TipoPersonaRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  /**
    * obtenerListaTipoIdentificacion
    * @return Future[Iterable[TipoIdentificacion]]
    */
  def obtenerLista(): Future[Iterable[Tipo]] =
    Future[Iterable[Tipo]] {
      db.withConnection { implicit connection =>
        val _set = {
            get[Option[Int]]("id_tipo_persona") ~
              get[Option[String]]("descripcion_persona") map {
              case id_persona ~ descripcion_persona =>
                Tipo(id_persona, descripcion_persona)
            }
          }
        SQL("""SELECT * FROM \"gen$tipospersona\"""").as(
          _set *
        )
      }
    }
}
