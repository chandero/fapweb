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
//

case class Auxiliar(
    id_comprobante: Option[Int],
    id_agencia: Option[Int],
    fecha: Option[DateTime],
    codigo: Option[String],
    cuenta: Option[String],
    saldo_anterior: Option[Long],
    debito: Option[Double],
    credito: Option[Double],
    nuevo_saldo: Option[Long],
    id_cuenta: Option[String],
    id_colocacion: Option[String],
    id_identificacion: Option[Int],
    id_persona: Option[String],
    persona: Option[String],
    monto_retencion: Option[Double],
    tasa_retencion: Option[Double],
    estadoaux: Option[String],
    tipo_comprobante: Option[String],
    abreviatura: Option[String],
    id: Option[Int],
    id_clase_operacion: Option[String],
    detalle: Option[String],
    cheque: Option[String]
)

object Auxiliar {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  val _set = {
    get[Option[Int]]("id_comprobante") ~
      get[Option[Int]]("id_agencia") ~
      get[Option[DateTime]]("fecha") ~
      get[Option[String]]("codigo") ~
      get[Option[String]]("cuenta") ~
      get[Option[Double]]("debito") ~
      get[Option[Double]]("credito") ~
      get[Option[String]]("id_cuenta") ~
      get[Option[String]]("id_colocacion") ~
      get[Option[Int]]("id_identificacion") ~
      get[Option[String]]("id_persona") ~
      get[Option[String]]("persona") ~
      get[Option[Double]]("monto_retencion") ~
      get[Option[Double]]("tasa_retencion") ~
      get[Option[String]]("estadoaux") ~
      get[Option[String]]("tipo_comprobante") ~
      get[Option[String]]("abreviatura") ~
      get[Option[Int]]("id") ~
      get[Option[String]]("id_clase_operacion") ~
      get[Option[String]]("detalle") ~
      get[Option[String]]("cheque") map {
      case id_comprobante ~
            id_agencia ~
            fecha ~
            codigo ~
            cuenta ~
            debito ~
            credito ~
            id_cuenta ~
            id_colocacion ~
            id_identificacion ~
            id_persona ~
            persona ~
            monto_retencion ~
            tasa_retencion ~
            estadoaux ~
            tipo_comprobante ~
            abreviatura ~
            id ~
            id_clase_operacion ~
            detalle ~
            cheque =>
        Auxiliar(
          id_comprobante,
          id_agencia,
          fecha,
          codigo,
          cuenta,
          Some(0L),
          debito,
          credito,
          Some(0L),
          id_cuenta,
          id_colocacion,
          id_identificacion,
          id_persona,
          persona,
          monto_retencion,
          tasa_retencion,
          estadoaux,
          tipo_comprobante,
          abreviatura,
          id,
          id_clase_operacion,
          detalle,
          cheque
        )
    }
  }
}

class AuxiliarRepository @Inject()(
    dbapi: DBApi,
    _gcon: GlobalesCon,
    _convert: Conversion,
    empresaService: EmpresaRepository
)(implicit ec: DatabaseExecutionContext) {
  private val db = dbapi.database("default")

  def consultar(
      codigo_inicial: String,
      codigo_final: String,
      fecha_inicial: Long,
      fecha_final: Long,
      id_identificacion: Int,
      id_persona: String
  ): Iterable[Auxiliar] = {
    var _listResult = new ListBuffer[Auxiliar]()
    val fi = new DateTime(fecha_inicial)
    val ff = new DateTime(fecha_final)
    var fisi = new DateTime()
    var ffsi = new DateTime()
    if (fi.monthOfYear().get().equals(1) && fi.dayOfMonth().get().equals(1)) {
      fisi = new DateTime(0, 1, 1, 0, 0, 0)
      ffsi = new DateTime(0, 1, 1, 0, 0, 0)
    } else {
      fisi = new DateTime(fi.year().get(), 1, 1, 0, 0, 0)
      ffsi = fi.minus(86400000)
    }

    var filter = ""
    var separador = ""
    var id: Option[Int] = None
    var ip: Option[String] = None
    if (id_identificacion != 0) {
      id = Some(id_identificacion)
    }
    if (id_persona != "") {
      ip = Some(id_persona)
    }
    id match {
      case Some(id) =>
        filter = filter + " a1.id_identificacion = " + id
        separador = " AND "
      case None => None
    }

    ip match {
      case Some(id) =>
        filter = filter + separador + " a1.id_persona = '" + id + "'"
      case None => None
    }

    val queryPuc =
      """SELECT * FROM CON$PUC p WHERE p.CODIGO >= {codigo_inicial} AND p.CODIGO <= {codigo_final} AND p.MOVIMIENTO = 1 ORDER BY p.CODIGO ASC"""

    var queryMovs =
      """SELECT a1.*, ae1.*, ctc1.ABREVIATURA, cp1.NOMBRE AS CUENTA, TRIM(p1.NOMBRE || ' ' || p1.PRIMER_APELLIDO || ' ' || p1.SEGUNDO_APELLIDO) AS persona 
                  FROM CON$COMPROBANTE c1
                  INNER JOIN CON$AUXILIAR a1 ON a1.TIPO_COMPROBANTE = c1.TIPO_COMPROBANTE AND a1.ID_COMPROBANTE = c1.ID_COMPROBANTE
                  LEFT JOINCON$AUXILIAREXT ae1 ON ae1.ID = a1.ID
                  LEFT JOIN "con$tipocomprobante" ctc1 ON ctc1.ID = c1.TIPO_COMPROBANTE 
                  LEFT JOIN CON$PUC cp1 ON cp1.CODIGO = a1.CODIGO 
                  LEFT JOIN "gen$persona" p1 ON p1.ID_IDENTIFICACION = a1.ID_IDENTIFICACION AND p1.ID_PERSONA = a1.ID_PERSONA
                  WHERE c1.ESTADO <> {estado} AND a1.CODIGO = {codigo} AND c1.FECHADIA BETWEEN {fecha_inicial} AND {fecha_final}
                  """
    if (filter != "") {
      queryMovs += " AND " + filter
    }
    queryMovs += " ORDER BY a1.CODIGO, a1.FECHA"

    println("Query:" + queryMovs)

    db.withTransaction { implicit connection =>
      val _listPuc = SQL(queryPuc)
        .on(
          "codigo_inicial" -> codigo_inicial,
          "codigo_final" -> codigo_final
        )
        .as(Puc._set *)

      _listPuc.foreach { p =>
        var _saldo_anterior =
          _gcon.saldo_anterior_codigo_puc(p.codigo.get, ffsi.getMillis())
        val _listMovs = SQL(queryMovs)
          .on(
            "codigo" -> p.codigo,
            'fecha_inicial -> fi,
            'fecha_final -> ff,
            'estado -> "N"
          )
          .as(Auxiliar._set *)
        _listMovs.foreach { m =>
          val _saldo_actual = _saldo_anterior + m.debito.get.longValue - m.credito.get.longValue
          val _movs = new Auxiliar(
            m.id_comprobante,
            m.id_agencia,
            m.fecha,
            m.codigo,
            m.cuenta,
            Some(_saldo_anterior),
            m.debito,
            m.credito,
            Some(_saldo_actual),
            m.id_cuenta,
            m.id_colocacion,
            m.id_identificacion,
            m.id_persona,
            m.persona,
            m.monto_retencion,
            m.tasa_retencion,
            m.estadoaux,
            m.tipo_comprobante,
            m.abreviatura,
            m.id,
            m.id_clase_operacion,
            m.detalle,
            m.cheque
          )
          _listResult += _movs
          _saldo_anterior = _saldo_actual
        }
      }
      _listResult.toList
    }
  }

  def aExcel(
      empr_id: Long,
      codigo_inicial: String,
      codigo_final: String,
      fecha_inicial: Long,
      fecha_final: Long,
      id_identificacion: Int,
      id_persona: String
  ): Array[Byte] = {
    val fmt = DateTimeFormat.forPattern("yyyyMMdd")
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    val fi = Calendar.getInstance()
    fi.setTimeInMillis(fecha_inicial)
    val ff = Calendar.getInstance()
    ff.setTimeInMillis(fecha_final)
    empresaService.buscarPorId(empr_id) match {
      case Some(empresa) =>
        var _listRow01 = new ListBuffer[com.norbitltd.spoiwo.model.Row]()
        var _listMerged01 = new ListBuffer[CellRange]()
        val sheet01 = Sheet(
          name = "Auxiliar",
          rows = {
            val titleRow1 = com.norbitltd.spoiwo.model
              .Row()
              .withCellValues(empresa.empr_descripcion)
            _listRow01 += titleRow1
            val titleRow2 = com.norbitltd.spoiwo.model
              .Row()
              .withCellValues("Auxiliar de Movimientos")
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
              )
            )
            _listRow01 += titleRow3
            val titleRow4 = com.norbitltd.spoiwo.model.Row(
              StringCell(
                "Fecha Desde:",
                Some(0),
                style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              DateCell(
                fi.getTime(),
                Some(1),
                style = Some(
                  CellStyle(dataFormat = CellDataFormat("YYYY/MM/DD"))
                ),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              StringCell(
                "Hasta:",
                Some(2),
                style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              DateCell(
                ff.getTime(),
                Some(3),
                style = Some(
                  CellStyle(dataFormat = CellDataFormat("YYYY/MM/DD"))
                ),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              )
            )
            _listRow01 += titleRow4
            val headerRow = com.norbitltd.spoiwo.model
              .Row()
              .withCellValues(
                "Código",
                "Cuenta",
                "Fecha",
                "Tipo",
                "Número",
                "Detalle",
                "Saldo Anterior",
                "Débito",
                "Crédito",
                "Nuevo Saldo",
                "Documento",
                "Persona",
                "Cuenta",
                "Colocación",
                "Cheque",
                "Monto RF",
                "% RF"
              )
            _listRow01 += headerRow
            val resultSet = consultar(
              codigo_inicial,
              codigo_final,
              fecha_inicial,
              fecha_final,
              id_identificacion,
              id_persona
            )
            var _codigo_anterior = ""
            var fila = 4
            val rows = resultSet.flatMap {
              i =>
                if (_codigo_anterior != i.codigo.get) {
                  fila += 1
                  _listRow01 += com.norbitltd.spoiwo.model.Row(
                    StringCell(
                      i.codigo match {
                        case Some(c) => _convert.codigopuc(c)
                        case None    => ""
                      },
                      Some(0),
                      style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                      CellStyleInheritance.CellThenRowThenColumnThenSheet
                    ),
                    StringCell(
                      i.cuenta match {
                        case Some(c) => c
                        case None    => ""
                      },
                      Some(1),
                      style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                      CellStyleInheritance.CellThenRowThenColumnThenSheet
                    )
                  )
                  _listMerged01 += CellRange((fila, fila), (1, 5))
                  _codigo_anterior = i.codigo.get
                }
                fila += 1
                _listRow01 += com.norbitltd.spoiwo.model.Row(
                  StringCell(
                    i.fecha match {
                      case Some(d) => sdf.format(new Date(d.getMillis()))
                      case None    => ""
                    },
                    Some(2),
                    style = Some(CellStyle(dataFormat = CellDataFormat("00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i.abreviatura match {
                      case Some(a) => a
                      case None    => ""
                    },
                    Some(3),
                    style = Some(
                      CellStyle(dataFormat = CellDataFormat("@"))
                    ),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i.id_comprobante match {
                      case Some(a) => a.toString
                      case None    => ""
                    },
                    Some(4),
                    style = Some(
                      CellStyle(dataFormat = CellDataFormat("#0"))
                    ),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i.detalle match {
                      case Some(d) => d
                      case None    => ""
                    },
                    Some(5),
                    style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i.saldo_anterior match {
                      case Some(s) => s
                      case None    => 0
                    },
                    Some(6),
                    style =
                      Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i.debito match {
                      case Some(s) => s
                      case None    => 0
                    },
                    Some(7),
                    style =
                      Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i.credito match {
                      case Some(s) => s
                      case None    => 0
                    },
                    Some(8),
                    style =
                      Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i.nuevo_saldo match {
                      case Some(s) => s
                      case None    => 0
                    },
                    Some(9),
                    style =
                      Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i.id_persona match {
                      case Some(d) => d
                      case None    => ""
                    },
                    Some(10),
                    style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i.persona match {
                      case Some(d) => d
                      case None    => ""
                    },
                    Some(11),
                    style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i.id_cuenta match {
                      case Some(d) => d
                      case None    => ""
                    },
                    Some(12),
                    style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i.id_colocacion match {
                      case Some(d) => d
                      case None    => ""
                    },
                    Some(13),
                    style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i.cheque match {
                      case Some(d) => d
                      case None    => ""
                    },
                    Some(14),
                    style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i.monto_retencion match {
                      case Some(d) => d
                      case None    => 0d
                    },
                    Some(15),
                    style =
                      Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i.tasa_retencion match {
                      case Some(d) => d
                      case None    => 0d
                    },
                    Some(16),
                    style =
                      Some(CellStyle(dataFormat = CellDataFormat("#,#0.00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  )
                )
            }
            _listRow01.toList
          },
          mergedRegions = {
            _listMerged01 += CellRange((0, 0), (0, 16))
            _listMerged01 += CellRange((1, 1), (0, 16))
            _listMerged01.toList
          }
        )
        var os: ByteArrayOutputStream = new ByteArrayOutputStream()
        Workbook(sheet01).writeToOutputStream(os)
        os.toByteArray

      case None =>
        var os: ByteArrayOutputStream = new ByteArrayOutputStream()
        os.toByteArray()
    }
  }

  /* def aPdf(codigo_inicial: String, codigo_final: String, fecha_inicial: Long, fecha_final: Long, id_identificacion: Int, id_persona: String): Array[Byte] = {

  } */
}
