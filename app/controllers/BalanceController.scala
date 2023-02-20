package controllers

import javax.inject.Inject

import models.BalanceRepository

import play.api.mvc._
import play.api.libs.json._

import net.liftweb.json._
import net.liftweb.json.Serialization.write
import net.liftweb.json.Serialization.read
import net.liftweb.json.parse

import org.joda.time.LocalDate
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import pdi.jwt.JwtSession

import utilities._

@Singleton
class BalanceController @Inject()(
    aService: BalanceRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ImplicitJsonFormats {
  implicit val formats = Serialization.formats(NoTypeHints) ++ List(
    DateTimeSerializer
  )

  def consultar() = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
    val empr_id = Utility.extraerEmpresa(request)
    val json = request.body.asJson.get
    val ci = json.\("ci").as[String]
    val cf = json.\("cf").as[String]
    val fc = json.\("fc").as[Long]
    val n = json.\("n").as[Int]
    println("Paso a consultar balance")
    val p = aService.consultar(empr_id.get, fc, ci, cf, n)
    Future.successful(Ok(write(p)))
  }

  def aExcel() = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
    val empr_id = Utility.extraerEmpresa(request)
    val json = request.body.asJson.get
    val ci = json.\("ci").as[String]
    val cf = json.\("cf").as[String]
    val fc = json.\("fc").as[Long]
    val n = json.\("n").as[Int]
    val cm = json.\("cm").as[Int]
    val os = aService.aExcel(empr_id.get, ci, cf, fc, n, cm)
    val fmt = DateTimeFormat.forPattern("yyyyMMdd")
    val filename = "FAP102"+"_BALANCE_GENERAL_CONTABLE_" + fmt.print(fc) + ".xlsx"
    val attach = "attachment; filename=" + filename
    Future.successful(Ok(os).as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").withHeaders("Content-Disposition" -> attach ))
    }

}