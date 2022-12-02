package controllers

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc._

import net.liftweb.json._
import net.liftweb.json.Serialization.write
import net.liftweb.json.Serialization.read
import net.liftweb.json.parse

import utilities._

import models.RecaudoOnlineRepository

class RecaudoOnlineController @Inject()(
    sService: RecaudoOnlineRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  implicit val formats = Serialization.formats(NoTypeHints) ++ List(
    DateTimeSerializer
  )

  def crear = authenticatedUserAction.async {
    implicit request: Request[AnyContent] =>
      val usua_id = Utility.extraerUsuario(request)
      println("JSON body:" + request.body.asJson.get)
      val json = net.liftweb.json.parse(request.body.asJson.get.toString())
      Future.successful(Ok(write(json)))
  }
}
