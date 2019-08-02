package dto

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

case class EmpresaDto(empr_id: Option[Long], empr_descripcion: String, token: String, perfil: String)

object EmpresaDto {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val empresadtoWrites = new Writes[EmpresaDto] {
        def writes(empresa: EmpresaDto) = Json.obj(
            "empr_id" -> empresa.empr_id,
            "empr_descripcion" -> empresa.empr_descripcion,
            "token" -> empresa.token,
            "perfil" -> empresa.perfil
        )
    }

    implicit val empresadtoReads: Reads[EmpresaDto] = (
        (__ \ "empr_id").readNullable[Long] and
        (__ \ "empr_descripcion").read[String] and
        (__ \ "token").read[String] and
        (__ \ "perfil").read[String]
    )(EmpresaDto.apply _)
}