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
class InformesGraficasController @Inject()(
    iService: InformesGraficasRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction,
    config: Configuration)(
    implicit ec: ExecutionContext)
    extends AbstractController(cc) {

    def recaudoDiarioMes(anho: Int, mes: Int) = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      val usua_id = Utility.extraerUsuario(request)
      iService.recaudoDiarioMes(anho, mes).map { lista =>
        Ok(Json.toJson(lista))
      }
    }
    
    def recaudoInteresCausadoPeriodoGracia(fecha_inicial: Long, fecha_final: Long) = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      val usua_id = Utility.extraerUsuario(request)
      iService.recaudoInteresCausadoPeriodoGracia(fecha_inicial, fecha_final).map { lista =>
        Ok(Json.toJson(lista))
      }
    }    
}