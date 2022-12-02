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
class CreditoController @Inject()(
    cService: CreditoRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {
        def liquidacionDePrueba(id_colocacion: String, cuotas: Int, fecha_corte: Long) = authenticatedUserAction.async { 
            implicit request: Request[AnyContent] =>
            cService.liquidar(id_colocacion, cuotas, fecha_corte).map { p =>
                Ok(Json.toJson(p))
            }
        }

        def liquidacionDePruebaAction(id_colocacion: String, cuotas: Int, fecha_corte: Long) = Action.async { 
            implicit request: Request[AnyContent] =>
            println("id_colocacion: " + id_colocacion)
            println("cuotas: " + cuotas)
            println("fecha_corte: " + fecha_corte)
            cService.liquidar(id_colocacion, cuotas, fecha_corte).map { p =>
                Ok(Json.toJson(p))
            }
        }
}