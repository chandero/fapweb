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

class TipoParentescoRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  /**
    * obtenerListaTipoParentesco
    * @return Future[Iterable[TipoParentesco]]
    */
  def obtenerLista(): Future[Iterable[Tipo]] =
    Future[Iterable[Tipo]] {
      db.withConnection { implicit connection =>
        val _set = {
            get[Option[Int]]("id_parentesco") ~
              get[Option[String]]("descripcion_parentesco") map {
              case id_parentesco ~ descripcion_parentesco =>
                Tipo(id_parentesco, descripcion_parentesco)
            }
          }
        SQL("""SELECT * FROM \"gen$tiposparentesco\"""").as(
          _set *
        )
      }
    }
}
