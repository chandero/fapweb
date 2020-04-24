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

case class ExtractoColocacionDetallado(
    id_agencia: Option[Int],
    id_cbte_colocacion: Option[Int],
    id_colocacion: Option[String],
    fecha_extracto: Option[DateTime],
    hora_extracto: Option[DateTime],
    codigo_puc: Option[String],
    fecha_inicial: Option[DateTime],
    fecha_final: Option[DateTime],
    dias_aplicados: Option[Int],
    tasa_liquidacion: Option[Double],
    valor_debito: Option[BigDecimal],
    valor_credito: Option[BigDecimal]
)

object ExtractoColocacionDetallado {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wWrites = new Writes[ExtractoColocacionDetallado] {
    def writes(e: ExtractoColocacionDetallado) = Json.obj(
      "id_agencia" -> e.id_agencia,
      "id_cbte_colocacion" -> e.id_cbte_colocacion,
      "id_colocacion" -> e.id_colocacion,
      "fecha_extracto" -> e.fecha_extracto,
      "hora_extracto" -> e.hora_extracto,
      "codigo_puc" -> e.codigo_puc,
      "fecha_inicial" -> e.fecha_inicial,
      "fecha_final" -> e.fecha_final,
      "dias_aplicados" -> e.dias_aplicados,
      "tasa_liquidacion" -> e.tasa_liquidacion,
      "valor_debito" -> e.valor_debito,
      "valor_credito" -> e.valor_credito      
    )
  }

  implicit val rReads: Reads[ExtractoColocacionDetallado] = (
    (__ \ "id_agencia").readNullable[Int] and
    (__ \ "id_cbte_colocacion").readNullable[Int] and
    (__ \ "id_colocacion").readNullable[String] and
    (__ \ "fecha_extracto").readNullable[DateTime] and
    (__ \ "hora_extracto").readNullable[DateTime] and
    (__ \ "codigo_puc").readNullable[String] and
    (__ \ "fecha_inicial").readNullable[DateTime] and
    (__ \ "fecha_final").readNullable[DateTime] and
    (__ \ "dias_aplicados").readNullable[Int] and
    (__ \ "tasa_liquidacion").readNullable[Double] and
    (__ \ "valor_debito").readNullable[BigDecimal] and
    (__ \ "valor_credito").readNullable[BigDecimal]
  )(ExtractoColocacionDetallado.apply _)

   val _set = {
    get[Option[Int]]("id_agencia") ~
    get[Option[Int]]("id_cbte_colocacion") ~
    get[Option[String]]("id_colocacion") ~
    get[Option[DateTime]]("fecha_extracto") ~
    get[Option[DateTime]]("hora_extracto") ~
    get[Option[String]]("codigo_puc") ~
    get[Option[DateTime]]("fecha_inicial")  ~
    get[Option[DateTime]]("fecha_final") ~
    get[Option[Int]]("dias_aplicados") ~
    get[Option[Double]]("tasa_liquidacion") ~
    get[Option[BigDecimal]]("valor_debito") ~
    get[Option[BigDecimal]]("valor_credito") map {
      case 
        id_agencia  ~
        id_cbte_colocacion ~
        id_colocacion ~
        fecha_extracto ~
        hora_extracto ~
        codigo_puc ~
        fecha_inicial  ~
        fecha_final ~
        dias_aplicados ~
        tasa_liquidacion ~
        valor_debito ~
        valor_credito => ExtractoColocacionDetallado(
                id_agencia,
                id_cbte_colocacion,
                id_colocacion,
                fecha_extracto,
                hora_extracto,
                codigo_puc,
                fecha_inicial,
                fecha_final,
                dias_aplicados,
                tasa_liquidacion,
                valor_debito,
                valor_credito
          )
    }
  }
}

class ExtractoColocacionDetalladoRepository @Inject()(dbapi: DBApi, colocacionService: ColocacionRepository, empresaRepository: EmpresaRepository)(
    implicit ec: DatabaseExecutionContext) {
  private val db = dbapi.database("default")

  def obtener(id_colocacion: String, id_cbte_colocacion: Int, fecha_extracto: Long): Future[Iterable[ExtractoColocacionDetallado]] = Future {
    db.withConnection { implicit connection => 
      val _fecha_extracto = new DateTime(fecha_extracto)
      SQL("""SELECT * FROM "col$extractodet" 
              WHERE ID_COLOCACION = {id_colocacion} and ID_CBTE_COLOCACION = {id_cbte_colocacion} and FECHA_EXTRACTO = {fecha_extracto} 
              ORDER BY CODIGO ASC""").
          on(
            'id_colocacion -> id_colocacion,
            'id_cbte_colocacion -> id_cbte_colocacion,
            'fecha_extracto -> fecha_extracto
          ).as(ExtractoColocacionDetallado._set *)
    }
  }

}