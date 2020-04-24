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

case class Tabla(
                  cuot_num: Int, 
                  cuot_fecha: DateTime, 
                  cuot_saldo: BigDecimal, 
                  cuot_capital: BigDecimal, 
                  cuot_interes: BigDecimal, 
                  cuot_otros: BigDecimal
                )

case class ADescontar(id_descuento: Long,
                      codigo: String,
                      cuota_numero: Int,
                      valor: BigDecimal)

case class DescuentoColocacion( id_descuento: Long, 
                                descripcion_descuento: String, 
                                descontar: Boolean)

case class Descuento(id_descuento: Long, 
                     codigo: String, 
                     descripcion_descuento: String, 
                     valor_descuento: BigDecimal, 
                     porcentaje_colocacion: Double,
                     porcentaje_cuota: Double,
                     en_desembolso: Int,
                     en_cuota: Int,
                     porcentaje_saldo: Double,
                     es_distribuido: Int,
                     es_activo: Int,
                     amortizacion: Int)

case class Adicion(cuot_num:Int, cuot_otros: BigDecimal)

object Tabla {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val tablaWrites = new Writes[Tabla] {
     def writes(tabla: Tabla) = Json.obj(
         "cuot_num" -> tabla.cuot_num,
         "cuot_fecha" -> tabla.cuot_fecha,
         "cuot_saldo" -> tabla.cuot_saldo,
         "cuot_capital" -> tabla.cuot_capital,
         "cuot_interes" -> tabla.cuot_interes,
         "cuot_otros" -> tabla.cuot_otros
     )
    }
    implicit val tablaReads: Reads[Tabla] = (
       (__ \ "cuot_num").read[Int] and
       (__ \ "cuot_fecha").read[DateTime] and
       (__ \ "cuot_saldo").read[BigDecimal] and
       (__ \ "cuot_capital").read[BigDecimal] and
       (__ \ "cuot_interes").read[BigDecimal] and
       (__ \ "cuot_otros").read[BigDecimal]
    )(Tabla.apply _)

    val _set = {
        get[Int]("cuota_numero") ~
        get[DateTime]("fecha_a_pagar") ~
        get[BigDecimal]("capital_a_pagar") ~
        get[BigDecimal]("interes_a_pagar") map {
            case 
                cuot_num ~
                cuot_fecha ~
                cuot_capital ~
                cuot_interes => Tabla(
                    cuot_num,
                    cuot_fecha,
                    0,
                    cuot_capital,
                    cuot_interes,
                    0
                )
        }
    }
}

object ADescontar {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val _Writes = new Writes[ADescontar] {
     def writes(t: ADescontar) = Json.obj(
         "id_descuento" -> t.id_descuento,
         "codigo" -> t.codigo,
         "cuota_numero" -> t.cuota_numero,
         "valor" -> t.valor
     )
    }
    implicit val _Reads: Reads[ADescontar] = (
       (__ \ "id_descuento").read[Long] and
       (__ \ "codigo").read[String] and
       (__ \ "cuota_numero").read[Int] and
       (__ \ "valor").read[BigDecimal] 
    )(ADescontar.apply _)  
}

object DescuentoColocacion {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val _Writes = new Writes[DescuentoColocacion] {
     def writes(t: DescuentoColocacion) = Json.obj(
         "id_descuento" -> t.id_descuento,
         "descripcion_descuento" -> t.descripcion_descuento,
         "descontar" -> t.descontar
     )
    }
    implicit val _Reads: Reads[DescuentoColocacion] = (
       (__ \ "id_descuento").read[Long] and
       (__ \ "descripcion_descuento").read[String] and
       (__ \ "descontar").read[Boolean] 
    )(DescuentoColocacion.apply _)
    
    val _set = {
        get[Long]("id_descuento") ~
        get[String]("descripcion_descuento") ~
        get[Boolean]("descontar") map {
        case
          id_descuento ~
          descripcion_descuento ~
          descontar => DescuentoColocacion(
              id_descuento, 
              descripcion_descuento, 
              descontar)            
        }
    }
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

    val _set = {
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

class CalculoRepository @Inject()(dbapi: DBApi, _globalesCol: GlobalesCol, _funcion: Funcion)(implicit ec: DatabaseExecutionContext) {
    private val db = dbapi.database("default")

    /**
    * Parsear un Aap desde un ResultSet
    */
    private val simple = {
        get[Int]("tabla.cout_num") ~ 
        get[DateTime]("tabla.cout_fecha") ~
        get[BigDecimal]("tabla.cuot_saldo") ~
        get[BigDecimal]("tabla.cuot_capital") ~
        get[BigDecimal]("tabla.cuot_interes") ~
        get[BigDecimal]("tabla.cuot_otros") map {
           case cuot_num ~ cuot_fecha ~ cuot_saldo ~ cuot_capital ~ cuot_interes ~ cuot_otros => Tabla(cuot_num, cuot_fecha, cuot_saldo, cuot_capital, cuot_interes, cuot_otros)
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
                     var _listResult = new ListBuffer[Tabla]
                     var _dsDescuento = new ListBuffer[Tabla]
                     var amortizacion:Int = 0
                     var cuot_saldo:BigDecimal = valor
                     val plazo = plazo_mes * 30
                     var cuot_fecha:DateTime = new DateTime()
                     if (linea == 3){
                         amortizacion = plazo
                     } else {
                         amortizacion = 30
                     }
                     val tasae:Double = SQL("""SELECT l.TASA FROM "col$lineas" l WHERE l.ID_LINEA = {id_linea}""").
                     on(
                         'id_linea -> linea
                     ).as(SqlParser.scalar[Double].single)

                     val descuentos = SQL("""SELECT d.* FROM "col$descuentos" d WHERE d.ES_ACTIVO = 1""").as(Descuento._set *)

                     val tasan = Conversion.efectiva_a_nominal(tasae, amortizacion)
                     val cuota = Conversion.cuotafija(valor, plazo, tasae, amortizacion)
                     println("cuota:"+cuota)

                     var i = 0
                     for (i <- 1 to (plazo/amortizacion)){
                        println("tasan:"+tasan)
                        var cuot_interes:BigDecimal = Conversion.round((cuot_saldo * (tasan/100)*(amortizacion)/360).doubleValue,0)
                        println("interes: " + cuot_interes)
                        var cuot_capital = cuota - cuot_interes
                        var cuot_otros:BigDecimal = 0
                        val tabla = new Tabla(i, cuot_fecha, cuot_saldo, cuot_capital, cuot_interes, cuot_otros)
                        cuot_fecha = _funcion.calculoFecha(cuot_fecha, 30)
                        list += tabla
                        cuot_saldo = cuot_saldo - cuot_capital
                        println(tabla)
                     }
                     val _descuentoColocacion = new ListBuffer[DescuentoColocacion]()
                     val _descuento = SQL("""SELECT * FROM "col$descuentos" WHERE ES_ACTIVO = 1""").as(Descuento._set *)
                     _descuento.foreach( d => {
                         val _dc = new DescuentoColocacion(d.id_descuento, d.descripcion_descuento, true)
                         _descuentoColocacion += _dc
                     })
                     val _adescontar = _globalesCol.CalcularDescuentoPorCuota(list, _descuentoColocacion.toList, valor, amortizacion, valor)
                     for( i <- 1 to list.length){
                        _listResult += new Tabla(list(i).cuot_num, list(i).cuot_fecha, list(i).cuot_saldo, list(i).cuot_capital, list(i).cuot_interes, _adescontar(i).valor)
                     }
                     
                     _listResult.toList
                }
     }
}