package models

import javax.inject.Inject
import java.util.Calendar

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

import play.api.db.DBApi

import anorm._
import anorm.SqlParser.{get, str}
import anorm.JodaParameterMetaData._

import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}
import scala.collection.mutable.ListBuffer

import org.joda.time.DateTime
import org.joda.time.LocalDate

import utilities._

class CalculoRepository @Inject()(
    dbapi: DBApi,
    _globalesCol: GlobalesCol,
    _funcion: Funcion
)(implicit ec: DatabaseExecutionContext) {
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
      case cuot_num ~ cuot_fecha ~ cuot_saldo ~ cuot_capital ~ cuot_interes ~ cuot_otros =>
        Tabla(
          cuot_num,
          cuot_fecha,
          cuot_saldo,
          cuot_capital,
          cuot_interes,
          cuot_otros
        )
    }
  }

  /**
        Procesar calculo de la tabla
    */
  def tabla(
      linea: Long,
      valor: BigDecimal,
      plazo_mes: Int
  ): Future[Iterable[Tabla]] = Future[Iterable[Tabla]] {
    db.withConnection { implicit connection =>
      /**
                    Leer la tasa a aplicar en base a la línea
        */
      var list = new ListBuffer[Tabla]
      var _listResult = new ListBuffer[Tabla]
      var _dsDescuento = new ListBuffer[Tabla]
      var amortizacion: Int = 0
      var cuot_saldo: BigDecimal = valor
      val plazo = plazo_mes * 30
      var cuot_fecha: DateTime = new DateTime()
      if (linea == 3) {
        amortizacion = plazo
      } else {
        amortizacion = 30
      }
      val tasae: Double = SQL(
        """SELECT l.TASA FROM "col$lineas" l WHERE l.ID_LINEA = {id_linea}"""
      ).on(
          'id_linea -> linea
        )
        .as(SqlParser.scalar[Double].single)

      val descuentos = SQL(
        """SELECT d.* FROM "col$descuentos" d WHERE d.ES_ACTIVO = 1"""
      ).as(Descuento._set *)

      val tasan = Conversion.efectiva_a_nominal(tasae, amortizacion)
      val cuota = Conversion.cuotafija(valor, plazo, tasae, amortizacion)
      println("cuota:" + cuota)

      var i = 0
      for (i <- 1 to (plazo / amortizacion)) {
        println("tasan:" + tasan)
        var cuot_interes: BigDecimal = Conversion.round(
          (cuot_saldo * (tasan / 100) * (amortizacion) / 360).doubleValue,
          0
        )
        println("interes: " + cuot_interes)
        var cuot_capital = cuota - cuot_interes
        var cuot_otros: BigDecimal = 0
        val tabla = new Tabla(
          i,
          cuot_fecha,
          cuot_saldo,
          cuot_capital,
          cuot_interes,
          cuot_otros
        )
        cuot_fecha = _funcion.calculoFecha(cuot_fecha, 30)
        list += tabla
        cuot_saldo = cuot_saldo - cuot_capital
        println(tabla)
      }
      val _descuentoColocacion = new ListBuffer[DescuentoColocacion]()
      val _descuento = SQL(
        """SELECT * FROM "col$descuentos" WHERE ES_ACTIVO = 1"""
      ).as(Descuento._set *)
      _descuento.foreach(d => {
        val _dc =
          new DescuentoColocacion(d.id_descuento, d.descripcion_descuento, true)
        _descuentoColocacion += _dc
      })
      val _adescontar = _globalesCol.calcularDescuentoPorCuota(
        list.toSeq,
        _descuentoColocacion.toList,
        valor,
        amortizacion,
        valor
      )
      for (i <- 1 to list.length) {
        _listResult += new Tabla(
          list(i).cuot_num,
          list(i).cuot_fecha,
          list(i).cuot_saldo,
          list(i).cuot_capital,
          list(i).cuot_interes,
          _adescontar(i).valor
        )
      }

      _listResult.toList
    }
  }
}
