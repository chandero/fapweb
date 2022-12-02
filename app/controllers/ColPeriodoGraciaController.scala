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
class ColPeriodoGraciaController @Inject()(
    cService: ColPeriodoGraciaRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

    def todos() = authenticatedUserAction.async {
        implicit request: Request[AnyContent] =>
              // val usua_id = Utility.extraerUsuario(request)
        cService.todos().map { p =>
            Ok(Json.toJson(p))
        }        
    }

    def agregarPeriodoGracia(id_colocacion: String, fecha: Long, dias: Int) = authenticatedUserAction.async {
        implicit request: Request[AnyContent] =>
              // val usua_id = Utility.extraerUsuario(request)
        cService.agregar(id_colocacion, fecha, dias).map { p =>
            Ok(Json.toJson(p))
        }        
    }

    def actualizarPeriodoGracia(id_colocacion: String, dias: Int) = authenticatedUserAction.async {
        implicit request: Request[AnyContent] =>
              // val usua_id = Utility.extraerUsuario(request)
        cService.actualizar(id_colocacion, dias).map { p =>
            Ok(Json.toJson(p))
        }        
    }    

    def normalizarColocacionPeriodoGracia(id_colocacion: String) = authenticatedUserAction.async {
        implicit request: Request[AnyContent] =>
              // val usua_id = Utility.extraerUsuario(request)
        cService.normalizarColocacionPeriodoGracia(id_colocacion).map { p =>
            Ok(Json.toJson(p))
        }        
    }

    def normalizarreversoColocacionPeriodoGracia(id_colocacion: String) = authenticatedUserAction.async {
        implicit request: Request[AnyContent] =>
              // val usua_id = Utility.extraerUsuario(request)
        cService.normalizarreversoColocacionPeriodoGracia(id_colocacion).map { p =>
            Ok(Json.toJson(p))
        }        
    }    

    def marcarEliminado(id_colocacion: String) = authenticatedUserAction.async {
        implicit request: Request[AnyContent] =>
              // val usua_id = Utility.extraerUsuario(request)
        cService.marcarEliminado(id_colocacion).map { p =>
            Ok(Json.toJson(p))
        }        
    }
}