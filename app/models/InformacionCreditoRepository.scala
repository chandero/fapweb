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

class InformacionCreditoRepository @Inject()(dbapi: DBApi, colocacionService: ColocacionRepository, empresaRepository: EmpresaRepository)(
    implicit ec: DatabaseExecutionContext) {
  private val db = dbapi.database("default")

  /**
  *  Buscar Información de Créditos Locales
  */
  def buscarCredito(id_identificacion: Int, id_persona: String, empr_id: Long): Future[Future[Iterable[InformacionCredito]]] = Future[Future[Iterable[InformacionCredito]]] {
        var _lista: ListBuffer[InformacionCredito] = new ListBuffer[InformacionCredito]
        val empresa = empresaRepository.buscarPorId(empr_id).get
        val entidad = empresa.empr_descripcion
        var csc = 1
        val colocaciones = colocacionService.buscarColocacion(id_identificacion, id_persona)
        colocaciones.map { creditos =>
            for (r <- creditos) {
                    println("recorriendo colocacion: " + r.a.id_colocacion.get)
                    val valor_inicial = r.a.valor_desembolso
                    val saldo = r.a.valor_desembolso.get - r.b.abonos_capital.get
                    val cuota_mensual = r.b.valor_cuota
                    val vencimiento = r.a.fecha_vencimiento
                    val id_persona = r.a.id_persona
                    val id_identificacion = r.a.id_identificacion
                    val es_descuento = 0
                    val id_solicitud = None
                    val id_colocacion = r.a.id_colocacion
                    val fecha_capital = r.b.fecha_capital
                    val fecha_interes = r.b.fecha_interes
                    val estado = r.b.id_estado_colocacion
                    val ic = new InformacionCredito(Some(entidad), 
                                        valor_inicial, 
                                        Some(saldo),
                                        cuota_mensual, 
                                        vencimiento, 
                                        id_persona, 
                                        id_identificacion, 
                                        Some(es_descuento),
                                        id_solicitud, 
                                        id_colocacion, 
                                        fecha_capital, 
                                        fecha_interes, 
                                        estado, 
                                        Some(csc))
                    _lista += ic
                    csc+=1
                    println("Lista: " + _lista)
                  }
                  // println("Lista Final: " + _lista)
                  _lista.toList
        }
  }

}
