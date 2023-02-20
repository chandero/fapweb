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
class SolicitudExternaController @Inject()(
    sService: SolicitudExternaRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {
        def crear() = Action.async {
            implicit request: Request[AnyContent] =>
            val json = request.body.asJson.get
            var solicitud = json.as[SolicitudExterna]
              sService.crear(solicitud).map { r =>
                Ok(Json.toJson(r))
              }
          }        
    }