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
class UsuarioController @Inject()(
    usuarioService: UsuarioRepository,
    empresaService: EmpresaRepository,
    perfilService: PerfilRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction,
    config: Configuration)(
    implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def todos(page_size:Long, current_page:Long) = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
    val empr_id = Utility.extraerEmpresa(request);
    val total = usuarioService.cuenta(empr_id.get)
    usuarioService.todos(empr_id.get, page_size:Long, current_page:Long).map { usuarios =>
     Ok(Json.obj("usuarios" -> usuarios, "total" -> total))
    }
  }

  def autenticar(usua_email: String, usua_clave: String): Action[AnyContent] =
    Action.async { request =>
      usuarioService.autenticar(usua_email, usua_clave).flatMap { esValido =>
        if (esValido) {
          usuarioService.buscarPorEmail(usua_email).flatMap { usuario =>
            var session = request.session
            session = session + ("usua_id" -> usuario.get.id.get.toString())
            session = session + ("empr_id" -> usuario.get.empr_id.get.toString())
            val token = java.util.Base64.getEncoder.encodeToString(session.toString().getBytes(StandardCharsets.UTF_8))
            val user = new UsuarioDto(usuario.get.id,
                                      usuario.get.email.get,
                                      Some(""),
                                      usuario.get.nombre.get,
                                      usuario.get.primer_apellido.get,
                                      Some(token),
                                      0)
            Future(Ok(Json.toJson(user)).withSession(session))
          }
        } else {
          Future(Unauthorized("Usuario o Contresaña Incorrecto!"))
        }
      }
    }

  def buscarporemail(usua_email: String): Action[AnyContent] = Action.async {
    usuarioService.buscarPorEmail(usua_email).map { usuario =>
      Ok(Json.toJson(usuario))
    }
  }

  def buscarporid(usua_id: Long): Action[AnyContent] = Action.async {
    usuarioService.buscarPorId(usua_id).map { usuario =>
      Ok(Json.toJson(usuario))
    }
  }

  def logout() = authenticatedUserAction.async { implicit request => 
    Future.successful(Ok("logout").withNewSession)
  }

  def userinfo(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      val token = request.headers.get("Authorization")
      var session = request.session
      val usua_id = Utility.extraerUsuario(request)
      val empr_id = Utility.extraerEmpresa(request)
      usuarioService.buscarPorId(usua_id.get).map { usuario =>
        usuario match {
          case None => 
            Forbidden("No hay registro activo.")
          
          case Some(usuario) => 
            val uep = perfilService.buscarPorId(usuario.perf_id.get)
            val empresa = empresaService.buscarPorId(empr_id.get)
            val perfil = new ListBuffer[String]()
            perfil += uep.get.perf_abreviatura
            empresa match {
                case None => 
                  Forbidden("No hay registro activo.")
                
                case Some(empresa) => 
                  var newsession = request.session
                  newsession = newsession + ("usua_id" -> usuario.id.get.toString())
                  newsession = newsession + ("empr_id" -> empresa.empr_id.get.toString())
                  var newtoken = java.util.Base64.getEncoder.encodeToString(newsession.toString().getBytes(StandardCharsets.UTF_8))
                  var userinfo = new UserInfoDto(
                    usuario.id.get,
                    usuario.email.get,
                    usuario.nombre.get,
                    usuario.primer_apellido.get,
                    empresa.empr_id.get,
                    empresa.empr_descripcion,
                    newtoken,
                    perfil.toList,
                    "admin"
                  )
                  Ok(Json.toJson(userinfo)).withSession(session)
              }
        }
      }
    }

  def guardar() = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
    val json = request.body.asJson.get
    println(json)
    val id_empleado = Utility.extraerUsuario(request)
    val empr_id = Utility.extraerEmpresa(request)
    val usuario = json.as[Usuario]
    val nuevousuario = new Usuario(usuario.id_empleado, 
                                   usuario.documento, 
                                   usuario.primer_apellido, 
                                   usuario.segundo_apellido, 
                                   usuario.nombre, 
                                   usuario.numero_cuenta, 
                                   usuario.ultimo_cambio_pasabordo, 
                                   usuario.ag, 
                                   usuario.contrasena, 
                                   usuario.tipo, 
                                   usuario.email, 
                                   usuario.ultima_sesion, 
                                   empr_id, 
                                   usuario.perf_id,
                                   usuario.id)
    usuarioService.crear(nuevousuario).map { result =>
        if (result > 0){
          Created(Json.toJson("true"))
        } else {
          NotAcceptable(Json.toJson("true"))
        }
      }
  }

  def recuperarClave(usua_email:String) = Action.async { request: Request[AnyContent] =>
    val linkProtocol = config.get[String]("link.protocol")
    usuarioService.recuperar(linkProtocol,usua_email).map { result => 
      if (result){
        Ok(Json.toJson("true"))
      } else {
        NotAcceptable(Json.toJson("false"))
      }
    }
  }

  def linkValidator(link: String) = Action.async { request: Request[AnyContent] =>
    usuarioService.validarEnlace(link).map { result =>
      if (result) {
        Ok(Json.toJson("true"))
      } else {
        NotFound
      }
    }
  }

  def cambiarClave() = Action(parse.json) { request =>
    val link = (request.body \ "link").as[String]
    val clave = (request.body \ "password").as[String]
    val result:Boolean = usuarioService.cambiarClave(link, clave)
    if (result) {
      Ok(Json.toJson("true"))
    } else {
      NotFound
    }
  }
}
