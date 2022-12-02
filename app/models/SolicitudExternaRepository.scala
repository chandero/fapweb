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

case class SolicitudExterna(
    id: Option[Long],
    fecharegistro: Option[DateTime],
    id_identificacion: Option[Int],
    documento: Option[String],
    fechaexpedicion: Option[DateTime],
    ingreso: Option[BigDecimal],
    gasto: Option[BigDecimal],
    monto: Option[BigDecimal],
    plazo: Option[Int],
    telefono: Option[String],
    email: Option[String],
    estado: Option[Int]
)

case class Respuesta(id: Int, cliente: String, mensaje: String)

object Respuesta {
  implicit val seWrites = new Writes[Respuesta] {
    def writes(se: Respuesta) = Json.obj(
        "id" -> se.id,
        "cliente" -> se.cliente,
        "mensaje" -> se.mensaje
    )
  }
}

object SolicitudExterna {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val seWrites = new Writes[SolicitudExterna] {
    def writes(se: SolicitudExterna) = Json.obj(
        "id" -> se.id,
        "fecharegistro" -> se.fecharegistro,
        "id_identificacion" -> se.id_identificacion,
        "documento" -> se.documento,
        "fechaexpedicion" -> se.fechaexpedicion,
        "ingreso" -> se.ingreso,
        "gasto" -> se.gasto,
        "monto" -> se.monto,
        "plazo" -> se.plazo,
        "telefono" -> se.telefono,
        "email" -> se.email,
        "estado" -> se.estado
    )
  }

  implicit val seReads: Reads[SolicitudExterna] = (
    (__ \ "id").readNullable[Long] and
    (__ \ "fecharegistro").readNullable[DateTime] and
    (__ \ "id_identificacion").readNullable[Int] and
    (__ \ "documento").readNullable[String] and
    (__ \ "fechaexpedicion").readNullable[DateTime] and
    (__ \ "ingreso").readNullable[BigDecimal] and
    (__ \ "gasto").readNullable[BigDecimal] and
    (__ \ "monto").readNullable[BigDecimal] and
    (__ \ "plazo").readNullable[Int] and
    (__ \ "telefono").readNullable[String] and
    (__ \ "email").readNullable[String] and
    (__ \ "estado").readNullable[Int]
  )(SolicitudExterna.apply _)

  val _set = {
    get[Option[Long]]("id") ~
    get[Option[DateTime]]("fecharegistro") ~
    get[Option[Int]]("id_identificacion") ~
    get[Option[String]]("documento") ~
    get[Option[DateTime]]("fechaexpedicion") ~
    get[Option[BigDecimal]]("ingreso") ~ 
    get[Option[BigDecimal]]("gasto") ~
    get[Option[BigDecimal]]("monto") ~ 
    get[Option[Int]]("plazo") ~
    get[Option[String]]("telefono") ~
    get[Option[String]]("email") ~
    get[Option[Int]]("estado") map {
      case id ~ fecharegistro ~ id_identificacion ~ documento ~ fechaexpedicion ~ ingreso ~ gasto ~ monto ~ plazo ~ telefono ~ email ~ estado=>
      SolicitudExterna(id, fecharegistro, id_identificacion, documento, fechaexpedicion, ingreso, gasto, monto, plazo, telefono, email, estado)
    }
  }

}

class SolicitudExternaRepository @Inject()(dbapi: DBApi, pService: PersonaRepository, iService: InformeAsociadoBuenPagoRepository)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  /**
    * obtenerListaTipoIdentificacion
    * @return Future[Iterable[TipoIdentificacion]]
    */
  def lista(): Future[Iterable[SolicitudExterna]] =
    Future[Iterable[SolicitudExterna]] {
      db.withConnection { implicit connection =>
        SQL("""SELECT * FROM COL$SOLICITUDEXTERNA ORDER BY FECHAREGISTRO DESC, ESTADO ASC""").as(
            SolicitudExterna._set *
        )
      }
  }

  def crear(s: SolicitudExterna): Future[Respuesta] = {

    db.withConnection { implicit connection => 
        // consultar a la persona en nuestra base de datos
        pService.obtener(s.id_identificacion.get, s.documento.get).map { p =>
          println("Persona: "+ p)
          var ab = iService.validarPersona(s.id_identificacion.get, s.documento.get)
          println("AsociadoBuenPago: " + ab)
          var r: Respuesta = new Respuesta(0, "", "")
          var nombre = ""
          ab.nombre match {
            case Some(n) => nombre = nombre + n
            case None => None
          }
          ab.primer_apellido match {
            case Some(n) => nombre = nombre + " " + n
            case None => None
          }
          ab.segundo_apellido match {
            case Some(n) => nombre = nombre + " " + n
            case None => None
          }

          ab.dias match {
            case Some(dias) => 
                              if (dias <= 30) {
                                  r = new Respuesta(1, nombre, "Continuar")
                              } else {
                                  r = new Respuesta(5, nombre, "No es Posible")
                              }
            case None => r =new Respuesta(9, "Cliente", "No Encontramos Información")
          }
          r
        }
    }
  }
}
