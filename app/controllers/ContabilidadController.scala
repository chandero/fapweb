package controllers

import javax.inject.Inject

import models.TipoComprobanteRepository

import play.api.mvc._
import play.api.libs.json._

import net.liftweb.json._
import net.liftweb.json.Serialization.write
import net.liftweb.json.Serialization.read
import net.liftweb.json.parse

import org.joda.time.LocalDate
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import pdi.jwt.JwtSession

import utilities._

@Singleton
class ContabilidadController @Inject()(
    tipoComprobanteService: TipoComprobanteRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ImplicitJsonFormats {
  implicit val formats = Serialization.formats(NoTypeHints) ++ List(
    DateTimeSerializer
  )

  def obtenerTiposComprobante() = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
    tipoComprobanteService.obtenerLista().map { p =>
      Ok(write(p))
    }
  }
}
