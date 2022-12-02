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
class InformeColocacionCausacionController @Inject()(
    infoService: InformeColocacionCausacionRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction,
    config: Configuration)(
    implicit ec: ExecutionContext)
    extends AbstractController(cc) {

    def consultar(id_colocacion: String) = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      val empr_id = Utility.extraerEmpresa(request)
      infoService.consultar(id_colocacion, empr_id.get).map { lista =>
        Ok(Json.toJson(lista))
      }
    }

    def extracto(id_colocacion: String, fecha_inicial: Long, fecha_final: Long) = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      val empr_id = Utility.extraerEmpresa(request)
      infoService.extracto(id_colocacion, fecha_inicial, fecha_final, empr_id.get).map { lista =>
        Ok(Json.toJson(lista))
      }
    }    

    def exportar(id_colocacion: String, empr_id: Long, token: String) = Action {
      val os = infoService.exportar(id_colocacion, empr_id)
      //val fmt = DateTimeFormat.forPattern("yyyyMMdd")
      val filename = "FAP999"+"_CAUSACION_COLOCACION_" + id_colocacion + ".xlsx"
      val attach = "attachment; filename=" + filename
      Ok(os).as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").withHeaders("Content-Disposition" -> attach )
    }
}