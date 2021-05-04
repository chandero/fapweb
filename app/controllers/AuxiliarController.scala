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
    val fi = json.\("fi").as[Long]
    val ff = json.\("ff").as[Long]
    val id = json.\("id").as[Int]
    val ip = json.\("ip").as[String]
    aService.consultar(ci, cf, fi, ff, Some(id), Some(ip)).map { p =>
      Ok(write(p))
    }
  }
}
