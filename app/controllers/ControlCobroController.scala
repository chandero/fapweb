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
class ControlCobroController @Inject()(
    cService: ControlCobroRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

    def obtenerCreditosPorEstado(estado: Int, dias_ini: Int, dias_fin: Int, ases_id: Int) = authenticatedUserAction.async {
        implicit request: Request[AnyContent] =>
              // val usua_id = Utility.extraerUsuario(request)
        cService.obtenerCreditos(estado, dias_ini, dias_fin, ases_id).map { p =>
            Ok(Json.toJson(p))
        }        
    }    

    def obtenerCreditosPorDocumento(id_identificacion: Int, id_persona: String) = authenticatedUserAction.async {
        implicit request: Request[AnyContent] =>
              // val usua_id = Utility.extraerUsuario(request)
        cService.obtenerCreditos(id_identificacion, id_persona).map { p =>
            Ok(Json.toJson(p))
        }
    }   
    
    def obtenerDireccion(id_identificacion: Int, id_persona: String) = authenticatedUserAction.async { 
        implicit request: Request[AnyContent] =>
        cService.obtenerDireccion(id_identificacion, id_persona).map { d =>
            Ok(Json.toJson(d))
        }
    }

    def obtenerControlCobro(id_colocacion: String) = authenticatedUserAction.async {
        implicit request: Request[AnyContent] =>
        cService.obtenerControlCobro(id_colocacion).map { c =>
          Ok(Json.toJson(c))
        }
    }

    def agregar = authenticatedUserAction.async {
        implicit request: Request[AnyContent] =>
        val json = request.body.asJson.get
        val cc = json.as[ControlCobro]
        val usua_id = Utility.extraerUsuario(request) match {
            case Some(u) => u
            case None => 0
        }
        cService.agregar(cc, usua_id).map { result =>
          if (result) {
              Ok
          } else {
              NotAcceptable
          }
        }
    }
}