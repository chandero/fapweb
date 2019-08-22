package controllers

import javax.inject.Inject
import java.time.Instant
import org.joda.time.DateTime
import models._
import dto._
import play.api.mvc._
import play.api.libs.json._
import com.google.inject.Singleton
import pdi.jwt.JwtSession
import play.api.Configuration

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}

import utilities._

@Singleton
class InformacionCreditoController @Inject()(
    infoService: InformacionCreditoRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction,
    config: Configuration)(
    implicit ec: ExecutionContext)
    extends AbstractController(cc) {

    def buscarCredito(id_identificacion: Int, id_persona: String) = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      val usua_id = Utility.extraerUsuario(request)
      val empr_id = Utility.extraerEmpresa(request)
      infoService.buscarCredito(id_identificacion, id_persona, empr_id.get).flatMap { lista =>
          lista.map {
            Ok(Json.toJson(lista))
          }
      }
    }
}