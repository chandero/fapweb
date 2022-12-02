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

class TipoReferenciaRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  /**
    * obtenerListaTipoReferencia
    * @return Future[Iterable[TipoReferencia]]
    */
  def obtenerLista(): Future[Iterable[Tipo]] =
    Future[Iterable[Tipo]] {
      db.withConnection { implicit connection =>
        val _set = {
            get[Option[Int]]("id_referencia") ~
              get[Option[String]]("descripcion_referencia") map {
              case id_referencia ~ descripcion_referencia =>
                Tipo(id_referencia, descripcion_referencia)
            }
          }
        SQL("""SELECT * FROM \"gen$tiposreferencia\"""").as(
          _set *
        )
      }
    }
}
