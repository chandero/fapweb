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

case class ColocacionA(
    id_agencia: Option[Int],
    id_colocacion: Option[String],
    id_identificacion: Option[Int],
    id_persona: Option[String],
    id_clasificacion: Option[Int],
    id_linea: Option[Int],
    id_inversion: Option[Int],
    id_respaldo: Option[Int],
    id_garantia: Option[Int],
    id_categoria: Option[String],
    id_evaluacion: Option[String],
    fecha_desembolso: Option[DateTime],
    valor_desembolso: Option[BigDecimal],
    plazo_colocacion: Option[Int],
    fecha_vencimiento: Option[DateTime],
    tipo_interes: Option[String],
    id_interes: Option[Int],
    tasa_interes_corriente: Option[Float],
    tasa_interes_mora: Option[Float],
    puntos_interes: Option[Float],
    id_tipo_cuota: Option[Int]
)
case class ColocacionB(
    amortiza_capital: Option[Int],
    amortiza_interes: Option[Int],
    periodo_gracia: Option[Int],
    dias_prorrogados: Option[Int],
    valor_cuota: Option[BigDecimal],
    abonos_capital: Option[BigDecimal],
    fecha_capital: Option[DateTime],
    fecha_interes: Option[DateTime],
    id_estado_colocacion: Option[Int],
    id_ente_aprobador: Option[Int],
    id_empleado: Option[String],
    nota_contable: Option[String],
    numero_cuenta: Option[Int],
    es_anormal: Option[Int],
    dias_pago: Option[Int],
    reciprocidad: Option[Float],
    fecha_saldado: Option[DateTime]
)

case class Credito(
    id_agencia: Option[Int],
    id_colocacion: Option[String],
    id_identificacion: Option[Int],
    id_persona: Option[String],
    id_clasificacion: Option[Int],
    id_linea: Option[Int],
    id_inversion: Option[Int],
    id_respaldo: Option[Int],
    id_garantia: Option[Int],
    id_categoria: Option[String],
    id_evaluacion: Option[String],
    fecha_desembolso: Option[DateTime],
    valor_desembolso: Option[BigDecimal],
    plazo_colocacion: Option[Int],
    fecha_vencimiento: Option[DateTime],
    tipo_interes: Option[String],
    id_interes: Option[Int],
    tasa_interes_corriente: Option[Float],
    tasa_interes_mora: Option[Float],
    puntos_interes: Option[Float],
    id_tipo_cuota: Option[Int],
    amortiza_capital: Option[Int],
    amortiza_interes: Option[Int],
    periodo_gracia: Option[Int],
    dias_prorrogados: Option[Int],
    valor_cuota: Option[BigDecimal],
    abonos_capital: Option[BigDecimal],
    fecha_capital: Option[DateTime],
    fecha_interes: Option[DateTime],
    id_estado_colocacion: Option[Int],
    id_ente_aprobador: Option[Int],
    id_empleado: Option[String],
    nota_contable: Option[String],
    numero_cuenta: Option[Int],
    es_anormal: Option[Int],
    dias_pago: Option[Int],
    reciprocidad: Option[Float],
    fecha_saldado: Option[DateTime]
)

case class Colocacion(a: ColocacionA, b: ColocacionB)

object ColocacionA {
    implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wWrites = new Writes[ColocacionA] {
    def writes(e: ColocacionA) = Json.obj(
      "id_agencia" -> e.id_agencia,
      "id_colocacion" -> e.id_colocacion,
      "id_identificacion" -> e.id_identificacion,
      "id_persona" -> e.id_persona,
      "id_clasificacion" -> e.id_clasificacion,
      "id_linea" -> e.id_linea,
      "id_inversion" -> e.id_inversion,
      "id_respaldo" -> e.id_respaldo,
      "id_garantia" -> e.id_garantia,
      "id_categoria" -> e.id_categoria,
      "id_evaluacion" -> e.id_evaluacion,
      "fecha_desembolso" -> e.fecha_desembolso,
      "valor_desembolso" -> e.valor_desembolso,
      "plazo_colocacion" -> e.plazo_colocacion,
      "fecha_vencimiento" -> e.fecha_vencimiento,
      "tipo_interes" -> e.tipo_interes,
      "id_interes" -> e.id_interes,
      "tasa_interes_corriente" -> e.tasa_interes_corriente,
      "tasa_interes_mora" -> e.tasa_interes_mora,
      "puntos_interes" -> e.puntos_interes,
      "id_tipo_cuota" -> e.id_tipo_cuota
    )
  }

  implicit val rReads: Reads[ColocacionA] = (
    (__ \ "id_agencia").readNullable[Int] and
      (__ \ "id_colocacion").readNullable[String] and
      (__ \ "id_identificacion").readNullable[Int] and
      (__ \ "id_persona").readNullable[String] and
      (__ \ "id_clasificacion").readNullable[Int] and
      (__ \ "id_linea").readNullable[Int] and
      (__ \ "id_inversion").readNullable[Int] and
      (__ \ "id_respaldo").readNullable[Int] and
      (__ \ "id_garantia").readNullable[Int] and
      (__ \ "id_categoria").readNullable[String] and
      (__ \ "id_evaluacion").readNullable[String] and
      (__ \ "fecha_desembolso").readNullable[DateTime] and
      (__ \ "valor_desembolso").readNullable[BigDecimal] and
      (__ \ "plazo_colocacion").readNullable[Int] and
      (__ \ "fecha_vencimiento").readNullable[DateTime] and
      (__ \ "tipo_interes").readNullable[String] and
      (__ \ "id_interes").readNullable[Int] and
      (__ \ "tasa_interes_corriente").readNullable[Float] and
      (__ \ "tasa_interes_mora").readNullable[Float] and
      (__ \ "puntos_interes").readNullable[Float] and
      (__ \ "id_tipo_cuota").readNullable[Int]
  )(ColocacionA.apply _)

   val _set = {
    get[Option[Int]]("id_agencia") ~
    get[Option[String]]("id_colocacion") ~
    get[Option[Int]]("id_identificacion") ~
    get[Option[String]]("id_persona") ~
    get[Option[Int]]("id_clasificacion") ~
    get[Option[Int]]("id_linea") ~
    get[Option[Int]]("id_inversion") ~
    get[Option[Int]]("id_respaldo") ~
    get[Option[Int]]("id_garantia") ~
    get[Option[String]]("id_categoria") ~
    get[Option[String]]("id_evaluacion") ~
    get[Option[DateTime]]("fecha_desembolso") ~
    get[Option[BigDecimal]]("valor_desembolso") ~
    get[Option[Int]]("plazo_colocacion") ~
    get[Option[DateTime]]("fecha_vencimiento") ~
    get[Option[String]]("tipo_interes") ~
    get[Option[Int]]("id_interes") ~
    get[Option[Float]]("tasa_interes_corriente") ~
    get[Option[Float]]("tasa_interes_mora") ~
    get[Option[Float]]("puntos_interes") ~
    get[Option[Int]]("id_tipo_cuota") map {
      case 
          id_agencia ~
          id_colocacion ~
          id_identificacion ~
          id_persona ~
          id_clasificacion ~
          id_linea ~
          id_inversion ~
          id_respaldo ~
          id_garantia ~
          id_categoria ~
          id_evaluacion ~
          fecha_desembolso ~
          valor_desembolso ~
          plazo_colocacion ~
          fecha_vencimiento ~
          tipo_interes ~
          id_interes ~
          tasa_interes_corriente ~
          tasa_interes_mora ~
          puntos_interes ~
          id_tipo_cuota =>
          ColocacionA(
                    id_agencia,
                    id_colocacion,
                    id_identificacion,
                    id_persona,
                    id_clasificacion,
                    id_linea,
                    id_inversion,
                    id_respaldo,
                    id_garantia,
                    id_categoria,
                    id_evaluacion,
                    fecha_desembolso,
                    valor_desembolso,
                    plazo_colocacion,
                    fecha_vencimiento,
                    tipo_interes,
                    id_interes,
                    tasa_interes_corriente,
                    tasa_interes_mora,
                    puntos_interes,
                    id_tipo_cuota
                )
    }
  }
}

object ColocacionB {
    implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wWrites = new Writes[ColocacionB] {
    def writes(e: ColocacionB) = Json.obj(
        "amortiza_capital" -> e.amortiza_capital,
        "amortiza_interes" -> e.amortiza_interes,
        "periodo_gracia" -> e.periodo_gracia,
        "dias_prorrogados" -> e.dias_prorrogados,
        "valor_cuota" -> e.valor_cuota,
        "abonos_capital" -> e.abonos_capital,
        "fecha_capital" -> e.fecha_capital,
        "fecha_interes" -> e.fecha_interes,
        "id_estado_colocacion" -> e.id_estado_colocacion,
        "id_ente_aprobador" -> e.id_ente_aprobador,
        "id_empleado" -> e.id_empleado,
        "nota_contable" -> e.nota_contable,
        "numero_cuenta" -> e.numero_cuenta,
        "es_anormal" -> e.es_anormal,
        "dias_pago" -> e.dias_pago,
        "reciprocidad" -> e.reciprocidad,
        "fecha_saldado" -> e.fecha_saldado
    )

    implicit val rReads: Reads[ColocacionB] = (
        (__ \ "amortiza_capital").readNullable[Int] and
        (__ \ "amortiza_interes").readNullable[Int] and
        (__ \ "periodo_gracia").readNullable[Int] and
        (__ \ "dias_prorrogados").readNullable[Int] and
        (__ \ "valor_cuota").readNullable[BigDecimal] and
        (__ \ "abonos_capital").readNullable[BigDecimal] and
        (__ \ "fecha_capital").readNullable[DateTime] and
        (__ \ "fecha_interes").readNullable[DateTime] and
        (__ \ "id_estado_colocacion").readNullable[Int] and
        (__ \ "id_ente_aprobador").readNullable[Int] and
        (__ \ "id_empleado").readNullable[String] and
        (__ \ "nota_contable").readNullable[String] and
        (__ \ "numero_cuenta").readNullable[Int] and
        (__ \ "es_anormal").readNullable[Int] and
        (__ \ "dias_pago").readNullable[Int] and
        (__ \ "reciprocidad").readNullable[Float] and
        (__ \ "fecha_saldado").readNullable[DateTime]
    )(ColocacionB.apply _)

    val _set = {
        get[Option[Int]]("amortiza_capital") ~
        get[Option[Int]]("amortiza_interes") ~
        get[Option[Int]]("periodo_gracia") ~
        get[Option[Int]]("dias_prorrogados") ~
        get[Option[BigDecimal]]("valor_cuota") ~
        get[Option[BigDecimal]]("abonos_capital") ~
        get[Option[DateTime]]("fecha_capital") ~
        get[Option[DateTime]]("fecha_interes") ~
        get[Option[Int]]("id_estado_colocacion") ~
        get[Option[Int]]("id_ente_aprobador") ~
        get[Option[String]]("id_empleado") ~
        get[Option[String]]("nota_contable") ~
        get[Option[Int]]("numero_cuenta") ~
        get[Option[Int]]("es_anormal") ~
        get[Option[Int]]("dias_pago") ~
        get[Option[Float]]("reciprocidad") ~
        get[Option[DateTime]]("fecha_saldado") map {
            case 
                amortiza_capital ~
                amortiza_interes ~
                periodo_gracia ~
                dias_prorrogados ~
                valor_cuota ~
                abonos_capital ~
                fecha_capital ~
                fecha_interes ~
                id_estado_colocacion ~
                id_ente_aprobador ~
                id_empleado ~
                nota_contable ~
                numero_cuenta ~
                es_anormal ~
                dias_pago ~
                reciprocidad ~
                fecha_saldado => ColocacionB(
                                    amortiza_capital,
                                    amortiza_interes,
                                    periodo_gracia,
                                    dias_prorrogados,
                                    valor_cuota,
                                    abonos_capital,
                                    fecha_capital,
                                    fecha_interes,
                                    id_estado_colocacion,
                                    id_ente_aprobador,
                                    id_empleado,
                                    nota_contable,
                                    numero_cuenta,
                                    es_anormal,
                                    dias_pago,
                                    reciprocidad,
                                    fecha_saldado
            )
        }
    }
  }
}

object Credito {
    val _set = {
        get[Option[Int]]("id_agencia") ~
        get[Option[String]]("id_colocacion") ~
        get[Option[Int]]("id_identificacion") ~
        get[Option[String]]("id_persona") ~
        get[Option[Int]]("id_clasificacion") ~
        get[Option[Int]]("id_linea") ~
        get[Option[Int]]("id_inversion") ~
        get[Option[Int]]("id_respaldo") ~
        get[Option[Int]]("id_garantia") ~
        get[Option[String]]("id_categoria") ~
        get[Option[String]]("id_evaluacion") ~
        get[Option[DateTime]]("fecha_desembolso") ~
        get[Option[BigDecimal]]("valor_desembolso") ~
        get[Option[Int]]("plazo_colocacion") ~
        get[Option[DateTime]]("fecha_vencimiento") ~
        get[Option[String]]("tipo_interes") ~
        get[Option[Int]]("id_interes") ~
        get[Option[Float]]("tasa_interes_corriente") ~
        get[Option[Float]]("tasa_interes_mora") ~
        get[Option[Float]]("puntos_interes") ~
        get[Option[Int]]("id_tipo_cuota") ~
        get[Option[Int]]("amortiza_capital") ~
        get[Option[Int]]("amortiza_interes") ~
        get[Option[Int]]("periodo_gracia") ~
        get[Option[Int]]("dias_prorrogados") ~
        get[Option[BigDecimal]]("valor_cuota") ~
        get[Option[BigDecimal]]("abonos_capital") ~
        get[Option[DateTime]]("fecha_capital") ~
        get[Option[DateTime]]("fecha_interes") ~
        get[Option[Int]]("id_estado_colocacion") ~
        get[Option[Int]]("id_ente_aprobador") ~
        get[Option[String]]("id_empleado") ~
        get[Option[String]]("nota_contable") ~
        get[Option[Int]]("numero_cuenta") ~
        get[Option[Int]]("es_anormal") ~
        get[Option[Int]]("dias_pago") ~
        get[Option[Float]]("reciprocidad") ~
        get[Option[DateTime]]("fecha_saldado") map {
          case 
              id_agencia ~
              id_colocacion ~
              id_identificacion ~
              id_persona ~
              id_clasificacion ~
              id_linea ~
              id_inversion ~
              id_respaldo ~
              id_garantia ~
              id_categoria ~
              id_evaluacion ~
              fecha_desembolso ~
              valor_desembolso ~
              plazo_colocacion ~
              fecha_vencimiento ~
              tipo_interes ~
              id_interes ~
              tasa_interes_corriente ~
              tasa_interes_mora ~
              puntos_interes ~
              id_tipo_cuota ~
              amortiza_capital ~
              amortiza_interes ~
              periodo_gracia ~
              dias_prorrogados ~
              valor_cuota ~
              abonos_capital ~
              fecha_capital ~
              fecha_interes ~
              id_estado_colocacion ~
              id_ente_aprobador ~
              id_empleado ~
              nota_contable ~
              numero_cuenta ~
              es_anormal ~
              dias_pago ~
              reciprocidad ~
              fecha_saldado =>
              Credito(
                        id_agencia,
                        id_colocacion,
                        id_identificacion,
                        id_persona,
                        id_clasificacion,
                        id_linea,
                        id_inversion,
                        id_respaldo,
                        id_garantia,
                        id_categoria,
                        id_evaluacion,
                        fecha_desembolso,
                        valor_desembolso,
                        plazo_colocacion,
                        fecha_vencimiento,
                        tipo_interes,
                        id_interes,
                        tasa_interes_corriente,
                        tasa_interes_mora,
                        puntos_interes,
                        id_tipo_cuota,
                        amortiza_capital,
                        amortiza_interes,
                        periodo_gracia,
                        dias_prorrogados,
                        valor_cuota,
                        abonos_capital,
                        fecha_capital,
                        fecha_interes,
                        id_estado_colocacion,
                        id_ente_aprobador,
                        id_empleado,
                        nota_contable,
                        numero_cuenta,
                        es_anormal,
                        dias_pago,
                        reciprocidad,
                        fecha_saldado                        
                    )
        }
      }
    
}