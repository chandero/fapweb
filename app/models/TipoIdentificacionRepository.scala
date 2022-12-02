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

case class TipoIdentificacion(
    id_identificacion: Option[Int],
    descripcion_identificacion: Option[String]
)

object TipoIdentificacion {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val tiWrites = new Writes[TipoIdentificacion] {
    def writes(ti: TipoIdentificacion) = Json.obj(
      "id_identificacion" -> ti.id_identificacion,
      "descripcion_identificacion" -> ti.descripcion_identificacion
    )
  }

  implicit val tiReads: Reads[TipoIdentificacion] = (
    (__ \ "id_identificacion").readNullable[Int] and
      (__ \ "descripcion_identificacion").readNullable[String]
  )(TipoIdentificacion.apply _)

  val _set = {
    get[Option[Int]]("id_identificacion") ~
      get[Option[String]]("descripcion_identificacion") map {
      case id_identificacion ~ descripcion_identificacion =>
        TipoIdentificacion(id_identificacion, descripcion_identificacion)
    }
  }

}

class TipoIdentificacionRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  /**
    * obtenerListaTipoIdentificacion
    * @return Future[Iterable[TipoIdentificacion]]
    */
  def obtenerListaTipoIdentificacion(): Future[Iterable[TipoIdentificacion]] =
    Future[Iterable[TipoIdentificacion]] {
      db.withConnection { implicit connection =>
        SQL("""SELECT * FROM \"gen$tiposidentificacion\"""").as(
          TipoIdentificacion._set *
        )
      }
    }
}
