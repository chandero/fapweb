package controllers

import javax.inject.Inject

import models._

import play.api.mvc._
import play.api.libs.json._

import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import net.liftweb.json._
import net.liftweb.json.Serialization.write

@Singleton
class FacturaController @Inject()(service: FacturaRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {
    def buscarPorNumero(fact_numero: Long): Action[AnyContent] = Action.async { request =>
        service.buscarPorNumero(fact_numero).map { factura => 
            Ok(Json.toJson(factura))
        }
    }

    def enviarFactura(fact_numero: Long): Action[AnyContent] = Action.async { request =>
        service.enviarFactura(fact_numero).map { rootInterface =>
            implicit val formats = DefaultFormats
            Ok(write(rootInterface))
        }
    }
}