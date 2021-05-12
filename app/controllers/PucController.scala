package controllers

import javax.inject.Inject

import models._

import play.api.mvc._
import play.api.libs.json._

import net.liftweb.json._
import net.liftweb.json.Serialization.write
import net.liftweb.json.Serialization.read
import net.liftweb.json.parse

import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import pdi.jwt.JwtSession

import utilities._

@Singleton
class PucController @Inject()(
    pService: PucRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) with ImplicitJsonFormats {
  implicit val formats = Serialization.formats(NoTypeHints) ++ List(
    DateTimeSerializer
  )
        def obtenerLista() = authenticatedUserAction.async { implicit request =>
            pService.obtenerLista.map { p => 
                Ok(write(p))
            }
        }

        def obtenerNombre(codigo: String) = authenticatedUserAction.async { implicit request =>
            pService.obtenerNombre(codigo).map { e =>
                Ok(Json.toJson(e))
            }
        }

        

    }