package utilities

import javax.inject.Inject

import play.api.db.DBApi

import play.api.libs.json._
import play.api.libs.concurrent._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

import scala.util.control.Breaks._
import scala.math.pow
import scala.math.BigDecimal

import anorm._
import anorm.SqlParser.{get, str, int, date}
import anorm.JodaParameterMetaData._

import org.joda.time.DateTime

class Funcion @Inject()(dbapi: DBApi){
  private val db = dbapi.database("default")

  def obtenerDiasMora(id_colocacion: String): Int = {
    var dias = 0
    val _fechaHoy = new DateTime()
    var _fechaDesembolso = new DateTime()
    val parser = 
            int("ID_ESTADO_COLOCACION") ~ 
            date("FECHA_DESEMBOLSO") ~ 
            date("FECHA_INTERES") ~ 
            int("ID_LINEA") ~
            int("AMORTIZA_INTERES") ~
            int("DIAS_PAGO") ~
            str("INTERES") map {
            case a ~ b ~ c ~ d ~ e ~ f ~ g => (a, b, c, d, e, f, g)
    }
    val c = db.withConnection { implicit connection =>
        SQL("""
           SELECT 
                c.ID_ESTADO_COLOCACION,
                c.FECHA_DESEMBOLSO,
                c.FECHA_INTERES,
                c.ID_LINEA,
                c.AMORTIZA_INTERES,
                c.DIAS_PAGO,
                t.INTERES
           FROM
                "col$colocacion" c
                INNER JOIN "col$tiposcuota" t ON (t.ID_TIPOS_CUOTA = c.ID_TIPO_CUOTA)
           WHERE 
                c.ID_COLOCACION = {id_colocacion}""").
           on(
               'id_colocacion -> id_colocacion
           ).as(parser.single)
    }

    var fecha = c._3
    val tipo = c._7
    val amortiza = c._5

    var _fecha = new DateTime()
    if (tipo.equals("V")) {
      _fecha = calculoFecha(new DateTime(fecha), amortiza)
    }

    _fecha = _fecha.plusDays(1)
    _fechaDesembolso = new DateTime(c._2)
    _fechaDesembolso = _fechaDesembolso.plusDays(c._6)
    dias = diasEntreFechas(_fecha, _fechaHoy, _fechaDesembolso)

    // validar si aplica periodo de gracia
    val _parseGracia = str("ID_COLOCACION") ~ date("FECHA_REGISTRO") ~ int("DIAS") map { case a ~ b ~ c => (a,b,c)}
    val gracia = db.withConnection { implicit connection =>
      SQL("""SELECT ID_COLOCACION, FECHA_REGISTRO, DIAS FROM COL_PERIODO_GRACIA WHERE ID_COLOCACION = {id_colocacion}""").
      on(
        'id_colocacion -> id_colocacion
      ).as(_parseGracia.singleOpt)
    }

    gracia match {
      case Some(g) => {
                        val _fi = new DateTime(g._2)
                        val _ff = calculoFecha(new DateTime(g._2), g._3)
                        if (_fi.getMillis() <= _fechaHoy.getMillis() && _ff.getMillis() >= _fechaHoy.getMillis()) {
                          dias = 0
                        }
                      }
      case None => None
    }

    dias
  }

  def diasEntreFechas(_fi: DateTime, _ff: DateTime, _fechaCorte: DateTime) = {
        var _fechaInicial = _fi
        var _fechaFinal = _ff
    
        var _fecha = new DateTime()
        var _negativo = false
        var _dias = 0

        var _b = 0

        if (_fechaInicial.getDayOfMonth.equals(_fechaInicial.dayOfMonth().withMaximumValue())) {
          _fechaInicial = _fechaInicial.plusDays(1)
        }

        if (_fechaInicial.isAfter(_fechaFinal)) {
          _fecha = _fechaInicial
          _fechaInicial = _fechaFinal
          _fechaFinal = _fecha
          _negativo = true
        }

        _dias = 1

        var (_a, _m, _d) = decodeDate(_fechaInicial)

        _b = _d
        var (_aa, _mm, _dd) = decodeDate(_fechaFinal)

        var (_aaa,_mmm,_ddd) = decodeDate(_fechaCorte)


        if ((_mm == 2) && (_dd == 28)) {
          if ( _ddd == 28 ) { _dd = _ddd }
          else if ( _ddd == 29 ) { _dd = _ddd }
          else if ( _ddd == 30 ) { _dd = 30 }
          else if ( _ddd < 28 ) { _dd = 30 }
        }
        else if (( _mm == 2 ) && ( _dd == 29)) {
          if ( _ddd == 29 ) { _dd = _ddd }
          else if ( _ddd == 30 ) { _dd = 30 }
          else if ( _ddd < 29 ) { _dd = 30 }
        }
        if ( _dd == 31) { _dd = 30 }
        if ( _d == 31 ) { _d = 30 }


        var _dddd = endOfAMonth(_fechaCorte.getYear, 2)

        if ((_dddd == 29) && ( _m == 3) && (_b == 1 )) {
          if ( _ddd ==29 ) { _dias = _dias + 1 }
          else if ( _ddd ==28 ) { _dias = _dias + 2 }
        }

        if (( _dddd == 28) && ( _m == 3) && ( _b ==1 )) {
          if (_ddd == 28) { _dias = _dias + 2 }
          else if ( _ddd == 29 ) { _dias = _dias + 1 }
        }

        if (( _dddd == 28) && (_dd == 28) && (_mm ==2 ) && (_ddd > 28)) {
          _dias = _dias + 2
        }

        if ((_dddd == 29) && (_dd == 29) && (_mm ==2 ) && (_ddd > 29)) {
          _dias = _dias + 1
        }

        breakable { 
         while (true) {
           if ((_aa == _a) && (_mm == _m) && (_dd == _d)) { break }
           _dias = _dias + 1
           _d = _d + 1
           if (_d > 30) {
             _d = 1
             _m = _m + 1
             if (_m > 12) {
               _m = 1
               _a = _a + 1
             }
           }
          }
        }

        if (_negativo) {
          _dias = -_dias
        }
        else {
           _dias
        }
      _dias
  }

  def endOfAMonth(_a:Int, _m: Int): Int = {
    var _fecha = new DateTime(_a, _m, 1, 0, 0)
    _fecha = _fecha.dayOfMonth().withMaximumValue()
    _fecha.getDayOfMonth
  }

  def decodeDate(_fecha: DateTime) = {
      val _a = _fecha.getYear
      val _m = _fecha.getMonthOfYear
      val _d = _fecha.getDayOfMonth
      (_a, _m, _d)
  }

  def calculoFecha(_fecha: DateTime, _dias: Int): DateTime = {
     var _anho = _fecha.getYear
     var _mes = _fecha.getMonthOfYear
     var _dia = _fecha.getDayOfMonth
     val _ultimo_dia = _fecha.dayOfMonth().withMaximumValue().getDayOfMonth;
     var _finmes = false
     if (_dia == _ultimo_dia) { 
       _finmes = true
     } else {
       _finmes = false
     }

     var _i = 0
     if (_dias > 0) {
        for (_i <- 1 to _dias) {
           _dia = _dia + 1
           if (_dia > 30) {
              _dia = 1
              _mes = _mes + 1
              if (_mes > 12) {
                _mes = 1
                _anho = _anho + 1
              }
            }
          }
    } else {
        for ( _i <- 1 to _dias.abs) {
           _dia = _dia - 1
           if (_dia == 0) {
              _dia = 30
              _mes = _mes - 1
              if (_mes == 0) {
                _mes = 12
                _anho = _anho - 1
              }
           }
        }
        _dia = _dia.abs
    }

    if (_mes == 2) {
         if (_dia > 28) {
            var _fecha = new DateTime(_anho, _mes, 1, 0, 0)
            _fecha = _fecha.dayOfMonth().withMaximumValue()
            _dia = _fecha.getDayOfMonth
         }
    }

    val _response = new DateTime(_anho, _mes, _dia, 0, 0)
    _response
  }


  def tasaNominalVencida(tasa_efectiva: Double, amortizacion: Int): Double = {
    var _amortiza = amortizacion
    if (_amortiza < 30) {_amortiza = 30}
    var _factor = _amortiza / 30
    _factor = 12 / _factor
    var _potencia = pow(1+(tasa_efectiva/100),(1/_factor))
    _potencia = ((_potencia-1)*_factor*100)
    round2(_potencia)
  }

  def round2(valor: Double) = {
    (valor * 100).round / 100.toDouble
  }

  def round0(valor: Double) = {
    (valor).round
  }
}