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
class GarantiaRealController @Inject()(
    cService: GarantiaRealRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {
        def obtener(id_colocacion: String) = Action.async {
            implicit request: Request[AnyContent] =>
              // val usua_id = Utility.extraerUsuario(request)
              cService.obtener(id_colocacion).map { p =>
                 Ok(Json.toJson(p))
              }
        }
}