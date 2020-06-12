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
class PersonaController @Inject()(
    pService: PersonaRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {
        def obtener(id_identificacion: Int, id_persona: String) = authenticatedUserAction.async {
            implicit request: Request[AnyContent] =>
              val usua_id = Utility.extraerUsuario(request)
              pService.obtener(id_identificacion, id_persona).map { p =>
                p.a match {
                  case Some(a) => Ok(Json.toJson(p))
                  case None => NotFound
                }
              }
        }

        def obtenerPorApellidosyNombres() = authenticatedUserAction.async {
            implicit request: Request[AnyContent] =>
              val usua_id = Utility.extraerUsuario(request)
              val json = request.body.asJson.get
              val primer_apellido = (json \ "primer_apellido").as[String]
              val segundo_apellido = (json \ "segundo_apellido").as[String]
              val nombre = (json \ "nombre").as[String]
              pService.obtenerPorApellidosyNombres(primer_apellido, segundo_apellido, nombre).map { p =>
                Ok(Json.toJson(p))
              }
        }
        
        def obtenerPersonaPorColocacion(c: String) = authenticatedUserAction.async {
            implicit request: Request[AnyContent] =>
              val usua_id = Utility.extraerUsuario(request)
              pService.obtenerPersonaPorColocacion(c).map { p =>
                Ok(Json.toJson(p))
              }
        }        

        def guardar() = authenticatedUserAction.async {
          implicit request: Request[AnyContent] =>
          val usua_id = Utility.extraerUsuario(request)
          val json = request.body.asJson.get
          var p = json.as[Persona]
          pService.guardar(p).map { result =>
            if (result) {
              Ok
            } else {
              NotModified
            }
          }
        }
    }