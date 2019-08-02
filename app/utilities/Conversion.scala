package utilities

import scala.math.pow

class Conversion {
    /**
    Convertir de Entero a Booleano
    @param valor: Int
    @return Boolean
    */
    def entero_a_boolean(valor: Int):Boolean = {
        if (valor == 0) {
            false
        } else {
            true
        }
    }
    def efectiva_a_nominal(tasae:Double, amortizacion:Int):Double = {
        var amortiza = amortizacion
        if (amortizacion < 30){
            amortiza = 30
        }
        val factor:Double = 12/(amortiza/30)
        var potencia = pow((1+(tasae/100)),(1/factor))
        potencia = ((potencia-1)*factor*100)
        round(potencia,2)
    }

    def factor(tasae:Double, plazo:Int, amortizacion:Int):Double = {
        val n = (plazo/30)/(amortizacion/30)
        var i = efectiva_a_nominal(tasae,amortizacion)
        val amortiza = amortizacion/30
        i = ((i/100)/12)*amortiza
        val potencia = pow((1+i),n)
        if (potencia > 1){
            (i*potencia)/(potencia-1)
        } else {
            0d
        }
    }

    def cuotafija(valor:BigDecimal, plazo:Int, tasae:Double, amortizacion:Int):BigDecimal = {
        val cuota = round((valor.doubleValue * factor(tasae, plazo, amortizacion)),0)
        cuota
    }

    def sha1(s: String) = java.security.MessageDigest.getInstance("SHA-1").digest(s.getBytes).map((b: Byte) => (if (b >= 0 & b < 16) "0" else "") + (b & 0xFF).toHexString).mkString

    def round(value: Either[Double, Float], places: Int) = {
        if (places < 0) 0
        else {
            val factor = Math.pow(10, places)
            value match {
                case Left(d) => (Math.round(d * factor) / factor)
                case Right(f) => (Math.round(f * factor) / factor)
            }
        }
    }

    def round(value: Double): Double = round(Left(value), 0)
    def round(value: Double, places: Int): Double = round(Left(value), places)
    def round(value: Float): Double = round(Right(value), 0)
    def round(value: Float, places: Int): Double = round(Right(value), places)
}

object Conversion extends Conversion
 
