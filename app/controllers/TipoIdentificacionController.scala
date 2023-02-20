package controllers

import javax.inject.Inject

import models._

import play.api.mvc._
import play.api.libs.json._
import play.api.Configuration

import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import pdi.jwt.JwtSession

import utilities._

@Singleton
class TipoIdentificacionController @Inject()(
    tiService: TipoIdentificacionRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction,
    config: Configuration
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {
  def obtenerListaTipoIdentificacion() = authenticatedUserAction.async {
    implicit request: Request[AnyContent] =>
      val usua_id = Utility.extraerUsuario(request)
      tiService.obtenerListaTipoIdentificacion().map { lista =>
        Ok(Json.toJson(lista))
      }
  }

  def obtenerListaTipoIdentificacionAction() = Action.async {
    implicit request: Request[AnyContent] =>
      val usua_id = Utility.extraerUsuario(request)
      tiService.obtenerListaTipoIdentificacion().map { lista =>
        Ok(Json.toJson(lista))
      }
  }

  def obtenerListaTipoIdentificacionExterna(token: String) = Action.async {
    implicit request: Request[AnyContent] =>
      val secret = config.get[String]("token")
      if (token.equals(secret)) {
        val usua_id = Utility.extraerUsuario(request)
        tiService.obtenerListaTipoIdentificacion().map { lista =>
          Ok(Json.toJson(lista))
        }
      } else {
        Future.successful(Ok(Json.toJson("Not Authorized")))
      }
  }

}
