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
class ExtractoColocacionDetalladoController @Inject()(
    eService: ExtractoColocacionDetalladoRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

        def obtener(id_colocacion: String, id_cbte_colocacion: Int, fecha_extracto: Long) = authenticatedUserAction.async { implicit request =>
            eService.obtener(id_colocacion, id_cbte_colocacion, fecha_extracto).map { e =>
                Ok(Json.toJson(e))
            }
        }

        def obtenerAction(id_colocacion: String, id_cbte_colocacion: Int, fecha_extracto: Long) = Action.async { implicit request =>
            eService.obtener(id_colocacion, id_cbte_colocacion, fecha_extracto).map { e =>
                Ok(Json.toJson(e))
            }
        }        
    }