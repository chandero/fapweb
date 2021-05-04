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
import scala.math.BigDecimal

import org.joda.time.DateTime
import org.joda.time.LocalDate
import scala.collection.mutable

case class Auxiliar (
    id_comprobante: Option[Int],
    id_agencia: Option[Int],
    fecha: Option[DateTime],
    codigo: Option[String],
    debito: Option[Double],
    credito: Option[Double],
    id_cuenta: Option[String],
    id_colocacion: Option[String],
    id_identificacion: Option[Int],
    id_persona: Option[String],
    persona: Option[String],
    monto_retencion: Option[Double],
    tasa_retencion: Option[Double],
    estado_aux: Option[String],
    tipo_comprobante: Option[Int],
    id: Option[Int],
    id_clase_operacion: Option[String]
)

object Auxiliar {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  val _set = {
    get[Option[Int]]("id_comprobante") ~
    get[Option[Int]]("id_agencia") ~
    get[Option[DateTime]]("fecha") ~
    get[Option[String]]("codigo") ~
    get[Option[Double]]("debito") ~
    get[Option[Double]]("credito") ~
    get[Option[String]]("id_cuenta") ~
    get[Option[String]]("id_colocacion") ~
    get[Option[Int]]("id_identificacion") ~
    get[Option[String]]("id_persona") ~
    get[Option[String]]("persona") ~
    get[Option[Double]]("monto_retencion") ~
    get[Option[Double]]("tasa_retencion") ~
    get[Option[String]]("estado_aux") ~
    get[Option[Int]]("tipo_comprobante") ~
    get[Option[Int]]("id") ~
    get[Option[String]]("id_clase_operacion") map {
      case 
        id_comprobante ~
        id_agencia ~ 
        fecha ~
        codigo ~
        debito ~
        credito ~
        id_cuenta ~
        id_colocacion ~
        id_identificacion ~
        id_persona ~
        persona ~
        monto_retencion ~
        tasa_retencion ~
        estado_aux ~
        tipo_comprobante ~
        id ~
        id_clase_operacion => Auxiliar(
          id_comprobante,
          id_agencia,
          fecha,
          codigo,
          debito,
          credito,
          id_cuenta,
          id_colocacion,
          id_identificacion,
          id_persona,
          persona,
          monto_retencion,
          tasa_retencion,
          estado_aux,
          tipo_comprobante,
          id,
          id_clase_operacion
        )
    }
  }
}

class AuxiliarRepository @Inject()(dbapi: DBApi, colocacionService: ColocacionRepository, empresaRepository: EmpresaRepository)(
    implicit ec: DatabaseExecutionContext) {
  private val db = dbapi.database("default")

  def consultar(codigo_inicial: String, codigo_final: String, fecha_inicial: Long, fecha_final: Long, id_identificacion: Option[Int], id_persona: Option[String]): Future[Iterable[Auxiliar]] = Future {
    val fi = new DateTime(fecha_inicial)
    val ff = new DateTime(fecha_final)
    var filter = ""
    var separador = ""
    id_identificacion match {
      case Some(id) => 
                filter = filter + " a1.id_identificacion = " + id_identificacion
                separador = " AND "
      case None => None
    }

    id_persona match {
      case Some(id) => 
                filter = filter + separador + " a1.id_persona = '" + id_persona + "'"
      case None => None
    }

    var query = """SELECT a1.*, TRIM(p1.NOMBRE || ' ' || p1.PRIMER_APELLIDO || ' ' || p1.SEGUNDO_APELLIDO) AS persona 
                  |FROM "con$auxiliar" a1 
                  |LEFT JOIN "gen$persona" p1 ON p1.ID_IDENTIFICACION = a1.ID_IDENTIFICACION AND p1.ID_PERSONA = a1.ID_PERSONA
                  |WHERE a1.CODIGO BETWEEN {codigo_inicial} AND {codigo_final} AND a1.FECHA BETWEEN {fecha_inicial} AND {fecha_final} AND a1.ESTADOAUX IN ('O', 'C')"""
    if (filter != "") {
      query += " AND " + filter
    }

    db.withTransaction { implicit connection =>
      SQL(query).
        on(
          'codigo_inicial -> codigo_inicial,
          'codigo_final -> codigo_final,
          'fecha_inicial -> fi,
          'fecha_final -> ff
        ).as(Auxiliar._set *)
    }
  }
}