package models

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

import scala.math.BigDecimal

import org.joda.time.DateTime

import anorm._
import anorm.SqlParser.{get, str}
import anorm.JodaParameterMetaData._

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
    tasa_interes_corriente: Option[Double],
    tasa_interes_mora: Option[Double],
    puntos_interes: Option[Double]
)
case class ColocacionB(
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
    reciprocidad: Option[Double],
    fecha_saldado: Option[DateTime],
    tipo: Option[String]
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
    tasa_interes_corriente: Option[Double],
    tasa_interes_mora: Option[Double],
    puntos_interes: Option[Double],
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
    reciprocidad: Option[Double],
    fecha_saldado: Option[DateTime],
    tipo: Option[String]
)

case class Colocacion(
    a: ColocacionA,
    b: ColocacionB
)

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
      "puntos_interes" -> e.puntos_interes
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
      (__ \ "tasa_interes_corriente").readNullable[Double] and
      (__ \ "tasa_interes_mora").readNullable[Double] and
      (__ \ "puntos_interes").readNullable[Double]
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
      get[Option[Double]]("tasa_interes_corriente") ~
      get[Option[Double]]("tasa_interes_mora") ~
      get[Option[Double]]("puntos_interes") map {
      case id_agencia ~
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
            puntos_interes =>
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
          puntos_interes
        )
    }
  }
}

object ColocacionB {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wbWrites = new Writes[ColocacionB] {
    def writes(e: ColocacionB) = Json.obj(
      "id_tipo_cuota" -> e.id_tipo_cuota,
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
      "fecha_saldado" -> e.fecha_saldado,
      "tipo" -> e.tipo
    )
  }

  implicit val rbReads: Reads[ColocacionB] = (
    (__ \ "id_tipo_cuota").readNullable[Int] and
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
      (__ \ "reciprocidad").readNullable[Double] and
      (__ \ "fecha_saldado").readNullable[DateTime] and
      (__ \ "tipo").readNullable[String]
  )(ColocacionB.apply _)

  val _set = {
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
      get[Option[Double]]("reciprocidad") ~
      get[Option[DateTime]]("fecha_saldado") ~
      get[Option[String]]("tipo") map {
      case id_tipo_cuota ~
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
            fecha_saldado ~
            tipo =>
        ColocacionB(
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
          fecha_saldado,
          tipo
        )
    }
  }
}

object Colocacion {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wWrites = new Writes[Colocacion] {
    def writes(c: Colocacion) = Json.obj(
      "a" -> c.a,
      "b" -> c.b
    )
  }

  implicit val rReads: Reads[Colocacion] = (
    (__ \ "a").read[ColocacionA] and
      (__ \ "b").read[ColocacionB]
  )(Colocacion.apply _)
}

object Credito {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

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
      get[Option[Double]]("tasa_interes_corriente") ~
      get[Option[Double]]("tasa_interes_mora") ~
      get[Option[Double]]("puntos_interes") ~
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
      get[Option[Double]]("reciprocidad") ~
      get[Option[DateTime]]("fecha_saldado") ~
      get[Option[String]]("tipo") map {
      case id_agencia ~
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
            fecha_saldado ~
            tipo =>
        (
          (
            id_agencia, // 1.1
            id_colocacion, // 1.2
            id_identificacion, // 1.3
            id_persona, // 1.4
            id_clasificacion
          ), // 1.5
          (
            id_linea, // 2.1
            id_inversion, // 2.2
            id_respaldo, // 2.3
            id_garantia, // 2.4
            id_categoria
          ), // 2.5
          (
            id_evaluacion, // 3.1
            fecha_desembolso, // 3.2
            valor_desembolso, // 3.3
            plazo_colocacion, // 3.4
            fecha_vencimiento
          ), // 3.5
          (
            tipo_interes, // 4.1
            id_interes, // 4.2
            tasa_interes_corriente, // 4.3
            tasa_interes_mora, // 4.4
            puntos_interes
          ), // 4.5
          (
            id_tipo_cuota, // 5.1
            amortiza_capital, // 5.2
            amortiza_interes, // 5.3
            periodo_gracia, // 5.4
            dias_prorrogados
          ), // 5.5
          (
            valor_cuota, // 6.1
            abonos_capital, // 6.2
            fecha_capital, // 6.3
            fecha_interes, // 6.4
            id_estado_colocacion // 6.5
          ),
          (
            id_ente_aprobador, // 7.1
            id_empleado, // 7.2
            nota_contable, // 7.3
            numero_cuenta, // 7.4
            es_anormal
          ), // 7.5
          (
            dias_pago, // 8.1
            reciprocidad, // 8.2
            fecha_saldado, // 8.3
            tipo
          ) // 8.4
        )
    }
  }
}

case class CuotasLiq(
    cuotaNumero: Int,
    codigoPuc: String,
    codigoNombre: String,
    fechaInicial: DateTime,
    fechaFinal: DateTime,
    dias: Int,
    tasa: Double,
    debito: BigDecimal,
    credito: BigDecimal,
    esCapital: Boolean,
    esCausado: Boolean,
    esCorriente: Boolean,
    esVencido: Boolean,
    esAnticipado: Boolean,
    esDevuelto: Boolean,
    esOtros: Boolean,
    esCajaBanco: Boolean,
    esCostas: Boolean,
    idClaseOperacion: String
)

case class FechaLiq(
    fecha_inicial: DateTime,
    fecha_final: DateTime,
    anticipado: Boolean,
    causado: Boolean,
    corrientes: Boolean,
    vencida: Boolean,
    devuelto: Boolean
)

object CuotasLiq {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wWrites = new Writes[CuotasLiq] {
    def writes(e: CuotasLiq) = Json.obj(
      "cuotaNumero" -> e.cuotaNumero,
      "codigoPuc" -> e.codigoPuc,
      "codigoNombre" -> e.codigoNombre,
      "fechaInicial" -> e.fechaInicial,
      "fechaFinal" -> e.fechaFinal,
      "dias" -> e.dias,
      "tasa" -> e.tasa,
      "debito" -> e.debito,
      "credito" -> e.credito,
      "esCapital" -> e.esCapital,
      "esCausado" -> e.esCausado,
      "esCorriente" -> e.esCorriente,
      "esVencido" -> e.esVencido,
      "esAnticipado" -> e.esAnticipado,
      "esDevuelto" -> e.esDevuelto,
      "esOtros" -> e.esOtros,
      "esCajaBanco" -> e.esCajaBanco,
      "esCostas" -> e.esCostas,
      "idClaseOperacion" -> e.idClaseOperacion
    )
  }

  implicit val rReads: Reads[CuotasLiq] = (
    (__ \ "cuotaNumero").read[Int] and
      (__ \ "codigoPuc").read[String] and
      (__ \ "codigoNombre").read[String] and
      (__ \ "fechaInicial").read[DateTime] and
      (__ \ "fechaFinal").read[DateTime] and
      (__ \ "dias").read[Int] and
      (__ \ "tasa").read[Double] and
      (__ \ "debito").read[BigDecimal] and
      (__ \ "credito").read[BigDecimal] and
      (__ \ "esCapital").read[Boolean] and
      (__ \ "esCausado").read[Boolean] and
      (__ \ "esCorriente").read[Boolean] and
      (__ \ "esVencido").read[Boolean] and
      (__ \ "esAnticipado").read[Boolean] and
      (__ \ "esDevuelto").read[Boolean] and
      (__ \ "esOtros").read[Boolean] and
      (__ \ "esCajaBanco").read[Boolean] and
      (__ \ "esCostas").read[Boolean] and
      (__ \ "idClaseOperacion").read[String]
  )(CuotasLiq.apply _)
}

case class Liquidacion(
    items: Option[List[CuotasLiq]],
    saldo: Option[BigDecimal],
    fecha_capital: Option[DateTime],
    fecha_interes: Option[DateTime],
    fecha_proxima: Option[DateTime],
    liquidado: Boolean,
    referencia: String
)

object Liquidacion {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wWrites = new Writes[Liquidacion] {
    def writes(e: Liquidacion) = Json.obj(
      "items" -> e.items,
      "saldo" -> e.saldo,
      "fecha_capital" -> e.fecha_capital,
      "fecha_interes" -> e.fecha_interes,
      "fecha_proxima" -> e.fecha_proxima,
      "liquidado" -> e.liquidado,
      "referencia" -> e.referencia
    )
  }

  implicit val rReads: Reads[Liquidacion] = (
    (__ \ "items").readNullable[List[CuotasLiq]] and
      (__ \ "saldo").readNullable[BigDecimal] and
      (__ \ "fecha_capital").readNullable[DateTime] and
      (__ \ "fecha_interes").readNullable[DateTime] and
      (__ \ "fecha_proxima").readNullable[DateTime] and
      (__ \ "liquidado").read[Boolean] and
      (__ \ "referencia").read[String]
  )(Liquidacion.apply _)
}

case class Tabla(
    cuot_num: Int,
    cuot_fecha: DateTime,
    cuot_saldo: BigDecimal,
    cuot_capital: BigDecimal,
    cuot_interes: BigDecimal,
    cuot_otros: BigDecimal
)

case class ADescontar(
    id_descuento: Long,
    codigo: String,
    cuota_numero: Int,
    valor: BigDecimal
)

case class DescuentoColocacion(
    id_descuento: Long,
    descripcion_descuento: String,
    descontar: Boolean
)

case class Descuento(
    id_descuento: Long,
    codigo: String,
    descripcion_descuento: String,
    valor_descuento: BigDecimal,
    porcentaje_colocacion: Double,
    porcentaje_cuota: Double,
    en_desembolso: Int,
    en_cuota: Int,
    porcentaje_saldo: Double,
    es_distribuido: Int,
    es_activo: Int,
    amortizacion: Int
)

case class Adicion(cuot_num: Int, cuot_otros: BigDecimal)

object Tabla {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val tablaWrites = new Writes[Tabla] {
    def writes(tabla: Tabla) = Json.obj(
      "cuot_num" -> tabla.cuot_num,
      "cuot_fecha" -> tabla.cuot_fecha,
      "cuot_saldo" -> tabla.cuot_saldo,
      "cuot_capital" -> tabla.cuot_capital,
      "cuot_interes" -> tabla.cuot_interes,
      "cuot_otros" -> tabla.cuot_otros
    )
  }
  implicit val tablaReads: Reads[Tabla] = (
    (__ \ "cuot_num").read[Int] and
      (__ \ "cuot_fecha").read[DateTime] and
      (__ \ "cuot_saldo").read[BigDecimal] and
      (__ \ "cuot_capital").read[BigDecimal] and
      (__ \ "cuot_interes").read[BigDecimal] and
      (__ \ "cuot_otros").read[BigDecimal]
  )(Tabla.apply _)

  val _set = {
    get[Int]("cuota_numero") ~
      get[DateTime]("fecha_a_pagar") ~
      get[BigDecimal]("capital_a_pagar") ~
      get[BigDecimal]("interes_a_pagar") map {
      case cuot_num ~
            cuot_fecha ~
            cuot_capital ~
            cuot_interes =>
        Tabla(
          cuot_num,
          cuot_fecha,
          0,
          cuot_capital,
          cuot_interes,
          0
        )
    }
  }
}

object ADescontar {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val _Writes = new Writes[ADescontar] {
    def writes(t: ADescontar) = Json.obj(
      "id_descuento" -> t.id_descuento,
      "codigo" -> t.codigo,
      "cuota_numero" -> t.cuota_numero,
      "valor" -> t.valor
    )
  }
  implicit val _Reads: Reads[ADescontar] = (
    (__ \ "id_descuento").read[Long] and
      (__ \ "codigo").read[String] and
      (__ \ "cuota_numero").read[Int] and
      (__ \ "valor").read[BigDecimal]
  )(ADescontar.apply _)
}

object DescuentoColocacion {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val _Writes = new Writes[DescuentoColocacion] {
    def writes(t: DescuentoColocacion) = Json.obj(
      "id_descuento" -> t.id_descuento,
      "descripcion_descuento" -> t.descripcion_descuento,
      "descontar" -> t.descontar
    )
  }
  implicit val _Reads: Reads[DescuentoColocacion] = (
    (__ \ "id_descuento").read[Long] and
      (__ \ "descripcion_descuento").read[String] and
      (__ \ "descontar").read[Boolean]
  )(DescuentoColocacion.apply _)

  val _set = {
    get[Long]("id_descuento") ~
      get[String]("descripcion_descuento") ~
      get[Boolean]("descontar") map {
      case id_descuento ~
            descripcion_descuento ~
            descontar =>
        DescuentoColocacion(id_descuento, descripcion_descuento, descontar)
    }
  }
}

object Descuento {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val descuentoWrites = new Writes[Descuento] {
    def writes(descuento: Descuento) = Json.obj(
      "id_descuento" -> descuento.id_descuento,
      "codigo" -> descuento.codigo,
      "descripcion_descuento" -> descuento.descripcion_descuento,
      "valor_descuento" -> descuento.valor_descuento,
      "porcentaje_colocacion" -> descuento.porcentaje_colocacion,
      "porcentaje_cuota" -> descuento.porcentaje_cuota,
      "en_desembolso" -> descuento.en_desembolso,
      "en_cuota" -> descuento.en_cuota,
      "porcentaje_saldo" -> descuento.porcentaje_saldo,
      "es_distribuido" -> descuento.es_distribuido,
      "es_activo" -> descuento.es_activo,
      "amortizacion" -> descuento.amortizacion
    )
  }
  implicit val descuentoReads: Reads[Descuento] = (
    (__ \ "id_descuento").read[Long] and
      (__ \ "codigo").read[String] and
      (__ \ "descripcion_descuento").read[String] and
      (__ \ "valor_descuento").read[BigDecimal] and
      (__ \ "porcentaje_colocacion").read[Double] and
      (__ \ "porcentaje_cuota").read[Double] and
      (__ \ "en_desembolso").read[Int] and
      (__ \ "en_cuota").read[Int] and
      (__ \ "porcentaje_saldo").read[Double] and
      (__ \ "es_distribuido").read[Int] and
      (__ \ "es_activo").read[Int] and
      (__ \ "amortizacion").read[Int]
  )(Descuento.apply _)

  val _set = {
    get[Long]("id_descuento") ~
      get[String]("codigo") ~
      get[String]("descripcion_descuento") ~
      get[BigDecimal]("valor_descuento") ~
      get[Double]("porcentaje_colocacion") ~
      get[Double]("porcentaje_cuota") ~
      get[Int]("en_desembolso") ~
      get[Int]("en_cuota") ~
      get[Double]("porcentaje_saldo") ~
      get[Int]("es_distribuido") ~
      get[Int]("es_activo") ~
      get[Int]("amortizacion") map {
      case id_descuento ~
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
            amortizacion =>
        Descuento(
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

object Adicion {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val adicionWrites = new Writes[Adicion] {
    def writes(adicion: Adicion) = Json.obj(
      "cuot_num" -> adicion.cuot_num,
      "cuot_otros" -> adicion.cuot_otros
    )
  }
  implicit val adicionReads: Reads[Adicion] = (
    (__ \ "cuot_num").read[Int] and
      (__ \ "cuot_otros").read[BigDecimal]
  )(Adicion.apply _)
}

case class ColPeriodoGracia(
    id_colocacion: Option[String],
    fecha_capital: Option[DateTime],
    fecha_interes: Option[DateTime],
    fecha_registro: Option[DateTime],
    dias: Option[Int],
    se_causa: Option[Int],
    fecha_cancelado: Option[DateTime],
    estado: Option[Int]
)

object ColPeriodoGracia {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[ColPeriodoGracia] {
    def writes(a: ColPeriodoGracia) = Json.obj(
      "id_colocacion" -> a.id_colocacion,
      "fecha_capital" -> a.fecha_capital,
      "fecha_interes" -> a.fecha_interes,
      "fecha_registro" -> a.fecha_registro,
      "dias" -> a.dias,
      "se_causa" -> a.se_causa,
      "fecha_cancelado" -> a.fecha_cancelado,
      "estado" -> a.estado
    )
  }
  implicit val Reads: Reads[ColPeriodoGracia] = (
    (__ \ "id_colocacion").readNullable[String] and
      (__ \ "fecha_capital").readNullable[DateTime] and
      (__ \ "fecha_interes").readNullable[DateTime] and
      (__ \ "fecha_registro").readNullable[DateTime] and
      (__ \ "dias").readNullable[Int] and
      (__ \ "se_causa").readNullable[Int] and
      (__ \ "fecha_cancelado").readNullable[DateTime] and
      (__ \ "estado").readNullable[Int]
  )(ColPeriodoGracia.apply _)

  val _set = {
    get[Option[String]]("id_colocacion") ~
      get[Option[DateTime]]("fecha_capital") ~
      get[Option[DateTime]]("fecha_interes") ~
      get[Option[DateTime]]("fecha_registro") ~
      get[Option[Int]]("dias") ~
      get[Option[Int]]("se_causa") ~
      get[Option[DateTime]]("fecha_cancelado") ~
      get[Option[Int]]("estado") map {
      case id_colocacion ~
            fecha_capital ~
            fecha_interes ~
            fecha_registro ~
            dias ~
            se_causa ~
            fecha_cancelado ~
            estado =>
        ColPeriodoGracia(
          id_colocacion,
          fecha_capital,
          fecha_interes,
          fecha_registro,
          dias,
          se_causa,
          fecha_cancelado,
          estado
        )
    }
  }
}

case class ColPeriodoGraciaVista(
    id_colocacion: Option[String],
    nombre: Option[String],
    saldo: Option[BigDecimal],
    cuota: Option[BigDecimal],
    fecha_capital: Option[DateTime],
    fecha_interes: Option[DateTime],
    fecha_registro: Option[DateTime],
    dias: Option[Int],
    se_causa: Option[Int],
    fecha_cancelado: Option[DateTime],
    estado: Option[Int]
)

object ColPeriodoGraciaVista {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[ColPeriodoGraciaVista] {
    def writes(a: ColPeriodoGraciaVista) = Json.obj(
      "id_colocacion" -> a.id_colocacion,
      "nombre" -> a.nombre,
      "saldo" -> a.saldo,
      "cuota" -> a.cuota,
      "fecha_capital" -> a.fecha_capital,
      "fecha_interes" -> a.fecha_interes,
      "fecha_registro" -> a.fecha_registro,
      "dias" -> a.dias,
      "se_causa" -> a.se_causa,
      "fecha_cancelado" -> a.fecha_cancelado,
      "estado" -> a.estado
    )
  }
  implicit val Reads: Reads[ColPeriodoGraciaVista] = (
    (__ \ "id_colocacion").readNullable[String] and
      (__ \ "nombre").readNullable[String] and
      (__ \ "saldo").readNullable[BigDecimal] and
      (__ \ "cuota").readNullable[BigDecimal] and
      (__ \ "fecha_capital").readNullable[DateTime] and
      (__ \ "fecha_interes").readNullable[DateTime] and
      (__ \ "fecha_registro").readNullable[DateTime] and
      (__ \ "dias").readNullable[Int] and
      (__ \ "se_causa").readNullable[Int] and
      (__ \ "fecha_cancelado").readNullable[DateTime] and
      (__ \ "estado").readNullable[Int]
  )(ColPeriodoGraciaVista.apply _)

  val _set = {
    get[Option[String]]("id_colocacion") ~
      get[Option[String]]("nombre") ~
      get[Option[BigDecimal]]("saldo") ~
      get[Option[BigDecimal]]("cuota") ~
      get[Option[DateTime]]("fecha_capital") ~
      get[Option[DateTime]]("fecha_interes") ~
      get[Option[DateTime]]("fecha_registro") ~
      get[Option[Int]]("dias") ~
      get[Option[Int]]("se_causa") ~
      get[Option[DateTime]]("fecha_cancelado") ~
      get[Option[Int]]("estado") map {
      case id_colocacion ~
            nombre ~
            saldo ~
            cuota ~
            fecha_capital ~
            fecha_interes ~
            fecha_registro ~
            dias ~
            se_causa ~
            fecha_cancelado ~
            estado =>
        ColPeriodoGraciaVista(
          id_colocacion,
          nombre,
          saldo,
          cuota,
          fecha_capital,
          fecha_interes,
          fecha_registro,
          dias,
          se_causa,
          fecha_cancelado,
          estado
        )
    }
  }

}

case class ControlCobroVista(
    id_agencia: Option[Int],
    id_colocacion: Option[String],
    nombre: Option[String],
    valor: Option[BigDecimal],
    saldo: Option[BigDecimal],
    cuota: Option[BigDecimal],
    plazo: Option[Int],
    amortiza_capital: Option[Int],
    amortiza_interes: Option[Int],
    tasa_nominal: Option[Double],
    numero_cuotas: Option[Int],
    fecha_capital: Option[DateTime],
    fecha_interes: Option[DateTime],
    estado: Option[String],
    dias_mora: Option[Int],
    id_identificacion: Option[Int],
    id_persona: Option[String],
    tipo_cuota: Option[String],
    tiene_compromiso: Option[String],
    centro_costo: Option[String],
    email: Option[String]
)

case class Compromiso(
    comp_id: Option[Long],
    id_colocacion: Option[String],
    comp_descripcion: Option[String],
    comp_activo: Option[Int],
    comp_fecha: Option[DateTime],
    comp_fechacompromiso: Option[DateTime],
    comp_repetircada: Option[String],
    id_empleado: Option[String]
)

case class ControlCobro(
    id_agencia: Option[Long],
    id_colocacion: Option[String],
    fecha_observacion: Option[DateTime],
    observacion: Option[String],
    es_observacion: Option[Int],
    es_compromiso: Option[Int],
    fecha_compromiso: Option[DateTime],
    id_usuario: Option[String],
    empleado: Option[String]
)

object ControlCobroVista {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val write = new Writes[ControlCobroVista] {
    def writes(e: ControlCobroVista) = Json.obj(
      "id_agencia" -> e.id_agencia,
      "id_colocacion" -> e.id_colocacion,
      "nombre" -> e.nombre,
      "valor" -> e.valor,
      "saldo" -> e.saldo,
      "cuota" -> e.cuota,
      "plazo" -> e.plazo,
      "amortiza_capital" -> e.amortiza_capital,
      "amortiza_interes" -> e.amortiza_interes,
      "tasa_nominal" -> e.tasa_nominal,
      "numero_cuotas" -> e.numero_cuotas,
      "fecha_capital" -> e.fecha_capital,
      "fecha_interes" -> e.fecha_interes,
      "estado" -> e.estado,
      "dias_mora" -> e.dias_mora,
      "id_identificacion" -> e.id_identificacion,
      "id_persona" -> e.id_persona,
      "tipo_cuota" -> e.tipo_cuota,
      "tiene_compromiso" -> e.tiene_compromiso,
      "centro_costo" -> e.centro_costo,
      "email" -> e.email
    )
  }

  implicit val rReads: Reads[ControlCobroVista] = (
    (__ \ "id_agencia").readNullable[Int] and
      (__ \ "id_colocacion").readNullable[String] and
      (__ \ "nombre").readNullable[String] and
      (__ \ "valor").readNullable[BigDecimal] and
      (__ \ "saldo").readNullable[BigDecimal] and
      (__ \ "cuota").readNullable[BigDecimal] and
      (__ \ "plazo").readNullable[Int] and
      (__ \ "amortiza_capital").readNullable[Int] and
      (__ \ "amortiza_interes").readNullable[Int] and
      (__ \ "tasa_nominal").readNullable[Double] and
      (__ \ "numero_cuotas").readNullable[Int] and
      (__ \ "fecha_capital").readNullable[DateTime] and
      (__ \ "fecha_interes").readNullable[DateTime] and
      (__ \ "estado").readNullable[String] and
      (__ \ "dias_mora").readNullable[Int] and
      (__ \ "id_identificacion").readNullable[Int] and
      (__ \ "id_persona").readNullable[String] and
      (__ \ "tipo_cuota").readNullable[String] and
      (__ \ "tiene_compromiso").readNullable[String] and
      (__ \ "centro_costo").readNullable[String] and
      (__ \ "email").readNullable[String]
  )(ControlCobroVista.apply _)
}

object Compromiso {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val write = new Writes[Compromiso] {
    def writes(e: Compromiso) = Json.obj(
      "comp_id" -> e.comp_id,
      "id_colocacion" -> e.id_colocacion,
      "comp_descripcion" -> e.comp_descripcion,
      "comp_activo" -> e.comp_activo,
      "comp_fecha" -> e.comp_fecha,
      "comp_fechacompromiso" -> e.comp_fechacompromiso,
      "comp_repetircada" -> e.comp_repetircada,
      "id_empleado" -> e.id_empleado
    )
  }

  implicit val rReads: Reads[Compromiso] = (
    (__ \ "comp_id").readNullable[Long] and
      (__ \ "id_colocacion").readNullable[String] and
      (__ \ "comp_descripcion").readNullable[String] and
      (__ \ "comp_activo").readNullable[Int] and
      (__ \ "comp_fecha").readNullable[DateTime] and
      (__ \ "comp_fechacompromiso").readNullable[DateTime] and
      (__ \ "comp_repetircada").readNullable[String] and
      (__ \ "id_empleado").readNullable[String]
  )(Compromiso.apply _)
}

object ControlCobro {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val write = new Writes[ControlCobro] {
    def writes(e: ControlCobro) = Json.obj(
      "id_agencia" -> e.id_agencia,
      "id_colocacion" -> e.id_colocacion,
      "fecha_observacion" -> e.fecha_observacion,
      "observacion" -> e.observacion,
      "es_observacion" -> e.es_observacion,
      "es_compromiso" -> e.es_compromiso,
      "fecha_compromiso" -> e.fecha_compromiso,
      "id_usuario" -> e.id_usuario,
      "empleado" -> e.empleado
    )
  }

  implicit val rReads: Reads[ControlCobro] = (
    (__ \ "id_agencia").readNullable[Long] and
      (__ \ "id_colocacion").readNullable[String] and
      (__ \ "fecha_observacion").readNullable[DateTime] and
      (__ \ "observacion").readNullable[String] and
      (__ \ "es_observacion").readNullable[Int] and
      (__ \ "es_compromiso").readNullable[Int] and
      (__ \ "fecha_compromiso").readNullable[DateTime] and
      (__ \ "id_usuario").readNullable[String] and
      (__ \ "empleado").readNullable[String]
  )(ControlCobro.apply _)

  val _set = {
    get[Option[Long]]("id_agencia") ~
      get[Option[String]]("id_colocacion") ~
      get[Option[DateTime]]("fecha_observacion") ~
      get[Option[String]]("observacion") ~
      get[Option[Int]]("es_observacion") ~
      get[Option[Int]]("es_compromiso") ~
      get[Option[DateTime]]("fecha_compromiso") ~
      get[Option[String]]("id_usuario") ~
      get[Option[String]]("empleado") map {
      case id_agencia ~
            id_colocacion ~
            fecha_observacion ~
            observacion ~
            es_observacion ~
            es_compromiso ~
            fecha_compromiso ~
            id_usuario ~
            empleado =>
        ControlCobro(
          id_agencia,
          id_colocacion,
          fecha_observacion,
          observacion,
          es_observacion,
          es_compromiso,
          fecha_compromiso,
          id_usuario,
          empleado
        )
    }
  }

}
