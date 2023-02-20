package controllers

import javax.inject.Inject

import models.AuxiliarRepository

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
class AuxiliarController @Inject()(
    aService: AuxiliarRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ImplicitJsonFormats {
  implicit val formats = Serialization.formats(NoTypeHints) ++ List(
    DateTimeSerializer
  )

  def consultar() = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
    val json = request.body.asJson.get
    val ci = json.\("ci").as[String]
    val cf = json.\("cf").as[String]
    val anho = json.\("anho").as[Int]
    val fi = json.\("fi").as[Long]
    val ff = json.\("ff").as[Long]
    val id = json.\("id").as[Int]
    val ip = json.\("ip").as[String]
    val p = aService.consultar(ci, cf, anho, fi, ff, id, ip)
    Future.successful(Ok(write(p)))
  }

  def aExcel() = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
    val empr_id = Utility.extraerEmpresa(request)
    val json = request.body.asJson.get
    val ci = json.\("ci").as[String]
    val cf = json.\("cf").as[String]
    val anho = json.\("anho").as[Int]
    val fi = json.\("fi").as[Long]
    val ff = json.\("ff").as[Long]
    val id = json.\("id").as[Int]
    val ip = json.\("ip").as[String]
    val os = aService.aExcel(empr_id.get, ci, cf, anho, fi, ff, id, ip)
    val fmt = DateTimeFormat.forPattern("yyyyMMdd")
    val filename = "FAP101"+"_AUXILIAR_CONTABLE_" + fmt.print(fi) + "_" + fmt.print(ff)  + ".xlsx"
    val attach = "attachment; filename=" + filename
    Future.successful(Ok(os).as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").withHeaders("Content-Disposition" -> attach ))
    }
}
