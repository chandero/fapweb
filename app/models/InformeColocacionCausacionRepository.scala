package models

import java.nio.file.{Files, Paths}
import javax.inject.Inject
import java.util.Calendar
import java.sql.Timestamp
import java.security.MessageDigest
import java.util.{ Map, HashMap, Date }
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import scala.util.{ Failure, Success }
import anorm._
import anorm.SqlParser.{ get, str, int, date, flatten, scalar }
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

// Excel Export
import com.norbitltd.spoiwo.model._
import com.norbitltd.spoiwo.model.enums.CellStyleInheritance
import com.norbitltd.spoiwo.natures.xlsx.Model2XlsxConversions._
import com.norbitltd.spoiwo.model.enums.{CellBorderStyle, CellFill, Pane, CellHorizontalAlignment => HA, CellVerticalAlignment => VA}


import utilities._
import java.sql.SQLXML

case class InformeColocacionCausacion(id_colocacion: Option[String], 
                                      id_persona: Option[String], 
                                      primer_apellido: Option[String], 
                                      segundo_apellido: Option[String], 
                                      nombre: Option[String],
                                      fecha_desembolso: Option[LocalDate],
                                      fecha_capital: Option[LocalDate],
                                      fecha_interes: Option[LocalDate],
                                      valor_desembolso: Option[BigDecimal],
                                      valor_deuda: Option[BigDecimal],
                                      tasa: Option[BigDecimal],
                                      dias_causados: Option[Int],
                                      dias_mora: Option[Int],
                                      anticipado: Option[BigDecimal],
                                      causado: Option[BigDecimal],
                                      pcapital: Option[BigDecimal],
                                      pinteres: Option[BigDecimal],
                                      corto: Option[BigDecimal],
                                      largo: Option[BigDecimal]
                                      )

case class Extracto(fecha: Option[DateTime], cbte: Option[Int], capital: Option[BigDecimal], anticipado: Option[BigDecimal], cxc: Option[BigDecimal], servicio: Option[BigDecimal], fecha_interes: Option[DateTime], detallado: Option[List[Detallado]])
case class Detallado(codigo: Option[String], nombre: Option[String], fecha_inicial: Option[DateTime], fecha_final: Option[DateTime], dias: Option[Int], tasa: Option[BigDecimal],  debito: Option[BigDecimal], credito: Option[BigDecimal])
case class Causado(fecha: Option[DateTime], valor: Option[BigDecimal], deuda: Option[BigDecimal], anticipado: Option[BigDecimal], causado: Option[BigDecimal], contingencia: Option[BigDecimal], dias: Option[Int], tasa: Option[BigDecimal], fecha_interes: Option[DateTime], extracto: Option[List[Extracto]])

object InformeColocacionCausacion {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")
   
}

object Detallado {

    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val writes = new Writes[Detallado] {
        def writes(e: Detallado) = Json.obj(
          "codigo" -> e.codigo,
          "nombre" -> e.nombre,
          "fecha_inicial" -> e.fecha_inicial,
          "fecha_final" -> e.fecha_final,
          "dias" -> e.dias,
          "tasa" -> e.tasa,
          "debito" -> e.debito,
          "credito" -> e.credito
        )
      }

    val _set = {
        get[Option[String]]("CODIGO_PUC") ~
        get[Option[String]]("NOMBRE") ~
        get[Option[DateTime]]("FECHA_INICIAL") ~
        get[Option[DateTime]]("FECHA_FINAL") ~
        get[Option[Int]]("DIAS_APLICADOS") ~
        get[Option[BigDecimal]]("TASA_LIQUIDACION") ~
        get[Option[BigDecimal]]("VALOR_DEBITO") ~
        get[Option[BigDecimal]]("VALOR_CREDITO") map {
            case codigo ~
                 nombre ~
                 fecha_inicial ~
                 fecha_final ~
                 dias ~
                 tasa ~
                 debito ~
                 credito => Detallado(codigo,
                                      nombre,
                                      fecha_inicial,
                                      fecha_final,
                                      dias,
                                      tasa,
                                      debito,
                                      credito
                                     )
        }
    }    
}

object Extracto {

    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val writes = new Writes[Extracto] {
        def writes(e: Extracto) = Json.obj(
          "fecha" -> e.fecha,
          "cbte" -> e.cbte,
          "capital" -> e.capital,
          "anticipado" -> e.anticipado,
          "cxc" -> e.cxc,
          "servicio" -> e.servicio,
          "fecha_interes" -> e.fecha_interes,
          "detallado" -> e.detallado
        )
      }

    val _set = {
        get[Option[DateTime]]("FECHA_EXTRACTO") ~ 
        get[Option[Int]]("ID_CBTE_COLOCACION") ~
        get[Option[BigDecimal]]("ABONO_CAPITAL") ~
        get[Option[BigDecimal]]("ABONO_ANTICIPADO") ~
        get[Option[BigDecimal]]("ABONO_CXC") ~
        get[Option[BigDecimal]]("ABONO_SERVICIOS") ~
        get[Option[DateTime]]("INTERES_PAGO_HASTA") map {
            case fecha ~
                 cbte ~
                 capital ~
                 anticipado ~
                 cxc ~
                 servicio ~
                 fecha_interes => Extracto(fecha,
                                      cbte,
                                      capital,
                                      anticipado,
                                      cxc,
                                      servicio,
                                      fecha_interes,
                                      None
                                     )
        }
    }    
}


object Causado {

    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val writes = new Writes[Causado] {
        def writes(e: Causado) = Json.obj(
          "fecha" -> e.fecha,
          "valor" -> e.valor,
          "deuda" -> e.deuda,
          "anticipado" -> e.anticipado,
          "causado" -> e.causado,
          "contingencia" -> e.contingencia,
          "dias" -> e.dias,
          "tasa" -> e.tasa,
          "fecha_interes" -> e.fecha_interes,
          "extracto" -> e.extracto
        )
      }

    val _set = {
        get[Option[DateTime]]("FECHA_CORTE") ~ 
        get[Option[BigDecimal]]("VALOR") ~ 
        get[Option[BigDecimal]]("DEUDA") ~
        get[Option[BigDecimal]]("ANTICIPADOS") ~
        get[Option[BigDecimal]]("CAUSADOS") ~
        get[Option[BigDecimal]]("CONTINGENCIAS") ~
        get[Option[Int]]("DIAS") ~
        get[Option[BigDecimal]]("TASA") ~
        get[Option[DateTime]]("FECHA_INTERES") map {
            case fecha ~
                 valor ~
                 deuda ~
                 anticipado ~
                 causado ~
                 contingencia ~
                 dias ~
                 tasa ~
                 fecha_interes => Causado(fecha,
                                          valor,
                                          deuda,
                                          anticipado,
                                          causado,
                                          contingencia,
                                          dias,
                                          tasa,
                                          fecha_interes,
                                          None
                                          )
        }
    }    
}




class InformeColocacionCausacionRepository @Inject()(dbapi: DBApi, config: Configuration, empresaService: EmpresaRepository)(implicit ec: DatabaseExecutionContext) {

    def consultar(id_colocacion: String, empr_id: scala.Long) : Future[Iterable[Causado]] = Future[Iterable[Causado]] {
        val base = "default"
        val db = dbapi.database(base)
        var _list = ListBuffer[Causado]()
        db.withConnection {
            implicit connection =>
            val empresa = empresaService.buscarPorId(empr_id)
            empresa match {
            
                case Some(empresa) =>
                 val _pParser = str("ID_PERSONA") ~ str("PRIMER_APELLIDO") ~ str("SEGUNDO_APELLIDO") ~ str("NOMBRE") map { case a ~ b ~ c ~ d => (a, b, c, d)}
                 val persona = SQL("""SELECT c.ID_PERSONA, p.PRIMER_APELLIDO, p.SEGUNDO_APELLIDO, p.NOMBRE FROM \"col$colocacion\" c INNER JOIN \"gen$persona\" p ON p.ID_IDENTIFICACION = c.ID_IDENTIFICACION and p.ID_PERSONA = c.ID_PERSONA WHERE c.ID_COLOCACION = {id_colocacion}""").
                 on(
                     'id_colocacion -> id_colocacion
                 ).as(_pParser.single)
                 val cauResulset = SQL("""SELECT c.FECHA_CORTE, c.VALOR, c.DEUDA, c.ANTICIPADOS, c.CAUSADOS, c.CONTINGENCIAS, c.DIAS, c.TASA, c.FECHA_INTERES FROM \"col$causaciondiaria\" c WHERE c.ID_COLOCACION = {id_colocacion} ORDER BY c.FECHA_CORTE""").
                 on(
                     'id_colocacion -> id_colocacion
                 ).as(Causado._set *)

                 cauResulset.map { c =>
                    /* System.out.println("year = " + jodaTime.getYear());
                    System.out.println("month = " + jodaTime.getMonthOfYear());
                    System.out.println("day = " + jodaTime.getDayOfMonth());
                    System.out.println("hour = " + jodaTime.getHourOfDay());
                    System.out.println("minute = " + jodaTime.getMinuteOfHour());
                    System.out.println("second = " + jodaTime.getSecondOfMinute());
                    System.out.println("millis = " + jodaTime.getMillisOfSecond()); */
                    val fi = c.fecha.get.plusDays(1)
                    val ff = fi.plusDays(30)
                    val extResulset = SQL("""SELECT e.FECHA_EXTRACTO, e.ID_CBTE_COLOCACION, e.ABONO_CAPITAL, e.ABONO_ANTICIPADO, e.ABONO_CXC, e.ABONO_SERVICIOS, e.INTERES_PAGO_HASTA FROM \"col$extracto\" e WHERE e.ID_COLOCACION = {id_colocacion} and e.FECHA_EXTRACTO BETWEEN {fecha_inicial} and {fecha_final} ORDER BY e.FECHA_EXTRACTO""").
                    on(
                        'id_colocacion -> id_colocacion,
                        'fecha_inicial -> fi,
                        'fecha_final -> ff
                    ).as(Extracto._set *)
                    var _lextracto = new ListBuffer[Extracto]()
                    extResulset.map { e =>
                      val detResultset = SQL("""SELECT d.CODIGO_PUC, p.NOMBRE, d.FECHA_INICIAL, d.FECHA_FINAL, d.DIAS_APLICADOS, d.TASA_LIQUIDACION, d.VALOR_DEBITO, d.VALOR_CREDITO FROM \"col$extractodet\" d
                                                LEFT JOIN \"con$puc\" p ON p.CODIGO = d.CODIGO_PUC
                                                WHERE d.ID_COLOCACION = {id_colocacion} and d.ID_CBTE_COLOCACION = {id_cbte_colocacion} and d.FECHA_EXTRACTO = {fecha_extracto}""").
                                                on(
                                                    'id_colocacion -> id_colocacion,
                                                    'id_cbte_colocacion -> e.cbte,
                                                    'fecha_extracto -> e.fecha
                                                ).as(Detallado._set *)
                      val extracto = e.copy(detallado = Some(detResultset))
                      _lextracto += extracto
                    }

                    _list += c.copy(extracto = Some(_lextracto.toList))
                 }
     
                 _list.toList
            }
        }        
    }

    def extracto(id_colocacion: String, fecha_inicial: scala.Long, fecha_final: scala.Long, empr_id: scala.Long) : Future[Iterable[Extracto]] = Future[Iterable[Extracto]] {
        val base = "default"
        val db = dbapi.database(base)        
        db.withConnection { implicit connection => 
            val fi = new DateTime(fecha_inicial)
            val ff = new DateTime(fecha_final)
            val extResulset = SQL("""SELECT e.FECHA_EXTRACTO, e.ID_CBTE_COLOCACION, e.ABONO_ANTICIPADO, e.ABONO_CXC, e.ABONO_SERVICIOS, e.INTERES_PAGO_HASTA FROM \"col$extracto\" e WHERE e.ID_COLOCACION = {id_colocacion} and e.FECHA_EXTRACTO BETWEEN {fecha_inicial} and {fecha_final} ORDER BY e.FECHA_EXTRACTO""").
            on(
                'id_colocacion -> id_colocacion,
                'fecha_inicial -> fi,
                'fecha_final -> ff
            ).as(Extracto._set *)  
            extResulset      
        }
    }

    def exportar(id_colocacion: String, empr_id: scala.Long) : Array[Byte] = {
        val base = "default"
        val db = dbapi.database(base)
        db.withConnection {
            implicit connection =>
            val empresa = empresaService.buscarPorId(empr_id)
          empresa match {
            
           case Some(empresa) =>
            val _pParser = str("ID_PERSONA") ~ str("PRIMER_APELLIDO") ~ str("SEGUNDO_APELLIDO") ~ str("NOMBRE") map { case a ~ b ~ c ~ d => (a, b, c, d)}
            val persona = SQL("""SELECT c.ID_PERSONA, p.PRIMER_APELLIDO, p.SEGUNDO_APELLIDO, p.NOMBRE FROM \"col$colocacion\" c WHERE c.ID_COLOCACION = {id_colocacion}""").
            on(
                'id_colocacion -> id_colocacion
            ).as(_pParser.single)
            val cauResulset = SQL("""SELECT c.FECHA_CORTE, c.VALOR, c.DEUDA, c.ANTICIPADOS, c.CAUSADOS, c.CONTINGENCIAS, c.DIAS, c.TASA, c.FECHA_INTERES FROM \"col$causaciondiaria\" c WHERE c.ID_COLOCACION = {id_colocacion}""").
            on(
                'id_colocacion -> id_colocacion
            ).as(Causado._set *)

            val extResulset = SQL("""SELECT e.FECHA_EXTRACTO, e.ID_CBTE_COLOCACION, e.ABONO_ANTICIPADO, e.ABONO_CXC, e.ABONO_SERVICIOS * FROM \"col$extracto\" e WHERE e.ID_COLOCACION = {id_colocacion}"""").
            on(
                'id_colocacion -> id_colocacion
            ).as(Extracto._set *)
            val format = new SimpleDateFormat("yyyy-MM-dd")
            val sheet1 = Sheet(name="Causado",
                rows = {
                    val titleRow = com.norbitltd.spoiwo.model.Row().withCellValues(empresa.empr_descripcion)
                    val headerRow = com.norbitltd.spoiwo.model.Row().withCellValues("Fecha Causación", "Valor Desembolso", "Valor Deuda", "Interés Anticipado", "Interés Causado", "Interés No Causado", "Días", "Tasa", "Fecha Desde la Cuál se Causa")
                    var j = 2
                    val rows = cauResulset.map { i => j+=1
                                                    com.norbitltd.spoiwo.model.Row(
                                                        StringCell(i.fecha match { case Some(value) => format.format(value) },Some(0), style = Some(CellStyle( dataFormat = CellDataFormat("YYYY-MM-DD"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                        ),
                                                        NumericCell(i.valor match { case Some(value) => value.toDouble },Some(1), style = Some(CellStyle( dataFormat = CellDataFormat("#0"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                        ),
                                                        NumericCell(i.deuda match { case Some(value) => value.toDouble },Some(2), style = Some(CellStyle( dataFormat = CellDataFormat("#0"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                        ),
                                                        NumericCell(i.anticipado match { case Some(value) => value.toDouble },Some(3), style = Some(CellStyle( dataFormat = CellDataFormat("#0"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                        ),
                                                        NumericCell(i.causado match { case Some(value) => value.toDouble },Some(4), style = Some(CellStyle( dataFormat = CellDataFormat("#0"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                        ),
                                                        NumericCell(i.contingencia match { case Some(value) => value.toDouble },Some(5), style = Some(CellStyle( dataFormat = CellDataFormat("#0"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                        ),
                                                        NumericCell(i.dias match { case Some(value) => value.toDouble },Some(6), style = Some(CellStyle( dataFormat = CellDataFormat("#0"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                        ),
                                                        NumericCell(i.tasa match { case Some(value) => value.toDouble },Some(7), style = Some(CellStyle( dataFormat = CellDataFormat("#0"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                        ),
                                                        StringCell(i.fecha_interes match { case Some(value) => format.format(value) },Some(8), style = Some(CellStyle( dataFormat = CellDataFormat("YYYY-MM-DD"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                        ),                                                        
                                                    )
                                             }
                    
                    titleRow :: headerRow :: rows.toList
                }
            )

            val sheet2 = Sheet(name="Extracto",
                rows = {
                    val titleRow = com.norbitltd.spoiwo.model.Row().withCellValues(empresa.empr_descripcion)
                    val headerRow = com.norbitltd.spoiwo.model.Row().withCellValues("Fecha Extracto", "Comprobante", "Interés Anticipado", "Interés Causado", "Interés Servicio", "Interés hasta")
                    var j = 2
                    val rows = extResulset.map { i => j+=1
                                                com.norbitltd.spoiwo.model.Row(
                                                    StringCell(i.fecha match { case Some(value) => format.format(value) },Some(0), style = Some(CellStyle( dataFormat = CellDataFormat("YYYY-MM-DD"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                    ),
                                                    NumericCell(i.cbte match { case Some(value) => value.toDouble },Some(1), style = Some(CellStyle( dataFormat = CellDataFormat("#0"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                    ),
                                                    NumericCell(i.anticipado match { case Some(value) => value.toDouble },Some(2), style = Some(CellStyle( dataFormat = CellDataFormat("#0"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                    ),
                                                    NumericCell(i.cxc match { case Some(value) => value.toDouble },Some(3), style = Some(CellStyle( dataFormat = CellDataFormat("#0"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                    ),
                                                    NumericCell(i.servicio match { case Some(value) => value.toDouble },Some(4), style = Some(CellStyle( dataFormat = CellDataFormat("#0"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                    ),
                                                    StringCell(i.fecha_interes match { case Some(value) => format.format(value) },Some(5), style = Some(CellStyle( dataFormat = CellDataFormat("YYYY-MM-DD"))), CellStyleInheritance.CellThenRowThenColumnThenSheet
                                                    ),                                                        
                                                )
                                         }
                
                    titleRow :: headerRow :: rows.toList
                }
            )
            println("Escribiendo en el Stream")
            var os:ByteArrayOutputStream = new ByteArrayOutputStream()
            Workbook(sheet1, sheet2).writeToOutputStream(os) 
            println("Stream Listo")
            os.toByteArray                    
           case None => var os:ByteArrayOutputStream = new ByteArrayOutputStream()
                       os.toByteArray
          }
        }
    }
}
