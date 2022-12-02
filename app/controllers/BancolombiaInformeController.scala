package controllers

import javax.inject.Inject

import java.util.Calendar

import models.BancolombiaInformeRepository

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
class BancolombiaInformeController @Inject()(
    aService: BancolombiaInformeRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ImplicitJsonFormats {
  implicit val formats = Serialization.formats(NoTypeHints) ++ List(
    DateTimeSerializer
  )

  def consultar() = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
    val os = aService.consultar()
    val fmt = DateTimeFormat.forPattern("yyyyMMdd")
    val fc = Calendar.getInstance().getTimeInMillis()
    val filename = "FAP999"+"_INFORME_BANCOLOMBIA_" + fmt.print(fc) + ".xlsx"
    val attach = "attachment; filename=" + filename
    Future.successful(Ok(os).as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").withHeaders("Content-Disposition" -> attach ))
    }
}