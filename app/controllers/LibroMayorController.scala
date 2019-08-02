package controllers

import javax.inject.Inject
import java.time.Instant
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
class LibroMayorController @Inject()(
    mayorService: LibroMayorRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction,
    config: Configuration)(
    implicit ec: ExecutionContext)
    extends AbstractController(cc) {

    def consultar(anho: Int) = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      val usua_id = Utility.extraerUsuario(request)
      mayorService.consultar(anho).map { lista =>
        Ok(Json.toJson(lista))
      }
    }

    def generar(periodo: Int, anho: Int) = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
        val usua_id = Utility.extraerUsuario(request)
        mayorService.generar(periodo, anho, usua_id.get).map { exito =>
          Ok(Json.toJson(exito))
        }
    }

    def ver(lire_anho: Int, lire_periodo: Int, lire_consecutivo: Int) = Action {
      val os = mayorService.ver(lire_anho, lire_periodo, lire_consecutivo)
      Ok(os).as("application/pdf")
    }

}