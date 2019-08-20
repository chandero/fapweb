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

case class InformacionCredito(entidad: Option[String],
                              valor_inicial: Option[BigDecimal],
                              saldo: Option[BigDecimal],
                              cuota_mensual: Option[BigDecimal],
                              vencimiento: Option[DateTime],
                              id_persona: Option[String],
                              id_identificacion: Option[Int],
                              es_descuento: Option[Int],
                              id_solicitud: Option[String],
                              id_colocacion: Option[String],
                              fecha_capital: Option[DateTime],
                              fecha_interes: Option[DateTime],
                              estado: Option[Int],
                              consecutivo: Option[Int])

object InformacionCredito {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wWrites = new Writes[InformacionCredito] {
    def writes(e: InformacionCredito) = Json.obj(
      "entidad" -> e.entidad,
      "valor_inicial" -> e.valor_inicial,
      "saldo" -> e.saldo,
      "cuota_mensual" -> e.cuota_mensual,
      "vencimiento" -> e.vencimiento,
      "id_persona" -> e.id_persona,
      "id_identificacion" -> e.id_identificacion,
      "es_descuento" -> e.es_descuento,
      "id_solicitud" -> e.id_solicitud,
      "id_colocacion" -> e.id_colocacion,
      "fecha_capital" -> e.fecha_capital,
      "fecha_interes" -> e.fecha_interes,
      "estado" -> e.estado,
      "consecutivo" -> e.consecutivo
    )
  }

  implicit val rReads: Reads[InformacionCredito] = (
    (__ \ "entidad").readNullable[String] and
      (__ \ "valor_inicial").readNullable[BigDecimal] and
      (__ \ "saldo").readNullable[BigDecimal] and
      (__ \ "cuota_mensual").readNullable[BigDecimal] and
      (__ \ "vencimiento").readNullable[DateTime] and
      (__ \ "id_persona").readNullable[String] and
      (__ \ "id_identificacion").readNullable[Int] and
      (__ \ "es_descuento").readNullable[Int] and
      (__ \ "id_solicitud").readNullable[String] and
      (__ \ "id_colocacion").readNullable[String] and
      (__ \ "fecha_capital").readNullable[DateTime] and
      (__ \ "fecha_interes").readNullable[DateTime] and
      (__ \ "estado").readNullable[Int] and
      (__ \ "consecutivo").readNullable[Int]
  )(InformacionCredito.apply _)

   val _set = {
    get[Option[String]]("entidad") ~
    get[Option[BigDecimal]]("valor_inicial") ~
    get[Option[BigDecimal]]("saldo") ~
    get[Option[BigDecimal]]("cuota_mensual") ~
    get[Option[DateTime]]("vencimiento") ~
    get[Option[String]]("id_persona") ~
    get[Option[Int]]("id_identificacion") ~
    get[Option[Int]]("es_descuento") ~
    get[Option[String]]("id_solicitud") ~
    get[Option[String]]("id_colocacion") ~
    get[Option[DateTime]]("fecha_capital") ~
    get[Option[DateTime]]("fecha_interes") ~
    get[Option[Int]]("estado") ~
    get[Option[Int]]("consecutivo") map {
      case 
          entidad ~
          valor_inicial ~
          saldo ~
          cuota_mensual ~
          vencimiento ~
          id_persona ~
          id_identificacion ~
          es_descuento ~
          id_solicitud ~
          id_colocacion ~
          fecha_capital ~
          fecha_interes ~
          estado ~
          consecutivo =>
          InformacionCredito(
                entidad,
                valor_inicial,
                saldo,
                cuota_mensual,
                vencimiento,
                id_persona,
                id_identificacion,
                es_descuento,
                id_solicitud,
                id_colocacion,
                fecha_capital,
                fecha_interes,
                estado,
                consecutivo
                )
    }
  }
}

class InformacionCreditoRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext) {
  private val db = dbapi.database("default")

  /**
  *  Buscar Información de Créditos Locales
  */
  def buscarCredito(id_identificacion: Int, id_persona: String): Future[Iterable[InformacionCredito]] = Future[Iterable[InformacionCredito]] {
    db.withConnection { implicit connection =>
      var _lista: ListBuffer[InformacionCredito] = new ListBuffer[InformacionCredito]
      val resultSet = SQL("""SELECT * FROM \"col$colocacion\" WHERE ID_IDENTIFICACION = {id_identificacion} and ID_PERSONA = {id_persona} and ID_ESTADO_COLOCACION IN (0,1,2,3,8,9)""").
      on(
        'id_identificacion -> id_identificacion,
        'id_persona -> id_persona
      ).as(InformacionCredito._set *)
      var csc = 1
      resultSet.map { r =>
        val ic = new InformacionCredito(entidad, 
                                        valor_inicial, 
                                        saldo, 
                                        cuota_mensual, 
                                        vencimiento, 
                                        id_persona, 
                                        id_identificacion, 
                                        es_descuento, 
                                        id_solicitud, 
                                        id_colocacion, 
                                        fecha_capital, 
                                        fecha_interes, 
                                        estado, 
                                        Some(csc))
        csc+=1
      }
      _lista.toList
    }
  }

}
