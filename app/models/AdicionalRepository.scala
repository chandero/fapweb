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
import scala.collection.mutable.ListBuffer

import org.joda.time.DateTime
import org.joda.time.LocalDate
import scala.collection.mutable

case class Adicional(
    id_descuento: Option[Long],
    codigo: Option[String],
    descripcion_descuento: Option[String],
    valor_descuento: Option[BigDecimal],
    porcentaje_colocacion: Option[Double],
    porcentaje_cuota: Option[Double],
    en_desembolso: Option[Int],
    en_cuota: Option[Int],
    porcentaje_saldo: Option[Double],
    es_distribuido: Option[Int],
    es_activo: Option[Int],
    amortizacion: Option[Int]
)
case class SolicitudAdicional(
    id_solicitud: Option[String],
    id_descuento: Option[Long],
    id_agencia: Option[Int]
)

object SolicitudAdicional {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val aWrites = new Writes[SolicitudAdicional] {
     def writes(a: SolicitudAdicional) = Json.obj(
         "id_solicitud" -> a.id_solicitud,
         "id_descuento" -> a.id_descuento,
         "id_agencia" -> a.id_agencia
     )
    }

    implicit val aRead: Reads[SolicitudAdicional] = (
        (__ \ "id_solicitud").readNullable[String] and
        (__ \ "id_descuento").readNullable[Long] and
        (__ \ "id_agencia").readNullable[Int]
    )(SolicitudAdicional.apply _)

    val _set = {
        get[Option[String]]("id_solicitud") ~
        get[Option[Long]]("id_descuento") ~
        get[Option[Int]]("id_agencia") map {
            case 
               id_solicitud ~
               id_descuento ~
               id_agencia => SolicitudAdicional(
                   id_solicitud,
                   id_descuento,
                   id_agencia
               )
        }
    }
}

object Adicional {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val aWrites = new Writes[Adicional] {
     def writes(a: Adicional) = Json.obj(
        "id_descuento" -> a.id_descuento,
        "codigo" -> a.codigo, 
        "descripcion_descuento" -> a.descripcion_descuento, 
        "valor_descuento" -> a.valor_descuento,
        "porcentaje_colocacion" -> a.porcentaje_colocacion,
        "porcentaje_cuota" -> a.porcentaje_cuota,
        "en_desembolso" -> a.en_desembolso,
        "en_cuota" -> a.en_cuota,
        "porcentaje_saldo" -> a.porcentaje_saldo,
        "es_distribuido" -> a.es_distribuido,
        "es_activo" -> a.es_activo,
        "amortizacion" -> a.amortizacion
     )
    }
    implicit val aReads: Reads[Adicional] = (
        (__ \ "id_descuento").readNullable[Long] and
        (__ \ "codigo").readNullable[String] and
        (__ \ "descripcion_descuento").readNullable[String] and
        (__ \ "valor_descuento").readNullable[BigDecimal] and
        (__ \ "porcentaje_colocacion").readNullable[Double] and
        (__ \ "porcentaje_cuota").readNullable[Double] and
        (__ \ "en_desembolso").readNullable[Int] and
        (__ \ "en_cuota").readNullable[Int] and
        (__ \ "porcentaje_saldo").readNullable[Double] and
        (__ \ "es_distribuido").readNullable[Int] and
        (__ \ "es_activo").readNullable[Int] and
        (__ \ "amortizacion").readNullable[Int]
    )(Adicional.apply _)

    val _set = {
        get[Option[Long]]("id_descuento") ~
        get[Option[String]]("codigo") ~
        get[Option[String]]("descripcion_descuento") ~
        get[Option[BigDecimal]]("valor_descuento") ~
        get[Option[Double]]("porcentaje_colocacion") ~
        get[Option[Double]]("porcentaje_cuota") ~
        get[Option[Int]]("en_desembolso") ~
        get[Option[Int]]("en_cuota") ~
        get[Option[Double]]("porcentaje_saldo") ~
        get[Option[Int]]("es_distribuido") ~
        get[Option[Int]]("es_activo") ~
        get[Option[Int]]("amortizacion") map {
            case 
                id_descuento ~
                codigo ~
                descripcion_descuento ~
                valor_descuento ~
                porcentaje_colocacion ~
                porcentaje_cuota ~
                en_desembolso ~
                en_cuota ~
                porcentaje_saldo ~
                es_distribuido ~
                es_activo ~
                amortizacion => Adicional(
                    id_descuento,
                    codigo,
                    descripcion_descuento,
                    valor_descuento,
                    porcentaje_colocacion,
                    porcentaje_cuota,
                    en_desembolso,
                    en_cuota,
                    porcentaje_saldo,
                    es_distribuido,
                    es_activo,
                    amortizacion
                )
                
        }
    }
}

class AdicionalRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext
) {

    private val db = dbapi.database("default")

    def activos(): Future[Iterable[Adicional]] = Future[Iterable[Adicional]] {
        val result = db.withConnection { implicit connection => 
            SQL("""SELECT * FROM \"col$descuentos\" WHERE ES_ACTIVO = 1""").as(Adicional._set *)
        }
        result
    }

    def buscar(id_solicitud: String): Future[Iterable[SolicitudAdicional]] = {
        val result = db.withConnection { implicit connection => 
            SQL("""SELECT * FROM \"col$solicituddescuento\" WHERE ID_SOLICITUD = {id_solicitud}""").
            on(
                'id_solicitud -> id_solicitud
            ).as(SolicitudAdicional._set *)
        }
        Future.successful(result)
    }
}