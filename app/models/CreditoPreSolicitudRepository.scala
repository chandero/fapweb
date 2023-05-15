package models

import javax.inject.Inject
import java.util.Calendar
import java.util.UUID.randomUUID

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

import play.api.db.DBApi

import anorm._
import anorm.SqlParser.{get, str, int, long, date, double}
import anorm.JodaParameterMetaData._

import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import scala.collection.mutable

import scala.collection.mutable.ListBuffer
import scala.math.BigDecimal

import utilities._
import java.time.DateTimeException
import java.sql.Connection
import notifiers.EmailSender

class CreditoPreSolicitudRepository @Inject()(
    dbapi: DBApi,
    _g: GlobalesCol,
    _gd: GlobalesCon,
    _funcion: Funcion
)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  def registrarSolicitud(presolicitud: PreSolicitud): Future[Boolean] = Future {
    var _result = false
    try {
      db.withTransaction { implicit connection =>
        val _query = """INSERT INTO COL$PRESOLICITUD (
	            ID_IDENTIFICACION,
	            ID_PERSONA,
	            PRSO_FECHA_EXPEDICION,
	            PRSO_LUGAR_EXPEDICION,
	            PRSO_PRIMER_NOMBRE,
	            PRSO_SEGUNDO_NOMBRE,
	            PRSO_PRIMER_APELLIDO,
	            PRSO_SEGUNDO_APELLIDO,
	            PRSO_INGRESOS,
	            PRSO_GASTOS,
	            PRSO_OTROS_EGRESOS,
	            PRSO_TELEFONO_CONTACTO,
	            PRSO_EMAIL,
	            ID_LINEA,
	            PRSO_MONTO_SOLICITADO,
	            PRSO_PLAZO_SOLICITADO,
              PRSO_EVALUACION_MORA,
              PRSO_EVALUACION_PREVIA,
	            PRSO_AUTORIZO,
              PRSO_AUTORIZO_CONSULTA,
	            PAIS_ID,
              DEPA_ID,
              CIUD_ID,
              PRSO_ORIGEN,
	            ID_EMPLEADO,
	            PRSO_ESTADO,
              CREATED_AT,
              UPDATED_AT,
              DELETED_AT
        ) VALUES (
            {id_identificacion},
            {id_persona},
            {prso_fecha_expedicion},
            {prso_lugar_expedicion},
            {prso_primer_nombre},
            {prso_segundo_nombre},
            {prso_primer_apellido},
            {prso_segundo_apellido},
            {prso_ingresos},
            {prso_gastos},
            {prso_otros_egresos},
            {prso_telefono_contacto},
            {prso_email},
            {id_linea},
            {prso_monto_solicitado},
            {prso_plazo_solicitado},
            {prso_evaluacion_mora},
            {prso_evaluacion_previa},
            {prso_autorizo},
            {prso_autorizo_consulta},
            {pais_id},
            {depa_id},
            {ciud_id},
            {prso_origen},
            {id_empleado},
            {prso_estado},
            {created_at},
            {updated_at},
            {deleted_at}
        )"""

        // Consultar si existe el cliente
        val _mora = validarMoraPersona(
          presolicitud.id_identificacion.get,
          presolicitud.id_persona.get
        )
        val _evaluacionMora = _mora match {
          case x if (x <= 5)            => 1
          case x if (x > 5 && x <= 15)  => 2
          case x if (x > 15 && x <= 30) => 3
          case x if (x > 30 && x <= 60) => 4
          case x if (x > 60)            => 5
        }
        _result = _evaluacionMora match {
          case 1 | 2 | 3 => true
          case 4 | 5     => false
        }
        SQL(_query)
          .on(
            "id_identificacion" -> presolicitud.id_identificacion,
            "id_persona" -> presolicitud.id_persona,
            "prso_fecha_expedicion" -> presolicitud.fecha_expedicion,
            "prso_lugar_expedicion" -> presolicitud.lugar_expedicion,
            "prso_primer_nombre" -> presolicitud.primer_nombre,
            "prso_segundo_nombre" -> presolicitud.segundo_nombre,
            "prso_primer_apellido" -> presolicitud.primer_apellido,
            "prso_segundo_apellido" -> presolicitud.segundo_apellido,
            "prso_ingresos" -> presolicitud.ingresos,
            "prso_gastos" -> presolicitud.gastos,
            "prso_otros_egresos" -> presolicitud.otros_egresos,
            "prso_telefono_contacto" -> presolicitud.numero_celular,
            "prso_email" -> presolicitud.email,
            "id_linea" -> presolicitud.id_linea,
            "prso_monto_solicitado" -> presolicitud.monto_solicitado,
            "prso_plazo_solicitado" -> presolicitud.plazo_solicitado,
            "prso_evaluacion_mora" -> _mora,
            "prso_evaluacion_previa" -> _evaluacionMora,
            "prso_autorizo" -> 1,
            "prso_autorizo_consulta" -> 1,
            "pais_id" -> presolicitud.pais_id,
            "depa_id" -> presolicitud.depa_id,
            "ciud_id" -> presolicitud.ciud_id,
            "prso_origen" -> "WEB",
            "id_empleado" -> Option.empty[String],
            "prso_estado" -> 1,
            "created_at" -> new DateTime().getMillis,
            "updated_at" -> Option.empty[scala.Long],
            "deleted_at" -> Option.empty[scala.Long]
          )
          .executeUpdate()
      }
    } catch {
      case e: Exception => {
        _result = false
      }
    }
    if (!_result) {
      enviarCorreoInviable(presolicitud)
    }
    _result
  }

  def enviarCorreoInviable(presolicitud: PreSolicitud) {
    val _correo_destino = presolicitud.email.getOrElse("")
    val _primer_nombre_destino = presolicitud.primer_nombre.getOrElse("")
    val _primer_apellido_destino = presolicitud.primer_apellido.getOrElse("")
    val _segundo_nombre_destino = presolicitud.segundo_nombre.getOrElse("")
    val _segundo_apellido_destino = presolicitud.segundo_apellido.getOrElse("")
    val _nombre_completo = _primer_nombre_destino + " " + _segundo_nombre_destino + " " + _primer_apellido_destino + " " + _segundo_apellido_destino
    EmailSender.sendSolicitudInviableInfo(_correo_destino, _nombre_completo)
  }

  def validarMoraPersona(id_identificacion: Int, id_persona: String)(
      implicit connection: Connection
  ) = {
    val _queryJuridico = """SELECT COUNT(*) FROM "col$colocacion" cc WHERE cc.ID_ESTADO_COLOCACION IN (2,3,6) AND cc.ID_PERSONA = {id_persona}"""
    val _queryNingunPagoVigente = """SELECT MAX((CURRENT_DATE - cc.FECHA_DESEMBOLSO) - cc.AMORTIZA_INTERES) AS DIAS_MORA FROM "col$colocacion" cc
                                     WHERE cc.ID_PERSONA = {id_persona} AND
                                    (SELECT COUNT(*) FROM "col$tablaliquidacion" tl WHERE tl.ID_COLOCACION = cc.ID_COLOCACION AND tl.PAGADA = 1) < 1;"""
    val _query =
      """SELECT MAX(DIAS) FROM (
                        SELECT cc.ID_COLOCACION, MAX(ct.FECHA_PAGADA - ct.FECHA_A_PAGAR) AS DIAS FROM "col$colocacion" cc
                        INNER JOIN "col$tablaliquidacion" ct ON ct.ID_COLOCACION = cc.ID_COLOCACION  
                        WHERE ct.PAGADA = 1 AND cc.ID_PERSONA = {id_persona} AND ct.FECHA_A_PAGAR > CURRENT_DATE - 1825
                        GROUP BY 1
                )"""
    val _resultJuridico = SQL(_queryJuridico).on("id_persona" -> id_persona).as(SqlParser.scalar[Int].single)
    val _diasJuridico = if (_resultJuridico > 0) {
      9999
    } else { 0 }
    val _diasNingunPago =
      SQL(_queryNingunPagoVigente).on("id_persona" -> id_persona).as(SqlParser.scalar[Int].single)

    val _result = SQL(_query)
      .on("id_persona" -> id_persona)
      .as(SqlParser.scalar[Int].singleOpt)

    val _diasUltimoPago = _result match {
      case Some(x) => x
      case None    => 0
    }

    var _diasMora = if (_diasUltimoPago > _diasNingunPago) {
      _diasUltimoPago
    } else {
      _diasNingunPago
    }
    _diasMora = if (_diasMora > _diasJuridico) {
      _diasMora
    } else {
      _diasJuridico
    }
    _diasMora
  }

  def obtenerListaPresolicitud(): Future[List[PreSolicitud]] = Future {
    val result = db.withConnection { implicit connection =>
      val _query = """SELECT * FROM COL$PRESOLICITUD"""
      SQL(_query).as(PreSolicitud._set *)
    }
    println("result: " + result)
    result.toList
  }
}
