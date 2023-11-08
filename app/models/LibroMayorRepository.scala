package models

import java.nio.file.{Files, Paths}

import javax.inject.Inject
import java.util.Calendar
import java.sql.Timestamp
import java.security.MessageDigest
import java.util.{ Map, HashMap, Date }

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import scala.util.{ Failure, Success }
import anorm._
import anorm.SqlParser.{ get, str }
import anorm.JodaParameterMetaData._
import play.api.db.DBApi
import scala.concurrent.Future
import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._
import notifiers._
import scala.collection.mutable.ListBuffer

import java.util.UUID.randomUUID
import java.text.SimpleDateFormat

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

// DataSource
import jrds.LibroMayorDS
import listeners.JrListener
import play.api.Configuration

case class PucLibro(codigo: Option[String], id_agencia: Option[Int], nombre: Option[String], saldo_inicial: Option[BigDecimal], descripcion_agencia: Option[String])
case class LibroMayor(codigo: Option[String], nombre: Option[String], saldo_inicial: Option[BigDecimal], debito: Option[BigDecimal], credito: Option[BigDecimal], saldo_final: Option[BigDecimal])
case class Saldo(debito: Option[BigDecimal], credito: Option[BigDecimal])
case class LMRelacion(lire_anho: Option[Int], lire_periodo: Option[Int], lire_consecutivo: Option[Int], lire_fecha: Option[DateTime], lire_hora: Option[DateTime])

object LMRelacion {
  implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val writes = new Writes[LMRelacion] {
    def writes(lm: LMRelacion) = Json.obj(
      "lire_anho" -> lm.lire_anho,
      "lire_periodo" -> lm.lire_periodo,
      "lire_consecutivo" -> lm.lire_consecutivo,
      "lire_fecha" -> lm.lire_fecha,
      "lire_hora" -> lm.lire_hora
    )
  }

  implicit val reads: Reads[LMRelacion] = (
    (__ \ "lire_anho").readNullable[Int] and
    (__ \ "lire_periodo").readNullable[Int] and
    (__ \ "lire_consecutivo").readNullable[Int] and
    (__ \ "lire_fecha").readNullable[DateTime] and
    (__ \ "lire_hora").readNullable[DateTime]
  )(LMRelacion.apply _)
  
  val _set = {
    get[Option[Int]]("LIRE_ANHO") ~
    get[Option[Int]]("LIRE_PERIODO") ~
    get[Option[Int]]("LIRE_CONSECUTIVO") ~
    get[Option[DateTime]]("LIRE_FECHA") ~
    get[Option[DateTime]]("LIRE_HORA") map {
      case lire_anho ~ 
           lire_periodo ~ 
           lire_consecutivo ~
           lire_fecha ~ 
           lire_hora => LMRelacion(
             lire_anho,
             lire_periodo,
             lire_consecutivo,
             lire_fecha,
             lire_hora
           )
    }
  }  
}

object PucLibro {
  implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  val _set = {
    get[Option[String]]("CODIGO") ~
    get[Option[Int]]("ID_AGENCIA") ~
    get[Option[String]]("NOMBRE") ~
    get[Option[BigDecimal]]("SALDOINICIAL") ~
    get[Option[String]]("DESCRIPCION_AGENCIA") map {
      case codigo ~ 
           id_agencia ~ 
           nombre ~ 
           saldo_inicial ~
           descripcion_agencia => PucLibro(
             codigo,
             id_agencia,
             nombre,
             saldo_inicial,
             descripcion_agencia
           )
    }
  }
}

object Saldo {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")
  
    val _set = {
      get[Option[BigDecimal]]("DEBITO") ~
      get[Option[BigDecimal]]("CREDITO") map {
        case 
             debito ~ 
             credito => Saldo(
               debito,
               credito
             )
      }
    }
}

object LibroMayor {
  implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")
  
  implicit val writes = new Writes[LibroMayor] {
    def writes(lm: LibroMayor) = Json.obj(
      "codigo" -> lm.codigo,
      "nombre" -> lm.nombre,
      "anterior" -> lm.saldo_inicial,
      "debito" -> lm.debito,
      "credito" -> lm.credito,
      "actual" -> lm.saldo_final,
    )
  }
}

class LibroMayorRepository @Inject()(dbapi: DBApi, conf: Configuration)(implicit ec: DatabaseExecutionContext) {
    // private val db = dbapi.database("default")
    private val REPORT_DEFINITION_PATH = System.getProperty("user.dir") + "/app/resources/"

    def consultar(anho: Int): Future[Iterable[LMRelacion]] = Future[Iterable[LMRelacion]] {
      val dbdefault = dbapi.database("default"); 
      var _listData = ListBuffer[LibroMayor]()      
      dbdefault.withConnection { implicit connection =>
        SQL("""SELECT * FROM CON$HISTORIALIBROREGISTRADO WHERE LIRE_ANHO = {lire_anho} AND LIRE_ID = 1
               ORDER BY LIRE_ANHO, LIRE_PERIODO DESC, LIRE_CONSECUTIVO DESC""").
        on(
          'lire_anho -> anho
        ).as(LMRelacion._set *)
      }

    }

    def generar(periodo: Int, anho: Int, usua_id: scala.Long, definitivo: Boolean): Future[Array[Byte]] = Future[Array[Byte]] {
        var base = ""
        val anho_actual:Int = Calendar.getInstance.get(Calendar.YEAR)
        if (anho == anho_actual) {
          base = "default"
        } else {
          base = "db"+"%04d".format(anho)
        }
        val db = dbapi.database(base)
        val dbdefault = dbapi.database("default")
        var _listData = ListBuffer[LibroMayor]()
        var _fecha_inicial = Calendar.getInstance()
        var _fecha_final = Calendar.getInstance()
        
        if (periodo == 1) {
          _fecha_inicial.set(Calendar.YEAR, 1973)
          _fecha_inicial.set(Calendar.MONTH, 0)
          _fecha_inicial.set(Calendar.DATE, 1)
          _fecha_inicial.set(Calendar.HOUR, 0)
          _fecha_inicial.set(Calendar.MINUTE, 0)
          _fecha_inicial.set(Calendar.SECOND, 0)
          _fecha_inicial.set(Calendar.MILLISECOND, 0)

          _fecha_final.set(Calendar.YEAR, 1973)
          _fecha_final.set(Calendar.MONTH, 0)
          _fecha_final.set(Calendar.DATE, 31)
          _fecha_final.set(Calendar.HOUR, 0)
          _fecha_final.set(Calendar.MINUTE, 0)
          _fecha_final.set(Calendar.SECOND, 0)
          _fecha_final.set(Calendar.MILLISECOND, 0)          
        } else {
          _fecha_inicial.set(Calendar.YEAR, anho)
          _fecha_inicial.set(Calendar.MONTH, periodo - 2)
          _fecha_inicial.set(Calendar.DATE, 1)
          _fecha_inicial.set(Calendar.HOUR, 0)
          _fecha_inicial.set(Calendar.MINUTE, 0)
          _fecha_inicial.set(Calendar.SECOND, 0)
          _fecha_inicial.set(Calendar.MILLISECOND, 0)
          var _last_day = _fecha_inicial.getActualMaximum(Calendar.DAY_OF_MONTH);
          _fecha_inicial.set(Calendar.MONTH, 0)
          _fecha_final.set(Calendar.YEAR, anho)
          _fecha_final.set(Calendar.MONTH, periodo-2)
          _fecha_final.set(Calendar.DATE, _last_day)
          _fecha_final.set(Calendar.HOUR, 23)
          _fecha_final.set(Calendar.MINUTE, 59)
          _fecha_final.set(Calendar.SECOND, 59)
          _fecha_final.set(Calendar.MILLISECOND, 999)
        }
        var _fecha_mov_inicial = Calendar.getInstance()
        var _fecha_mov_final = Calendar.getInstance()
        
        _fecha_mov_inicial.set(Calendar.YEAR, anho)
        _fecha_mov_inicial.set(Calendar.MONTH, periodo - 1)
        _fecha_mov_inicial.set(Calendar.DATE, 1)
        _fecha_mov_inicial.set(Calendar.HOUR, 0)
        _fecha_mov_inicial.set(Calendar.MINUTE, 0)
        _fecha_mov_inicial.set(Calendar.SECOND, 0)
        _fecha_mov_inicial.set(Calendar.MILLISECOND, 0)

        var _last_mov_day = _fecha_mov_inicial.getActualMaximum(Calendar.DAY_OF_MONTH);        
        _fecha_mov_final.set(Calendar.YEAR, anho)
        _fecha_mov_final.set(Calendar.MONTH, periodo - 1)
        _fecha_mov_final.set(Calendar.DATE, _last_mov_day)   

        var sdf = new SimpleDateFormat("yyyy-MM-dd");
      
        db.withConnection {
            implicit connection =>
            val id_agencia = conf.get[String]("id_agencia")
            val empresa: (String, String) = dbdefault.withConnection { implicit connection =>
              SQL("""SELECT EMPR_DESCRIPCION, EMPR_IDENTIFICACION FROM EMPRESA""").as(SqlParser.str(1) ~ SqlParser.str(2) map (SqlParser.flatten) single)
            }

            /* _query_mov = """SELECT SUM(a.DEBITO) AS DEBITO, SUM(a.CREDITO) AS CREDITO FROM "con$comprobante" c
                               INNER JOIN "con$auxiliar" a ON a.TIPO_COMPROBANTE = c.TIPO_COMPROBANTE and a.ID_COMPROBANTE = c.ID_COMPROBANTE
                               WHERE a.FECHA BETWEEN :FECHA_INI and :FECHA_FIN and a.CODIGO LIKE :CODIGO
                               and c.ESTADO = 'C'
            */
            val anterior = """SELECT SUM(a.DEBITO) AS DEBITO, SUM(a.CREDITO) AS CREDITO FROM "con$comprobante" c
                               INNER JOIN "con$auxiliar" a ON a.TIPO_COMPROBANTE = c.TIPO_COMPROBANTE and a.ID_COMPROBANTE = c.ID_COMPROBANTE
                               WHERE a.FECHA BETWEEN {fecha_inicial} and {fecha_final} and a.CODIGO LIKE {codigo}
                               and c.ESTADO = 'C'"""

            val actual = """SELECT SUM(a.DEBITO) AS DEBITO, SUM(a.CREDITO) AS CREDITO FROM "con$comprobante" c
                               INNER JOIN "con$auxiliar" a ON a.TIPO_COMPROBANTE = c.TIPO_COMPROBANTE and a.ID_COMPROBANTE = c.ID_COMPROBANTE
                               WHERE a.FECHA BETWEEN {fecha_inicial} and {fecha_final} and a.CODIGO LIKE {codigo}
                               and c.ESTADO = 'C'
                         """

            val puc = SQL("""SELECT "con$puc".CODIGO, "con$puc".ID_AGENCIA, "con$puc".NOMBRE, "con$puc".SALDOINICIAL, "gen$agencia".DESCRIPCION_AGENCIA
                     FROM "con$puc"
                     LEFT JOIN "gen$agencia" ON ("con$puc".ID_AGENCIA = "gen$agencia".ID_AGENCIA)
                     WHERE
                     ("con$puc".NIVEL = {nivel})
                     ORDER BY "con$puc".CODIGO """).on(
                        'nivel -> 3
                    ).as(PucLibro._set *)
            val usuario = dbdefault.withConnection { implicit connection =>
               SQL("""SELECT NOMBRE || ' ' || PRIMER_APELLIDO || ' ' || SEGUNDO_APELLIDO AS nombre FROM \"gen$empleado\" WHERE ID = {usua_id}""").
              on(
                'usua_id -> usua_id
              ).as(SqlParser.scalar[String].single)
            }
            puc.map { p =>
                        val _codigo = p.codigo match {
                          case Some(c) => c.substring(0,4) + '%'
                          case None => ""
                        }
                        println("Analizando: " + _codigo)
                        println("Fecha Periodo Anterior: " +sdf.format(_fecha_inicial.getTime()) + " - " + sdf.format(_fecha_final.getTime()))
                        println("Fecha Periodo Actual:" + sdf.format(_fecha_mov_inicial.getTime()) + " - " + sdf.format(_fecha_mov_final.getTime()))
                        val s_ant = SQL(anterior).on(
                                   'codigo -> _codigo,
                                   'fecha_inicial -> sdf.format(_fecha_inicial.getTime()),
                                   'fecha_final -> sdf.format(_fecha_final.getTime())
                               ).as(Saldo._set.singleOpt)
                        val s_act = SQL(actual).on(
                                    'codigo -> _codigo,
                                   'fecha_inicial -> sdf.format(_fecha_mov_inicial.getTime()),
                                   'fecha_final -> sdf.format(_fecha_mov_final.getTime())
                                ).as(Saldo._set.singleOpt)
                        var saldo_anterior = BigDecimal(0)
                        p.saldo_inicial match {
                          case Some(inicial) => saldo_anterior = inicial
                          case None => saldo_anterior = 0
                        }
                        var saldo_actual = BigDecimal(0)
                        var debito = BigDecimal(0)
                        var credito = BigDecimal(0)
                        var debito_ant = BigDecimal(0)
                        var credito_ant = BigDecimal(0)
                        var debito_act = BigDecimal(0)
                        var credito_act = BigDecimal(0)
                        s_ant match {
                            case Some(s) => //saldo_anterior = saldo_anterior + s.debito - s.credito
                              s.debito match {
                                case Some(deb) => debito_ant = deb
                                case None => debito_ant = 0
                              }

                              s.credito match {
                                case Some(cre) => credito_ant = cre
                                case None => credito_ant = 0
                              }
                            case None => None
                        }

                        s_act match {
                          case Some(s) => //saldo_anterior = saldo_anterior + s.debito - s.credito
                            s.debito match {
                              case Some(deb) => debito_act = deb
                              case None => debito_act = 0
                            }

                            s.credito match {
                              case Some(cre) => credito_act = cre
                              case None => credito_act = 0
                            }
                          case None => None
                        }

                        println("saldo_anterior: " + s_ant);
                        println("saldo_actual: " + s_act);

                        if (saldo_anterior != 0 || debito_ant != 0 || credito_ant != 0 || debito_act != 0 || credito_act != 0 ) {
                          val lm = new LibroMayor(p.codigo, p.nombre, Some(saldo_anterior + debito_ant - credito_ant),  Some(debito_act), Some(credito_act), Some(saldo_anterior + debito_ant - credito_ant + debito_act - credito_act))
                          _listData += lm
                        }
                        
                    }

                var paginas = 0
                val ds = new LibroMayorDS(_listData)
                val template = REPORT_DEFINITION_PATH + "libromayorybalance.jrxml"
                val jasperFile = new java.io.File(REPORT_DEFINITION_PATH + "libromayorybalance.jasper")
                println("ruta: " + template)
                val jasperReport: JasperReport = JRLoader.loadObject(jasperFile).asInstanceOf[JasperReport]

                var consecutivo = dbdefault.withConnection { implicit connection =>
                  SQL("""SELECT COUNT(*) AS LIRE_CONSECUTIVO FROM CON$HISTORIALIBROREGISTRADO WHERE LIRE_ID = 1 AND LIRE_PERIODO = {periodo} and LIRE_ANHO = {anho}""").
                  on(
                    'periodo -> periodo,
                    'anho -> anho
                  ).as(SqlParser.scalar[scala.Long].single)
                }
                consecutivo += 1
                println("linea 01")
                val pagina_libro = dbdefault.withConnection { implicit connection => 
                  SQL("""SELECT a.LIRE_PAGINA FROM CON$LIBROREGISTRADO a WHERE a.LIRE_ID = {lire_id}""").
                  on(
                    'lire_id -> 1
                  ).as(SqlParser.scalar[Int].single)
                }
                println("linea 02")
                println("pagina libro: " + pagina_libro)
                val nombre = "FAP999_"+anho+"%02d".format(periodo)+"_MAYORYBALANCE_"+ consecutivo +".pdf"
                var params = new HashMap[String, java.lang.Object]()
                params.put("EMPRESA", empresa._1)
                params.put("NIT", empresa._2)
                params.put("ARCHIVO", nombre)
                params.put("USUARIO", usuario)
                params.put("PAGINA_LIBRO", (pagina_libro).longValue().asInstanceOf[java.lang.Long])
                params.put("ANHO", "%d".format(anho))
                params.put("PERIODO", Utility.mes(periodo))

                val handle = AsynchronousFillHandle.createHandle(jasperReport, params, ds)
                var pagina = 0
                val listener = new JrListener(pagina)
                handle.addFillListener(listener)

                var accessor: AsyncJasperPrintAccessor = new AsyncJasperPrintAccessor(handle)

                handle.startFill();

                if (accessor.waitForFinalJasperPrint(5000)) {
                  val jasperPrint = accessor.getFinalJasperPrint();
                  pagina = listener.getPagina()
                  println("linea 03")
                  println("paginas: " + (pagina + 1))
                  if (definitivo) {
                    dbdefault.withConnection { implicit connection => 
                      SQL("""INSERT INTO CON$HISTORIALIBROREGISTRADO VALUES ({lire_id}, {lire_anho}, {lire_periodo}, {lire_consecutivo}, {lire_fecha}, {lire_hora}, {id})""").
                        on(
                          'lire_id -> 1,
                          'lire_anho -> anho,
                          'lire_periodo -> periodo,
                          'lire_consecutivo -> consecutivo,
                          'lire_fecha -> new DateTime(),
                          'lire_hora -> new DateTime(),
                          'id -> usua_id
                      ).executeUpdate()
                      SQL("""UPDATE CON$LIBROREGISTRADO SET LIRE_PAGINA = {lire_pagina}, LIRE_ULTIMOPERIODO = {lire_ultimoperiodo} WHERE LIRE_ID = {id}""").
                        on(
                        'id -> 1,
                        'lire_pagina -> (pagina_libro + (pagina + 1)),
                        'lire_ultimoperiodo -> _fecha_mov_final.getTime()
                      ).executeUpdate()
                    }
                  }
                  val destino = conf.get[String]("reporte_ruta")
                  JasperExportManager.exportReportToPdfFile(jasperPrint, destino + nombre)
                }
                val destino = conf.get[String]("reporte_ruta")
                val byteArray = Files.readAllBytes(Paths.get(destino + nombre))
                byteArray
            }
        }

    def ver(lire_anho: Int, lire_periodo: Int, lire_consecutivo: Int): Array[Byte] = {
      val nombre = "FAP999_"+lire_anho+"%02d".format(lire_periodo)+"_MAYORYBALANCE_"+ lire_consecutivo +".pdf"
      val destino = conf.get[String]("reporte_ruta")      
      val byteArray = Files.readAllBytes(Paths.get(destino + nombre))
      byteArray
    } 
}