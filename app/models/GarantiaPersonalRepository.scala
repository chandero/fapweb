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

case class GarantiaPersonalDto(
    primer_apellido: Option[String],
    segundo_apellido: Option[String],
    nombre: Option[String],
    id_colocacion: Option[String],
    id_identificacion: Option[Int],
    id_persona: Option[String],
    email: Option[String],
    telefono1: Option[String]
)

case class GarantiaPersonal(
    id_agencia: Option[Long],
    id_colocacion: Option[String],
    id_identificacion: Option[Int],
    id_persona: Option[String],
    email: Option[String],
    telefono1: Option[String]
)

object GarantiaPersonalDto {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wWrites = new Writes[GarantiaPersonalDto] {
    def writes(e: GarantiaPersonalDto) = Json.obj(
      "primer_apellido" -> e.primer_apellido,
      "segundo_apellido" -> e.segundo_apellido,
      "nombre" -> e.nombre,
      "id_colocacion" -> e.id_colocacion,
      "id_identificacion" -> e.id_identificacion,
      "id_persona" -> e.id_persona,
      "email" -> e.email,
      "telefono1" -> e.telefono1
    )
  }

  implicit val rReads: Reads[GarantiaPersonalDto] = (
    (__ \ "primer_apellido").readNullable[String] and
      (__ \ "segundo_apellido").readNullable[String] and
      (__ \ "nombre").readNullable[String] and
      (__ \ "id_colocacion").readNullable[String] and
      (__ \ "id_identificacion").readNullable[Int] and
      (__ \ "id_persona").readNullable[String] and
      (__ \ "email").readNullable[String] and
      (__ \ "telefono1").readNullable[String]
  )(GarantiaPersonalDto.apply _)

  val _set = {
    get[Option[String]]("primer_apellido") ~
      get[Option[String]]("segundo_apellido") ~
      get[Option[String]]("nombre") ~
      get[Option[String]]("id_colocacion") ~
      get[Option[Int]]("id_identificacion") ~
      get[Option[String]]("id_persona") ~
      get[Option[String]]("email") ~
      get[Option[String]]("telefono1") map {
      case primer_apellido ~
            segundo_apellido ~
            nombre ~
            id_colocacion ~
            id_identificacion ~
            id_persona ~
            email ~
            telefono1 =>
        GarantiaPersonalDto(
          primer_apellido,
          segundo_apellido,
          nombre,
          id_colocacion,
          id_identificacion,
          id_persona,
          email,
          telefono1
        )
    }
  }
}

object GarantiaPersonal {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wWrites = new Writes[GarantiaPersonal] {
    def writes(e: GarantiaPersonal) = Json.obj(
      "id_agencia" -> e.id_agencia,
      "id_colocacion" -> e.id_colocacion,
      "id_identificacion" -> e.id_identificacion,
      "id_persona" -> e.id_persona,
      "email" -> e.email,
      "telefono1" -> e.telefono1
    )
  }

  implicit val rReads: Reads[GarantiaPersonal] = (
    (__ \ "id_agencia").readNullable[Long] and
      (__ \ "id_colocacion").readNullable[String] and
      (__ \ "id_identificacion").readNullable[Int] and
      (__ \ "id_persona").readNullable[String] and
      (__ \ "email").readNullable[String] and
      (__ \ "telefono1").readNullable[String]
  )(GarantiaPersonal.apply _)

  val _set = {
    get[Option[Long]]("id_agencia") ~
      get[Option[String]]("id_colocacion") ~
      get[Option[Int]]("id_identificacion") ~
      get[Option[String]]("id_persona") ~
      get[Option[String]]("email") ~
      get[Option[String]]("telefono1") map {
      case id_agencia ~
            id_colocacion ~
            id_identificacion ~
            id_persona ~
            email ~
            telefono1 =>
        GarantiaPersonal(
          id_agencia,
          id_colocacion,
          id_identificacion,
          id_persona,
          email,
          telefono1
        )
    }
  }
}

class GarantiaPersonalRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  def obtener(id_colocacion: String): Future[Iterable[GarantiaPersonalDto]] =
    Future[Iterable[GarantiaPersonalDto]] {
      db.withConnection { implicit connection =>
        SQL("""SELECT
                    p.PRIMER_APELLIDO,
                    p.SEGUNDO_APELLIDO,
                    p.NOMBRE,
                    g.ID_COLOCACION,
                    g.ID_IDENTIFICACION,
                    g.ID_PERSONA,
                    p.EMAIL,
                    gd.TELEFONO1
                   FROM "col$colgarantias" g
                   INNER JOIN "gen$persona" p on
                    (p.ID_PERSONA = g.ID_PERSONA) and (p.ID_IDENTIFICACION = g.ID_IDENTIFICACION)
                   LEFT JOIN "gen$direccion" gd ON (gd.ID_PERSONA = g.ID_PERSONA) and (gd.ID_IDENTIFICACION = g.ID_IDENTIFICACION) AND gd.ID_DIRECCION = 1
                   WHERE g.ID_COLOCACION = {id_colocacion}""")
          .on(
            'id_colocacion -> id_colocacion
          )
          .as(GarantiaPersonalDto._set *)
      }
    }

}
