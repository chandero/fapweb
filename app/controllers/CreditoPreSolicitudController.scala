package controllers

import javax.inject.Inject

import models.AuxiliarRepository

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
import models.PreSolicitud
import models.CreditoPreSolicitudRepository

@Singleton
class CreditoPreSolicitudController @Inject()(
    aService: CreditoPreSolicitudRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ImplicitJsonFormats {
  implicit val formats = Serialization.formats(NoTypeHints) ++ List(
    DateTimeSerializer
  )
  def registrarSolicitud = Action.async(parse.json) {
    implicit request =>
      //val body = request.body.asText.get
      //val json = Json.parse(body)
      //json.extract[PreSolicitud]
      val _presol = net.liftweb.json.parse(request.body.toString()).extract[PreSolicitud]
        aService.registrarSolicitud(_presol).map { result =>
          Ok(Json.toJson(result))
        }
    }

    def obtenerListaPresolicitud()= authenticatedUserAction.async {
        implicit request =>
            aService.obtenerListaPresolicitud().map { result =>
                Ok(write(result))
            }
    }
}
