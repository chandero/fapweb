package utilities

import javax.inject.Inject
import java.util.Calendar

import play.api.db.DBApi

import anorm._
import anorm.SqlParser.{ get, str }
import anorm.JodaParameterMetaData._

import scala.collection.mutable.ListBuffer

import models._

class GlobalesCol @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {
    private val db = dbapi.database("default")

    def CalcularDescuentoPorCuota(_cuota: Seq[Tabla], _descuento: Seq[DescuentoColocacion], _valorDesembolso: BigDecimal, _amortizaCapital: Int, _saldoActual: BigDecimal): Iterable[ADescontar] = {
        var _descontar = new ListBuffer[ADescontar]
        var nValor: BigDecimal = 0
        var nPorcColocacion: Double = 0
        var nPorcSaldo: Double = 0
        var nPorcCuota: Double = 0
        var bEnDesembolso: Boolean = false
        var bEnCuota: Boolean = false
        var bEsDistribuido: Boolean = false
        var sCodigo: String = ""
        var nCobro: BigDecimal = 0
        var idDescuento: Int = 0
        var nAmortiza: Int = 0

        _descuento.foreach { _d =>
          if (_d.descontar) { 
            val descuento = db.withConnection { implicit connection => 
                SQL("""SELECT * FROM "col$descuentos" WHERE ID_DESCUENTO = {id_descuento}""").
                on(
                    'id_descuento -> _d.id_descuento
                ).as(Descuento._set.single)
            }

            nValor = descuento.valor_descuento
            nPorcColocacion = descuento.porcentaje_colocacion
            nPorcSaldo = descuento.porcentaje_saldo
            nPorcCuota = descuento.porcentaje_cuota
            bEnDesembolso = Conversion.entero_a_boolean(descuento.en_desembolso)
            bEnCuota = Conversion.entero_a_boolean(descuento.en_cuota)
            bEsDistribuido = Conversion.entero_a_boolean(descuento.es_distribuido)
            sCodigo = descuento.codigo
            nAmortiza = descuento.amortizacion

            if (nValor > 0 ) {
                if (bEnDesembolso) { return _descontar.toList }
                if (bEsDistribuido) { nCobro = math.round(nValor.doubleValue / _cuota.length) }
                if (bEnCuota) {
                    nCobro = nValor
                    _cuota.foreach { _c =>
                        var _ad = new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
                        _descontar += _ad
                    }
                }
            } else if (nPorcColocacion > 0) {
                if (bEnDesembolso) { return _descontar.toList }
                nCobro = math.round(_valorDesembolso.doubleValue * nPorcColocacion/100)*(_amortizaCapital/nAmortiza);  
                if (bEsDistribuido) { nCobro = math.round(nCobro.doubleValue / _cuota.length) }
                if (bEnCuota) {
                    _cuota.foreach { _c =>
                        var _ad = new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
                        _descontar += _ad
                    }
                }              
            } else if (nPorcSaldo > 0) {
                if (bEnDesembolso) { return _descontar.toList }
                if (bEnCuota && !bEsDistribuido) {
                    nValor = _valorDesembolso
                    _cuota.foreach { _c =>
                        nCobro = math.round(nValor.doubleValue * nPorcSaldo/100)*(_amortizaCapital/nAmortiza)
                        nValor = nValor - _c.cuot_capital
                        var _ad = new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
                        _descontar += _ad
                    }
                } else if (bEnCuota && bEsDistribuido) {
                    nValor = _valorDesembolso
                    nCobro = 0
                    _cuota.foreach { _c =>
                        nCobro = nCobro + math.round(nValor.doubleValue * nPorcSaldo/100)*(_amortizaCapital/nAmortiza)
                        nValor = nValor - _c.cuot_capital
                    }
                    nCobro = math.round(nCobro.doubleValue / _cuota.length)
                    _cuota.foreach { _c =>
                        var _ad = new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
                        _descontar += _ad
                    }
                }
            } else if (nPorcCuota > 0) {
                if (bEnDesembolso) { return _descontar.toList }
                if (bEnCuota && !bEsDistribuido){
                    _cuota.foreach { _c => 
                        nCobro = math.round(_c.cuot_capital.doubleValue * nPorcCuota)
                        var _ad = new ADescontar(_d.id_descuento, sCodigo, _c.cuot_num, nCobro)
                        _descontar += _ad                        
                    }
                }
            }
          }
        }
        _descontar.toList
    }
}