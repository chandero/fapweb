package models

import javax.inject.Inject
import java.util.Calendar

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

import play.api.db.DBApi

import anorm._
import anorm.SqlParser.{get, str, int, float, double}
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


case class Balance(
    codigo: Option[String],
    cuenta: Option[String],
    debito_ant: Option[Double],
    credito_ant: Option[Double],
    debito_mov: Option[Double],
    credito_mov: Option[Double],
    debito_act: Option[Double],
    credito_act: Option[Double]
)

object Balance {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  val _set = {
      get[Option[String]]("codigo") ~
      get[Option[String]]("cuenta") ~
      get[Option[Double]]("debito_ant") ~
      get[Option[Double]]("credito_ant") ~
      get[Option[Double]]("debito_mov") ~
      get[Option[Double]]("credito_mov") ~
      get[Option[Double]]("debito_act") ~
      get[Option[Double]]("credito_act") map {
          case 
            codigo ~
            cuenta ~
            debito_ant ~
            credito_ant ~
            debito_mov ~
            credito_mov ~
            debito_act ~
            credito_act => Balance(
                codigo,
                cuenta,
                debito_ant,
                credito_ant,
                debito_mov,
                credito_mov,
                debito_act,
                credito_act
            )
      }
  }
}

class BalanceRepository @Inject()(dbapi: DBApi, _gcon: GlobalesCon, _convert: Conversion, empresaService: EmpresaRepository)(
    implicit ec: DatabaseExecutionContext) {
  private val db = dbapi.database("default")

  def consultar(empr_id: Long, fecha_corte: Long, codigo_inicial: String, codigo_final: String, nivel: Int): Iterable[Balance] = {
    var _listResult = new ListBuffer[Balance]()
    var _listTabla00 = new ListBuffer[Balance]()
    var _listTabla01 = new ListBuffer[Balance]()
    val fc = new DateTime(fecha_corte)
    val periodo = fc.getMonthOfYear()
    val periodo_ant = periodo - 1
    var fisi = new LocalDate()
    var ffsi = new LocalDate()
    var fim = new LocalDate()
    var ffm = new LocalDate()
    if (periodo == 1) {
      fisi = new LocalDate(0,1,1)      
      ffsi = new LocalDate(0,1,1)
    } else {
      fisi = new LocalDate(fc.year().get(), 1, 1)
      ffsi = new LocalDate(fc.year().get(), periodo_ant, 1)
      ffsi = ffsi.dayOfMonth().withMaximumValue()
    }

    fim = new LocalDate(fc.year().get, periodo, 1)
    ffm = fim.dayOfMonth().withMaximumValue()

    println("fisi: " + fisi)
    println("ffsi: " + ffsi)


    println("fim: " + fim)
    println("ffm: " + ffm)

    val queryPuc = """SELECT
          cp1.*,
          ga1.*
          FROM "con$puc" cp1
          LEFT JOIN "gen$agencia" ga1 ON (ga1.ID_AGENCIA = cp1.ID_AGENCIA)
          WHERE
          cp1.NIVEL <= {nivel}
          AND (cp1.CODIGO >= {codigo_inicial} and cp1.CODIGO <= {codigo_final})
          ORDER BY cp1.CODIGO"""

    val querySaldo = """SELECT SUM(ca1.DEBITO) AS DEBITO, SUM(ca1.CREDITO) AS CREDITO FROM "con$comprobante" cc1
                        INNER JOIN "con$auxiliar" ca1 ON ca1.TIPO_COMPROBANTE = cc1.TIPO_COMPROBANTE and ca1.ID_COMPROBANTE = cc1.ID_COMPROBANTE
                        WHERE cc1.FECHADIA BETWEEN {fecha_inicial} and {fecha_final} and ca1.CODIGO LIKE {codigo}
                        AND cc1.ESTADO = {estado}"""

    var longitud = 0
     db.withTransaction { implicit connection =>
        val _resultSetPuc = SQL(queryPuc).
                          on(
                              'codigo_inicial -> codigo_inicial,
                              'codigo_final -> codigo_final,
                              'nivel -> nivel
                          ).as(Puc._set *)
        _resultSetPuc.map { puc =>
            puc.nivel match {
              case Some(nivel) => nivel match {
                case 1 => longitud = 1
                case 2 => longitud = 2
                case 3 => longitud = 4
                case 4 => longitud = 6
                case 5 => longitud = 8
                case 6 => longitud = 10
                case 7 => longitud = 12
                case 8 => longitud = 14
                case 9 => longitud = 16
                case 10 => longitud = 18
              }
              case None => longitud = 0
            }

            val codigo = puc.codigo.get.substring(0,longitud)
            var _debito_ant = 0D
            var _credito_ant = 0D
            if (periodo > 1) {
               val _saldoParse = get[Option[Double]]("debito") ~ get[Option[Double]]("credito") map { case a ~ b => (a,b) }
               val _resultSetSaldo = SQL(querySaldo).on(
                   'fecha_inicial -> fisi,
                   'fecha_final -> ffsi,
                   'codigo -> (codigo + "%"),
                   'estado -> "C"
               ).as(_saldoParse.single)
               _debito_ant = _resultSetSaldo._1 match {
                 case Some(v) => v
                 case None => 0D
               }
               _credito_ant = _resultSetSaldo._2 match {
                 case Some(v) => v
                 case None => 0D
               }
            } else {
                _debito_ant = 0D;
                _credito_ant = 0D;
            }

            val _saldo_anterior = puc.saldoinicial.get.doubleValue() + _debito_ant - _credito_ant
            if (_saldo_anterior > 0D) {
                _debito_ant = _saldo_anterior
                _credito_ant = 0D
            } else {
                _debito_ant = 0D
                _credito_ant = -_saldo_anterior
            }
            val _saldoParse = get[Option[Double]]("debito") ~ get[Option[Double]]("credito") map { case a ~ b => (a,b) }
            val _resultSetSaldo = SQL(querySaldo).on(
                   'fecha_inicial -> fim,
                   'fecha_final -> ffm,
                   'codigo -> (codigo + "%"),
                   'estado -> "C"
               ).as(_saldoParse.single)
            val _debito_mov = _resultSetSaldo._1 match {
                 case Some(v) => v
                 case None => 0D
               }
            val _credito_mov = _resultSetSaldo._2 match {
                 case Some(v) => v
                 case None => 0D
               }

            val _saldo_actual = _debito_ant - _credito_ant + _debito_mov - _credito_mov
            var _debito_act = 0D
            var _credito_act = 0D
            if (_saldo_actual > 0D) {
                _debito_act = _saldo_actual
                _credito_act = 0D
            } else {
                _debito_act = 0D
                _credito_act = -_saldo_actual
            }
            if (_debito_ant != 0 || _credito_ant != 0 || _debito_mov != 0 || _credito_mov != 0 || _debito_act != 0 || _credito_mov != 0) {
              _listTabla00 += new Balance(puc.codigo, puc.nombre, Some(_debito_ant), Some(_credito_ant), Some(_debito_mov), Some(_credito_mov), Some(_debito_act), Some(_credito_act))
            }
        }

        _listTabla00.toList
    }
  }

  def aExcel(empr_id: Long, codigo_inicial: String, codigo_final: String, fecha_corte: Long, nivel: Int, columnas: Int): Array[Byte] = {
    val fmt = DateTimeFormat.forPattern("yyyyMMdd")
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    val fc = Calendar.getInstance()
    fc.setTimeInMillis(fecha_corte)
    empresaService.buscarPorId(empr_id) match {
       case Some(empresa) =>
        var _listRow01 = new ListBuffer[com.norbitltd.spoiwo.model.Row]()
        var _listMerged01 = new ListBuffer[CellRange]()
        val sheet01 = Sheet(
          name = "Balance",
          rows = {
            val titleRow1 = com.norbitltd.spoiwo.model
                .Row()
                .withCellValues(empresa.empr_descripcion)
            _listRow01 += titleRow1
            val titleRow2 = com.norbitltd.spoiwo.model
                .Row()
                .withCellValues("Balance General")
            _listRow01 += titleRow2
            val titleRow3 = com.norbitltd.spoiwo.model.Row(
              StringCell(
                "Código Desde:",
                Some(0),
                style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              StringCell(
                _convert.codigopuc(codigo_inicial), 
                Some(1),
                        style = Some(
                          CellStyle(dataFormat = CellDataFormat("@"))
                        ),
                        CellStyleInheritance.CellThenRowThenColumnThenSheet               
              ),   
              StringCell(
                "Hasta:",
                Some(2),
                style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),              
              StringCell(
                _convert.codigopuc(codigo_final),
                Some(3),
                        style = Some(
                          CellStyle(dataFormat = CellDataFormat("@"))
                        ),
                        CellStyleInheritance.CellThenRowThenColumnThenSheet               
              ),   
            )       
            _listRow01 += titleRow3            
            val titleRow4 = com.norbitltd.spoiwo.model.Row(
              StringCell(
                "Periodo Corte:",
                Some(0),
                style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              DateCell(
                fc.getTime(), 
                Some(1),
                        style = Some(
                          CellStyle(dataFormat = CellDataFormat("YYYY/MM"))
                        ),
                        CellStyleInheritance.CellThenRowThenColumnThenSheet               
              )
            )   
            _listRow01 += titleRow4
            if (columnas == 6) {
              val mergedHeaderRow = com.norbitltd.spoiwo.model
                .Row()
                .withCellValues(
                  "",
                  "",
                  "ANTERIOR",
                  "",
                  "MOVIMIENTO",              
                  "",
                  "ACTUAL"
                ).withStyle(CellStyle(horizontalAlignment = HA.Center))
              _listRow01 += mergedHeaderRow
            }
            if (columnas == 4) {
              val mergedHeaderRow = com.norbitltd.spoiwo.model
                .Row()
                .withCellValues(
                  "",
                  "",
                  "",
                  "MOVIMIENTO",              
                  ""
                )      
              _listRow01 += mergedHeaderRow
            }
            if (columnas == 1) {
              val mergedHeaderRow = com.norbitltd.spoiwo.model
                .Row()
                .withCellValues(
                  "",
                )      
              _listRow01 += mergedHeaderRow
            }      
            if (columnas == 6 ) {
              val headerRow = com.norbitltd.spoiwo.model
                .Row()
                .withCellValues(
                  "Código",
                  "Cuenta",
                  "Débito",              
                  "Crédito",
                  "Débito",
                  "Crédito",
                  "Débito",
                  "Crédito"
                )
              _listRow01 += headerRow
              _listMerged01 += CellRange((4,4), (2,3))
              _listMerged01 += CellRange((4,4), (4,5))
              _listMerged01 += CellRange((4,4), (6,7))
            }
            if (columnas == 4 ) {
              val headerRow = com.norbitltd.spoiwo.model
                .Row()
                .withCellValues(
                  "Código",
                  "Cuenta",
                  "Saldo Anterior",              
                  "Débito",
                  "Crédito",
                  "Saldo Actual"
                )
              _listRow01 += headerRow
              _listMerged01 += CellRange((4,4), (3,4))
            }
            if (columnas == 1 ) {
              val headerRow = com.norbitltd.spoiwo.model
                .Row()
                .withCellValues(
                  "Código",
                  "Cuenta",
                  "Saldo Actual",
                )
              _listRow01 += headerRow
            }
            val resultSet = consultar(empr_id, fecha_corte, codigo_inicial, codigo_final, nivel)
            var _codigo_anterior = ""
            var fila = 4
            val rows = resultSet.flatMap { i =>
              if (columnas == 6) {
                _listRow01 += com.norbitltd.spoiwo.model.Row(
                    StringCell(
                    i.codigo match { case Some(d) => _convert.codigopuc(d) case None => "" },
                    Some(0),
                    style = Some(CellStyle(dataFormat = CellDataFormat("00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i.cuenta match { case Some(a) => a case None => "" },
                    Some(1),
                            style = Some(
                              CellStyle(dataFormat = CellDataFormat("@"))
                            ),
                            CellStyleInheritance.CellThenRowThenColumnThenSheet               
                  ),
                  NumericCell(
                    i.debito_ant match { case Some(s) => s case None => 0},
                    Some(2),
                    style = Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i.credito_ant match { case Some(s) => s case None => 0},
                    Some(3),
                    style = Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i.debito_mov match { case Some(s) => s case None => 0},
                    Some(4),
                    style = Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i.credito_mov match { case Some(s) => s case None => 0},
                    Some(5),
                    style = Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),  
                  NumericCell(
                    i.debito_act match { case Some(s) => s case None => 0},
                    Some(6),
                    style = Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i.credito_act match { case Some(s) => s case None => 0},
                    Some(7),
                    style = Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),                                  
                )
              } else if (columnas == 4) {
                _listRow01 += com.norbitltd.spoiwo.model.Row(
                    StringCell(
                    i.codigo match { case Some(d) => _convert.codigopuc(d) case None => "" },
                    Some(0),
                    style = Some(CellStyle(dataFormat = CellDataFormat("00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i.cuenta match { case Some(a) => a case None => "" },
                    Some(1),
                            style = Some(
                              CellStyle(dataFormat = CellDataFormat("@"))
                            ),
                            CellStyleInheritance.CellThenRowThenColumnThenSheet               
                  ),
                  NumericCell(
                    i.debito_ant.get - i.credito_ant.get,
                    Some(2),
                    style = Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i.debito_mov match { case Some(s) => s case None => 0},
                    Some(3),
                    style = Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i.credito_mov match { case Some(s) => s case None => 0},
                    Some(4),
                    style = Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i.debito_act.get - i.credito_act.get,
                    Some(5),
                    style = Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  )
                )
              } else {
                _listRow01 += com.norbitltd.spoiwo.model.Row(
                    StringCell(
                    i.codigo match { case Some(d) => _convert.codigopuc(d) case None => "" },
                    Some(0),
                    style = Some(CellStyle(dataFormat = CellDataFormat("00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i.cuenta match { case Some(a) => a case None => "" },
                    Some(1),
                            style = Some(
                              CellStyle(dataFormat = CellDataFormat("@"))
                            ),
                            CellStyleInheritance.CellThenRowThenColumnThenSheet               
                  ),
                  NumericCell(
                    i.debito_act.get - i.credito_act.get,
                    Some(2),
                    style = Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  )
                )                
              }
            }
            _listRow01.toList
          },
          mergedRegions = {
           _listMerged01 += CellRange((0, 0), (0, 16))
           _listMerged01 += CellRange((1, 1), (0, 16))
           _listMerged01.toList
          })
          var os: ByteArrayOutputStream = new ByteArrayOutputStream()
          Workbook(sheet01).writeToOutputStream(os)
          os.toByteArray 
          
        case None => var os: ByteArrayOutputStream = new ByteArrayOutputStream()
                     os.toByteArray()
      }
  }

}