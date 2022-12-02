package controllers

import javax.inject.Inject

import models._

import play.api.mvc._

import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import pdi.jwt.JwtSession

import utilities._

import models.ViabilidadRepository

import net.liftweb.json._
import net.liftweb.json.Serialization.write
import net.liftweb.json.Serialization.read
import net.liftweb.json.parse

@Singleton
class ViabilidadController @Inject()(
    tiService: ViabilidadRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  implicit val formats = Serialization.formats(NoTypeHints) ++ List(
    DateTimeSerializer
  )
  def viabilidad() = Action.async { implicit request: Request[AnyContent] =>
    val usua_id = Utility.extraerUsuario(request)
    println("JSON body:" + request.body.asJson.get)
    val json = net.liftweb.json.parse(request.body.asJson.get.toString())
    val v = json.extract[ViabilidadPeticion]
    val result = tiService.viabilidad(v)
    Future.successful(Ok(write(result)))
  }
}
