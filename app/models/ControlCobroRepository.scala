package models

import javax.inject.Inject
import java.util.Calendar

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

import play.api.db.DBApi

import anorm._
import anorm.SqlParser.{get, str, int}
import anorm.JodaParameterMetaData._

import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}

import org.joda.time.DateTime
import org.joda.time.LocalDate
import scala.collection.mutable

import utilities._
import java.util.ArrayList
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef

// Jasper
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperRunManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.JRExporter
import net.sf.jasperreports.engine.JRExporterParameter
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import net.sf.jasperreports.engine.fill.AsynchronousFillHandle
import net.sf.jasperreports.web.servlets.AsyncJasperPrintAccessor
import net.sf.jasperreports.engine.fill.FillListener
import net.sf.jasperreports.engine.util.JRLoader

import java.util.HashMap
import net.sf.jasperreports.engine.JREmptyDataSource
import java.io.ByteArrayOutputStream
import notifiers.EmailSender
import java.io.FileInputStream
import java.io.File

class ControlCobroRepository @Inject()(
    dbapi: DBApi,
    _f: Funcion,
    _gcol: GlobalesCol,
    _uService: UsuarioRepository
)(
    implicit ec: DatabaseExecutionContext
) {
  private val REPORT_DEFINITION_PATH = System.getProperty("user.dir") + "/app/resources/"
  private val db = dbapi.database("default")

  def obtenerCreditos(
      estado: Int,
      dias_ini: Int,
      dias_fin: Int,
      ases_id: Int
  ): Future[Iterable[ControlCobroVista]] = Future[Iterable[ControlCobroVista]] {
    var _lista = new mutable.ListBuffer[ControlCobroVista]()
    val creditos = db.withConnection { implicit connection =>
      var query =
        """SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'D' AS tipo FROM \"col$colocacion\" a
              LEFT JOIN COLOCACIONASESOR r ON r.ID_COLOCACION = a.ID_COLOCACION
              WHERE a.ID_ESTADO_COLOCACION IN (0,1,2,3,7,8,9)
            """
      if (ases_id > 0) {
        query += " and r.ASES_ID = {ases_id}"
      }
      var _sql = SQL(query).on()
      if (ases_id > 0) {
        _sql = _sql.on("ases_id" -> ases_id)
      }
      _sql.as(Credito._set *)
    }
    // Debo verificar el filtro de busqueda
    db.withConnection { implicit connection =>
      for (c <- creditos) {
        val _parsePersona = str("NOMBRE") ~ get[Option[String]]("EMAIL") map {
          case a ~ b => (a, b)
        }
        val _persona =
          SQL(
            """SELECT (p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO) AS NOMBRE, p.EMAIL FROM "gen$persona" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona} """
          ).on(
              'id_identificacion -> c._1._3.get,
              'id_persona -> c._1._4.get
            )
            .as(_parsePersona.singleOpt)

        val centro_costo = SQL(
          """SELECT DESCRIPCION_AGENCIA FROM "gen$agencia" WHERE ID_AGENCIA = {id_agencia}"""
        ).on(
            'id_agencia -> c._1._1.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val estadoDesc = SQL(
          """SELECT DESCRIPCION_ESTADO_COLOCACION FROM "col$estado" WHERE ID_ESTADO_COLOCACION = {id_estado_colocacion}"""
        ).on(
            'id_estado_colocacion -> c._6._5.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val tipo_cuota = SQL(
          """SELECT DESCRIPCION_TIPO_CUOTA FROM "col$tiposcuota" WHERE ID_TIPOS_CUOTA = {id_tipos_cuota}"""
        ).on(
            'id_tipos_cuota -> c._5._1.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val numero_cuotas = SQL(
          """SELECT COUNT(*) FROM "col$tablaliquidacion" t WHERE t.ID_COLOCACION = {id_colocacion} and t.PAGADA = 0"""
        ).on(
            'id_colocacion -> c._1._2.get
          )
          .as(SqlParser.scalar[Int].singleOpt)

        var valor = c._3._3.get
        var abono = c._6._2.get
        var deuda = valor - abono
        var tasa_nominal = _f.tasaNominalVencida(c._4._3.get, c._5._3.get)

        var dias_mora = 0
        if (deuda > 0) {
          dias_mora = _gcol.obtenerDiasMora(c._1._2.get)
          if (dias_mora < -1) {
            dias_mora += 2
          }
        }
        val estCol = c._6._5.get
        estado match {
          case -1 =>
            if (dias_mora >= dias_ini && dias_mora <= dias_fin) {
              var _cc = new ControlCobroVista(
                c._1._1,
                c._1._2,
                _persona match {
                  case Some(v) => Some(v._1)
                  case None    => None
                },
                Some(valor),
                Some(deuda),
                c._6._1,
                c._3._4,
                c._5._2,
                c._5._3,
                Some(tasa_nominal),
                numero_cuotas,
                c._6._3,
                c._6._4,
                estadoDesc,
                Some(dias_mora),
                c._1._3,
                c._1._4,
                tipo_cuota,
                Some(""),
                centro_costo,
                _persona match {
                  case Some(v) =>
                    v._2 match { case Some(v) => Some(v); case None => None }
                  case None => None
                }
              )
              _lista += _cc
            }
          case estCol =>
            if (dias_mora >= dias_ini && dias_mora <= dias_fin) {
              var _cc = new ControlCobroVista(
                c._1._1,
                c._1._2,
                _persona match {
                  case Some(v) => Some(v._1)
                  case None    => None
                },
                Some(valor),
                Some(deuda),
                c._6._1,
                c._3._4,
                c._5._2,
                c._5._3,
                Some(tasa_nominal),
                numero_cuotas,
                c._6._3,
                c._6._4,
                estadoDesc,
                Some(dias_mora),
                c._1._3,
                c._1._4,
                tipo_cuota,
                Some(""),
                centro_costo,
                _persona match {
                  case Some(v) =>
                    v._2 match { case Some(v) => Some(v); case None => None }
                  case None => None
                }
              )
              _lista += _cc
            }
        }
      }
    }
    _lista.toList
  }

  def obtenerCreditoPorColocacion(
      id_colocacion: String
  ): Future[Iterable[ControlCobroVista]] = Future[Iterable[ControlCobroVista]] {
    var _lista = new mutable.ListBuffer[ControlCobroVista]()
    val creditos = db.withConnection { implicit connection =>
      SQL("""SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'D' AS tipo FROM \"col$colocacion\" a
             WHERE a.ID_COLOCACION = {id_colocacion} and a.ID_ESTADO_COLOCACION IN (0,1,2,3,7,8,9)
            """)
        .on(
          'id_colocacion -> id_colocacion
        )
        .as(Credito._set *)
    }
    // Debo verificar el filtro de busqueda
    db.withConnection { implicit connection =>
      for (c <- creditos) {
        val _parsePersona = str("NOMBRE") ~ get[Option[String]]("EMAIL") map {
          case a ~ b => (a, b)
        }
        val _persona = SQL(
          """SELECT (p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO) AS NOMBRE, p.EMAIL FROM "gen$persona" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona} """
        ).on(
            'id_identificacion -> c._1._3.get,
            'id_persona -> c._1._4.get
          )
          .as(_parsePersona.singleOpt)

        val centro_costo = SQL(
          """SELECT DESCRIPCION_AGENCIA FROM "gen$agencia" WHERE ID_AGENCIA = {id_agencia}"""
        ).on(
            'id_agencia -> c._1._1.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val estadoDesc = SQL(
          """SELECT DESCRIPCION_ESTADO_COLOCACION FROM "col$estado" WHERE ID_ESTADO_COLOCACION = {id_estado_colocacion}"""
        ).on(
            'id_estado_colocacion -> c._6._5.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val tipo_cuota = SQL(
          """SELECT DESCRIPCION_TIPO_CUOTA FROM "col$tiposcuota" WHERE ID_TIPOS_CUOTA = {id_tipos_cuota}"""
        ).on(
            'id_tipos_cuota -> c._5._1.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val numero_cuotas = SQL(
          """SELECT COUNT(*) FROM "col$tablaliquidacion" t WHERE t.ID_COLOCACION = {id_colocacion} and t.PAGADA = 0"""
        ).on(
            'id_colocacion -> c._1._2.get
          )
          .as(SqlParser.scalar[Int].singleOpt)

        var valor = c._3._3.get
        var abono = c._6._2.get
        var deuda = valor - abono
        var tasa_nominal = _f.tasaNominalVencida(c._4._3.get, c._5._3.get)

        var dias_mora = 0
        if (deuda > 0) {
          dias_mora = _gcol.obtenerDiasMora(c._1._2.get)
          if (dias_mora < -1) {
            dias_mora += 2
          }
        }
        var _cc = new ControlCobroVista(
          c._1._1,
          c._1._2,
          _persona match {
            case Some(v) => Some(v._1)
            case None    => None
          },
          Some(valor),
          Some(deuda),
          c._6._1,
          c._3._4,
          c._5._2,
          c._5._3,
          Some(tasa_nominal),
          numero_cuotas,
          c._6._3,
          c._6._4,
          estadoDesc,
          Some(dias_mora),
          c._1._3,
          c._1._4,
          tipo_cuota,
          Some(""),
          centro_costo,
          _persona match {
            case Some(v) =>
              v._2 match { case Some(v) => Some(v); case None => None }
            case None => None
          }
        )
        _lista += _cc
      }
    }
    _lista.toList
  }

  def obtenerCreditos(
      id_identificacion: Int,
      id_persona: String
  ): Future[Iterable[ControlCobroVista]] = Future[Iterable[ControlCobroVista]] {
    var _lista = new mutable.ListBuffer[ControlCobroVista]()
    val creditos = db.withConnection { implicit connection =>
      SQL("""SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'D' AS tipo FROM \"col$colocacion\" a
             WHERE a.ID_IDENTIFICACION = {id_identificacion} and a.ID_PERSONA = {id_persona} and a.ID_ESTADO_COLOCACION IN (0,1,2,3,7,8,9)
             UNION ALL
             SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'F' AS tipo FROM \"col$colocacion\" a
             LEFT JOIN \"col$colgarantias\" g ON g.ID_COLOCACION = a.ID_COLOCACION WHERE g.ID_IDENTIFICACION = {id_identificacion} and g.ID_PERSONA = {id_persona} and a.ID_ESTADO_COLOCACION IN (0,1,2,3,7,8,9)
            """)
        .on(
          'id_identificacion -> id_identificacion,
          'id_persona -> id_persona
        )
        .as(Credito._set *)
    }
    // Debo verificar el filtro de busqueda
    db.withConnection { implicit connection =>
      for (c <- creditos) {
        val _parsePersona = str("NOMBRE") ~ get[Option[String]]("EMAIL") map {
          case nombre ~ email => (nombre, email)
        }
        val _persona = SQL(
          """SELECT (p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO) AS NOMBRE, P.EMAIL FROM "gen$persona" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona} """
        ).on(
            'id_identificacion -> c._1._3.get,
            'id_persona -> c._1._4.get
          )
          .as(_parsePersona.singleOpt)

        val centro_costo = SQL(
          """SELECT DESCRIPCION_AGENCIA FROM "gen$agencia" WHERE ID_AGENCIA = {id_agencia}"""
        ).on(
            'id_agencia -> c._1._1.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val estadoDesc = SQL(
          """SELECT DESCRIPCION_ESTADO_COLOCACION FROM "col$estado" WHERE ID_ESTADO_COLOCACION = {id_estado_colocacion}"""
        ).on(
            'id_estado_colocacion -> c._6._5.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val tipo_cuota = SQL(
          """SELECT DESCRIPCION_TIPO_CUOTA FROM "col$tiposcuota" WHERE ID_TIPOS_CUOTA = {id_tipos_cuota}"""
        ).on(
            'id_tipos_cuota -> c._5._1.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val numero_cuotas = SQL(
          """SELECT COUNT(*) FROM "col$tablaliquidacion" t WHERE t.ID_COLOCACION = {id_colocacion} and t.PAGADA = 0"""
        ).on(
            'id_colocacion -> c._1._2.get
          )
          .as(SqlParser.scalar[Int].singleOpt)

        var valor = c._3._3.get
        var abono = c._6._2.get
        var deuda = valor - abono
        var tasa_nominal = _f.tasaNominalVencida(c._4._3.get, c._5._3.get)

        var dias_mora = 0
        if (deuda > 0) {
          dias_mora = _gcol.obtenerDiasMora(c._1._2.get)
          if (dias_mora < -1) {
            dias_mora += 2
          }
        }
        var _cc = new ControlCobroVista(
          c._1._1,
          c._1._2,
          _persona match {
            case Some(v) => Some(v._1)
            case None    => None
          },
          Some(valor),
          Some(deuda),
          c._6._1,
          c._3._4,
          c._5._2,
          c._5._3,
          Some(tasa_nominal),
          numero_cuotas,
          c._6._3,
          c._6._4,
          estadoDesc,
          Some(dias_mora),
          c._1._3,
          c._1._4,
          tipo_cuota,
          Some(""),
          centro_costo,
          _persona match {
            case Some(v) =>
              v._2 match { case Some(v) => Some(v); case None => None }
            case None => None
          }
        )
        _lista += _cc
      }
    }
    _lista.toList
  }

  def obtenerCartera(
      id_identificacion: Int,
      id_persona: String
  ): Future[Iterable[ControlCobroVista]] = Future[Iterable[ControlCobroVista]] {
    var _lista = new mutable.ListBuffer[ControlCobroVista]()
    val creditos = db.withConnection { implicit connection =>
      SQL("""SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'D' AS tipo FROM \"col$colocacion\" a
             WHERE a.ID_IDENTIFICACION = {id_identificacion} and a.ID_PERSONA = {id_persona} and a.ID_ESTADO_COLOCACION IN (0,1,2,3)
            """)
        .on(
          'id_identificacion -> id_identificacion,
          'id_persona -> id_persona
        )
        .as(Credito._set *)
    }
    // Debo verificar el filtro de busqueda
    db.withConnection { implicit connection =>
      for (c <- creditos) {
        val _parsePersona = str("NOMBRE") ~ get[Option[String]]("EMAIL") map {
          case nombre ~ email => (nombre, email)
        }
        val _persona = SQL(
          """SELECT (p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO) AS NOMBRE, p.EMAIL FROM "gen$persona" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona} """
        ).on(
            'id_identificacion -> c._1._3.get,
            'id_persona -> c._1._4.get
          )
          .as(_parsePersona.singleOpt)

        val centro_costo = SQL(
          """SELECT DESCRIPCION_AGENCIA FROM "gen$agencia" WHERE ID_AGENCIA = {id_agencia}"""
        ).on(
            'id_agencia -> c._1._1.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val estadoDesc = SQL(
          """SELECT DESCRIPCION_ESTADO_COLOCACION FROM "col$estado" WHERE ID_ESTADO_COLOCACION = {id_estado_colocacion}"""
        ).on(
            'id_estado_colocacion -> c._6._5.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val tipo_cuota = SQL(
          """SELECT DESCRIPCION_TIPO_CUOTA FROM "col$tiposcuota" WHERE ID_TIPOS_CUOTA = {id_tipos_cuota}"""
        ).on(
            'id_tipos_cuota -> c._5._1.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val numero_cuotas = SQL(
          """SELECT COUNT(*) FROM "col$tablaliquidacion" t WHERE t.ID_COLOCACION = {id_colocacion} and t.PAGADA = 0"""
        ).on(
            'id_colocacion -> c._1._2.get
          )
          .as(SqlParser.scalar[Int].singleOpt)

        var valor = c._3._3.get
        var abono = c._6._2.get
        var deuda = valor - abono
        var tasa_nominal = _f.tasaNominalVencida(c._4._3.get, c._5._3.get)

        var dias_mora = 0
        if (deuda > 0) {
          dias_mora = _gcol.obtenerDiasMora(c._1._2.get)
          if (dias_mora < -1) {
            dias_mora += 2
          }
        }
        if (dias_mora < 0) {
          dias_mora = 0
        }
        var _cc = new ControlCobroVista(
          c._1._1,
          c._1._2,
          _persona match {
            case Some(v) => Some(v._1)
            case None    => None
          },
          Some(valor),
          Some(deuda),
          c._6._1,
          c._3._4,
          c._5._2,
          c._5._3,
          Some(tasa_nominal),
          numero_cuotas,
          c._6._3,
          c._6._4,
          estadoDesc,
          Some(dias_mora),
          c._1._3,
          c._1._4,
          tipo_cuota,
          Some(""),
          centro_costo,
          _persona match {
            case Some(v) =>
              v._2 match { case Some(v) => Some(v); case None => None }
            case None => None
          }
        )
        _lista += _cc
      }
    }
    _lista.toList
  }

  def obtenerHistoria(
      id_identificacion: Int,
      id_persona: String
  ): Future[Iterable[ControlCobroVista]] = Future[Iterable[ControlCobroVista]] {
    var _lista = new mutable.ListBuffer[ControlCobroVista]()
    val creditos = db.withConnection { implicit connection =>
      SQL("""SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'D' AS tipo FROM \"col$colocacion\" a
             WHERE a.ID_IDENTIFICACION = {id_identificacion} and a.ID_PERSONA = {id_persona} and a.ID_ESTADO_COLOCACION IN (6,7)
            """)
        .on(
          'id_identificacion -> id_identificacion,
          'id_persona -> id_persona
        )
        .as(Credito._set *)
    }
    // Debo verificar el filtro de busqueda
    db.withConnection { implicit connection =>
      for (c <- creditos) {
        val _parsePersona = str("NOMBRE") ~ get[Option[String]]("EMAIL")
        val _persona = SQL(
          """SELECT (p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO) AS NOMBRE, p.EMAIL FROM "gen$persona" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona} """
        ).on(
            'id_identificacion -> c._1._3.get,
            'id_persona -> c._1._4.get
          )
          .as(_parsePersona.singleOpt)

        val centro_costo = SQL(
          """SELECT DESCRIPCION_AGENCIA FROM "gen$agencia" WHERE ID_AGENCIA = {id_agencia}"""
        ).on(
            'id_agencia -> c._1._1.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val estadoDesc = SQL(
          """SELECT DESCRIPCION_ESTADO_COLOCACION FROM "col$estado" WHERE ID_ESTADO_COLOCACION = {id_estado_colocacion}"""
        ).on(
            'id_estado_colocacion -> c._6._5.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val tipo_cuota = SQL(
          """SELECT DESCRIPCION_TIPO_CUOTA FROM "col$tiposcuota" WHERE ID_TIPOS_CUOTA = {id_tipos_cuota}"""
        ).on(
            'id_tipos_cuota -> c._5._1.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val numero_cuotas = SQL(
          """SELECT COUNT(*) FROM "col$tablaliquidacion" t WHERE t.ID_COLOCACION = {id_colocacion} and t.PAGADA = 0"""
        ).on(
            'id_colocacion -> c._1._2.get
          )
          .as(SqlParser.scalar[Int].singleOpt)

        var valor = c._3._3.get
        var abono = c._6._2.get
        var deuda = valor - abono
        var tasa_nominal = _f.tasaNominalVencida(c._4._3.get, c._5._3.get)

        var dias_mora = 0
        if (deuda > 0) {
          dias_mora = _gcol.obtenerDiasMora(c._1._2.get)
          if (dias_mora < -1) {
            dias_mora += 2
          }
        }
        var _cc = new ControlCobroVista(
          c._1._1,
          c._1._2,
          _persona match {
            case Some(v) => Some(v._1)
            case None    => None
          },
          Some(valor),
          Some(deuda),
          c._6._1,
          c._3._4,
          c._5._2,
          c._5._3,
          Some(tasa_nominal),
          numero_cuotas,
          c._6._3,
          c._6._4,
          estadoDesc,
          Some(dias_mora),
          c._1._3,
          c._1._4,
          tipo_cuota,
          Some(""),
          centro_costo,
          _persona match {
            case Some(v) =>
              v._2 match { case Some(v) => Some(v); case None => None }
            case None => None
          }
        )
        _lista += _cc
      }
    }
    _lista.toList
  }

  def obtenerFianza(
      id_identificacion: Int,
      id_persona: String
  ): Future[Iterable[ControlCobroVista]] = Future[Iterable[ControlCobroVista]] {
    var _lista = new mutable.ListBuffer[ControlCobroVista]()
    val creditos = db.withConnection { implicit connection =>
      SQL("""
             SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'F' AS tipo FROM \"col$colocacion\" a
             LEFT JOIN \"col$colgarantias\" g ON g.ID_COLOCACION = a.ID_COLOCACION WHERE g.ID_IDENTIFICACION = {id_identificacion} and g.ID_PERSONA = {id_persona} and a.ID_ESTADO_COLOCACION IN (0,1,2,3)
            """)
        .on(
          'id_identificacion -> id_identificacion,
          'id_persona -> id_persona
        )
        .as(Credito._set *)
    }
    // Debo verificar el filtro de busqueda
    db.withConnection { implicit connection =>
      for (c <- creditos) {
        val _parsePersona = str("NOMBRE") ~ get[Option[String]]("EMAIL") map {
          case nombre ~ email => (nombre, email)
        }
        val _persona = SQL(
          """SELECT (p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO) AS NOMBRE, p.EMAIL FROM "gen$persona" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona} """
        ).on(
            'id_identificacion -> c._1._3.get,
            'id_persona -> c._1._4.get
          )
          .as(_parsePersona.singleOpt)

        val centro_costo = SQL(
          """SELECT DESCRIPCION_AGENCIA FROM "gen$agencia" WHERE ID_AGENCIA = {id_agencia}"""
        ).on(
            'id_agencia -> c._1._1.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val estadoDesc = SQL(
          """SELECT DESCRIPCION_ESTADO_COLOCACION FROM "col$estado" WHERE ID_ESTADO_COLOCACION = {id_estado_colocacion}"""
        ).on(
            'id_estado_colocacion -> c._6._5.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val tipo_cuota = SQL(
          """SELECT DESCRIPCION_TIPO_CUOTA FROM "col$tiposcuota" WHERE ID_TIPOS_CUOTA = {id_tipos_cuota}"""
        ).on(
            'id_tipos_cuota -> c._5._1.get
          )
          .as(SqlParser.scalar[String].singleOpt)

        val numero_cuotas = SQL(
          """SELECT COUNT(*) FROM "col$tablaliquidacion" t WHERE t.ID_COLOCACION = {id_colocacion} and t.PAGADA = 0"""
        ).on(
            'id_colocacion -> c._1._2.get
          )
          .as(SqlParser.scalar[Int].singleOpt)

        var valor = c._3._3.get
        var abono = c._6._2.get
        var deuda = valor - abono
        var tasa_nominal = _f.tasaNominalVencida(c._4._3.get, c._5._3.get)

        var dias_mora = 0
        if (deuda > 0) {
          dias_mora = _gcol.obtenerDiasMora(c._1._2.get)
          if (dias_mora < -1) {
            dias_mora += 2
          }
        }
        var _cc = new ControlCobroVista(
          c._1._1,
          c._1._2,
          _persona match {
            case Some(v) => Some(v._1)
            case None    => None
          },
          Some(valor),
          Some(deuda),
          c._6._1,
          c._3._4,
          c._5._2,
          c._5._3,
          Some(tasa_nominal),
          numero_cuotas,
          c._6._3,
          c._6._4,
          estadoDesc,
          Some(dias_mora),
          c._1._3,
          c._1._4,
          tipo_cuota,
          Some(""),
          centro_costo,
          _persona match {
            case Some(v) =>
              v._2 match { case Some(v) => Some(v); case None => None }
            case None => None
          }
        )
        _lista += _cc
      }
    }
    _lista.toList
  }

  def obtenerDireccion(
      id_identificacion: Int,
      id_persona: String
  ): Future[Iterable[Direccion]] = Future[Iterable[Direccion]] {
    val direcciones = db.withConnection { implicit connection =>
      SQL(
        """SELECT d.consecutivo, d.id_direccion, d.direccion, d.barrio, d.cod_municipio, m.NOMBRE AS municipio, d.telefono1, d.telefono2, d.telefono3, d.telefono4 FROM "gen$direccion" d 
             INNER JOIN "gen$municipios" m ON m.COD_MUNICIPIO = d.COD_MUNICIPIO
             WHERE d.ID_IDENTIFICACION = {id_identificacion} AND d.ID_PERSONA = {id_persona}
             ORDER BY d.ID_DIRECCION ASC"""
      ).on(
          'id_identificacion -> id_identificacion,
          'id_persona -> id_persona
        )
        .as(Direccion._set *)
    }
    direcciones
  }

  def obtenerControlCobro(
      id_colocacion: String
  ): Future[Iterable[ControlCobro]] = Future[Iterable[ControlCobro]] {
    val lista = db.withConnection { implicit connection =>
      SQL(
        """SELECT c.ID_AGENCIA,c.ID_COLOCACION,c.FECHA_OBSERVACION,c.OBSERVACION, c.ES_OBSERVACION,c.ES_COMPROMISO, c.FECHA_COMPROMISO, c.ID_USUARIO, e.NOMBRE || ' ' || e.PRIMER_APELLIDO || ' ' || e.SEGUNDO_APELLIDO AS EMPLEADO from "col$controlcobro" c
             INNER JOIN "gen$empleado" e ON (e.ID_EMPLEADO = c.ID_USUARIO)
             WHERE c.ID_COLOCACION = {id_colocacion} ORDER BY FECHA_OBSERVACION DESC"""
      ).on(
          'id_colocacion -> id_colocacion
        )
        .as(ControlCobro._set *)
    }
    lista
  }

  def agregar(cc: ControlCobro, usua_id: Long): Future[Boolean] = {
    _uService.buscarPorId(usua_id).map { empleado =>
      var id_empleado: Option[String] = None
      empleado match {
        case Some(e) => id_empleado = e.id_empleado
        case None    => None
      }
      db.withConnection { implicit connection =>
        SQL("""INSERT INTO "col$controlcobro" VALUES (
          {id_agencia}, 
          {id_colocacion}, 
          {fecha_observacion}, 
          {observacion}, 
          {es_observacion}, 
          {es_compromiso}, 
          {fecha_compromiso}, 
          {id_empleado}
        )""")
          .on(
            'id_agencia -> cc.id_agencia,
            'id_colocacion -> cc.id_colocacion,
            'fecha_observacion -> cc.fecha_observacion,
            'observacion -> cc.observacion,
            'es_observacion -> cc.es_observacion,
            'es_compromiso -> cc.es_compromiso,
            'fecha_compromiso -> cc.fecha_compromiso,
            'id_empleado -> id_empleado
          )
          .executeUpdate() > 0
      }
    }
  }

  def getPazYSalvoData(id_colocacion: String) = Future {
    var _deudores = mutable.ArrayBuffer.empty[(String, String, String)]
    db.withConnection { implicit connection =>
      val _personaSql =
        """SELECT (p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO) AS NOMBRE,  p.ID_PERSONA AS DOCUMENTO, p.SEXO FROM "gen$persona" p WHERE p.ID_IDENTIFICACION = {id_identificacion} and p.ID_PERSONA = {id_persona}"""
      val _carteraSql =
        """SELECT ID_COLOCACION, ID_IDENTIFICACION, ID_PERSONA FROM "col$colocacion" WHERE ID_COLOCACION = {id_colocacion} AND ID_ESTADO_COLOCACION IN (6,7)"""
      val _garantiaSql =
        """SELECT ID_IDENTIFICACION, ID_PERSONA FROM "col$colgarantias" WHERE ID_COLOCACION = {id_colocacion}"""
      val _parsePersona = str("NOMBRE") ~ str("DOCUMENTO") ~ str("SEXO") map {
        case nombre ~ documento ~ sexo =>
          (nombre, documento, sexo)
      }
      val _parseCartera = str("ID_COLOCACION") ~ int("ID_IDENTIFICACION") ~ str(
        "ID_PERSONA"
      ) map {
        case id_colocacion ~ id_identificacion ~ id_persona =>
          (id_colocacion, id_identificacion, id_persona)
      }
      val _parseGarantia = int("ID_IDENTIFICACION") ~ str("ID_PERSONA") map {
        case id_identificacion ~ id_persona =>
          (id_identificacion, id_persona)
      }
      val _cartera =
        SQL(_carteraSql).on('id_colocacion -> id_colocacion).as(_parseCartera *)
      _cartera.map { _c =>
        val _persona =
          SQL(_personaSql)
            .on(
              'id_identificacion -> _c._2,
              'id_persona -> _c._3
            )
            .as(_parsePersona *)
        _persona.map { _p =>
          _deudores += ((_p._1, _p._2, "SOLICITANTE"))
        }
        val _garantia =
          SQL(_garantiaSql).on('id_colocacion -> _c._1).as(_parseGarantia *)
        _garantia.map { _g =>
          val persona = SQL(_personaSql)
            .on(
              'id_identificacion -> _g._1,
              'id_persona -> _g._2
            )
            .as(_parsePersona *)
          _persona.map { _p =>
            _deudores += ((_p._1, _p._2, (_p._3 match {
              case "F" => "CODEUDORA"
              case "M" => "CODEUDOR"
              case _   => "CODEUDOR"
            })))
          }
        }
      }
    }
    _deudores.toList
  }

  def cartaPrimerAviso(
      _data: (
          String,
          String,
          String,
          String,
          String,
          String,
          String,
          String,
          String,
          String
      )
  ) = {
    val CREDITO = _data._1
    val DEUDOR = _data._2
    val CODEUDOR = _data._3
    val DIRECCION = _data._4
    val MUNICIPIO = _data._5
    val BARRIO = _data._6
    val TELEFONO1 = _data._7
    val TELEFONO2 = _data._8
    val TIPO = _data._9
    val EMAIL = _data._10

    var params = new HashMap[String, java.lang.Object]()
    params.put("CREDITO", CREDITO)
    params.put("DEUDOR", DEUDOR)
    params.put("CODEUDOR", CODEUDOR)
    params.put("DIRECCION", DIRECCION)
    params.put("BARRIO", BARRIO)
    params.put("TELEFONO", TELEFONO1)
    params.put("CIUDAD", MUNICIPIO)
    params.put("CONCOPIA", TIPO match {
      case "DEUDOR" => "c.c. Codeudor"
      case _        => ""
    })
    params.put("URL_LOGO", "https://fap.fundacionapoyo.com/image/logofull.png")
    params.put(
      "URL_FIRMA",
      "https://fap.fundcionapoyo.com/image/FirmaEliecer.png"
    )

    val _jasperPrint = JasperFillManager.fillReport(
      REPORT_DEFINITION_PATH + "FaP_Carta_Primer_Cobro.jasper",
      params,
      new JREmptyDataSource()
    )

    val _fileName = System.getProperty("java.io.tmpdir") + "/FaP_Carta_Primer_Cobro_" + CREDITO + ".html"
    REPORT_DEFINITION_PATH + "FaP_Carta_Primer_Cobro.pdf"
    JasperExportManager.exportReportToHtmlFile(
      _jasperPrint,
      _fileName
    )
    val _output = new ByteArrayOutputStream()
    val _file = new FileInputStream(new File(_fileName))
    _file.transferTo(_output)

    val _html = _output.toString("UTF-8")

    EmailSender.sendHtml(EMAIL, "Notificación de Reporte a Centrales", _html)

    true
  }
}
