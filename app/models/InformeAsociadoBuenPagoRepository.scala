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
import jrds.AsociadoBuenPagoDS
import listeners.JrListener

case class AsociadoBuenPago(primer_apellido: Option[String], segundo_apellido: Option[String], nombre: Option[String], documento: Option[String], direccion: Option[String], telefono: Option[String], dias: Option[Int])

object AsociadoBuenPago {
  implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")
  
  implicit val writes = new Writes[AsociadoBuenPago] {
    def writes(lm: AsociadoBuenPago) = Json.obj(
      "primer_apellido" -> lm.primer_apellido,
      "segundo_apellido" -> lm.segundo_apellido,
      "nombre" -> lm.nombre,
      "documento" -> lm.documento,
      "direccion" -> lm.direccion,
      "telefono" -> lm.telefono,
      "dias" -> lm.dias,
    )
  }

  val _set = {
    get[Option[String]]("primer_apellido") ~
    get[Option[String]]("segundo_apellido") ~
    get[Option[String]]("nombre") ~
    get[Option[String]]("documento") ~
    get[Option[String]]("direccion") ~
    get[Option[String]]("telefono") ~
    get[Option[Int]]("dias") map {
      case primer_apellido ~
           segundo_apellido ~
           nombre ~
           id_persona ~
           direccion ~
           telefono ~
           dias => new AsociadoBuenPago(primer_apellido, segundo_apellido, nombre, id_persona, direccion, telefono, dias)
    }
  }
}

class InformeAsociadoBuenPagoRepository @Inject()(dbapi: DBApi, config: Configuration)(implicit ec: DatabaseExecutionContext) {
    // private val db = dbapi.database("default")
    private val REPORT_DEFINITION_PATH = System.getProperty("user.dir") + "/app/resources/"

    def consultar(): Future[Iterable[AsociadoBuenPago]] = Future[Iterable[AsociadoBuenPago]] {
      val anho_actual:Int = Calendar.getInstance.get(Calendar.YEAR)
        val base = "default"

        val db = dbapi.database(base)
        db.withConnection {
            implicit connection =>
              val _list = SQL("""SELECT s.* FROM CLIENTEBUENPAGO s ORDER BY s.DOCUMENTO""").as(AsociadoBuenPago._set.*)
              _list
        }
    }

    def generar() = Future[Boolean] {
        println("Generando Datos Buen Pago")
        val anho_actual:Int = Calendar.getInstance.get(Calendar.YEAR)
        val base = "default"

        val db = dbapi.database(base)
        var _listData = ListBuffer[AsociadoBuenPago]()
        db.withConnection {
            implicit connection =>
            val id_agencia = config.get[String]("id_agencia")
            val empresa: (String, String) = SQL("""SELECT EMPR_DESCRIPCION, EMPR_IDENTIFICACION FROM EMPRESA""").as(SqlParser.str(1) ~ SqlParser.str(2) map (SqlParser.flatten) single)
            val pParser = get[Option[Int]]("ID_IDENTIFICACION") ~ get[Option[String]]("ID_PERSONA") ~ get[Option[String]]("PRIMER_APELLIDO") ~ get[Option[String]]("SEGUNDO_APELLIDO") ~ get[Option[String]]("NOMBRE") ~ get[Option[String]]("DIRECCION") ~ get[Option[String]]("TELEFONO") map { case a ~ b ~ c ~ d ~ e ~ f ~ g=> (a,b,c,d,e,f,g) }
            val eParser = int("ID_CBTE_COLOCACION") ~ int("DIAS_APLICADOS") map { case a ~ b => (a,b) }
            val persona = SQL("""SELECT DISTINCT c.ID_IDENTIFICACION, c.ID_PERSONA, g.PRIMER_APELLIDO, g.SEGUNDO_APELLIDO, g.NOMBRE, d.DIRECCION, (d.TELEFONO1 || ' ' || d.TELEFONO2) AS TELEFONO FROM "col$colocacion" c 
                                  LEFT JOIN "gen$persona" g ON (g.ID_IDENTIFICACION = c.ID_IDENTIFICACION and g.ID_PERSONA = c.ID_PERSONA)
                                  LEFT JOIN "gen$direccion" d ON (d.ID_IDENTIFICACION = c.ID_IDENTIFICACION and d.ID_PERSONA = c.ID_PERSONA and d.CONSECUTIVO = 1 and d.ID_DIRECCION = 1)
                                 WHERE c.ID_PERSONA NOT IN (
                                 SELECT DISTINCT c.ID_PERSONA FROM "col$colocacion" c
                                 WHERE c.ID_ESTADO_COLOCACION IN (0,1,2)
                                 )
                                 ORDER BY g.PRIMER_APELLIDO""").as(pParser.*)
            val fecha = Calendar.getInstance()
              persona.map { p =>
                println("Validando Persona: " + p._2)
                val extractodet = SQL("""SELECT ID_CBTE_COLOCACION, DIAS_APLICADOS FROM "col$extractodet" e 
                                     WHERE e.ID_COLOCACION IN (SELECT c.ID_COLOCACION FROM "col$colocacion" c
                                     WHERE c.ID_IDENTIFICACION = {id_identificacion} and c.ID_PERSONA = {id_persona})
                                     AND e.CODIGO_PUC IN ('415020020100000000', '415021020100000000', '415022010000000000')
                          """).on(
                            'id_identificacion -> p._1,
                            'id_persona -> p._2
                         ).as(eParser.*)
                var dias = 0
                var cbte_ant = 0
                var mora = 0
                extractodet.map { e =>
                    println("Validando Persona Cbte: " + e._1)
                    var cbte = e._1
                    if (cbte != cbte_ant) {
                      cbte_ant = cbte
                      if (mora > dias) { dias = mora }
                      mora = e._2
                    } else {
                      mora = mora + e._2
                    }
                }
                println("Validando Persona Dias: " + dias)
                if (dias <= 30) {
                  println("Insertado Persona :" + p._2)
                  val insertar = SQL("""INSERT INTO CLIENTEBUENPAGO (FECHA, DOCUMENTO, PRIMER_APELLIDO, SEGUNDO_APELLIDO, NOMBRE, DIRECCION, TELEFONO, DIAS) 
                    VALUES({fecha}, {documento}, {primer_apellido}, {segundo_apellido}, {nombre}, {direccion}, {telefono}, {dias})""")
                  .on(
                    'fecha -> new DateTime(fecha),
                    'documento -> p._2,
                    'primer_apellido -> p._3,
                    'segundo_apellido -> p._4,
                    'nombre -> p._5,
                    'direccion -> p._6,
                    'telefono -> p._7,
                    'dias -> dias
                  ).executeInsert()
                  println("Insertado Persona : " + insertar)
                  val lm = new AsociadoBuenPago(p._3, p._4, p._5, p._2, p._6, p._7, Some(dias))
                  print("cliente: " + p._3 + " " + p._4 + " " +  p._5)
                  println(" <-> dias: " + dias)
                  _listData += lm
                }
                println("Continuando...")
              }
            println("Finalizado....")
            true
            /**    
            println("Finalizado....")
            var os = Array[Byte]()
            var paginas = 0
            val ds = new AsociadoBuenPagoDS(_listData)
            val nombre = "FAP999_"+"_CLIENTEBUENPAGO"+".pdf"
            var reportParams = new HashMap[String, java.lang.Object]()
            reportParams.put("EMPRESA", empresa._1)
            reportParams.put("NIT", empresa._2)
            val compiledFile = REPORT_DEFINITION_PATH + "AsociadoBuenPago.jasper"
            os = JasperRunManager.runReportToPdf(compiledFile, reportParams, connection)
            os
            
            _listData
            **/
        }
      }

    def exportar(): Array[Byte] = {
      val nombre = "FAP999"+"_CLIENTES_PAGO_NORMAL_SIN_CREDITO_ACTUAL"
      val base = "default"
      val db = dbapi.database(base)

      db.withConnection { implicit connection =>
        val dt = new DateTime(Calendar.getInstance().getTimeInMillis())
        val fmt = DateTimeFormat.forPattern("yyyyMMdd")
        val sheet = Sheet(name="ClientesBuenPago" + fmt.print(dt),
            rows = { 
                val headerRow = com.norbitltd.spoiwo.model.Row().withCellValues(
                                                     "Primer Apellido",
                                                     "Segundo Apellido", 
                                                     "Nombre", 
                                                     "Documento", 
                                                     "Dirección", 
                                                     "Teléfono", 
                                                     "Máx Mora")
                val resultSet = SQL("""SELECT c.PRIMER_APELLIDO, c.SEGUNDO_APELLIDO, c.NOMBRE, c.DOCUMENTO, c.DIRECCION, c.TELEFONO, c.DIAS FROM CLIENTEBUENPAGO c""").
                as(AsociadoBuenPago._set *)
                val rows = resultSet.map { i => 
                  com.norbitltd.spoiwo.model.Row().withCellValues(
                                    i.primer_apellido match { case Some(value) => value case None => "" },
                                    i.segundo_apellido match { case Some(value) => value case None => "" },
                                    i.nombre match { case Some(value) => value case None => "" },
                                    i.documento match { case Some(value) => value case None => "" },
                                    i.direccion match { case Some(value) => value case None => "" },
                                    i.telefono match { case Some(value) => value case None => "" },
                                    i.dias match { case Some(value) => value case None => "" },
                )}
                headerRow :: rows.toList
            }
        )  
        println("Escribiendo en el Stream")
        var os:ByteArrayOutputStream = new ByteArrayOutputStream()
        Workbook(sheet).writeToOutputStream(os) 
        println("Stream Listo")
        os.toByteArray
      }
    } 

    def validarPersona(id_identificacion: Int, id_persona: String) : AsociadoBuenPago =  {
      val base = "default"
      val db = dbapi.database(base)
      val pParser = get[Option[Int]]("ID_IDENTIFICACION") ~ get[Option[String]]("ID_PERSONA") ~ get[Option[String]]("PRIMER_APELLIDO") ~ get[Option[String]]("SEGUNDO_APELLIDO") ~ get[Option[String]]("NOMBRE") map { case a ~ b ~ c ~ d ~ e => (a,b,c,d,e) }
      val eParser = int("ID_CBTE_COLOCACION") ~ int("DIAS_APLICADOS") map { case a ~ b => (a,b) }      
      db.withConnection { implicit connection =>
        val p = SQL("""SELECT p.ID_IDENTIFICACION, p.ID_PERSONA, p.PRIMER_APELLIDO, p.SEGUNDO_APELLIDO, p.NOMBRE FROM "gen$persona" p
                             WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.id_persona = {id_persona}
                          """).on(
                            'id_identificacion -> id_identificacion,
                            'id_persona -> id_persona
                          ).as(pParser.singleOpt)
        p match {
          case Some(p) =>
        val fecha = Calendar.getInstance()
        println("Validando Persona: " + p._2)
          val extractodet = SQL("""SELECT ID_CBTE_COLOCACION, DIAS_APLICADOS FROM "col$extractodet" e 
           WHERE e.ID_COLOCACION IN (SELECT c.ID_COLOCACION FROM "col$colocacion" c
           WHERE c.ID_IDENTIFICACION = {id_identificacion} and c.ID_PERSONA = {id_persona})
           AND e.CODIGO_PUC = '415020020100000000'
          """).on(
          'id_identificacion -> p._1,
          'id_persona -> p._2
          ).as(eParser.*)
          var dias = 0
          var cbte_ant = 0
          var mora = 0
          extractodet.map { e =>
            println("Validando Persona Cbte: " + e._1)
            var cbte = e._1
            if (cbte != cbte_ant) {
              cbte_ant = cbte
            if (mora > dias) { dias = mora }
              mora = e._2
            } else {
              mora = mora + e._2
            }
          }
        println("Validando Persona Dias: " + dias)
        val ab = new AsociadoBuenPago(p._3, p._4, p._5, p._2, None, None, Some(dias))
        ab

        case None => val ab = new AsociadoBuenPago(None, None, None, None, None, None, None)
        ab
        }
    }
  }
}