package dto

case class WompiEventDataTransactionDto(
       id: String,
       created_at: String,
       finalized_at: Option[String],
       amount_in_cents: Long,
       reference: Option[String],
       customer_email: Option[String],
       currency: Option[String],
       payment_method_type: Option[String],
       payment_method: Option[String],
       status: Option[String],
       status_message: Option[String],
       shipping_address: Option[String],
       redirect_url: Option[String],
       payment_source_id: Option[String],
       payment_link_id: Option[String],
       customer_data: Option[String],
       billing_data: Option[String]
)

case class WompiEventDataDto (
    transaction: WompiEventDataTransactionDto
)

case class WompiEventDto (

)