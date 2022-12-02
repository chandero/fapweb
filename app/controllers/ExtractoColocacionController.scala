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
class ExtractoColocacionController @Inject()(
    eService: ExtractoColocacionRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

        def obtener(id_colocacion: String) = authenticatedUserAction.async { implicit request =>
            eService.obtener(id_colocacion).map { e =>
                Ok(Json.toJson(e))
            }
        }

        def obtenerAction(id_colocacion: String) = Action.async { implicit request =>
            eService.obtener(id_colocacion).map { e =>
                Ok(Json.toJson(e))
            }
        }
    }