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
import anorm.SqlParser.{get, str, int, date, double, flatten, scalar}
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

import java.util.Calendar

import scala.collection.mutable.ArrayBuffer

import utilities.GlobalesCol

class InformesRepository @javax.inject.Inject()(
    dbapi: DBApi,
    empresaService: EmpresaRepository,
    creditoService: CreditoRepository,
    _gcol: GlobalesCol
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
                INNER JOIN LIQUIDACION_DETALLE ld ON UPPER(ld.REFERENCIA) = UPPER(l.REFERENCIA) AND ld.ES_CAJABANCO = 1
                INNER JOIN "gen$persona" gp ON gp.ID_IDENTIFICACION = l.ID_IDENTIFICACION AND gp.ID_PERSONA = l.ID_PERSONA
                WHERE l.CONFIRMADA = 1 AND l.APLICADA = 1 AND l.APLICADA_EN BETWEEN {fecha_inicial} AND {fecha_final} AND l.LIQU_ORIGEN = {origen}
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
                i._5 match {
                  case Some(value) => value
                  case None        => 0
                },
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
                style = Some(CellStyle(dataFormat = CellDataFormat("#,#00"))),
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

  def getLiquidacionPendiente(
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
        get[DateTime]("CONFIRMADA_EN") ~
        get[String]("REFERENCIA") ~
        get[String]("ID_COLOCACION") ~
        get[Int]("ID_IDENTIFICACION") ~
        get[String]("ID_PERSONA") ~
        get[String]("NOMBRE") ~
        get[Double]("TOTAL") map {
        case fecha ~ confirmada_en ~ referencia ~ id_colocacion ~ id_identificacion ~ id_persona ~ nombre ~ sum =>
          (
            fecha,
            confirmada_en,
            referencia,
            id_colocacion,
            id_identificacion,
            id_persona,
            nombre,
            sum
          )
      }
    }
    db.withConnection { implicit connection =>
      SQL("""SELECT l.FECHA, l.CONFIRMADA_EN, l.REFERENCIA, l.ID_COLOCACION, l.ID_IDENTIFICACION, l.ID_PERSONA, (gp.NOMBRE || ' ' || gp.PRIMER_APELLIDO || gp.SEGUNDO_APELLIDO) AS NOMBRE, SUM(ld.DEBITO) AS TOTAL FROM LIQUIDACION l
                INNER JOIN LIQUIDACION_DETALLE ld ON ld.REFERENCIA = l.REFERENCIA AND ld.ES_CAJABANCO = 1
                INNER JOIN "gen$persona" gp ON gp.ID_IDENTIFICACION = l.ID_IDENTIFICACION AND gp.ID_PERSONA = l.ID_PERSONA
                WHERE l.CONFIRMADA = 1 AND l.APLICADA = 0 AND l.CONFIRMADA_EN BETWEEN {fecha_inicial} AND {fecha_final} AND l.LIQU_ORIGEN = {origen}
            GROUP BY 1,2,3,4,5,6,7""")
        .on(
          'fecha_inicial -> fi,
          'fecha_final -> ff,
          'origen -> origen
        )
        .as(_parser *)
    }
  }

  def getLiquidacionPendienteXlsx(
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
    val _resultSet = getLiquidacionPendiente(fecha_inicial, fecha_final, origen)
    val empresa = empresaService.buscarPorId(empr_id).get
    val format = new SimpleDateFormat("yyyy-MM-dd")
    val sheet1 = Sheet(
      name = "Recaudo Por Aplicar",
      rows = {
        val titleRow = com.norbitltd.spoiwo.model
          .Row()
          .withCellValues(empresa.empr_descripcion)
        val titleRow1 = com.norbitltd.spoiwo.model
          .Row()
          .withCellValues("RECAUDO DE CARTERA PENDIENTE POR APLICAR VIA WEB")
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
              StringCell(
                i._6,
                Some(3),
                style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              StringCell(
                i._7,
                Some(4),
                style = Some(CellStyle(dataFormat = CellDataFormat("@"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              NumericCell(
                i._8,
                Some(5),
                style = Some(CellStyle(dataFormat = CellDataFormat("#,#00"))),
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
    var file = new File("/tmp/recaudospendientes.xlsx")
    Workbook(sheet1).writeToOutputStream(new FileOutputStream(file))
    println("Stream Listo")
    os.toByteArray
  }

  def informeRotacionCreditosXlsx(empr_id: scala.Long): Array[Byte] = {
    val anho = Calendar.getInstance().get(Calendar.YEAR)
    val mes = Calendar.getInstance().get(Calendar.MONTH) + 1
    var _fcc = new DateTime(anho, mes, 1, 0, 0, 0)
    var _last_day = _fcc.dayOfMonth().getMaximumValue();
    _fcc = _fcc.dayOfMonth().setCopy(_last_day)
    var _fcb = new DateTime(anho, mes, 1, 0, 0, 0)
    _fcb = _fcb.plusMonths(-1)
    _last_day = _fcb.dayOfMonth().getMaximumValue();
    _fcb = _fcb.dayOfMonth().setCopy(_last_day)
    var ft = new DateTime()
    var _listRow = new ArrayBuffer[com.norbitltd.spoiwo.model.Row]()
    val _query =
      """SELECT 
                        c.ID_COLOCACION, 
                        p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO AS DEUDOR,
                        c.VALOR_DESEMBOLSO - c.ABONOS_A_CAPITAL AS SALDO_ACTUAL,
                    FROM "col$colocacion" c

                    WHERE ID_ESTADO_COLOCACION IN (0,1,2,3)"""
    val _queryCorte =
      """SELECT DEUDA, ID_ARRASTRE, MOROSIDAD FROM "col$causaciondiaria" WHERE ID_COLOCACION = c.ID_COLOCACION AND FECHA_CORTE = {fecha_corte}"""
    val _parserCredito = str("ID_COLOCACION") ~ str("DEUDOR") ~ double(
      "SALDO_ACTUAL"
    ) map {
      case id_colocacion ~ deudor ~ saldo_actual =>
        (
          id_colocacion,
          deudor,
          saldo_actual
        )
    }
    val _parserCorte = double("DEUDA") ~ int("ID_ARRASTRE") ~ int("MOROSIDAD") map {
      case deuda ~ id_arrastre ~ morosidad =>
        (
          deuda,
          id_arrastre,
          morosidad
        )
    }

    val empresa = empresaService.buscarPorId(empr_id).get
    val format = new SimpleDateFormat("yyyy-MM-dd")
    val sheet1 = Sheet(
      name = "Rotación de Mora",
      rows = {
        val titleRow = com.norbitltd.spoiwo.model
          .Row()
          .withCellValues(empresa.empr_descripcion)
        val titleRow1 = com.norbitltd.spoiwo.model
          .Row()
          .withCellValues("INFORME DE ROTACIÓN DE MORA DE CARTERA")
        val titleRow2 = com.norbitltd.spoiwo.model
          .Row()
          .withCellValues(
            "Periodo Evaluado: " + anho + " - " + mes
          )
        val headerRow = com.norbitltd.spoiwo.model
          .Row()
          .withCellValues(
            "Colocación",
            "Nombre",
            "Saldo Actual",
            "Saldo Al Corte Anterior",
            "Mora Corte Anterior",
            "Edad Corte Anterior",
            "Saldo al Corte",
            "Mora al Corte",
            "Edad al Corte",
            "Mora Actual"
          )
        db.withConnection { implicit connection =>
          val _resultSet = SQL(_query).as(_parserCredito *)
          var j = 4
          _resultSet.map { i =>
            j += 1
            val (_saldocb, _edadcb, _moracb) = SQL(_queryCorte).on('fecha_corte -> _fcb).as(_parserCorte.single)
            val (_saldocc, _edadcc, _moracc) = SQL(_queryCorte).on('fecha_corte -> _fcc).as(_parserCorte.single)
            val _moraactual: Double = if (i._3 > 0) {
              var dias_mora:Double = _gcol.obtenerDiasMora(i._1).toDouble
              if (dias_mora < -1.toDouble) {
                0
              } else {
                dias_mora
              }
            } else { 0.0 }
            _listRow += com.norbitltd.spoiwo.model.Row(
              StringCell(
                i._1,
                Some(0),
                style = Some(
                  CellStyle(dataFormat = CellDataFormat("@"))
                ),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              StringCell(
                i._2,
                Some(1),
                style = Some(
                  CellStyle(dataFormat = CellDataFormat("@"))
                ),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              NumericCell(
                i._3,
                Some(2),
                style = Some(CellStyle(dataFormat = CellDataFormat("#,#00"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              NumericCell(
                _saldocb,
                Some(3),
                style = Some(CellStyle(dataFormat = CellDataFormat("#,#00"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              NumericCell(
                _moracb,
                Some(4),
                style = Some(CellStyle(dataFormat = CellDataFormat("#,#00"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              NumericCell(
                _edadcb,
                Some(5),
                style = Some(CellStyle(dataFormat = CellDataFormat("#,#00"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              NumericCell(
                _saldocc,
                Some(6),
                style = Some(CellStyle(dataFormat = CellDataFormat("#,#00"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              NumericCell(
                _moracc,
                Some(7),
                style = Some(CellStyle(dataFormat = CellDataFormat("#,#00"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              NumericCell(
                _edadcc,
                Some(8),
                style = Some(CellStyle(dataFormat = CellDataFormat("#,#00"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              ),
              NumericCell(
                _moraactual,
                Some(9),
                style = Some(CellStyle(dataFormat = CellDataFormat("#,#00"))),
                CellStyleInheritance.CellThenRowThenColumnThenSheet
              )
            )
          }
          titleRow :: titleRow1 :: titleRow :: headerRow :: _listRow.toList
        }
      }
    )
    println("Escribiendo en el Stream")
    var os: ByteArrayOutputStream = new ByteArrayOutputStream()
    Workbook(sheet1).writeToOutputStream(os)
    var file = new File("/tmp/recaudospendientes.xlsx")
    Workbook(sheet1).writeToOutputStream(new FileOutputStream(file))
    println("Stream Listo")
    os.toByteArray
  }
}
