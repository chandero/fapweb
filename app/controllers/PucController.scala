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
class PucController @Inject()(
    pService: PucRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

        def obtenerNombre(codigo: String) = authenticatedUserAction.async { implicit request =>
            pService.obtenerNombre(codigo).map { e =>
                Ok(Json.toJson(e))
            }
        }

    }