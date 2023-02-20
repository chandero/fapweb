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
