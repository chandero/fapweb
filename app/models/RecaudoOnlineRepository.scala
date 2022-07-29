package models

object AbonoStatus extends Enumeration {
  type AbonoStatus = Value

  val Pending, In_Progress, Success, Failed = Value
}

case class AbonoOnline(
    abono_id_transaccion: String,
    id_colocacion: String,
    abono_cuota: Int,
    abono_total: Double,
    abono_fecha_generado: Long,
    abono_fecha_respuesta: Option[Long],
    abono_estado: String
)

case class AbonoOnlineDetail(
    abono_id_transaccion: String,
    fecha_extracto: Long,
    hora_extracto: Long,
    codigo_puc: String,
    fecha_inicial: Long,
    fecha_final: Long,
    dias_aplicados: Int,
    tasa_liquidacion: Double,
    valor_debito: Double,
    valor_credito: Double,
    es_capital: Boolean,
    es_causado: Boolean,
    es_corriente: Boolean,
    es_vencido: Boolean,
    es_anticipado: Boolean,
    es_devuelto: Boolean,
    es_otros: Boolean,
    es_cajabanco: Boolean,
    es_costas: Boolean
)

class RecaudoOnlineRepository {
  def create(data: AbonoOnlineDetail): Unit = {}

  def apply(id_transaccion: String): Unit = {
    // TODO
  }

  def reject(id_transaccion: String): Unit = {
    // TODO
  }
}
