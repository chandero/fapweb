package controllers

import javax.inject.Inject
import models._
import play.api.mvc._
import play.api.libs.json._
import com.google.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}

import pdi.jwt.JwtSession

import dto.SimuladorDto

import utilities._

@Singleton
class CalculoController @Inject()(
    calculoService: CalculoRepository,
    cc: ControllerComponents)(
    implicit ec: ExecutionContext)
    extends AbstractController(cc) {

        def tabla() = Action.async { implicit request: Request[AnyContent] =>
          val json = request.body.asJson.get
          val linea = (json \ "linea").as[Long]
          val valor = (json \ "valor").as[BigDecimal]
          val plazo = (json \ "plazo").as[Int]

          calculoService.tabla(linea, valor, plazo).map { tabla =>
            println(tabla.toString) 
            Ok(Json.toJson(tabla))
          }
        }

    }