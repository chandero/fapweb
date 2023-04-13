/* SQL("""SELECT e.ID_COLOCACION, p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO AS NOMBRE, SUM(e.ABONO_ANTICIPADO) AS ANTICIPADO, SUM(e.ABONO_CXC) AS CAUSADO, SUM(ABONO_SERVICIOS) AS SERVICIO, SUM(ABONO_MORA) AS MORA FROM "col$extracto" e
LEFT JOIN "col$colocacion" c ON c.ID_COLOCACION = e.ID_COLOCACION
LEFT JOIN "gen$persona" p ON p.ID_IDENTIFICACION = c.ID_IDENTIFICACION AND p.ID_PERSONA = c.ID_PERSONA
WHERE e.FECHA_EXTRACTO BETWEEN {fecha_inicial} AND {fecha_final}
GROUP BY e.ID_COLOCACION, p.NOMBRE, p.PRIMER_APELLIDO, p.SEGUNDO_APELLIDO
HAVING SUM(e.ABONO_ANTICIPADO) <> 0 OR SUM(e.ABONO_CXC) <> 0 OR SUM(ABONO_SERVICIOS)  <> 0 OR SUM(ABONO_MORA) <> 0""")" +
 */
package models

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import scala.util.{Failure, Success}
import anorm._
import anorm.SqlParser.{get, str, int, date, flatten, scalar}
import anorm.JodaParameterMetaData._
import play.api.db.DBApi
import scala.concurrent.Future

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
import java.text.SimpleDateFormat
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.File
import scala.collection.mutable.ArrayBuffer

class InformesRepository @javax.inject.Inject()(
    dbapi: DBApi,
    empresaService: EmpresaRepository
)(implicit ec: DatabaseExecutionContext) {
  private val db = dbapi.database("default")

  def getLiquidacionAplicada(
      fecha_inicial: scala.Long,
      fecha_final: scala.Long,
      origen: String
  ) = {
    var fi = new DateTime(fecha_inicial)
    var ff = new DateTime(fecha_final)
    fi = fi.hourOfDay().setCopy(0)
    fi = fi.minuteOfHour().setCopy(0)
    fi = fi.secondOfMinute().setCopy(0)
    ff = ff.hourOfDay().setCopy(23)
    ff = ff.minuteOfHour().setCopy(59)
    ff = ff.secondOfMinute().setCopy(59)
    val _parser = {
      get[DateTime]("FECHA") ~
        get[DateTime]("APLICADA_EN") ~
        get[String]("REFERENCIA") ~
        get[String]("ID_COLOCACION") ~
        get[Option[Int]]("ID_COMPROBANTE") ~
        get[Int]("ID_IDENTIFICACION") ~
        get[String]("ID_PERSONA") ~
        get[String]("NOMBRE") ~
        get[Double]("TOTAL") map {
        case fecha ~ aplicada_en ~ referencia ~ id_colocacion ~ id_comprobante ~ id_identificacion ~ id_persona ~ nombre ~ sum =>
          (
            fecha,
            aplicada_en,
            referencia,
            id_colocacion,
            id_comprobante,
            id_identificacion,
            id_persona,
            nombre,
            sum
          )
      }
    }
    db.withConnection { implicit connection =>
      SQL("""SELECT l.FECHA, l.APLICADA_EN, l.REFERENCIA, l.ID_COLOCACION, l.ID_COMPROBANTE, l.ID_IDENTIFICACION, l.ID_PERSONA, (gp.NOMBRE || ' ' || gp.PRIMER_APELLIDO || gp.SEGUNDO_APELLIDO) AS NOMBRE, SUM(ld.DEBITO) AS TOTAL FROM LIQUIDACION l
                INNER JOIN LIQUIDACION_DETALLE ld ON ld.REFERENCIA = l.REFERENCIA AND ld.ES_CAJABANCO = 1
                INNER JOIN "gen$persona" gp ON gp.ID_IDENTIFICACION = l.ID_IDENTIFICACION AND gp.ID_PERSONA = l.ID_PERSONA
                WHERE l.APLICADA = 1 AND l.APLICADA_EN BETWEEN {fecha_inicial} AND {fecha_final} AND l.LIQU_ORIGEN = {origen}
            GROUP BY 1,2,3,4,5,6,7,8""")
        .on(
          'fecha_inicial -> fi,
          'fecha_final -> ff,
          'origen -> origen
        )
        .as(_parser *)
    }
  }

  def getLiquidacionAplicadaXlsx(
      fecha_inicial: scala.Long,
      fecha_final: scala.Long,
      origen: String,
      empr_id: Long
  ): Array[Byte] = {
    var fi = new DateTime(fecha_inicial)
    var ff = new DateTime(fecha_final)
    fi = fi.hourOfDay().setCopy(0)
    fi = fi.minuteOfHour().setCopy(0)
    fi = fi.secondOfMinute().setCopy(0)
    ff = fi.hourOfDay().setCopy(23)
    ff = fi.minuteOfHour().setCopy(59)
    ff = fi.secondOfMinute().setCopy(59)
    var _listRow = new ArrayBuffer[com.norbitltd.spoiwo.model.Row]()
    val _resultSet = getLiquidacionAplicada(fecha_inicial, fecha_final, origen)
    val empresa = empresaService.buscarPorId(empr_id).get
    val format = new SimpleDateFormat("yyyy-MM-dd")
    val sheet1 = Sheet(
      name = "Recaudo",
      rows = {
        val titleRow = com.norbitltd.spoiwo.model
          .Row()
          .withCellValues(empresa.empr_descripcion)
        val titleRow1 = com.norbitltd.spoiwo.model
          .Row()
          .withCellValues("RECAUDO DE CARTERA VIA WEB")
        val titleRow2 = com.norbitltd.spoiwo.model
          .Row()
          .withCellValues(
            "Periodo Evaluado: " + format.format(fecha_inicial) + " - " + format
              .format(fecha_final)
          )
        val headerRow = com.norbitltd.spoiwo.model
          .Row()
          .withCellValues(
            "Fecha Recaudo",
            "Colocación",
            "Referencia",
            "Comprobante",
            "Documento",
            "Nombre",
            "Valor Recaudo"
          )
        var j = 4
        _resultSet.map {
              i =>
                j += 1
                _listRow += com.norbitltd.spoiwo.model.Row(
                  DateCell(
                   i._2.toDate(),
                    Some(0),
                    style = Some(
                      CellStyle(dataFormat = CellDataFormat("YYYY-MM-DD"))
                    ),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i._4,
                    Some(1),
                    style = Some(
                      CellStyle(dataFormat = CellDataFormat("@"))
                    ),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i._3,
                    Some(2),
                    style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i._5 match { case Some(value) => value case None => 0 },
                    Some(3),
                    style = Some(CellStyle(dataFormat = CellDataFormat("#0"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i._7,
                    Some(4),
                    style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  StringCell(
                    i._8,
                    Some(5),
                    style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  ),
                  NumericCell(
                    i._9,
                    Some(6),
                    style =
                      Some(CellStyle(dataFormat = CellDataFormat("#,#00"))),
                    CellStyleInheritance.CellThenRowThenColumnThenSheet
                  )
                )
            }
        titleRow :: titleRow1 :: titleRow :: headerRow :: _listRow.toList
      }
    )
    println("Escribiendo en el Stream")
    var os: ByteArrayOutputStream = new ByteArrayOutputStream()
    Workbook(sheet1).writeToOutputStream(os)
    var file = new File("/tmp/recaudos.xlsx")
    Workbook(sheet1).writeToOutputStream(new FileOutputStream(file))
    println("Stream Listo")
    os.toByteArray
  }
}
