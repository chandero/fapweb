package models

import javax.inject.Inject
import java.util.Calendar

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

import play.api.db.DBApi

import anorm._
import anorm.SqlParser.{get, str, int, date, float, double}
import anorm.JodaParameterMetaData._

import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}
import scala.collection.mutable.ListBuffer
import scala.math.BigDecimal

import org.joda.time.DateTime
import org.joda.time.LocalDate
import scala.collection.mutable
import utilities._
import java.io.ByteArrayOutputStream

// Excel Export
import com.norbitltd.spoiwo.model._
import com.norbitltd.spoiwo.model.enums.CellStyleInheritance
import com.norbitltd.spoiwo.natures.xlsx.Model2XlsxConversions._
import com.norbitltd.spoiwo.model.enums.{
  CellBorderStyle,
  CellFill,
  Pane,
  CellHorizontalAlignment => HA,
  CellVerticalAlignment => VA
}
import Height._
import org.apache.poi.common.usermodel.HyperlinkType
import org.apache.poi.ss.usermodel.CellType

// Jasper
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperRunManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JRExporter
import net.sf.jasperreports.engine.JRExporterParameter
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration
import org.joda.time.format.DateTimeFormat
import java.text.SimpleDateFormat
import java.util.Date

class BancolombiaInformeRepository @Inject()(dbapi: DBApi, _gcon: GlobalesCon, _convert: Conversion, empresaService: EmpresaRepository)(
    implicit ec: DatabaseExecutionContext) {
  private val db = dbapi.database("default")

  def consultar(): Array[Byte] = {
    db.withConnection { implicit connection =>
        val _parse = str("identificacion") ~ str("nombre") ~ str("referencia1") ~ date("fecha_vencimiento") map {
            case id ~ nombre ~ ref1 ~ fecha_vencimiento =>
              (id, nombre, ref1, fecha_vencimiento)
        }
        val sql = """SELECT 
	                IIF(POSITION('-' IN cc.ID_PERSONA) = 0, cc.ID_PERSONA, (SUBSTRING(cc.ID_PERSONA FROM 1 FOR (POSITION('-' IN cc.ID_PERSONA)-1)))) AS IDENTIFICACION, 
	                TRIM(IIF(POSITION(' ' IN gp.NOMBRE) = 0,gp.NOMBRE,(SUBSTRING(gp.NOMBRE FROM 1 FOR (POSITION(' ' IN gp.NOMBRE))))) || ' ' || gp.PRIMER_APELLIDO) AS NOMBRE,
	                LPAD(IIF(POSITION('-' IN cc.ID_PERSONA) = 0, cc.ID_PERSONA, (SUBSTRING(cc.ID_PERSONA FROM 1 FOR (POSITION('-' IN cc.ID_PERSONA)-1)))), 13, '0') AS REFERENCIA1, 
	                DATEADD(360 DAY TO CURRENT_DATE) AS FECHA_VENCIMIENTO
	                FROM "col$colocacion"  cc
                    INNER JOIN "gen$persona" gp ON gp.ID_IDENTIFICACION = cc.ID_IDENTIFICACION AND gp.ID_PERSONA = cc.ID_PERSONA 
                    WHERE cc.ID_ESTADO_COLOCACION IN (0,1,2)"""
        val result = SQL(sql).as(_parse *)
        var _listRow01 = new ListBuffer[com.norbitltd.spoiwo.model.Row]()
        val fmt = DateTimeFormat.forPattern("yyyy-MM-dd")
        val _sheet01 = Sheet(
          name = "Informe Bancolombia",
          rows = {
                _listRow01 += com.norbitltd.spoiwo.model.Row(
                        StringCell(
                            value = "Identificacion",
                            index = Some(0),
                            style = Some(CellStyle(
                                font = Font(bold = true))),
                                CellStyleInheritance.CellThenRowThenColumnThenSheet),
                        StringCell(
                            value = "Nombre",
                            index = Some(1),
                            style = Some(CellStyle(
                                font = Font(bold = true))),
                                CellStyleInheritance.CellThenRowThenColumnThenSheet),
                        StringCell(
                            value = "Referencia1",
                            index = Some(2),
                            style = Some(CellStyle(
                                font = Font(bold = true))),
                                CellStyleInheritance.CellThenRowThenColumnThenSheet),
                        StringCell(
                            value = "Fecha Vencimiento",
                            index = Some(3),
                            style = Some(CellStyle(
                                font = Font(bold = true))),
                            CellStyleInheritance.CellThenRowThenColumnThenSheet)
                )
                result.map { _item =>
                    _listRow01 += com.norbitltd.spoiwo.model.Row(
                        StringCell(
                            value = _item._1,
                            index = Some(0),
                            style = Some(CellStyle(
                                dataFormat = CellDataFormat("@"))),
                            styleInheritance = CellStyleInheritance.CellThenRowThenColumnThenSheet
                        ),
                        StringCell(
                            value = _item._2,
                            index = Some(1),
                            style = Some(CellStyle(
                                dataFormat = CellDataFormat("@"))),
                            styleInheritance = CellStyleInheritance.CellThenRowThenColumnThenSheet),
                        StringCell(
                            value = _item._3,
                            index = Some(2),
                            style = Some(CellStyle(
                               dataFormat = CellDataFormat("@"))),
                            styleInheritance = CellStyleInheritance.CellThenRowThenColumnThenSheet),
                        StringCell(
                            value = fmt.print(_item._4.getTime()),
                            index = Some(3),
                            style = Some(CellStyle(
                                dataFormat = CellDataFormat("@"))),
                            styleInheritance = CellStyleInheritance.CellThenRowThenColumnThenSheet)
                    )
                }
                _listRow01.toList
            }
        )
        println("Sheet:" + _sheet01.name)
        var os: ByteArrayOutputStream = new ByteArrayOutputStream()
        Workbook(_sheet01).writeToOutputStream(os)
        os.toByteArray 
    }
  }
}