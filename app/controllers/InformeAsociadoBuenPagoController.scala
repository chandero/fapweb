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
class InformeAsociadoBuenPagoController @Inject()(
    infoService: InformeAsociadoBuenPagoRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction,
    config: Configuration)(
    implicit ec: ExecutionContext)
    extends AbstractController(cc) {

    def consultar() = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      val usua_id = Utility.extraerUsuario(request)
      infoService.consultar().map { lista =>
        Ok(Json.toJson(lista))
      }
    }

    def generar() =  authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      val usua_id = Utility.extraerUsuario(request)
      scala.concurrent.Future { infoService.generar() }
      Future.successful(Ok(Json.toJson("procesando")))
    }

    def exportar() = Action {
      val os = infoService.exportar()
      //val fmt = DateTimeFormat.forPattern("yyyyMMdd")
      val filename = "FAP999"+"_CLIENTES_PAGO_NORMAL_SIN_CREDITO_ACTUAL" + ".xlsx"
      val attach = "attachment; filename=" + filename
      Ok(os).as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").withHeaders("Content-Disposition" -> attach )
    }
}