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

case class Tipo(
    id: Option[Int],
    descripcion: Option[String]
)

object Tipo {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val tWrites = new Writes[Tipo] {
    def writes(t: Tipo) = Json.obj(
      "id" -> t.id,
      "descripcion" -> t.descripcion
    )
  }

  implicit val tReads: Reads[Tipo] = (
    (__ \ "id").readNullable[Int] and
      (__ \ "descripcion").readNullable[String]
  )(Tipo.apply _)
}
