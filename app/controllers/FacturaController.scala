package controllers

import javax.inject.Inject

import models._

import play.api.mvc._
import play.api.libs.json._
import play.api.Configuration

import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import net.liftweb.json._
import net.liftweb.json.Serialization.write
import net.liftweb.json.Serialization.read
import net.liftweb.json.parse

import org.joda.time.LocalDate
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import dto.QueryDto
import dto.ResultDto

import utilities._

@Singleton
class FacturaController @Inject()(
    service: FacturaRepository,
    cc: ControllerComponents,
    config: Configuration,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ImplicitJsonFormats {
  implicit val formats = Serialization.formats(NoTypeHints) ++ List(
    DateTimeSerializer
  )

  def buscarPorNumero(fact_numero: Long): Action[AnyContent] = Action.async {
    request =>
      service.buscarPorNumero(fact_numero).map { factura =>
        factura match {
          case Some(f) => Ok(write(f))
          case None    => NotFound
        }
      }
  }

    def buscarDSAPorNumero(fact_numero: Long): Action[AnyContent] = Action.async {
    request =>
      service.buscarDSAPorNumero(fact_numero).map { factura =>
        factura match {
          case Some(f) => Ok(write(f))
          case None    => NotFound
        }
      }
  }

  def todosFactura(): Action[AnyContent] =
    authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      val json = request.body.asJson.get
      val page_size = (json \ "page_size").as[Long]
      val current_page = (json \ "current_page").as[Long]
      val orderby = (json \ "orderby").as[String]
      val filter = (json \ "filter").as[QueryDto]
      var filtro = Utility.procesarFiltrado(filter)
      if (filtro == "()") {
        filtro = ""
      }
      val empr_id = Utility.extraerEmpresa(request)
      val total = service.cuentaFactura()
      service
        .todosFactura(page_size, current_page, empr_id.get, orderby, filtro)
        .map { facturas =>
          Ok(write(new ResultDto(facturas, total)))
        }
    }

  def todosDSA(): Action[AnyContent] =
    authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      val json = request.body.asJson.get
      val page_size = (json \ "page_size").as[Long]
      val current_page = (json \ "current_page").as[Long]
      val orderby = (json \ "orderby").as[String]
      val filter = (json \ "filter").as[QueryDto]
      var filtro = Utility.procesarFiltrado(filter)
      if (filtro == "()") {
        filtro = ""
      }
      val empr_id = Utility.extraerEmpresa(request)
      val total = service.cuentaDSA()
      service
        .todosDSA(page_size, current_page, empr_id.get, orderby, filtro)
        .map { facturas =>
          Ok(write(new ResultDto(facturas, total)))
        }
    }    

  def todosNotaDebito(): Action[AnyContent] =
    authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      val json = request.body.asJson.get
      val page_size = (json \ "page_size").as[Long]
      val current_page = (json \ "current_page").as[Long]
      val orderby = (json \ "orderby").as[String]
      val filter = (json \ "filter").as[QueryDto]
      var filtro = Utility.procesarFiltrado(filter)
      if (filtro == "()") {
        filtro = ""
      }
      val empr_id = Utility.extraerEmpresa(request)
      val total = service.cuentaNotaDebito()
      service
        .todosNotaDebito(page_size, current_page, empr_id.get, orderby, filtro)
        .map { facturas =>
          println("notas: " + write(facturas))
          Ok(write(new ResultDto(facturas, total)))
        }
    }

  def todosNotaCredito(): Action[AnyContent] =
    authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      val json = request.body.asJson.get
      val page_size = (json \ "page_size").as[Long]
      val current_page = (json \ "current_page").as[Long]
      val orderby = (json \ "orderby").as[String]
      val filter = (json \ "filter").as[QueryDto]
      var filtro = Utility.procesarFiltrado(filter)
      if (filtro == "()") {
        filtro = ""
      }
      val empr_id = Utility.extraerEmpresa(request)
      val total = service.cuentaNotaCredito()
      service
        .todosNotaCredito(page_size, current_page, empr_id.get, orderby, filtro)
        .map { facturas =>
          Ok(write(new ResultDto(facturas, total)))
        }
    }

  def todosNotaCreditoDSA(): Action[AnyContent] =
    authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      val json = request.body.asJson.get
      val page_size = (json \ "page_size").as[Long]
      val current_page = (json \ "current_page").as[Long]
      val orderby = (json \ "orderby").as[String]
      val filter = (json \ "filter").as[QueryDto]
      var filtro = Utility.procesarFiltrado(filter)
      if (filtro == "()") {
        filtro = ""
      }
      val empr_id = Utility.extraerEmpresa(request)
      val total = service.cuentaNotaCreditoDSA()
      service
        .todosNotaCreditoDSA(page_size, current_page, empr_id.get, orderby, filtro)
        .map { facturas =>
          Ok(write(new ResultDto(facturas, total)))
        }
    }    

  def enviarFactura(fact_numero: Long): Action[AnyContent] = Action.async {
    request =>
      service.enviarFactura(fact_numero).map { rootInterface =>
        implicit val formats = DefaultFormats
        Ok(write(rootInterface))
      }
  }

  def enviarDSA(dsa_numero: Long): Action[AnyContent] = Action.async {
    request =>
      service.enviarDSA(dsa_numero).map { rootInterface =>
        implicit val formats = DefaultFormats
        Ok(write(rootInterface))
      }
  }

  def crearNotaFactura() = authenticatedUserAction.async { implicit request =>
    val json = request.body.asJson.get
    println("Json Nota: " + json)
    var nota = net.liftweb.json.parse(json.toString()).extract[FacturaNota]
    val usua_id = Utility.extraerUsuario(request)
    val empr_id = Utility.extraerEmpresa(request)
    empr_id match {
      case Some(e) =>
        service.crearNota(nota, usua_id.get).map { result =>
          Ok(write(result))
        }
      case None => Future.successful(NotFound)
    }
  }

  def enviarNotaDebito(fact_nota_numero: Long): Action[AnyContent] =
    Action.async { request =>
      service.enviarNotaDebito(fact_nota_numero).map { rootInterface =>
        implicit val formats = DefaultFormats
        Ok(write(rootInterface))
      }
    }

  def enviarNotaCredito(fact_nota_numero: Long): Action[AnyContent] =
    Action.async { request =>
      service.enviarNotaCredito(fact_nota_numero).map { rootInterface =>
        implicit val formats = DefaultFormats
        Ok(write(rootInterface))
      }
    }

  def enviarNotaCreditoDSA(fact_nota_numero: Long): Action[AnyContent] =
    Action.async { request =>
      service.enviarNotaCreditoDSA(fact_nota_numero).map { rootInterface =>
        implicit val formats = DefaultFormats
        Ok(write(rootInterface))
      }
    }
}
