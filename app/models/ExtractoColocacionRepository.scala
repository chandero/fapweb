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

case class ExtractoColocacion(
    id_agencia: Option[Int],
    id_cbte_colocacion: Option[Int],
    id_colocacion: Option[String],
    fecha_extracto: Option[DateTime],
    hora_extracto: Option[DateTime],
    cuota_extracto: Option[Int],
    tipo_operacion: Option[Int],
    saldo_anterior_extracto: Option[BigDecimal],
    abono_capital: Option[BigDecimal],
    abono_cxc: Option[BigDecimal],
    abono_anticipado: Option[BigDecimal],
    abono_servicios: Option[BigDecimal],
    abono_mora: Option[BigDecimal],
    abono_seguro: Option[BigDecimal],
    abono_pagxcli: Option[BigDecimal],
    abono_honorarios: Option[BigDecimal],
    abono_otros: Option[BigDecimal],
    tasa_interes_liquidacion: Option[Double],
    id_empleado: Option[String],
    interes_pago_hasta: Option[DateTime],
    capital_pago_hasta: Option[DateTime],
    tipo_abono: Option[Int]
)

object ExtractoColocacion {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wWrites = new Writes[ExtractoColocacion] {
    def writes(e: ExtractoColocacion) = Json.obj(
      "id_agencia" -> e.id_agencia,
      "id_cbte_colocacion" -> e.id_cbte_colocacion,
      "id_colocacion" -> e.id_colocacion,
      "fecha_extracto" -> e.fecha_extracto,
      "hora_extracto" -> e.hora_extracto,
      "cuota_extracto" -> e.cuota_extracto,
      "tipo_operacion" -> e.tipo_operacion,
      "saldo_anterior_extracto" -> e.saldo_anterior_extracto,
      "abono_capital" -> e.abono_capital,
      "abono_cxc" -> e.abono_cxc,
      "abono_anticipado" -> e.abono_anticipado,
      "abono_servicios" -> e.abono_servicios,
      "abono_mora" -> e.abono_mora,
      "abono_seguro" -> e.abono_seguro,
      "abono_pagxcli" -> e.abono_pagxcli,
      "abono_honorarios" -> e.abono_honorarios,
      "abono_otros" -> e.abono_otros,
      "tasa_interes_liquidacion" -> e.tasa_interes_liquidacion,
      "id_empleado" -> e.id_empleado,
      "interes_pago_hasta" -> e.interes_pago_hasta,
      "capital_pago_hasta" -> e.capital_pago_hasta,
      "tipo_abono" -> e.tipo_abono,      
    )
  }

  implicit val rReads: Reads[ExtractoColocacion] = (
    (__ \ "id_agencia").readNullable[Int] and
    (__ \ "id_cbte_colocacion").readNullable[Int] and
    (__ \ "id_colocacion").readNullable[String] and
    (__ \ "fecha_extracto").readNullable[DateTime] and
    (__ \ "hora_extracto").readNullable[DateTime] and
    (__ \ "cuota_extracto").readNullable[Int] and
    (__ \ "tipo_operacion").readNullable[Int] and
    (__ \ "saldo_anterior_extracto").readNullable[BigDecimal] and
    (__ \ "abono_capital").readNullable[BigDecimal] and
    (__ \ "abono_cxc").readNullable[BigDecimal] and
    (__ \ "abono_anticipado").readNullable[BigDecimal] and
    (__ \ "abono_servicios").readNullable[BigDecimal] and
    (__ \ "abono_mora").readNullable[BigDecimal] and
    (__ \ "abono_seguro").readNullable[BigDecimal] and
    (__ \ "abono_pagxcli").readNullable[BigDecimal] and
    (__ \ "abono_honorarios").readNullable[BigDecimal] and
    (__ \ "abono_otros").readNullable[BigDecimal] and
    (__ \ "tasa_interes_liquidacion").readNullable[Double] and
    (__ \ "id_empleado").readNullable[String] and
    (__ \ "interes_pago_hasta").readNullable[DateTime] and
    (__ \ "capital_pago_hasta").readNullable[DateTime] and
    (__ \ "tipo_abono").readNullable[Int]
  )(ExtractoColocacion.apply _)

   val _set = {
    get[Option[Int]]("id_agencia") ~
    get[Option[Int]]("id_cbte_colocacion") ~
    get[Option[String]]("id_colocacion") ~
    get[Option[DateTime]]("fecha_extracto") ~
    get[Option[DateTime]]("hora_extracto") ~
    get[Option[Int]]("cuota_extracto") ~
    get[Option[Int]]("tipo_operacion") ~
    get[Option[BigDecimal]]("saldo_anterior_extracto") ~
    get[Option[BigDecimal]]("abono_capital") ~
    get[Option[BigDecimal]]("abono_cxc") ~
    get[Option[BigDecimal]]("abono_anticipado") ~
    get[Option[BigDecimal]]("abono_servicios") ~
    get[Option[BigDecimal]]("abono_mora") ~
    get[Option[BigDecimal]]("abono_seguro") ~
    get[Option[BigDecimal]]("abono_pagxcli") ~
    get[Option[BigDecimal]]("abono_honorarios") ~
    get[Option[BigDecimal]]("abono_otros") ~
    get[Option[Double]]("tasa_interes_liquidacion") ~
    get[Option[String]]("id_empleado") ~
    get[Option[DateTime]]("interes_pago_hasta") ~
    get[Option[DateTime]]("capital_pago_hasta") ~
    get[Option[Int]]("tipo_abono") map {
      case id_agencia ~
        id_cbte_colocacion ~
        id_colocacion ~
        fecha_extracto ~
        hora_extracto ~
        cuota_extracto ~
        tipo_operacion ~
        saldo_anterior_extracto ~
        abono_capital ~
        abono_cxc ~
        abono_anticipado ~
        abono_servicios ~
        abono_mora ~
        abono_seguro ~
        abono_pagxcli ~
        abono_honorarios ~
        abono_otros ~
        tasa_interes_liquidacion ~
        id_empleado ~
        interes_pago_hasta ~
        capital_pago_hasta ~
        tipo_abono => ExtractoColocacion(
            id_agencia,
            id_cbte_colocacion,
            id_colocacion,
            fecha_extracto,
            hora_extracto,
            cuota_extracto,
            tipo_operacion,
            saldo_anterior_extracto,
            abono_capital,
            abono_cxc,
            abono_anticipado,
            abono_servicios,
            abono_mora,
            abono_seguro,
            abono_pagxcli,
            abono_honorarios,
            abono_otros,
            tasa_interes_liquidacion,
            id_empleado,
            interes_pago_hasta,
            capital_pago_hasta,
            tipo_abono
          )
    }
  }
}

class ExtractoColocacionRepository @Inject()(dbapi: DBApi, colocacionService: ColocacionRepository, empresaRepository: EmpresaRepository)(
    implicit ec: DatabaseExecutionContext) {
  private val db = dbapi.database("default")

  def obtener(id_colocacion: String):Future[Iterable[ExtractoColocacion]] = Future {
    var _list = new ListBuffer[ExtractoColocacion]()
    db.withConnection { implicit connection =>
      SQL("""SELECT * FROM "col$extracto" WHERE ID_COLOCACION = {id_colocacion}  
             ORDER BY FECHA_EXTRACTO, HORA_EXTRACTO""").
        on(
          'id_colocacion -> id_colocacion
        ).as(ExtractoColocacion._set *)
    }
  }

}