package controllers

import javax.inject.Inject

import models._

import play.api.mvc._
import play.api.libs.json._

import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import pdi.jwt.JwtSession

import utilities._

@Singleton
class BienRaizController  @Inject()(sService: BienRaizRepository, cc: ControllerComponents, authenticatedUserAction: AuthenticatedUserAction)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  def buscar(id_solicitud: String)= authenticatedUserAction.async { implicit request: Request[AnyContent] =>
     sService.buscar(id_solicitud).map { inmuebles =>
       Ok(Json.toJson(inmuebles))
     }
  }
}