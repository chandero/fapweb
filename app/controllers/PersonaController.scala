package controllers

import javax.inject.Inject

import models._

import play.api.mvc._
import play.api.libs.json._

import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import pdi.jwt.JwtSession

import utilities._

import net.liftweb.json._
import net.liftweb.json.Serialization.write
import net.liftweb.json.Serialization.read
import net.liftweb.json.parse

@Singleton
class PersonaController @Inject()(
    pService: PersonaRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) with ImplicitJsonFormats {
  implicit val formats = Serialization.formats(NoTypeHints) ++ List(
    DateTimeSerializer
  )
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

        def obtenerAction(id_identificacion: Int, id_persona: String) = Action.async {
            implicit request: Request[AnyContent] =>
              pService.obtener(id_identificacion, id_persona).map { p =>
                p.a match {
                  case Some(a) => Ok(write(p))
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

        def guardarAction() = Action.async {
          implicit request: Request[AnyContent] =>
          val json = request.body.asJson.get
          var p = net.liftweb.json.parse(json.toString()).extract[Persona]
          pService.guardar(p).map { result =>
            if (result) {
              Ok
            } else {
              NotModified
            }
          }
        }        
    }