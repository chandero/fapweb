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
class InformeColocacionSaldadaAsesorController @Inject()(
    iService: InformeColocacionSaldadaAsesorRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction,
    config: Configuration)(
    implicit ec: ExecutionContext)
    extends AbstractController(cc) {

    def generarXlsx(ases_id: Long) = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      val usua_id = Utility.extraerUsuario(request)
      val empr_id = Utility.extraerEmpresa(request)
      val os = iService.generarXlsx(ases_id, empr_id.get)
      val filename = "FAP103"+"_COLOCACION_SALDADA_POR_ASESOR" + ".xlsx"
      val attach = "attachment; filename=" + filename
      Future.successful(Ok(os).as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").withHeaders("Content-Disposition" -> attach ))
    }
}