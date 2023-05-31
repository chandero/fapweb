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

import net.liftweb.json._
import net.liftweb.json.Serialization.write
import net.liftweb.json.Serialization.read
import net.liftweb.json.parse

import utilities._

@Singleton
class InformesController @Inject()(
    iService: InformesRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ImplicitJsonFormats {
  implicit val formats = Serialization.formats(NoTypeHints) ++ List(
    DateTimeSerializer
  )

  def getLiquidacionAplicadaWeb(fecha_inicial: scala.Long, fecha_final: scala.Long) = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
    val p = iService.getLiquidacionAplicada(fecha_inicial, fecha_final, "WEB")
    Future.successful(Ok(write(p)))
  }

  def getLiquidacionAplicadaWebXlsx(fecha_inicial: scala.Long, fecha_final: scala.Long) = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
    val empr_id = Utility.extraerEmpresa(request)
    val sdf = new java.text.SimpleDateFormat("yyyy-MM-dd")
      val os = iService.getLiquidacionAplicadaXlsx(fecha_inicial, fecha_final, "WEB", empr_id.get)
      //val fmt = DateTimeFormat.forPattern("yyyyMMdd")
      val filename = "FAP999"+"_RECAUDO_WEB_Periodo_" + sdf.format(fecha_inicial) + "_" + sdf.format(fecha_final) + ".xlsx"
      val attach = "attachment; filename=" + filename
      Future.successful(Ok(os).as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").withHeaders("Content-Disposition" -> attach ))
  }

  def getLiquidacionPendienteWeb(fecha_inicial: scala.Long, fecha_final: scala.Long) = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
    val p = iService.getLiquidacionPendiente(fecha_inicial, fecha_final, "WEB")
    Future.successful(Ok(write(p)))
  }

  def getLiquidacionPendienteWebXlsx(fecha_inicial: scala.Long, fecha_final: scala.Long) = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
    val empr_id = Utility.extraerEmpresa(request)
    val sdf = new java.text.SimpleDateFormat("yyyy-MM-dd")
      val os = iService.getLiquidacionPendienteXlsx(fecha_inicial, fecha_final, "WEB", empr_id.get)
      //val fmt = DateTimeFormat.forPattern("yyyyMMdd")
      val filename = "FAP999"+"_RECAUDO_PENDIENTE_POR_APLICAR_WEB_Periodo_" + sdf.format(fecha_inicial) + "_" + sdf.format(fecha_final) + ".xlsx"
      val attach = "attachment; filename=" + filename
      Future.successful(Ok(os).as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").withHeaders("Content-Disposition" -> attach ))
  }    
}