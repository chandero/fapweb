package controllers

import javax.inject.Inject

import models._

import play.api.mvc._
import play.api.libs.json._

import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import net.liftweb.json._
import net.liftweb.json.Serialization.write
import net.liftweb.json.Serialization.read
import net.liftweb.json.parse

import utilities._

@Singleton
class DepartamentoController @Inject()(depaService: DepartamentoRepository,cc: ControllerComponents, authenticatedUserAction: AuthenticatedUserAction)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ImplicitJsonFormats {

  implicit val formats = Serialization.formats(NoTypeHints) ++ List(
    DateTimeSerializer
  )


    def todos() = authenticatedUserAction.async { implicit request: Request[AnyContent] => 
        depaService.todos().map { departamentos =>
            Ok(write(departamentos))
        }
    }
}