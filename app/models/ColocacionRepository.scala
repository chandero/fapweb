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
import scala.collection.mutable

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
      case  id_tipo_cuota ~
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

class ColocacionRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  /**
    * def buscarColocacion
    * @param id_identificacion: Int
    * @param id_persona: String
    * @return Colocacion
    * */
  def buscarColocacion(
      id_identificacion: Int,
      id_persona: String
  ): Future[Iterable[Colocacion]] = Future[Iterable[Colocacion]] {
    var _lista = new mutable.ListBuffer[Colocacion]()
    println(
      "Buscando Colocaciones Actuales: id_identificacion: " + id_identificacion + ", id_persona: " + id_persona
    )
    val result = db.withConnection { implicit connection =>
      SQL("""SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'D' AS tipo FROM \"col$colocacion\" a
             WHERE a.ID_IDENTIFICACION = {id_identificacion} and a.ID_PERSONA = {id_persona} and a.ID_ESTADO_COLOCACION IN (0,1,2,3,8,9)
             UNION ALL
             SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'F' AS tipo FROM \"col$colocacion\" a
             LEFT JOIN \"col$colgarantias\" g ON g.ID_COLOCACION = a.ID_COLOCACION WHERE g.ID_IDENTIFICACION = {id_identificacion} and g.ID_PERSONA = {id_persona} and a.ID_ESTADO_COLOCACION IN (0,1,2,3,8,9)
      """)
        .on(
          'id_identificacion -> id_identificacion,
          'id_persona -> id_persona
        )
        .as(Credito._set *)
    }
    for (c <- result) {
      val a = new ColocacionA(
        c._1._1,
        c._1._2,
        c._1._3,
        c._1._4,
        c._1._5,
        c._2._1,
        c._2._2,
        c._2._3,
        c._2._4,
        c._2._5,
        c._3._1,
        c._3._2,
        c._3._3,
        c._3._4,
        c._3._5,
        c._4._1,
        c._4._2,
        c._4._3,
        c._4._4,
        c._4._5
      )

      val b = new ColocacionB(
        c._5._1,        
        c._5._2,
        c._5._3,
        c._5._4,
        c._5._5,
        c._6._1,
        c._6._2,
        c._6._3,
        c._6._4,
        c._6._5,
        c._7._1,
        c._7._2,
        c._7._3,
        c._7._4,
        c._7._5,
        c._8._1,
        c._8._2,
        c._8._3,
        c._8._4
      )
      val colocacion = new Colocacion(a, b)
      _lista += colocacion

    }
    _lista.toList
  }

  def buscarColocacion(id_colocacion: String): Future[Option[Colocacion]] = Future {
    val credito = db.withConnection { implicit connection =>
      SQL("""SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'D' AS tipo FROM \"col$colocacion\" a
             WHERE a.ID_COLOCACION = {id_colocacion}
      """)
        .on(
          'id_colocacion -> id_colocacion
        )
        .as(Credito._set.singleOpt)
    }
    credito match {
      case Some(c) => 
        val a = new ColocacionA(
          c._1._1,
          c._1._2,
          c._1._3,
          c._1._4,
          c._1._5,
          c._2._1,
          c._2._2,
          c._2._3,
          c._2._4,
          c._2._5,
          c._3._1,
          c._3._2,
          c._3._3,
          c._3._4,
          c._3._5,
          c._4._1,
          c._4._2,
          c._4._3,
          c._4._4,
          c._4._5
        )

        val b = new ColocacionB(
          c._5._1,        
          c._5._2,
          c._5._3,
          c._5._4,
          c._5._5,
          c._6._1,
          c._6._2,
          c._6._3,
          c._6._4,
          c._6._5,
          c._7._1,
          c._7._2,
          c._7._3,
          c._7._4,
          c._7._5,
          c._8._1,
          c._8._2,
          c._8._3,
          c._8._4
        )
        val colocacion = new Colocacion(a, b)
       Some(colocacion)
      case None => None
    }
  }
}
