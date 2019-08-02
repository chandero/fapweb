package models

import javax.inject.Inject
import java.util.Calendar

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

import play.api.db.DBApi

import anorm._
import anorm.SqlParser.{ get, str }
import anorm.JodaParameterMetaData._

import scala.util.{ Failure, Success }
import scala.concurrent.{ Await, Future }
import scala.collection.mutable.ListBuffer

import org.joda.time.DateTime
import org.joda.time.LocalDate

import utilities._

case class Tabla(cuot_num:Int, cuot_saldo:BigDecimal, cuot_capital:BigDecimal, cuot_interes:BigDecimal, cuot_otros:BigDecimal)

case class Descuento(id_descuento:Long, 
                     codigo: String, 
                     descripcion_descuento: String, 
                     valor_descuento: BigDecimal, 
                     porcentaje_colocacion: Double,
                     porcentaje_cuota:Double,
                     en_desembolso:Int,
                     en_cuota:Int,
                     porcentaje_saldo:Double,
                     es_distribuido:Int,
                     es_activo:Int,
                     amortizacion:Int)

case class Adicion(cuot_num:Int, cuot_otros: BigDecimal)

object Tabla {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val tablaWrites = new Writes[Tabla] {
     def writes(tabla: Tabla) = Json.obj(
         "cuot_num" -> tabla.cuot_num,
         "cuot_saldo" -> tabla.cuot_saldo,
         "cuot_capital" -> tabla.cuot_capital,
         "cuot_interes" -> tabla.cuot_interes,
         "cuot_otros" -> tabla.cuot_otros
     )
    }
    implicit val tablaReads: Reads[Tabla] = (
       (__ \ "cuot_num").read[Int] and
       (__ \ "cuot_saldo").read[BigDecimal] and
       (__ \ "cuot_capital").read[BigDecimal] and
       (__ \ "cuot_interes").read[BigDecimal] and
       (__ \ "cuot_otros").read[BigDecimal]
    )(Tabla.apply _)
    
}

object Descuento {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val descuentoWrites = new Writes[Descuento] {
     def writes(descuento: Descuento) = Json.obj(
        "id_descuento" -> descuento.id_descuento,
        "codigo" -> descuento.codigo, 
        "descripcion_descuento" -> descuento.descripcion_descuento, 
        "valor_descuento" -> descuento.valor_descuento,
        "porcentaje_colocacion" -> descuento.porcentaje_colocacion,
        "porcentaje_cuota" -> descuento.porcentaje_cuota,
        "en_desembolso" -> descuento.en_desembolso,
        "en_cuota" -> descuento.en_cuota,
        "porcentaje_saldo" -> descuento.porcentaje_saldo,
        "es_distribuido" -> descuento.es_distribuido,
        "es_activo" -> descuento.es_activo,
        "amortizacion" -> descuento.amortizacion
     )
    }
    implicit val descuentoReads: Reads[Descuento] = (
        (__ \ "id_descuento").read[Long] and
        (__ \ "codigo").read[String] and
        (__ \ "descripcion_descuento").read[String] and
        (__ \ "valor_descuento").read[BigDecimal] and
        (__ \ "porcentaje_colocacion").read[Double] and
        (__ \ "porcentaje_cuota").read[Double] and
        (__ \ "en_desembolso").read[Int] and
        (__ \ "en_cuota").read[Int] and
        (__ \ "porcentaje_saldo").read[Double] and
        (__ \ "es_distribuido").read[Int] and
        (__ \ "es_activo").read[Int] and
        (__ \ "amortizacion").read[Int]
    )(Descuento.apply _)


}

object Adicion {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val adicionWrites = new Writes[Adicion] {
     def writes(adicion: Adicion) = Json.obj(
         "cuot_num" -> adicion.cuot_num,
         "cuot_otros" -> adicion.cuot_otros
     )
    }
    implicit val adicionReads: Reads[Adicion] = (
       (__ \ "cuot_num").read[Int] and
       (__ \ "cuot_otros").read[BigDecimal]
    )(Adicion.apply _)   
}

class CalculoRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {
    private val db = dbapi.database("default")

    /**
    * Parsear un Aap desde un ResultSet
    */
    private val simple = {
        get[Int]("tabla.cout_num") ~ 
        get[BigDecimal]("tabla.cuot_saldo") ~
        get[BigDecimal]("tabla.cuot_capital") ~
        get[BigDecimal]("tabla.cuot_interes") ~
        get[BigDecimal]("tabla.cuot_otros") map {
           case cuot_num ~ cuot_saldo ~ cuot_capital ~ cuot_interes ~ cuot_otros => Tabla(cuot_num, cuot_saldo, cuot_capital, cuot_interes, cuot_otros)
        }
    }

    private val descuento = {
        get[Long]("id_descuento") ~
        get[String]("codigo") ~
        get[String]("descripcion_descuento") ~
        get[BigDecimal]("valor_descuento") ~
        get[Double]("porcentaje_colocacion") ~
        get[Double]("porcentaje_cuota") ~
        get[Int]("en_desembolso") ~
        get[Int]("en_cuota") ~
        get[Double]("porcentaje_saldo") ~
        get[Int]("es_distribuido") ~
        get[Int]("es_activo") ~
        get[Int]("amortizacion") map {
        case 
        id_descuento ~
        codigo ~
        descripcion_descuento ~
        valor_descuento ~
        porcentaje_colocacion ~
        porcentaje_cuota ~
        en_desembolso ~
        en_cuota ~
        porcentaje_saldo ~
        es_distribuido ~
        es_activo ~
        amortizacion => Descuento(
        id_descuento,
        codigo,
        descripcion_descuento,
        valor_descuento,
        porcentaje_colocacion,
        porcentaje_cuota,
        en_desembolso,
        en_cuota,
        porcentaje_saldo,
        es_distribuido,
        es_activo,
        amortizacion
        )
        }
    }


    /**
        Procesar calculo de la tabla
     */
     def tabla(linea:Long, valor:BigDecimal, plazo_mes:Int): Future[Iterable[Tabla]] = Future[Iterable[Tabla]] {
                db.withConnection { implicit connection =>
                    /**
                    Leer la tasa a aplicar en base a la línea
                     */
                     var list = new  ListBuffer[Tabla]
                     var amortizacion:Int = 0
                     var cuot_saldo:BigDecimal = valor
                     val plazo = plazo_mes * 30
                     if (linea == 3){
                         amortizacion = plazo
                     } else {
                         amortizacion = 30
                     }
                     val tasae:Double = SQL("""SELECT l.TASA FROM "col$lineas" l WHERE l.ID_LINEA = {id_linea}""").
                     on(
                         'id_linea -> linea
                     ).as(SqlParser.scalar[Double].single)

                     val descuentos = SQL("""SELECT d.* FROM "col$descuentos" d WHERE d.ES_ACTIVO = 1""").as(descuento *)

                     val tasan = Conversion.efectiva_a_nominal(tasae, amortizacion)
                     val cuota = Conversion.cuotafija(valor, plazo, tasae, amortizacion)
                     println("cuota:"+cuota)

                     var i = 0;
                     for (i <- 1 to (plazo/amortizacion)){
                        println("tasan:"+tasan)
                        var cuot_interes:BigDecimal = Conversion.round((cuot_saldo * (tasan/100)*(amortizacion)/360).doubleValue,0)
                        println("interes:"+cuot_interes)
                        var cuot_capital = cuota - cuot_interes
                        var cuot_otros:BigDecimal = 0
                        descuentos.map { descuento =>
                            if (descuento.valor_descuento > 0){
                                if (!Conversion.entero_a_boolean(descuento.en_desembolso)){
                                    if (Conversion.entero_a_boolean(descuento.es_distribuido)) {
                                        val acobrar = descuento.valor_descuento / (plazo/amortizacion)
                                        cuot_otros = cuot_otros + acobrar
                                    } else if (Conversion.entero_a_boolean(descuento.en_cuota)){
                                        cuot_otros = cuot_otros + descuento.valor_descuento
                                    }
                                }
                            } else if (descuento.porcentaje_colocacion > 0) {
                                if (!Conversion.entero_a_boolean(descuento.en_desembolso)){
                                    if (Conversion.entero_a_boolean(descuento.es_distribuido)) {
                                        val acobrar:Double = Conversion.round((((valor * descuento.porcentaje_colocacion/100) * (amortizacion/descuento.amortizacion))/(plazo/amortizacion)).doubleValue)
                                        cuot_otros = cuot_otros + acobrar
                                    } else if (Conversion.entero_a_boolean(descuento.en_cuota)){
                                        val acobrar:Double = Conversion.round(((valor * descuento.porcentaje_colocacion/100) * (amortizacion/descuento.amortizacion)).doubleValue)
                                        cuot_otros = cuot_otros + acobrar
                                    }
                                }
                            } else if (descuento.porcentaje_saldo > 0) {
                                if (Conversion.entero_a_boolean(descuento.en_cuota) && (Conversion.entero_a_boolean(descuento.es_distribuido))){
                                    var j = 0;
                                    for (j <- 1 to (plazo/amortizacion)){

                                    }
                                } else if (Conversion.entero_a_boolean(descuento.en_cuota) && (!Conversion.entero_a_boolean(descuento.es_distribuido))) {
                                    val acobrar:Double = Conversion.round(((cuot_saldo * descuento.porcentaje_saldo/100) * (amortizacion/descuento.amortizacion)).doubleValue)
                                    cuot_otros = cuot_otros + acobrar
                                }
                            }

                            if ( i == (plazo/amortizacion)){
                                if (cuot_saldo > cuot_capital) {
                                    val diff:BigDecimal = cuot_saldo - cuot_capital
                                    cuot_interes = cuot_interes - diff
                                    cuot_capital = cuot_capital + diff
                                }
                            }
                        }
                        
                        val tabla = new Tabla(i,cuot_saldo, cuot_capital, cuot_interes, cuot_otros)
                        list += tabla
                        cuot_saldo = cuot_saldo - cuot_capital
                        println(tabla)
                     }
                     list.toList
                }
     }
}