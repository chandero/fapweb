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
}