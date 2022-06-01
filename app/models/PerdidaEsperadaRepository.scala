package models

import anorm._
import anorm.SqlParser.{get, str}
import anorm.JodaParameterMetaData._

import javax.inject.Inject
import java.util.Calendar
import java.util.UUID
import java.text.SimpleDateFormat
import java.io.File
import java.io.FileInputStream

import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel._

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._
import play.api.libs.EventSource.Event

import play.api.db.DBApi

import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}
import scala.math.BigDecimal
import scala.jdk.CollectionConverters._

import anorm.SqlParser.{get, str, int, float}

import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import org.joda.time.LocalDate
import java.io.ByteArrayInputStream

import scala.util.control.NonFatal

import java.io.{BufferedWriter, FileOutputStream, OutputStreamWriter}

import com.univocity.parsers.csv.{CsvWriter, CsvWriterSettings}

import services.ProcessingProgressTrackerEvent.registerNewStatus

import utilities._
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import scala.io.Source
import java.sql.Date

class PerdidaEsperadaRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {

  private val pe = dbapi.database("pebase")
  private val df = new DataFormatter(true)
  private val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  private val sdfConvert = new SimpleDateFormat("dd/MM/yyyy")
  private var c_id = 0L
  private var e_id = 0L
  private var anho = 0
  private var mes = 0
  /* Procesar archivos
   */

  df.addFormat("-1,234", new java.text.DecimalFormat("0"))
  df.addFormat("-1,234.00", new java.text.DecimalFormat("0"))
  // df.addFormat("12/31/1999", new java.text.SimpleDateFormat("MM/dd/yyyy"))

  private val dateFormat: DateTimeFormatter =
    DateTimeFormat.forPattern("dd/MM/yyyy")

  def ultimaCarga(empr_id: Long): (String, String, Int, Int) = {
    pe.withConnection { implicit connection =>
      val parser = int("control_carga_anho") ~ int("control_carga_mes") map {
        case a ~ b => (a, b)
      }
      val periodo = SQL(
        """SELECT FIRST 1 cc1.control_carga_anho, cc1.control_carga_mes FROM control_carga cc1
             WHERE cc1.control_carga_estado = 1
             ORDER BY control_carga_anho DESC, control_carga_mes DESC"""
      ).as(parser.singleOpt)
      periodo match {
        case Some(periodo) =>
          val aDate: LocalDate = new LocalDate(periodo._1, periodo._2, 1)
          val lastDay = aDate.dayOfMonth().getMaximumValue();
          val conDia = Utility.mes(periodo._2) + " " + lastDay + " de " + periodo._1
          val sinDia = Utility.mes(periodo._2) + " de " + periodo._1
          (sinDia, conDia, periodo._1, periodo._2)
        case None => ("", "", 0, 0)
      }
    }
  }

  def todos = Future {
    pe.withConnection { implicit connection =>
      SQL("SELECT * FROM CONTROL_CARGA").as(CONTROL_CARGA._set *)
    }
  }

  def delete(a: Int, m: Int): Boolean = {
    val _fecha_corte = "%04d%02d".format(a, m)
    pe.withTransaction { implicit connection =>
        val _r1 =
          SQL(
            """DELETE FROM USUARIO WHERE USUARIO_FECHA_CORTE = {fecha_corte}"""
          ).on(
              "fecha_corte" -> _fecha_corte
            )
            .executeUpdate()
        val _r2 =
          SQL(
            """DELETE FROM CARTERA WHERE CARTERA_FECHA_CORTE = {fecha_corte}"""
          ).on(
              "fecha_corte" -> _fecha_corte
            )
            .executeUpdate()
        val _r3 =
          SQL(
            """DELETE FROM DEPOSITO WHERE DEPOSITO_FECHA_CORTE = {fecha_corte}"""
          ).on(
              "fecha_corte" -> _fecha_corte
            )
            .executeUpdate()
        val _r4 =
          SQL("""DELETE FROM APORTE WHERE APORTE_FECHA_CORTE = {fecha_corte}""")
            .on(
              "fecha_corte" -> _fecha_corte
            )
            .executeUpdate()

        val _r5 =
          SQL(
            """DELETE FROM CONTROL_CARGA WHERE CONTROL_CARGA_ANHO = {anho} AND CONTROL_CARGA_PERIODO = {mes}"""
          ).on(
              "anho" -> a,
              "mes" -> m
            )
            .executeUpdate()
        _r1 > 0 && _r2 > 0 && _r3 > 0 && _r4 > 0 && _r5 > 0
      }
      .booleanValue()
  }

  def loadData(a: Int, m: Int, usua_alias: Long, uuid: String): Boolean = {
    loadData1UsuarioCsv(a, m, usua_alias, uuid)
    loadData2CarteraCsv(a, m, usua_alias, uuid)
    loadData3DepositoCsv(a, m, usua_alias, uuid)
    loadData4AporteCsv(a, m, usua_alias, uuid)
    true
  }

  def loadData1Usuario(
      a: Int,
      m: Int,
      usua_alias: Long,
      uuid: String
  ): Boolean = {
    val tipo = 1
    val hoy: LocalDateTime = new LocalDateTime(
      Calendar.getInstance().getTimeInMillis()
    )
    val path = System.getProperty("java.io.tmpdir")
    val newTempDir = new File(path, "cargadata")
    val filenameone =
      "file_" + a + "_" + m + "_" + tipo + ".xlsx"
    val filestringone = path + "/cargadata/" + filenameone
    val fileone = new File(filestringone)
    var lastEventTime = Calendar.getInstance().getTime()
    var currentEventTime = Calendar.getInstance().getTime()
    var exito = true
    var msg = "Carga Exitosa"
    val sessionUUID = UUID.fromString(uuid)
    println("token a usar: " + uuid)
    var headersLength = 0
    var isHeadersWritten = false
    var filaPos = 0
    val wb = WorkbookFactory.create(fileone)
    val formulaEvaluator: FormulaEvaluator =
      wb.getCreationHelper().createFormulaEvaluator()
    registerNewStatus(
      new ProcessEvent("0", "0", "1", "carga" + tipo + "PreparingEvent"),
      sessionUUID
    )
    wb.asScala.zipWithIndex.map {
      case (sheet, index) =>
        // val sheet = wb.getSheetAt(0)
        println("Inicio Carga: " + sdf.format(Calendar.getInstance().getTime()))
        val totalRow = sheet.getLastRowNum().toString
        println("Filas: " + totalRow)
        sheet.asScala.foreach {
          case row =>
            if (Option(row).isDefined) {
              try {
                filaPos += 1
                currentEventTime = Calendar.getInstance().getTime()
                if ((currentEventTime.getTime() - lastEventTime
                      .getTime()) / 1000 >= 1) {
                  registerNewStatus(
                    new ProcessEvent(
                      totalRow.toString(),
                      filaPos.toString(),
                      "2",
                      "carga" + tipo + "ParsingEvent"
                    ),
                    sessionUUID
                  )
                  lastEventTime = currentEventTime
                }
                if (filaPos >= 2) {
                  val rowData = (0 until row.getLastCellNum).toArray.map {
                    index =>
                      index match {
                        case 5 | 19 | 25 =>
                          sdf.format(
                            row
                              .getCell(index)
                              .getDateCellValue()
                          )
                        case _ =>
                          removeLineBreakingChars(
                            df.formatCellValue(
                              row.getCell(
                                index,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                              ),
                              formulaEvaluator
                            )
                          )
                      }
                  }
                  escribirFila1(rowData, a, m)
                }
              } catch {
                case NonFatal(th) =>
                  msg = th.getMessage() + ", [Linea " + (filaPos) + "]"
                  exito = false
                  th.printStackTrace()
              }
            }
        }
        registerNewStatus(
          new ProcessEvent("0", "0", "3", "carga" + tipo + "DoneEvent"),
          sessionUUID
        )
        println(
          "Fin Carga Usuario: " + sdf.format(Calendar.getInstance().getTime())
        )
    }
    pe.withConnection { implicit connection =>
      SQL("""INSERT INTO CONTROL_CARGA 
                    (
                        control_carga_anho, 
                        control_carga_periodo,
                        control_carga_fecha,
                        id_empleado,
                        control_carga_tipo,
                        control_carga_estado,
                        control_carga_registros
                    ) VALUES (
                        {control_carga_anho}, 
                        {control_carga_periodo},
                        {control_carga_fecha},
                        {id_empleado},
                        {control_carga_tipo},
                        {control_carga_estado},
                        {control_carga_registros}
                    )""")
        .on(
          "control_carga_anho" -> a,
          "control_carga_periodo" -> m,
          "control_carga_fecha" -> hoy,
          "id_empleado" -> usua_alias,
          "control_carga_tipo" -> tipo,
          "control_carga_estado" -> (if (exito) {
                                       1
                                     } else {
                                       0
                                     }),
          "control_carga_registros" -> (filaPos - 1)
        )
        .executeUpdate()
    }
    exito
  }

  def loadData1UsuarioCsv(
      a: Int,
      m: Int,
      usua_alias: Long,
      uuid: String
  ): Boolean = {
    val tipo = 1
    val hoy: LocalDateTime = new LocalDateTime(
      Calendar.getInstance().getTimeInMillis()
    )
    val path = System.getProperty("java.io.tmpdir")
    val filenameone =
      "file_" + a + "_" + m + "_" + tipo + ".csv"
    val filestringone = path + "/cargadata/" + filenameone
    var lastEventTime = Calendar.getInstance().getTime()
    var currentEventTime = Calendar.getInstance().getTime()
    var exito = true
    var msg = "Carga Exitosa"
    val sessionUUID = UUID.fromString(uuid)
    var headersLength = 0
    var isHeadersWritten = false
    var filaPos = 0
    registerNewStatus(
      new ProcessEvent("0", "0", "1", "carga" + tipo + "PreparingEvent"),
      sessionUUID
    )
    val src = Source.fromFile(filestringone)
    val _list = src.getLines().toList // .drop(1) //.map(_.split(";"))
    val totalRow = _list.length
    _list.map { row =>
      //try {
      filaPos += 1
      currentEventTime = Calendar.getInstance().getTime()
      if ((currentEventTime.getTime() - lastEventTime
            .getTime()) / 1000 >= 1) {
        registerNewStatus(
          new ProcessEvent(
            totalRow.toString(),
            filaPos.toString(),
            "2",
            "carga" + tipo + "ParsingEvent"
          ),
          sessionUUID
        )
        lastEventTime = currentEventTime
      }
      val rowData = row.split(";")
      val lastCellNum = rowData.length
      println(
        "Evaluando fila: " + filaPos + ", columnas: " + lastCellNum
      )
      if (filaPos >= 2) {
        escribirFila1(rowData, a, m)
      }
    }
    registerNewStatus(
      new ProcessEvent("0", "0", "3", "carga" + tipo + "DoneEvent"),
      sessionUUID
    )
    println(
      "Fin Carga Cartera: " + sdf.format(Calendar.getInstance().getTime())
    )
    pe.withConnection { implicit connection =>
      SQL("""INSERT INTO CONTROL_CARGA 
                    (
                        control_carga_anho, 
                        control_carga_periodo,
                        control_carga_fecha,
                        id_empleado,
                        control_carga_tipo,
                        control_carga_estado,
                        control_carga_registros
                    ) VALUES (
                        {control_carga_anho}, 
                        {control_carga_periodo},
                        {control_carga_fecha},
                        {id_empleado},
                        {control_carga_tipo},
                        {control_carga_estado},
                        {control_carga_registros}
                    )""")
        .on(
          "control_carga_anho" -> a,
          "control_carga_periodo" -> m,
          "control_carga_fecha" -> hoy,
          "id_empleado" -> usua_alias,
          "control_carga_tipo" -> tipo,
          "control_carga_estado" -> (if (exito) {
                                       1
                                     } else {
                                       0
                                     }),
          "control_carga_registros" -> (filaPos - 1)
        )
        .executeUpdate()
    }
    exito
  }

  def escribirFila1(fila: Array[String], anho: Int, mes: Int): Unit = {
    val usuario_tipo_identificacion = if (fila.length >= 1) {
      fila(0) match {
        case "" => Option.empty[String]
        case v  => Some(v.trim())
      }
    } else {
      Option.empty[String]
    }
    val usuario_id_persona = fila(1) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val usuario_primer_apellido = fila(2) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val usuario_segundo_apellido = fila(3) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val usuario_nombres = fila(4) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val usuario_fecha_vinculacion = fila(5) match {
      case "" => Option.empty[Long]
      case v =>
        Some(DateTime.parse(v, dateFormat).getMillis())
    }
    val usuario_telefono = fila(6) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val usuario_direccion = fila(7) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val usuario_asociado = fila(8) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_activo = fila(9) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_ciiu = fila(10) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_municipio = fila(11) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_email = fila(12) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val usuario_genero = fila(13) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_empleado = fila(14) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_tipo_contrato = fila(15) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_nivel_escolaridad = fila(16) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_estrato = fila(17) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_nivel_ingresos = fila(18) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_fecha_nacimiento = fila(19) match {
      case "" => Option.empty[Long]
      case v  => Some(DateTime.parse(v, dateFormat).getMillis())
    }
    val usuario_estado_civil = fila(20) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_cabeza_familia = fila(21) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_ocupacion = fila(22) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_sector_economico = fila(23) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_jornada_laboral = fila(24) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val usuario_fecha_retiro = if (fila.length >= 26) {
      fila(25) match {
        case "" => Option.empty[Long]
        case v  => Some(DateTime.parse(v, dateFormat).getMillis())
      }
    } else {
      Option.empty[Long]
    }
    val usuario_asamblea = if (fila.length >= 27) {
      fila(26) match {
        case "" => Option.empty[Int]
        case v  => Some(v.trim().toInt)
      }
    } else {
      Option.empty[Int]
    }
    val usuario_fecha_corte = Some("%04d%02d".format(anho, mes))
    pe.withConnection { implicit connection =>
      SQL("""INSERT INTO USUARIO (
            USUARIO_TIPO_IDENTIFICACION,
            USUARIO_ID_PERSONA,
            USUARIO_PRIMER_APELLIDO,
            USUARIO_SEGUNDO_APELLIDO,
            USUARIO_NOMBRES,
            USUARIO_FECHA_VINCULACION,
            USUARIO_TELEFONO,
            USUARIO_DIRECCION,
            USUARIO_ASOCIADO,
            USUARIO_ACTIVO,
            USUARIO_CIIU,
            USUARIO_MUNICIPIO,
            USUARIO_EMAIL,
            USUARIO_GENERO,
            USUARIO_EMPLEADO,
            USUARIO_TIPO_CONTRATO,
            USUARIO_NIVEL_ESCOLARIDAD,
            USUARIO_ESTRATO,
            USUARIO_NIVEL_INGRESOS,
            USUARIO_FECHA_NACIMIENTO,
            USUARIO_ESTADO_CIVIL,
            USUARIO_CABEZA_FAMILIA,
            USUARIO_OCUPACION,
            USUARIO_SECTOR_ECONOMICO,
            USUARIO_JORNADA_LABORAL,
            USUARIO_FECHA_RETIRO,
            USUARIO_ASAMBLEA,
            USUARIO_FECHA_CORTE,
            CREATED_AT) VALUES (
              {usuario_tipo_identificacion},
              {usuario_id_persona},
              {usuario_primer_apellido},
              {usuario_segundo_apellido},
              {usuario_nombres},
              {usuario_fecha_vinculacion},
              {usuario_telefono},
              {usuario_direccion},
              {usuario_asociado},
              {usuario_activo},
              {usuario_ciiu},
              {usuario_municipio},
              {usuario_email},
              {usuario_genero},
              {usuario_empleado},
              {usuario_tipo_contrato},
              {usuario_nivel_escolaridad},
              {usuario_estrato},
              {usuario_nivel_ingresos},
              {usuario_fecha_nacimiento},
              {usuario_estado_civil},
              {usuario_cabeza_familia},
              {usuario_ocupacion},
              {usuario_sector_economico},
              {usuario_jornada_laboral},
              {usuario_fecha_retiro},
              {usuario_asamblea},
              {usuario_fecha_corte},
              {created_at})""")
        .on(
          "usuario_tipo_identificacion" -> usuario_tipo_identificacion,
          "usuario_id_persona" -> usuario_id_persona,
          "usuario_primer_apellido" -> usuario_primer_apellido,
          "usuario_segundo_apellido" -> usuario_segundo_apellido,
          "usuario_nombres" -> usuario_nombres,
          "usuario_fecha_vinculacion" -> usuario_fecha_vinculacion,
          "usuario_telefono" -> usuario_telefono,
          "usuario_direccion" -> usuario_direccion,
          "usuario_asociado" -> usuario_asociado,
          "usuario_activo" -> usuario_activo,
          "usuario_ciiu" -> usuario_ciiu,
          "usuario_municipio" -> usuario_municipio,
          "usuario_email" -> usuario_email,
          "usuario_genero" -> usuario_genero,
          "usuario_empleado" -> usuario_empleado,
          "usuario_tipo_contrato" -> usuario_tipo_contrato,
          "usuario_nivel_escolaridad" -> usuario_nivel_escolaridad,
          "usuario_estrato" -> usuario_estrato,
          "usuario_nivel_ingresos" -> usuario_nivel_ingresos,
          "usuario_fecha_nacimiento" -> usuario_fecha_nacimiento,
          "usuario_estado_civil" -> usuario_estado_civil,
          "usuario_cabeza_familia" -> usuario_cabeza_familia,
          "usuario_ocupacion" -> usuario_ocupacion,
          "usuario_sector_economico" -> usuario_sector_economico,
          "usuario_jornada_laboral" -> usuario_jornada_laboral,
          "usuario_fecha_retiro" -> usuario_fecha_retiro,
          "usuario_asamblea" -> usuario_asamblea,
          "usuario_fecha_corte" -> usuario_fecha_corte,
          "created_at" -> Calendar.getInstance().getTime()
        )
        .executeUpdate()
    }
  }

  def loadData2Cartera(
      a: Int,
      m: Int,
      usua_alias: Long,
      uuid: String
  ): Boolean = {
    val tipo = 2
    val hoy: LocalDateTime = new LocalDateTime(
      Calendar.getInstance().getTimeInMillis()
    )
    val path = System.getProperty("java.io.tmpdir")
    val newTempDir = new File(path, "cargadata")
    val filenameone =
      "file_" + a + "_" + m + "_" + tipo + ".xlsx"
    val filestringone = path + "/cargadata/" + filenameone
    val fileone = new File(filestringone)
    var lastEventTime = Calendar.getInstance().getTime()
    var currentEventTime = Calendar.getInstance().getTime()
    var exito = true
    var msg = "Carga Exitosa"
    val sessionUUID = UUID.fromString(uuid)
    var headersLength = 0
    var isHeadersWritten = false
    var filaPos = 0
    val wb = WorkbookFactory.create(fileone)
    val formulaEvaluator: FormulaEvaluator =
      wb.getCreationHelper().createFormulaEvaluator()
    registerNewStatus(
      new ProcessEvent("0", "0", "1", "carga" + tipo + "PreparingEvent"),
      sessionUUID
    )
    wb.asScala.zipWithIndex.map {
      case (sheet, index) =>
        // val sheet = wb.getSheetAt(0)
        println("Inicio Carga: " + sdf.format(Calendar.getInstance().getTime()))
        val totalRow = sheet.getLastRowNum().toString
        println("Filas: " + totalRow)
        sheet.asScala.foreach {
          case row =>
            if (Option(row).isDefined) {
              try {
                filaPos += 1
                currentEventTime = Calendar.getInstance().getTime()
                if ((currentEventTime.getTime() - lastEventTime
                      .getTime()) / 1000 >= 1) {
                  registerNewStatus(
                    new ProcessEvent(
                      totalRow.toString(),
                      filaPos.toString(),
                      "2",
                      "carga" + tipo + "ParsingEvent"
                    ),
                    sessionUUID
                  )
                  lastEventTime = currentEventTime
                }
                if (filaPos >= 2) {
                  val lastCellNum = row.getLastCellNum()
                  println(
                    "Evaluando fila: " + filaPos + ", columnas: " + lastCellNum
                  )
                  val rowData = (0 until lastCellNum).toArray.map { index =>
                    println("Leyendo Index: " + index)
                    index match {
                      case 5 | 6 | 20 | 26 | 40 =>
                        val celda = row.getCell(
                          index,
                          Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        )
                        if (celda != null) {
                          val value = celda.getDateCellValue()
                          if (value != null) {
                            sdf.format(value)
                          } else {
                            ""
                          }
                        } else {
                          ""
                        }
                      case 3 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 | 16 |
                          17 | 18 | 19 | 21 | 22 | 23 | 24 | 27 | 29 | 30 | 31 |
                          42 =>
                        val celda = row.getCell(
                          index,
                          Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        )
                        if (celda != null) {
                          val value = celda.getNumericCellValue()
                          value.toString
                        } else {
                          ""
                        }
                      case 60 =>
                        val celda = row.getCell(
                          index,
                          Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                        )
                        if (celda != null) {
                          val value = celda.getNumericCellValue()
                          println("Double value 55: " + value)
                          value.toString()
                        } else {
                          ""
                        }
                      case _ =>
                        removeLineBreakingChars(
                          df.formatCellValue(
                            row.getCell(
                              index,
                              Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                            ),
                            formulaEvaluator
                          )
                        )
                    }
                  }
                  escribirFila2(rowData, a, m)
                }
              } catch {
                case NonFatal(th) =>
                  msg = th
                    .getMessage() + ", [Linea " + (filaPos) + "]" + " [Index: " + index + "]"
                  exito = false
                  println("error msg: " + msg)
                // th.printStackTrace()
                case th if NonFatal(th) => println("error:" + th.getMessage())
              }
            }
        }
        registerNewStatus(
          new ProcessEvent("0", "0", "3", "carga" + tipo + "DoneEvent"),
          sessionUUID
        )
        println(
          "Fin Carga Cartera: " + sdf.format(Calendar.getInstance().getTime())
        )
    }
    pe.withConnection { implicit connection =>
      SQL("""INSERT INTO CONTROL_CARGA 
                    (
                        control_carga_anho, 
                        control_carga_periodo,
                        control_carga_fecha,
                        id_empleado,
                        control_carga_tipo,
                        control_carga_estado,
                        control_carga_registros
                    ) VALUES (
                        {control_carga_anho}, 
                        {control_carga_periodo},
                        {control_carga_fecha},
                        {id_empleado},
                        {control_carga_tipo},
                        {control_carga_estado},
                        {control_carga_registros}
                    )""")
        .on(
          "control_carga_anho" -> a,
          "control_carga_periodo" -> m,
          "control_carga_fecha" -> hoy,
          "id_empleado" -> usua_alias,
          "control_carga_tipo" -> tipo,
          "control_carga_estado" -> (if (exito) {
                                       1
                                     } else {
                                       0
                                     }),
          "control_carga_registros" -> (filaPos - 1)
        )
        .executeUpdate()
    }
    exito
  }

  def loadData2CarteraCsv(
      a: Int,
      m: Int,
      usua_alias: Long,
      uuid: String
  ): Boolean = {
    val tipo = 2
    val hoy: LocalDateTime = new LocalDateTime(
      Calendar.getInstance().getTimeInMillis()
    )
    val path = System.getProperty("java.io.tmpdir")
    val filenameone =
      "file_" + a + "_" + m + "_" + tipo + ".csv"
    val filestringone = path + "/cargadata/" + filenameone
    var lastEventTime = Calendar.getInstance().getTime()
    var currentEventTime = Calendar.getInstance().getTime()
    var exito = true
    var msg = "Carga Exitosa"
    val sessionUUID = UUID.fromString(uuid)
    var headersLength = 0
    var isHeadersWritten = false
    var filaPos = 0
    registerNewStatus(
      new ProcessEvent("0", "0", "1", "carga" + tipo + "PreparingEvent"),
      sessionUUID
    )
    val src = Source.fromFile(filestringone)
    val _list = src.getLines().toList // .drop(1) //.map(_.split(";"))
    val totalRow = _list.length
    _list.map { row =>
      //try {
      filaPos += 1
      currentEventTime = Calendar.getInstance().getTime()
      if ((currentEventTime.getTime() - lastEventTime
            .getTime()) / 1000 >= 1) {
        registerNewStatus(
          new ProcessEvent(
            totalRow.toString(),
            filaPos.toString(),
            "2",
            "carga" + tipo + "ParsingEvent"
          ),
          sessionUUID
        )
        lastEventTime = currentEventTime
      }
      val rowData = row.split(";")
      val lastCellNum = rowData.length
      println(
        "Evaluando fila: " + filaPos + ", columnas: " + lastCellNum
      )
      if (filaPos >= 2) {
        escribirFila2(rowData, a, m)
      }
    }
    registerNewStatus(
      new ProcessEvent("0", "0", "3", "carga" + tipo + "DoneEvent"),
      sessionUUID
    )
    println(
      "Fin Carga Cartera: " + sdf.format(Calendar.getInstance().getTime())
    )
    pe.withConnection { implicit connection =>
      SQL("""INSERT INTO CONTROL_CARGA 
                    (
                        control_carga_anho, 
                        control_carga_periodo,
                        control_carga_fecha,
                        id_empleado,
                        control_carga_tipo,
                        control_carga_estado,
                        control_carga_registros
                    ) VALUES (
                        {control_carga_anho}, 
                        {control_carga_periodo},
                        {control_carga_fecha},
                        {id_empleado},
                        {control_carga_tipo},
                        {control_carga_estado},
                        {control_carga_registros}
                    )""")
        .on(
          "control_carga_anho" -> a,
          "control_carga_periodo" -> m,
          "control_carga_fecha" -> hoy,
          "id_empleado" -> usua_alias,
          "control_carga_tipo" -> tipo,
          "control_carga_estado" -> (if (exito) {
                                       1
                                     } else {
                                       0
                                     }),
          "control_carga_registros" -> (filaPos - 1)
        )
        .executeUpdate()
    }
    exito
  }

  def escribirFila2(fila: Array[String], anho: Int, mes: Int): Unit = {
    val usuario_tipo_identificacion = fila(0) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val usuario_id_persona = fila(1) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val cartera_codigo_contable = fila(2) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val cartera_modificacion_credito = fila(3) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val cartera_id_colocacion = fila(4) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val cartera_fecha_desembolso = fila(5) match {
      case "" => Option.empty[Long]
      case x  => Some((DateTime.parse(x.trim(), dateFormat)).getMillis())
    }
    val cartera_fecha_vencimiento = fila(6) match {
      case "" => Option.empty[Long]
      case x  => Some((DateTime.parse(x.trim(), dateFormat)).getMillis())
    }
    val cartera_morosidad = fila(7) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_tipo_cuota = fila(8) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_altura_cuota = fila(9) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_amorti_interes = fila(10) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_modalidad = fila(11) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_tasa_nominal = fila(12) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_tasa_efectiva = fila(13) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_valor_prestamo = fila(14) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_valor_cuota = fila(15) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_saldo_capital = fila(16) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_saldo_interes = fila(17) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_otros_saldos = fila(18) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_garantia = fila(19) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_fecha_avaluo = fila(20) match {
      case "" => Option.empty[Long]
      case x  => Some(DateTime.parse(x, dateFormat).getMillis())
    }
    val cartera_provision_capital = fila(21) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_provision_interes = fila(22) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_contingencia = fila(23) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_valor_cuota_extra = fila(24) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_meses_cuota_extra = fila(25) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_fecha_ultimo_pago = fila(26) match {
      case "" => Option.empty[Long]
      case x  => Some((DateTime.parse(x.trim(), dateFormat)).getMillis())
    }
    val cartera_clase_garantia = fila(27) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_destino_credito = fila(28) match {
      case "" => Option.empty[String]
      case x  => Some(x.trim())
    }
    val cartera_codigo_oficina = fila(29) match {
      case "" => Option.empty[String]
      case x  => Some(x.trim())
    }
    val cartera_amortiza_capital = fila(30) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_valor_mora = fila(31) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_tipo_vivienda = fila(32) match {
      case "" => Option.empty[String]
      case x  => Some(x.trim())
    }
    val cartera_vis = fila(33) match {
      case "" => Option.empty[String]
      case x  => Some(x.trim())
    }
    val cartera_rango_tipo = fila(34) match {
      case "" => Option.empty[String]
      case x  => Some(x.trim())
    }
    val cartera_entidad_redescuento = fila(35) match {
      case "" => Option.empty[String]
      case x  => Some(x.trim())
    }
    val cartera_margen_redescuento = fila(36) match {
      case "" => Option.empty[String]
      case x  => Some(x.trim())
    }
    val cartera_subsidio = fila(37) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_desembolso = fila(38) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_moneda = fila(39) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_fecha_reestructuracion = fila(40) match {
      case "" => Option.empty[Long]
      case x  => Some((DateTime.parse(x.trim(), dateFormat)).getMillis())
    }
    val cartera_cate_reestructuracion = fila(41) match {
      case "" => Option.empty[String]
      case x  => Some(x.trim())
    }
    val cartera_aporte_social = fila(42) match {
      case "" => Option.empty[Double]
      case x  => Some(x.trim().toDouble)
    }
    val cartera_linea = fila(43) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_numero_modificacion = fila(44) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_estado_credito = fila(45) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_nit_patronal = fila(46) match {
      case "" => Option.empty[String]
      case x  => Some(x.trim())
    }
    val cartera_nombre_patronal = fila(47) match {
      case "" => Option.empty[String]
      case x  => Some(x.trim())
    }
    val cartera_modcrece1120 = fila(48) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_tipomodce1120 = fila(49) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_fechamodce1120 = fila(50) match {
      case "" => Option.empty[Long]
      case x  => Some((DateTime.parse(x.trim(), dateFormat)).getMillis())
    }
    val cartera_califantemodce1120 = fila(51) match {
      case "" => Option.empty[String]
      case x  => Some(x.trim())
    }
    val cartera_periodo_gracia = fila(52) match {
      case "" => Option.empty[String]
      case x  => Some(x.trim())
    }
    val cartera_tarjcrecredpro = fila(53) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_entotogarant = fila(54) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_modcredce1720 = fila(55) match {
      case "" => Option.empty[Int]
      case x  => Some(x.trim().toInt)
    }
    val cartera_fecha_corte = Some("%04d%02d".format(anho, mes))
    pe.withConnection { implicit connection =>
      SQL("""INSERT INTO CARTERA (
            USUARIO_TIPO_IDENTIFICACION,
            USUARIO_ID_PERSONA,
            CARTERA_CODIGO_CONTABLE,
            CARTERA_MODIFICACION_CREDITO,
            CARTERA_ID_COLOCACION,
            CARTERA_FECHA_DESEMBOLSO,
            CARTERA_FECHA_VENCIMIENTO,
            CARTERA_MOROSIDAD,
            CARTERA_TIPO_CUOTA,
            CARTERA_ALTURA_CUOTA,
            CARTERA_AMORTI_INTERES,
            CARTERA_MODALIDAD,
            CARTERA_TASA_NOMINAL,
            CARTERA_TASA_EFECTIVA,
            CARTERA_VALOR_PRESTAMO,
            CARTERA_VALOR_CUOTA,
            CARTERA_SALDO_CAPITAL,
            CARTERA_SALDO_INTERES,
            CARTERA_OTROS_SALDOS,
            CARTERA_GARANTIA,
            CARTERA_FECHA_AVALUO,
            CARTERA_PROVISION_CAPITAL,
            CARTERA_PROVISION_INTERES,
            CARTERA_CONTINGENCIA,
            CARTERA_VALOR_CUOTA_EXTRA,
            CARTERA_MESES_CUOTA_EXTRA,
            CARTERA_FECHA_ULTIMO_PAGO,
            CARTERA_CLASE_GARANTIA,
            CARTERA_DESTINO_CREDITO,
            CARTERA_CODIGO_OFICINA,
            CARTERA_AMORTIZA_CAPITAL,
            CARTERA_VALOR_MORA,
            CARTERA_TIPO_VIVIENDA,
            CARTERA_VIS,
            CARTERA_RANGO_TIPO,
            CARTERA_ENTIDAD_REDESCUENTO,
            CARTERA_MARGEN_REDESCUENTO,
            CARTERA_SUBSIDIO,
            CARTERA_DESEMBOLSO,
            CARTERA_MONEDA,
            CARTERA_FECHA_REESTRUCTURACION,
            CARTERA_CATE_REESTRUCURACION,
            CARTERA_APORTE_SOCIAL,
            CARTERA_LINEA,
            CARTERA_NUMERO_MODIFICACION,
            CARTERA_ESTADO_CREDITO,
            CARTERA_NIT_PATRONAL,
            CARTERA_NOMBRE_PATRONAL,
            CARTERA_MODCRECE1120,
            CARTERA_TIPOMODCE1120,
            CARTERA_FECHAMODCE1120,
            CARTERA_CALIFANTEMODCE1120,
            CARTERA_PERIODO_GRACIA,
            CARTERA_TARJCREDCUPRO,
            CARTERA_ENTOTOGARANT,
            CARTERA_MODCREDCE1720,
            CARTERA_FECHA_CORTE,
            CREATED_AT) VALUES (
            {usuario_tipo_identificacion},
            {usuario_id_persona},
            {cartera_codigo_contable},
            {cartera_modificacion_credito},
            {cartera_id_colocacion},
            {cartera_fecha_desembolso},
            {cartera_fecha_vencimiento},
            {cartera_morosidad},
            {cartera_tipo_cuota},
            {cartera_altura_cuota},
            {cartera_amorti_interes},
            {cartera_modalidad},
            {cartera_tasa_nominal},
            {cartera_tasa_efectiva},
            {cartera_valor_prestamo},
            {cartera_valor_cuota},
            {cartera_saldo_capital},
            {cartera_saldo_interes},
            {cartera_otros_saldos},
            {cartera_garantia},
            {cartera_fecha_avaluo},
            {cartera_provision_capital},
            {cartera_provision_interes},
            {cartera_contingencia},
            {cartera_valor_cuota_extra},
            {cartera_meses_cuota_extra},
            {cartera_fecha_ultimo_pago},
            {cartera_clase_garantia},
            {cartera_destino_credito},
            {cartera_codigo_oficina},
            {cartera_amortiza_capital},
            {cartera_valor_mora},
            {cartera_tipo_vivienda},
            {cartera_vis},
            {cartera_rango_tipo},
            {cartera_entidad_redescuento},
            {cartera_margen_redescuento},
            {cartera_subsidio},
            {cartera_desembolso},
            {cartera_moneda},
            {cartera_fecha_reestructuracion},
            {cartera_cate_reestructuracion},
            {cartera_aporte_social},
            {cartera_linea},
            {cartera_numero_modificacion},
            {cartera_estado_credito},
            {cartera_nit_patronal},
            {cartera_nombre_patronal},
            {cartera_modcrece1120},
            {cartera_tipomodce1120},
            {cartera_fechamodce1120},
            {cartera_califantemodce1120},
            {cartera_periodo_gracia},
            {cartera_tarjcrecredpro},
            {cartera_entotogarant},
            {cartera_modcredce1720},
            {cartera_fecha_corte},
            {created_at})""")
        .on(
          "usuario_tipo_identificacion" -> usuario_tipo_identificacion,
          "usuario_id_persona" -> usuario_id_persona,
          "cartera_codigo_contable" -> cartera_codigo_contable,
          "cartera_modificacion_credito" -> cartera_modificacion_credito,
          "cartera_id_colocacion" -> cartera_id_colocacion,
          "cartera_fecha_desembolso" -> cartera_fecha_desembolso,
          "cartera_fecha_vencimiento" -> cartera_fecha_vencimiento,
          "cartera_morosidad" -> cartera_morosidad,
          "cartera_tipo_cuota" -> cartera_tipo_cuota,
          "cartera_altura_cuota" -> cartera_altura_cuota,
          "cartera_amorti_interes" -> cartera_amorti_interes,
          "cartera_modalidad" -> cartera_modalidad,
          "cartera_tasa_nominal" -> cartera_tasa_nominal,
          "cartera_tasa_efectiva" -> cartera_tasa_efectiva,
          "cartera_valor_prestamo" -> cartera_valor_prestamo,
          "cartera_valor_cuota" -> cartera_valor_cuota,
          "cartera_saldo_capital" -> cartera_saldo_capital,
          "cartera_saldo_interes" -> cartera_saldo_interes,
          "cartera_otros_saldos" -> cartera_otros_saldos,
          "cartera_garantia" -> cartera_garantia,
          "cartera_fecha_avaluo" -> cartera_fecha_avaluo,
          "cartera_provision_capital" -> cartera_provision_capital,
          "cartera_provision_interes" -> cartera_provision_interes,
          "cartera_contingencia" -> cartera_contingencia,
          "cartera_valor_cuota_extra" -> cartera_valor_cuota_extra,
          "cartera_meses_cuota_extra" -> cartera_meses_cuota_extra,
          "cartera_fecha_ultimo_pago" -> cartera_fecha_ultimo_pago,
          "cartera_clase_garantia" -> cartera_clase_garantia,
          "cartera_destino_credito" -> cartera_destino_credito,
          "cartera_codigo_oficina" -> cartera_codigo_oficina,
          "cartera_amortiza_capital" -> cartera_amortiza_capital,
          "cartera_valor_mora" -> cartera_valor_mora,
          "cartera_tipo_vivienda" -> cartera_tipo_vivienda,
          "cartera_vis" -> cartera_vis,
          "cartera_rango_tipo" -> cartera_rango_tipo,
          "cartera_entidad_redescuento" -> cartera_entidad_redescuento,
          "cartera_margen_redescuento" -> cartera_margen_redescuento,
          "cartera_subsidio" -> cartera_subsidio,
          "cartera_desembolso" -> cartera_desembolso,
          "cartera_moneda" -> cartera_moneda,
          "cartera_fecha_reestructuracion" -> cartera_fecha_reestructuracion,
          "cartera_cate_reestructuracion" -> cartera_cate_reestructuracion,
          "cartera_aporte_social" -> cartera_aporte_social,
          "cartera_linea" -> cartera_linea,
          "cartera_numero_modificacion" -> cartera_numero_modificacion,
          "cartera_estado_credito" -> cartera_estado_credito,
          "cartera_nit_patronal" -> cartera_nit_patronal,
          "cartera_nombre_patronal" -> cartera_nombre_patronal,
          "cartera_modcrece1120" -> cartera_modcrece1120,
          "cartera_tipomodce1120" -> cartera_tipomodce1120,
          "cartera_fechamodce1120" -> cartera_fechamodce1120,
          "cartera_califantemodce1120" -> cartera_califantemodce1120,
          "cartera_periodo_gracia" -> cartera_periodo_gracia,
          "cartera_tarjcrecredpro" -> cartera_tarjcrecredpro,
          "cartera_entotogarant" -> cartera_entotogarant,
          "cartera_modcredce1720" -> cartera_modcredce1720,
          "cartera_fecha_corte" -> cartera_fecha_corte,
          "created_at" -> Calendar.getInstance().getTimeInMillis()
        )
        .executeUpdate()
    }
  }

  def loadData3Deposito(
      a: Int,
      m: Int,
      usua_alias: Long,
      uuid: String
  ): Boolean = {
    val tipo = 3
    val hoy: LocalDateTime = new LocalDateTime(
      Calendar.getInstance().getTimeInMillis()
    )
    val path = System.getProperty("java.io.tmpdir")
    val newTempDir = new File(path, "cargadata")
    val filenameone =
      "file_" + a + "_" + m + "_" + tipo + ".xlsx"
    val filestringone = path + "/cargadata/" + filenameone
    val fileone = new File(filestringone)
    var lastEventTime = Calendar.getInstance().getTime()
    var currentEventTime = Calendar.getInstance().getTime()
    var exito = true
    var msg = "Carga Exitosa"
    val sessionUUID = UUID.fromString(uuid)
    println("token a usar: " + uuid)
    var headersLength = 0
    var isHeadersWritten = false
    var filaPos = 0
    val wb = WorkbookFactory.create(fileone)
    val formulaEvaluator: FormulaEvaluator =
      wb.getCreationHelper().createFormulaEvaluator()
    registerNewStatus(
      new ProcessEvent("0", "0", "1", "carga" + tipo + "PreparingEvent"),
      sessionUUID
    )
    wb.asScala.zipWithIndex.map {
      case (sheet, index) =>
        // val sheet = wb.getSheetAt(0)
        println("Inicio Carga: " + sdf.format(Calendar.getInstance().getTime()))
        val totalRow = sheet.getLastRowNum().toString
        println("Filas: " + totalRow)
        sheet.asScala.foreach {
          case row =>
            if (Option(row).isDefined) {
              try {
                filaPos += 1
                currentEventTime = Calendar.getInstance().getTime()
                if ((currentEventTime.getTime() - lastEventTime
                      .getTime()) / 1000 >= 1) {
                  registerNewStatus(
                    new ProcessEvent(
                      totalRow.toString(),
                      filaPos.toString(),
                      "2",
                      "carga" + tipo + "ParsingEvent"
                    ),
                    sessionUUID
                  )
                  lastEventTime = currentEventTime
                }
                if (filaPos >= 2) {
                  val rowData = (0 until row.getLastCellNum).toArray.map {
                    index =>
                      index match {
                        case 6 | 8 | 17 =>
                          sdf.format(
                            row
                              .getCell(index)
                              .getDateCellValue()
                          )
                        case _ =>
                          removeLineBreakingChars(
                            df.formatCellValue(
                              row.getCell(
                                index,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                              ),
                              formulaEvaluator
                            )
                          )
                      }
                  }
                  escribirFila3(rowData, a, m)
                }
              } catch {
                case NonFatal(th) =>
                  msg = th.getMessage() + ", [Linea " + (filaPos) + "]"
                  exito = false
                  th.printStackTrace()
              }
            }
        }
        registerNewStatus(
          new ProcessEvent("0", "0", "3", "carga" + tipo + "DoneEvent"),
          sessionUUID
        )
        println(
          "Fin Carga Deposito: " + sdf.format(Calendar.getInstance().getTime())
        )
    }
    pe.withConnection { implicit connection =>
      SQL("""INSERT INTO CONTROL_CARGA 
                    (
                        control_carga_anho, 
                        control_carga_periodo,
                        control_carga_fecha,
                        id_empleado,
                        control_carga_tipo,
                        control_carga_estado,
                        control_carga_registros
                    ) VALUES (
                        {control_carga_anho}, 
                        {control_carga_periodo},
                        {control_carga_fecha},
                        {id_empleado},
                        {control_carga_tipo},
                        {control_carga_estado},
                        {control_carga_registros}
                    )""")
        .on(
          "control_carga_anho" -> a,
          "control_carga_periodo" -> m,
          "control_carga_fecha" -> hoy,
          "id_empleado" -> usua_alias,
          "control_carga_tipo" -> tipo,
          "control_carga_estado" -> (if (exito) {
                                       1
                                     } else {
                                       0
                                     }),
          "control_carga_registros" -> (filaPos - 1)
        )
        .executeUpdate()
    }
    exito
  }

  def loadData3DepositoCsv(
      a: Int,
      m: Int,
      usua_alias: Long,
      uuid: String
  ): Boolean = {
    val tipo = 3
    val hoy: LocalDateTime = new LocalDateTime(
      Calendar.getInstance().getTimeInMillis()
    )
    val path = System.getProperty("java.io.tmpdir")
    val filenameone =
      "file_" + a + "_" + m + "_" + tipo + ".csv"
    val filestringone = path + "/cargadata/" + filenameone
    var lastEventTime = Calendar.getInstance().getTime()
    var currentEventTime = Calendar.getInstance().getTime()
    var exito = true
    var msg = "Carga Exitosa"
    val sessionUUID = UUID.fromString(uuid)
    var headersLength = 0
    var isHeadersWritten = false
    var filaPos = 0
    registerNewStatus(
      new ProcessEvent("0", "0", "1", "carga" + tipo + "PreparingEvent"),
      sessionUUID
    )
    val src = Source.fromFile(filestringone)
    val _list = src.getLines().toList // .drop(1) //.map(_.split(";"))
    val totalRow = _list.length
    _list.map { row =>
      //try {
      filaPos += 1
      currentEventTime = Calendar.getInstance().getTime()
      if ((currentEventTime.getTime() - lastEventTime
            .getTime()) / 1000 >= 1) {
        registerNewStatus(
          new ProcessEvent(
            totalRow.toString(),
            filaPos.toString(),
            "2",
            "carga" + tipo + "ParsingEvent"
          ),
          sessionUUID
        )
        lastEventTime = currentEventTime
      }
      val rowData = row.split(";")
      val lastCellNum = rowData.length
      println(
        "Evaluando fila: " + filaPos + ", columnas: " + lastCellNum
      )
      if (filaPos >= 2) {
        escribirFila3(rowData, a, m)
      }
    }
    registerNewStatus(
      new ProcessEvent("0", "0", "3", "carga" + tipo + "DoneEvent"),
      sessionUUID
    )
    println(
      "Fin Carga Cartera: " + sdf.format(Calendar.getInstance().getTime())
    )
    pe.withConnection { implicit connection =>
      SQL("""INSERT INTO CONTROL_CARGA 
                    (
                        control_carga_anho, 
                        control_carga_periodo,
                        control_carga_fecha,
                        id_empleado,
                        control_carga_tipo,
                        control_carga_estado,
                        control_carga_registros
                    ) VALUES (
                        {control_carga_anho}, 
                        {control_carga_periodo},
                        {control_carga_fecha},
                        {id_empleado},
                        {control_carga_tipo},
                        {control_carga_estado},
                        {control_carga_registros}
                    )""")
        .on(
          "control_carga_anho" -> a,
          "control_carga_periodo" -> m,
          "control_carga_fecha" -> hoy,
          "id_empleado" -> usua_alias,
          "control_carga_tipo" -> tipo,
          "control_carga_estado" -> (if (exito) {
                                       1
                                     } else {
                                       0
                                     }),
          "control_carga_registros" -> (filaPos - 1)
        )
        .executeUpdate()
    }
    exito
  }

  def escribirFila3(fila: Array[String], anho: Int, mes: Int): Unit = {
    val usuario_tipo_identificacion = fila(0) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val usuario_id_persona = fila(1) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val deposito_codigo_contable = fila(2) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val deposito_nombre = fila(3) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val deposito_tipo_ahorro = fila(4) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val deposito_amortizacion = fila(5) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val deposito_fecha_apertura = fila(6) match {
      case "" => Option.empty[Long]
      case v  => Some(DateTime.parse(v, dateFormat).getMillis())
    }
    val deposito_plazo = fila(7) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val deposito_fecha_vencimiento = fila(8) match {
      case "" => Option.empty[Long]
      case v  => Some(DateTime.parse(v, dateFormat).getMillis())
    }
    val deposito_modalidad = fila(9) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val deposito_tasa_nominal = fila(10) match {
      case "" => Option.empty[Double]
      case v  => Some(v.trim().toDouble)
    }
    val deposito_tasa_efectiva = fila(11) match {
      case "" => Option.empty[Double]
      case v  => Some(v.trim().toDouble)
    }
    val deposito_interes_causado = fila(12) match {
      case "" => Option.empty[Double]
      case v  => Some(v.trim().toDouble)
    }
    val deposito_saldo = fila(13) match {
      case "" => Option.empty[Double]
      case v  => Some(v.trim().toDouble)
    }
    val deposito_inicial = fila(14) match {
      case "" => Option.empty[Double]
      case v  => Some(v.trim().toDouble)
    }
    val deposito_numero_cuenta = fila(15) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val deposito_excenta_gmf = fila(16) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val deposito_fecha_aceptacion = fila(17) match {
      case "" => Option.empty[Long]
      case v  => Some(DateTime.parse(v, dateFormat).getMillis())
    }
    val deposito_estado = fila(18) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val deposito_bajo_monto = fila(19) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val deposito_cotitulares = fila(20) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val deposito_conjunta = fila(21) match {
      case "" => Option.empty[Int]
      case v  => Some(v.trim().toInt)
    }
    val deposito_fecha_corte = Some("%04d%02d".format(anho, mes))
    val created_at = Some(Calendar.getInstance().getTime)
    pe.withConnection { implicit connection =>
      SQL("""INSERT INTO DEPOSITO (
              USUARIO_TIPO_IDENTIFICACION,
              USUARIO_ID_PERSONA,
              DEPOSITO_CODIGO_CONTABLE,
              DEPOSITO_NOMBRE,
              DEPOSITO_TIPO_AHORRO,
              DEPOSITO_AMORTIZACION,
              DEPOSITO_FECHA_APERTURA,
              DEPOSITO_PLAZO,
              DEPOSITO_FECHA_VENCIMIENTO,
              DEPOSITO_MODALIDAD,
              DEPOSITO_TASA_NOMINAL,
              DEPOSITO_TASA_EFECTIVA,
              DEPOSITO_INTERES_CAUSADO,
              DEPOSITO_SALDO,
              DEPOSITO_INICIAL,
              DEPOSITO_NUMERO_CUENTA,
              DEPOSITO_EXCENTA_GMF,
              DEPOSITO_FECHA_ACEPTACION,
              DEPOSITO_ESTADO,
              DEPOSITO_BAJO_MONTO,
              DEPOSITO_COTITULARES,
              DEPOSITO_CONJUNTA,
              DEPOSITO_FECHA_CORTE,
            CREATED_AT) VALUES (
              {usuario_tipo_identificacion},
              {usuario_id_persona},
              {deposito_codigo_contable},
              {deposito_nombre},
              {deposito_tipo_ahorro},
              {deposito_amortizacion},
              {deposito_fecha_apertura},
              {deposito_plazo},
              {deposito_fecha_vencimiento},
              {deposito_modalidad},
              {deposito_tasa_nominal},
              {deposito_tasa_efectiva},
              {deposito_interes_causado},
              {deposito_saldo},
              {deposito_inicial},
              {deposito_numero_cuenta},
              {deposito_excenta_gmf},
              {deposito_fecha_aceptacion},
              {deposito_estado},
              {deposito_bajo_monto},
              {deposito_cotitulares},
              {deposito_conjunta},
              {deposito_fecha_corte},            
            {created_at}
                )""")
        .on(
          "usuario_tipo_identificacion" -> usuario_tipo_identificacion,
          "usuario_id_persona" -> usuario_id_persona,
          "deposito_codigo_contable" -> deposito_codigo_contable,
          "deposito_nombre" -> deposito_nombre,
          "deposito_tipo_ahorro" -> deposito_tipo_ahorro,
          "deposito_amortizacion" -> deposito_amortizacion,
          "deposito_fecha_apertura" -> deposito_fecha_apertura,
          "deposito_plazo" -> deposito_plazo,
          "deposito_fecha_vencimiento" -> deposito_fecha_vencimiento,
          "deposito_modalidad" -> deposito_modalidad,
          "deposito_tasa_nominal" -> deposito_tasa_nominal,
          "deposito_tasa_efectiva" -> deposito_tasa_efectiva,
          "deposito_interes_causado" -> deposito_interes_causado,
          "deposito_saldo" -> deposito_saldo,
          "deposito_inicial" -> deposito_inicial,
          "deposito_numero_cuenta" -> deposito_numero_cuenta,
          "deposito_excenta_gmf" -> deposito_excenta_gmf,
          "deposito_fecha_aceptacion" -> deposito_fecha_aceptacion,
          "deposito_estado" -> deposito_estado,
          "deposito_bajo_monto" -> deposito_bajo_monto,
          "deposito_cotitulares" -> deposito_cotitulares,
          "deposito_conjunta" -> deposito_conjunta,
          "deposito_fecha_corte" -> deposito_fecha_corte,
          "created_at" -> Calendar.getInstance().getTime()
        )
        .executeUpdate()
    }
  }

  def loadData4Aporte(
      a: Int,
      m: Int,
      usua_alias: Long,
      uuid: String
  ): Future[Boolean] = Future {
    val tipo = 4
    val hoy: LocalDateTime = new LocalDateTime(
      Calendar.getInstance().getTimeInMillis()
    )
    val path = System.getProperty("java.io.tmpdir")
    val newTempDir = new File(path, "cargadata")
    val filenameone =
      "file_" + a + "_" + m + "_" + tipo + ".xlsx"
    val filestringone = path + "/cargadata/" + filenameone
    val fileone = new File(filestringone)
    var lastEventTime = Calendar.getInstance().getTime()
    var currentEventTime = Calendar.getInstance().getTime()
    var exito = true
    var msg = "Carga Exitosa"
    val sessionUUID = UUID.fromString(uuid)
    println("token a usar: " + uuid)
    var headersLength = 0
    var isHeadersWritten = false
    var filaPos = 0
    val wb = WorkbookFactory.create(fileone)
    val formulaEvaluator: FormulaEvaluator =
      wb.getCreationHelper().createFormulaEvaluator()
    registerNewStatus(
      new ProcessEvent("0", "0", "1", "carga" + tipo + "PreparingEvent"),
      sessionUUID
    )
    wb.asScala.zipWithIndex.map {
      case (sheet, index) =>
        // val sheet = wb.getSheetAt(0)
        println("Inicio Carga: " + sdf.format(Calendar.getInstance().getTime()))
        val totalRow = sheet.getLastRowNum().toString
        println("Filas: " + totalRow)
        sheet.asScala.foreach {
          case row =>
            if (Option(row).isDefined) {
              try {
                filaPos += 1
                currentEventTime = Calendar.getInstance().getTime()
                if ((currentEventTime.getTime() - lastEventTime
                      .getTime()) / 1000 >= 1) {
                  registerNewStatus(
                    new ProcessEvent(
                      totalRow.toString(),
                      filaPos.toString(),
                      "2",
                      "carga" + tipo + "ParsingEvent"
                    ),
                    sessionUUID
                  )
                  lastEventTime = currentEventTime
                }
                if (filaPos >= 5) {
                  val rowData = (0 until row.getLastCellNum).toArray.map {
                    index =>
                      index match {
                        case 8 =>
                          sdf.format(
                            row
                              .getCell(index)
                              .getDateCellValue()
                          )
                        case _ =>
                          removeLineBreakingChars(
                            df.formatCellValue(
                              row.getCell(
                                index,
                                Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                              ),
                              formulaEvaluator
                            )
                          )
                      }
                  }
                  escribirFila4(rowData, a, m)
                }
              } catch {
                case NonFatal(th) =>
                  msg = th.getMessage() + ", [Linea " + (filaPos) + "]"
                  exito = false
                  th.printStackTrace()
              }
            }
        }
        registerNewStatus(
          new ProcessEvent("0", "0", "3", "carga" + tipo + "DoneEvent"),
          sessionUUID
        )
        println(
          "Fin Carga Aporte: " + sdf.format(Calendar.getInstance().getTime())
        )
    }
    pe.withConnection { implicit connection =>
      SQL("""INSERT INTO CONTROL_CARGA 
                    (
                        control_carga_anho, 
                        control_carga_periodo,
                        control_carga_fecha,
                        id_empleado,
                        control_carga_tipo,
                        control_carga_estado,
                        control_carga_registros
                    ) VALUES (
                        {control_carga_anho}, 
                        {control_carga_periodo},
                        {control_carga_fecha},
                        {id_empleado},
                        {control_carga_tipo},
                        {control_carga_estado},
                        {control_carga_registros}
                    )""")
        .on(
          "control_carga_anho" -> a,
          "control_carga_periodo" -> m,
          "control_carga_fecha" -> hoy,
          "id_empleado" -> usua_alias,
          "control_carga_tipo" -> tipo,
          "control_carga_estado" -> (if (exito) {
                                       1
                                     } else {
                                       0
                                     }),
          "control_carga_registros" -> (filaPos - 4)
        )
        .executeUpdate()
    }
    exito
  }

  def loadData4AporteCsv(
      a: Int,
      m: Int,
      usua_alias: Long,
      uuid: String
  ): Boolean = {
    val tipo = 4
    val hoy: LocalDateTime = new LocalDateTime(
      Calendar.getInstance().getTimeInMillis()
    )
    val path = System.getProperty("java.io.tmpdir")
    val filenameone =
      "file_" + a + "_" + m + "_" + tipo + ".csv"
    val filestringone = path + "/cargadata/" + filenameone
    var lastEventTime = Calendar.getInstance().getTime()
    var currentEventTime = Calendar.getInstance().getTime()
    var exito = true
    var msg = "Carga Exitosa"
    val sessionUUID = UUID.fromString(uuid)
    var headersLength = 0
    var isHeadersWritten = false
    var filaPos = 0
    registerNewStatus(
      new ProcessEvent("0", "0", "1", "carga" + tipo + "PreparingEvent"),
      sessionUUID
    )
    val src = Source.fromFile(filestringone)
    val _list = src.getLines().toList // .drop(1) //.map(_.split(";"))
    val totalRow = _list.length
    _list.map { row =>
      //try {
      filaPos += 1
      currentEventTime = Calendar.getInstance().getTime()
      if ((currentEventTime.getTime() - lastEventTime
            .getTime()) / 1000 >= 1) {
        registerNewStatus(
          new ProcessEvent(
            totalRow.toString(),
            filaPos.toString(),
            "2",
            "carga" + tipo + "ParsingEvent"
          ),
          sessionUUID
        )
        lastEventTime = currentEventTime
      }
      val rowData = row.split(";")
      val lastCellNum = rowData.length
      println(
        "Evaluando fila: " + filaPos + ", columnas: " + lastCellNum
      )
      if (filaPos >= 5) {
        escribirFila4(rowData, a, m)
      }
    }
    registerNewStatus(
      new ProcessEvent("0", "0", "3", "carga" + tipo + "DoneEvent"),
      sessionUUID
    )
    println(
      "Fin Carga Cartera: " + sdf.format(Calendar.getInstance().getTime())
    )
    pe.withConnection { implicit connection =>
      SQL("""INSERT INTO CONTROL_CARGA 
                    (
                        control_carga_anho, 
                        control_carga_periodo,
                        control_carga_fecha,
                        id_empleado,
                        control_carga_tipo,
                        control_carga_estado,
                        control_carga_registros
                    ) VALUES (
                        {control_carga_anho}, 
                        {control_carga_periodo},
                        {control_carga_fecha},
                        {id_empleado},
                        {control_carga_tipo},
                        {control_carga_estado},
                        {control_carga_registros}
                    )""")
        .on(
          "control_carga_anho" -> a,
          "control_carga_periodo" -> m,
          "control_carga_fecha" -> hoy,
          "id_empleado" -> usua_alias,
          "control_carga_tipo" -> tipo,
          "control_carga_estado" -> (if (exito) {
                                       1
                                     } else {
                                       0
                                     }),
          "control_carga_registros" -> (filaPos - 1)
        )
        .executeUpdate()
    }
    exito
  }

  def escribirFila4(fila: Array[String], anho: Int, mes: Int): Unit = {
    val usuario_tipo_identificacion = fila(0) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val usuario_id_persona = fila(1) match {
      case "" => Option.empty[String]
      case v  => Some(v.trim())
    }
    val aporte_saldo = fila(2) match {
      case "" => Option.empty[Double]
      case v =>
        Some(v.trim().toDouble)
    }
    val aporte_mensual = fila(3) match {
      case "" => Option.empty[Double]
      case v  => Some(v.trim().toDouble)
    }
    val aporte_ordinario = fila(4) match {
      case "" => Option.empty[Double]
      case v  => Some(v.trim().toDouble)
    }
    val aporte_extraordinario = fila(5) match {
      case "" => Option.empty[Double]
      case v  => Some(v.trim().toDouble)
    }
    val aporte_revalorizacion = fila(6) match {
      case "" => Option.empty[Double]
      case v  => Some(v.trim().toDouble)
    }
    val aporte_promedio = fila(7) match {
      case "" => Option.empty[Double]
      case v  => Some(v.trim().toDouble)
    }
    val aporte_ultima_fecha = fila(8) match {
      case "" => Option.empty[Long]
      case v  => Some(DateTime.parse(v, dateFormat).getMillis())
    }
    val aporte_fecha_corte = Some("%04d%02d".format(anho, mes))
    val created_at = Some(Calendar.getInstance().getTime)
    pe.withConnection { implicit connection =>
      SQL("""INSERT INTO APORTE (
              USUARIO_TIPO_IDENTIFICACION,
              USUARIO_ID_PERSONA,
              APORTE_SALDO,
              APORTE_MENSUAL,
              APORTE_ORDINARIO,
              APORTE_EXTRAORDINARIO,
              APORTE_REVALORIZACION,
              APORTE_PROMEDIO,
              APORTE_ULTIMA_FECHA,
              APORTE_FECHA_CORTE,
            CREATED_AT) VALUES (
              {usuario_tipo_identificacion},
              {usuario_id_persona},
              {aporte_saldo},
              {aporte_mensual},
              {aporte_ordinario},
              {aporte_extraordinario},
              {aporte_revalorizacion},
              {aporte_promedio},
              {aporte_ultima_fecha},
              {aporte_fecha_corte},            
              {created_at}
                )""")
        .on(
          "usuario_tipo_identificacion" -> usuario_tipo_identificacion,
          "usuario_id_persona" -> usuario_id_persona,
          "aporte_saldo" -> aporte_saldo,
          "aporte_mensual" -> aporte_mensual,
          "aporte_ordinario" -> aporte_ordinario,
          "aporte_extraordinario" -> aporte_extraordinario,
          "aporte_revalorizacion" -> aporte_revalorizacion,
          "aporte_promedio" -> aporte_promedio,
          "aporte_ultima_fecha" -> aporte_ultima_fecha,
          "aporte_fecha_corte" -> aporte_fecha_corte,
          "created_at" -> Calendar.getInstance().getTime()
        )
        .executeUpdate()
    }
  }

  def removeLineBreakingChars(cell: String): String =
    cell.replaceAll("[\\t\\n\\r]", " ")

}
