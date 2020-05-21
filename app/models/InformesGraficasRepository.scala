package models

import java.nio.file.{Files, Paths}
import javax.inject.Inject
import java.util.Calendar
import java.sql.Timestamp
import java.security.MessageDigest
import java.util.{ Map, HashMap, Date }
import java.io.ByteArrayOutputStream

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import scala.util.{ Failure, Success }
import anorm._
import anorm.SqlParser.{ get, str, int, date, flatten }
import anorm.JodaParameterMetaData._
import play.api.db.DBApi
import scala.concurrent.Future
import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._
import play.api.Configuration
import notifiers._
import scala.collection.mutable.ListBuffer

import java.util.UUID.randomUUID

import utilities._

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

// Joda
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat

// Excel Export
import com.norbitltd.spoiwo.model._
import com.norbitltd.spoiwo.model.enums.CellStyleInheritance
import com.norbitltd.spoiwo.natures.xlsx.Model2XlsxConversions._
import com.norbitltd.spoiwo.model.enums.{CellBorderStyle, CellFill, Pane, CellHorizontalAlignment => HA, CellVerticalAlignment => VA}

// DataSource
import listeners.JrListener

case class InformeRecaudoMes(
    anho: Option[Int],
    mes:  Option[Int],
    dia:  Option[Int],
    codigo: Option[String],
    cuenta: Option[String],
    recaudo: Option[BigDecimal]
)

object InformeRecaudoMes {
  implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")
  
  implicit val writes = new Writes[InformeRecaudoMes] {
    def writes(e: InformeRecaudoMes) = Json.obj(
      "anho" -> e.anho,
      "mes" -> e.mes,
      "dia" -> e.dia,
      "codigo" -> e.codigo,
      "cuenta" -> e.cuenta,
      "recaudo" -> e.recaudo
    )
  }

  val _set = {
    get[Option[Int]]("anho") ~
    get[Option[Int]]("mes") ~
    get[Option[Int]]("dia") ~
    get[Option[String]]("codigo") ~
    get[Option[String]]("cuenta") ~
    get[Option[BigDecimal]]("recaudo") map {
      case anho ~
           mes ~
           dia ~
           codigo ~
           cuenta ~
           recaudo => new InformeRecaudoMes(
                anho,
                mes,
                dia,
                codigo,
                cuenta,
                recaudo               
           )
    }
  }    
}

case class InformeRecaudoInteresCausado(
    id_colocacion: Option[String],
    id_persona: Option[String],
    nombre: Option[String],
    fecha_recaudo: Option[DateTime],
    codigo: Option[String],
    cuenta: Option[String],
    fecha_inicial: Option[DateTime],
    fecha_final: Option[DateTime],
    interes: Option[BigDecimal],
    dias: Option[Int]
)

object InformeRecaudoInteresCausado {
  implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")
  
  implicit val writes = new Writes[InformeRecaudoInteresCausado] {
    def writes(e: InformeRecaudoInteresCausado) = Json.obj(
      "id_colocacion" -> e.id_colocacion,
      "id_persona" -> e.id_persona,
      "nombre" -> e.nombre,
      "fecha_recaudo" -> e.fecha_recaudo,
      "codigo" -> e.codigo,
      "cuenta" -> e.cuenta,
      "fecha_inicial" -> e.fecha_inicial,
      "fecha_final" -> e.fecha_final,
      "interes" -> e.interes,
      "dias" -> e.dias
    )
  }

  val _set = {
    get[Option[String]]("id_colocacion") ~
    get[Option[String]]("id_persona") ~
    get[Option[String]]("nombre") ~
    get[Option[DateTime]]("fecha_recaudo") ~
    get[Option[String]]("codigo") ~
    get[Option[String]]("cuenta") ~
    get[Option[DateTime]]("fecha_inicial") ~
    get[Option[DateTime]]("fecha_final") ~
    get[Option[BigDecimal]]("interes") ~
    get[Option[Int]]("dias") map {
      case     
        id_colocacion ~
        id_persona ~
        nombre ~
        fecha_recaudo ~
        codigo ~
        cuenta ~
        fecha_inicial ~
        fecha_final ~
        interes ~
        dias => new InformeRecaudoInteresCausado(
                id_colocacion,
                id_persona,
                nombre,
                fecha_recaudo,
                codigo,
                cuenta,
                fecha_inicial,
                fecha_final,
                interes,
                dias              
           )
    }
  }    
}

class InformesGraficasRepository @Inject()(dbapi: DBApi, config: Configuration)(implicit ec: DatabaseExecutionContext) {
    // private val db = dbapi.database("default")
    private val REPORT_DEFINITION_PATH = System.getProperty("user.dir") + "/app/resources/"
    private val db = dbapi.database("default")

    def recaudoDiarioMes(anho: Int, mes: Int):Future[Iterable[InformeRecaudoMes]] = Future[Iterable[InformeRecaudoMes]] {
        db.withConnection { implicit connection =>
            SQL(""" SELECT EXTRACT(YEAR FROM e.FECHA_EXTRACTO) AS ANHO, EXTRACT(MONTH FROM e.FECHA_EXTRACTO) AS MES, EXTRACT(DAY FROM e.FECHA_EXTRACTO) AS DIA, e.CODIGO_PUC AS CODIGO, p.NOMBRE AS CUENTA, SUM(e.VALOR_DEBITO) AS RECAUDO FROM "col$extractodet" e
                LEFT JOIN "con$puc" p ON p.CODIGO = e.CODIGO_PUC
                WHERE EXTRACT(MONTH FROM e.FECHA_EXTRACTO) = {mes} AND EXTRACT(YEAR FROM e.FECHA_EXTRACTO) = {anho} AND e.CODIGO_PUC LIKE '11%'
                GROUP BY e.FECHA_EXTRACTO, e.CODIGO_PUC, p.NOMBRE
                ORDER BY e.FECHA_EXTRACTO, e.CODIGO_PUC  """).
                on(
                    'anho -> anho,
                    'mes -> mes
                ).as(InformeRecaudoMes._set *)
        }
    }

    def recaudoInteresCausadoPeriodoGracia(fecha_inicial: Long, fecha_final: Long): Future[Iterable[InformeRecaudoInteresCausado]] = Future[Iterable[InformeRecaudoInteresCausado]] {
      val fi = new DateTime(fecha_inicial)
      val ff = new DateTime(fecha_final)
      db.withConnection { implicit connection =>
        SQL(""" SELECT d.ID_COLOCACION, c.ID_PERSONA, r.NOMBRE || ' ' || r.PRIMER_APELLIDO || ' ' || r.SEGUNDO_APELLIDO AS NOMBRE, d.FECHA_EXTRACTO AS FECHA_RECAUDO, d.CODIGO_PUC AS CODIGO, p.NOMBRE AS CUENTA, MIN(d.FECHA_INICIAL) AS FECHA_INICIAL, MAX(d.FECHA_FINAL) AS FECHA_FINAL, SUM(d.VALOR_CREDITO) AS INTERES, SUM(d.DIAS_APLICADOS) AS DIAS FROM COL_PERIODO_GRACIA g
                INNER JOIN "col$extractodet" d ON d.ID_COLOCACION = g.ID_COLOCACION
                INNER JOIN "con$puc" p ON p.CODIGO = d.CODIGO_PUC
                INNER JOIN "col$colocacion" c ON c.ID_COLOCACION = d.ID_COLOCACION
                INNER JOIN "gen$persona" r ON r.ID_IDENTIFICACION = c.ID_IDENTIFICACION AND r.ID_PERSONA = c.ID_PERSONA
                WHERE d.FECHA_EXTRACTO BETWEEN {fecha_inicial} AND {fecha_final} AND g.ESTADO = 0 AND d.CODIGO_PUC LIKE '1345%' AND g.FECHA_REGISTRO <= d.FECHA_EXTRACTO
                GROUP BY d.ID_COLOCACION, c.ID_PERSONA, r.NOMBRE, r.PRIMER_APELLIDO, r.SEGUNDO_APELLIDO, d.FECHA_EXTRACTO, d.CODIGO_PUC, p.NOMBRE
                ORDER BY d.ID_COLOCACION """).
                on(
                  'fecha_inicial -> fi,
                  'fecha_final -> ff
                ).as(InformeRecaudoInteresCausado._set *)
      }
    }
}