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
import jrds.LibroCajaDiarioDS
import listeners.JrListener

case class LibroCajaDiario(dia: Option[Int], codigo: Option[String], nombre: Option[String], debito: Option[BigDecimal], credito: Option[BigDecimal])
case class LibroCajaDiarioResumen(codigo: Option[String], nombre: Option[String], debito: Option[BigDecimal], credito: Option[BigDecimal])
case class LCDRelacion(
                        hilr_id: Option[Long], 
                        lire_id: Option[Int], 
                        lire_anho: Option[Int], 
                        lire_periodo: Option[Int], 
                        lire_consecutivo: Option[Int], 
                        lire_paginainicial: Option[Int], 
                        lire_paginafinal: Option[Int], 
                        lire_fecha: Option[DateTime], 
                        lire_hora: Option[DateTime]
                        )

object LCDRelacion {

  implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val writes = new Writes[LCDRelacion] {
    def writes(lm: LCDRelacion) = Json.obj(
      "hilr_id" -> lm.hilr_id,
      "lire_id" -> lm.hilr_id,
      "lire_anho" -> lm.lire_anho,
      "lire_periodo" -> lm.lire_periodo,
      "lire_consecutivo" -> lm.lire_consecutivo,
      "lire_paginainicial" -> lm.lire_paginainicial,
      "lire_paginafinal" -> lm.lire_paginafinal,
      "lire_fecha" -> lm.lire_fecha,
      "lire_hora" -> lm.lire_hora
    )
  }

  implicit val reads: Reads[LCDRelacion] = (
    (__ \ "hilr_id").readNullable[Long] and
    (__ \ "lire_id").readNullable[Int] and
    (__ \ "lire_anho").readNullable[Int] and
    (__ \ "lire_periodo").readNullable[Int] and
    (__ \ "lire_consecutivo").readNullable[Int] and
    (__ \ "lire_paginainicial").readNullable[Int] and
    (__ \ "lire_paginafinal").readNullable[Int] and
    (__ \ "lire_fecha").readNullable[DateTime] and
    (__ \ "lire_hora").readNullable[DateTime]
  )(LCDRelacion.apply _)
  
  val _set = {
    get[Option[Long]]("HILR_ID") ~
    get[Option[Int]]("LIRE_ID") ~
    get[Option[Int]]("LIRE_ANHO") ~
    get[Option[Int]]("LIRE_PERIODO") ~
    get[Option[Int]]("LIRE_CONSECUTIVO") ~
    get[Option[Int]]("LIRE_PAGINAINICIAL") ~
    get[Option[Int]]("LIRE_PAGINAFINAL") ~
    get[Option[DateTime]]("LIRE_FECHA") ~
    get[Option[DateTime]]("LIRE_HORA") map {
      case hilr_id ~
           lire_id ~
           lire_anho ~ 
           lire_periodo ~ 
           lire_consecutivo ~
           lire_paginainicial ~
           lire_paginafinal ~
           lire_fecha ~ 
           lire_hora => LCDRelacion(
             hilr_id,
             lire_id,
             lire_anho,
             lire_periodo,
             lire_consecutivo,
             lire_paginainicial,
             lire_paginafinal,
             lire_fecha,
             lire_hora
           )
    }
  }  
}

object LibroCajaDiario {
  implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val writes = new Writes[LibroCajaDiario] {
    def writes(lm: LibroCajaDiario) = Json.obj(
      "dia" -> lm.dia,
      "codigo" -> lm.codigo,
      "nombre" -> lm.nombre,
      "debito" -> lm.debito,
      "credito" -> lm.credito
    )
  }

  val _set = {
    get[Option[Int]]("DIA") ~
    get[Option[String]]("CODIGO") ~
    get[Option[String]]("NOMBRE") ~
    get[Option[BigDecimal]]("DEBITO") ~
    get[Option[BigDecimal]]("CREDITO") map {
      case dia ~
           codigo ~ 
           nombre ~ 
           debito ~
           credito => LibroCajaDiario (
             dia,
             codigo,
             nombre,
             debito,
             credito
           )
    }
  }
}

object LibroCajaDiarioResumen {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")
  
    val _set = {
      get[Option[String]]("CODIGO") ~
      get[Option[String]]("NOMBRE") ~
      get[Option[BigDecimal]]("DEBITO") ~
      get[Option[BigDecimal]]("CREDITO") map {
        case codigo ~ 
             nombre ~
             debito ~ 
             credito => LibroCajaDiarioResumen(
               codigo,
               nombre,
               debito,
               credito
             )
      }
    }
}

class LibroCajaDiarioRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {
    // private val db = dbapi.database("default")
    private val REPORT_DEFINITION_PATH = System.getProperty("user.dir") + "/app/resources/"

    def consultar(anho: Int): Future[Iterable[LCDRelacion]] = Future[Iterable[LCDRelacion]] {
      var base = ""
      anho match {
        case 2019 =>  //println("en 2019") 
                      base = "db2019"        
        case 2018 =>  //println("en 2018") 
                      base = "db2018"
        case 2017 =>  //println("en 2017")
                      base = "db2017"
        case 2016 =>  //println("en 2016")
                      base = "db2016"
        case _ => //println("en __")
                  base = "default"
      }
      val db = dbapi.database(base)
      var _listData = ListBuffer[LibroCajaDiario]()      
      db.withConnection { implicit connection =>
        SQL("""SELECT * FROM CON$HISTORIALIBROREGISTRADO WHERE LIRE_ANHO = {lire_anho}""").
        on(
          'lire_anho -> anho
        ).as(LCDRelacion._set *)
      }

    }

    def generar(periodo: Int, anho: Int, usua_id: scala.Long): Future[Boolean] = Future[Boolean] {
        var base = ""
        val anho_actual:Int = Calendar.getInstance.get(Calendar.YEAR)
        anho match {
          case 2019 =>  //println("en 2019") 
                      base = "db2019"              
          case 2018 =>  //println("en 2018") 
                        base = "db2018"
          case 2017 =>  //println("en 2017")
                        base = "db2017"
          case 2016 =>  //println("en 2016")
                        base = "db2016"
          case _ => //println("en __")
                    base = "default"
        }
        val db = dbapi.database(base)
        var _listData = ListBuffer[LibroCajaDiario]()
        db.withConnection {
            implicit connection =>
            val id_agencia = play.Play.application.configuration.getString("id_agencia")
            val empresa: (String, String) = SQL("""SELECT EMPR_DESCRIPCION, EMPR_IDENTIFICACION FROM EMPRESA""").as(SqlParser.str(1) ~ SqlParser.str(2) map (SqlParser.flatten) single)
            val usuario = SQL("""SELECT NOMBRE || ' ' || PRIMER_APELLIDO || ' ' || SEGUNDO_APELLIDO AS nombre FROM \"gen$empleado\" WHERE ID = {usua_id}""").
            on(
              'usua_id -> usua_id
            ).as(SqlParser.scalar[String].single)

            // Libro Caja Diario
            val libro = SQL("""SELECT EXTRACT(DAY FROM a.FECHA) AS DIA, a.CODIGO, p.NOMBRE, SUM(a.DEBITO) AS DEBITO, SUM(a.CREDITO) AS CREDITO FROM "con$auxiliar" a
                    INNER JOIN "con$puc" p ON a.CODIGO = p.CODIGO
                    WHERE
                    EXTRACT(MONTH FROM a.FECHA) = {mes} and a.ESTADOAUX = 'C'
                    GROUP BY DIA, CODIGO, NOMBRE
                    ORDER BY DIA""").on(
                        'mes -> periodo
                    ).as(LibroCajaDiario._set *)

            // Resumen
            val resumen = SQL("""SELECT a.CODIGO, p.NOMBRE, SUM(a.DEBITO) AS DEBITO, SUM(a.CREDITO) AS CREDITO FROM "con$auxiliar" a
                        INNER JOIN "con$puc" p ON a.CODIGO = p.CODIGO
                        WHERE EXTRACT(MONTH FROM a.FECHA) = {mes} and a.ESTADOAUX = 'C'
                        GROUP BY a.CODIGO, p.NOMBRE
                        ORDER BY CODIGO ASC
                """).on(
                    'mes -> periodo
                    ).as(LibroCajaDiarioResumen._set *)

                var paginas = 0
                val ds = new LibroCajaDiarioDS(_listData)
                val template = REPORT_DEFINITION_PATH + "libromayorybalance.jrxml"
                val jasperFile = new java.io.File(REPORT_DEFINITION_PATH + "libromayorybalance.jasper")
                println("ruta: " + template)
                val jasperReport: JasperReport = JRLoader.loadObject(jasperFile).asInstanceOf[JasperReport]

                var consecutivo = SQL("""SELECT COUNT(*) AS LIRE_CONSECUTIVO FROM CON$HISTORIALIBROREGISTRADO WHERE LIRE_ID = 2""").as(SqlParser.scalar[scala.Long].single)
                consecutivo += 1
                println("linea 01")
                val pagina_libro = SQL("""SELECT a.LIRE_PAGINA FROM CON$LIBROREGISTRADO a WHERE a.LIRE_ID = {lire_id}""").
                on(
                  'lire_id -> 2
                ).as(SqlParser.scalar[Int].single)
                println("linea 02")
                println("pagina libro: " + pagina_libro)
                val nombre = "FAP999_"+anho+"%02d".format(periodo)+"_MAYORYBALANCE_"+ consecutivo +".pdf"
                var params = new HashMap[String, java.lang.Object]()
                params.put("EMPRESA", empresa._1)
                params.put("NIT", empresa._2)
                params.put("ARCHIVO", nombre)
                params.put("USUARIO", usuario)
                params.put("PAGINA_LIBRO", (pagina_libro + 1).longValue().asInstanceOf[java.lang.Long])
                params.put("ANHO", "%d".format(anho))
                params.put("PERIODO", Utility.obtenerMes(periodo))

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
                  SQL("""UPDATE CON$LIBROREGISTRADO SET LIRE_PAGINA = {lire_pagina} WHERE LIRE_ID = {id}""").
                  on(
                    'id -> 1,
                    'lire_pagina -> (pagina_libro + pagina)
                  ).executeUpdate()

                  val destino = play.Play.application.configuration.getString("reporte_ruta")
                  JasperExportManager.exportReportToPdfFile(jasperPrint, destino + nombre)

                  
                }
                true
            }
        }

    def ver(lire_anho: Int, lire_periodo: Int, lire_consecutivo: Int): Array[Byte] = {
      val nombre = "FAP999_"+lire_anho+"%02d".format(lire_periodo)+"_CAJADIARIO_"+ lire_consecutivo +".pdf"
      val destino = play.Play.application.configuration.getString("reporte_ruta")      
      val byteArray = Files.readAllBytes(Paths.get(destino + nombre))
      byteArray
    } 
}