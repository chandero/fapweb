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
        get[BigDecimal]("tabla.cuot_otros") ~
        get[BigDecimal]("tabla.cuot_aporte") map {
           case cuot_num ~ cuot_fecha ~ cuot_saldo ~ cuot_capital ~ cuot_interes ~ cuot_otros ~ cuot_aporte => Tabla(cuot_num, cuot_fecha, cuot_saldo, cuot_capital, cuot_interes, cuot_otros, cuot_aporte)
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

                     var i = 0
                     for (i <- 1 to (plazo/amortizacion)){
                        var cuot_interes:BigDecimal = Conversion.round((cuot_saldo * (tasan/100)*(amortizacion)/360).doubleValue,0)
                        var cuot_capital = cuota - cuot_interes
                        var cuot_otros:BigDecimal = 0
                        var cuot_aporte:BigDecimal = 0
                        val tabla = new Tabla(i, cuot_fecha, cuot_saldo, cuot_capital, cuot_interes, cuot_otros, cuot_aporte)
                        cuot_fecha = _funcion.calculoFecha(cuot_fecha, 30)
                        list += tabla
                        cuot_saldo = cuot_saldo - cuot_capital
                     }
                     val _descuentoColocacion = new ListBuffer[DescuentoColocacion]()
                     val _descuento = SQL("""SELECT * FROM "col$descuentos" WHERE ES_ACTIVO = 1""").as(Descuento._set *)
                     _descuento.foreach( d => {
                         val _dc = new DescuentoColocacion(d.id_descuento, d.descripcion_descuento, true)
                         _descuentoColocacion += _dc
                     })
                     val _adescontar = _globalesCol.calcularDescuentoPorCuota(list, _descuentoColocacion.toList, valor, amortizacion, valor)
                     for( i <- 1 to list.length){
                        _listResult += new Tabla(list(i).cuot_num, list(i).cuot_fecha, list(i).cuot_saldo, list(i).cuot_capital, list(i).cuot_interes, _adescontar(i).valor, list(i).cuot_aporte)
                     }
                     
                     _listResult.toList
                }
     }

    /**
        Procesar calculo de la tabla
     */
     def tablaPagoSimulador(id_linea:Long, periodicidad: Int, valor:BigDecimal, plazo_mes:Int): Future[Iterable[Tabla]] = Future[Iterable[Tabla]] {
                db.withConnection { implicit connection =>
                    /**
                    Leer la tasa a aplicar en base a la línea
                     */
                     var list = new  ListBuffer[Tabla]
                     var _listResult = new ListBuffer[Tabla]
                     var _dsDescuento = new ListBuffer[Tabla]
                     var amortizacion:Int = periodicidad match {
                        case 1 => 30
                        case 2 => 90
                        case 3 => 180
                        case _ => 30
                     }
                     var cuot_saldo:BigDecimal = valor
                     val plazo = plazo_mes * 30
                     var cuot_fecha:DateTime = new DateTime()
/*                      if (linea == 3){
                         amortizacion = plazo
                     } else {
                         amortizacion = 30
                     } */
                     val tasae:Double = SQL("""SELECT l.TASA FROM "col$lineas" l WHERE l.ID_LINEA = {id_linea}""").
                     on(
                         'id_linea -> id_linea
                     ).as(SqlParser.scalar[Double].single)

                     val _tasa_aporte = SQL("""SELECT COLAPO_TASA FROM COL$APORTEDONACION WHERE {plazo} BETWEEN COLAPO_PLAZO_MIN AND COLAPO_PLAZO_MAX""").
                     on(
                        "plazo" -> plazo
                     ).as(SqlParser.scalar[Double].singleOpt)

                     val _valor_aporte = _tasa_aporte match {
                        case Some(tasa) => Conversion.round((valor * tasa).doubleValue,0)
                        case None => 0
                     }

                    val _valorTotalAporte = _valor_aporte
                    var _valor_cobrar_aporte = Conversion.round((_valorTotalAporte / (plazo/amortizacion)).doubleValue(), 0)


                     val descuentos = SQL("""SELECT d.* FROM "col$descuentos" d WHERE d.ES_ACTIVO = 1""").as(Descuento._set *)

                     val tasan = Conversion.efectiva_a_nominal(tasae, amortizacion)
                     val cuota = Conversion.cuotafija(valor, plazo, tasae, amortizacion)
                     var _aporte_cobrado = 0
                     var _saldo_cobrado = 0
                     println("cuota:"+cuota)

                     var i = 0
                     val cuotas = plazo/amortizacion
                     for (i <- 1 to cuotas){
                        var cuot_interes:BigDecimal = Conversion.round((cuot_saldo * (tasan/100)*(amortizacion)/360).doubleValue,0)
                        var cuot_capital = cuota - cuot_interes
                        if (i == cuotas){
                            cuot_capital = cuot_saldo
                        }
                        var cuot_otros:BigDecimal = 0
                        var cuot_aporte:BigDecimal = _valor_cobrar_aporte
                        val tabla = new Tabla(i, cuot_fecha, cuot_saldo, cuot_capital, cuot_interes, cuot_otros, cuot_aporte)
                        cuot_fecha = _funcion.calculoFecha(cuot_fecha, 30)
                        list += tabla
                        cuot_saldo = cuot_saldo - cuot_capital
                        _aporte_cobrado = _aporte_cobrado + _valor_cobrar_aporte.intValue()
                        if (_valor_cobrar_aporte > (_valorTotalAporte - _aporte_cobrado) ) {
                            _valor_cobrar_aporte = _valorTotalAporte - _aporte_cobrado
                        }
                     }
                     val _descuentoColocacion = new ListBuffer[DescuentoColocacion]()
                     val _descuento = SQL("""SELECT * FROM "col$descuentos" WHERE ES_ACTIVO = 1""").as(Descuento._set *)
                     _descuento.foreach( d => {
                         val _dc = new DescuentoColocacion(d.id_descuento, d.descripcion_descuento, true)
                         _descuentoColocacion += _dc
                     })
                     println("Descuentos: " + _descuentoColocacion.toList)
                     val _adescontar = _globalesCol.calcularDescuentoPorCuota(list, _descuentoColocacion.toList, valor, amortizacion, valor)
                     for( i <- 0 to list.length - 1){
                        var _descuento = BigDecimal(0)
                        for (j <- 0 to _adescontar.length - 1){
                            if (_adescontar(j).cuota_numero == (i+1)){
                                _descuento = _descuento + _adescontar(j).valor
                            }
                        }
                        _listResult += new Tabla(list(i).cuot_num, list(i).cuot_fecha, list(i).cuot_saldo, list(i).cuot_capital, list(i).cuot_interes, (_descuento + list(i).cuot_aporte), list(i).cuot_aporte)
                     }
                     
                     _listResult.toList
                }
     }     
}