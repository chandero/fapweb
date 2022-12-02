package controllers

import javax.inject.Inject
import java.time.Instant
import java.nio.charset.StandardCharsets

import org.joda.time.DateTime
import models._
import dto._
import play.api.mvc._
import play.api.libs.json._
import com.google.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}
import pdi.jwt.JwtSession
import play.api.Configuration

import scala.collection.mutable.ListBuffer

import utilities._

@Singleton
class TransaccionalController @Inject()(
    transaccionalService: TransaccionalRepository,
    empresaService: EmpresaRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction,
    config: Configuration
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def autenticar(
      id_identificacion: Int,
      id_persona: String,
      contrasena: String
  ): Action[AnyContent] =
    Action.async { request =>
      println("Recibiendo peticion de autenticacion")
      println(id_identificacion)
      println(id_persona)
      println(contrasena)
      transaccionalService
        .autenticar(id_identificacion, id_persona, contrasena)
        .flatMap { esValido =>
          if (esValido) {
            transaccionalService
              .buscarPorDocumento(id_identificacion, id_persona)
              .flatMap { cliente =>
                var session = request.session
                session = session + ("id_identificacion" -> id_identificacion.toString)
                session = session + ("id_persona" -> id_persona)
                session = session + ("empr_id" -> "1")
                session = session + ("usua_id" -> "9999")
                val token = java.util.Base64.getEncoder.encodeToString(
                  session.toString().getBytes(StandardCharsets.UTF_8)
                )
                val client = new UsuarioDto(
                  Some(0),
                  cliente.get._4,
                  Some(""),
                  cliente.get._1,
                  cliente.get._2,
                  Some(token),
                  0
                )
                Future(Ok(Json.toJson(client)).withSession(session))
              }
          } else {
            Future(Unauthorized("Usuario o Contresaña Incorrecto!"))
          }
        }
    }

  def registrar(
      id_identificacion: Int,
      id_persona: String,
      email: String
  ): Action[AnyContent] =
    Action.async { request =>
      println("Recibiendo peticion de autenticacion")
      println(id_identificacion)
      println(id_persona)
      println(email)
      transaccionalService.registrar(id_identificacion, id_persona, email).map {
        case true  => Ok("Registro Exitoso")
        case false => BadRequest("Registro Fallido")
      }
    }

  def validarEnlace(token: String) = Action.async {
    transaccionalService.validarEnlace(token).map { (result) =>
      result._1 match {
        case true  => Ok(result._2)
        case false => BadRequest(result._2)
      }
    }
  }

  def logout() = authenticatedUserAction.async { implicit request =>
    Future.successful(Ok("logout").withNewSession)
  }

  def recuperarClave(usua_email: String) = Action.async {
    request: Request[AnyContent] =>
      val linkProtocol = config.get[String]("link.protocol")
      transaccionalService.recuperar(linkProtocol, usua_email).map { result =>
        if (result) {
          Ok(Json.toJson("true"))
        } else {
          NotAcceptable(Json.toJson("false"))
        }
      }
  }

  def cambiarClave() = Action(parse.json) { request =>
    val link = (request.body \ "link").as[String]
    val clave = (request.body \ "password").as[String]
    val result: Boolean = transaccionalService.cambiarClave(link, clave)
    if (result) {
      Ok("true")
    } else {
      NotFound
    }
  }
}
