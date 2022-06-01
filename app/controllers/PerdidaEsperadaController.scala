package controllers

import javax.inject.Inject

import models._

import play.api.mvc._
import play.api.libs.json._

import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

import pdi.jwt.JwtSession

import utilities._

import net.liftweb.json._
import net.liftweb.json.Serialization.write
import net.liftweb.json.Serialization.read
import net.liftweb.json.parse

@Singleton
class PerdidaEsperadaController @Inject()(
    service: PerdidaEsperadaRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ImplicitJsonFormats {
  implicit val formats = Serialization.formats(NoTypeHints) ++ List(
    DateTimeSerializer
  )

  def todos() = authenticatedUserAction.async {
    implicit request: Request[AnyContent] =>
      service.todos.map { result =>
        Ok(write(result))
      }
  }

  def ultimaCarga() = authenticatedUserAction { implicit request =>
    val empr_id = Utility.extraerEmpresa(request)
    val ultimaCarga = service.ultimaCarga(empr_id.get)
    Ok(write(ultimaCarga))
  }

  def delete(anho: Int, mes: Int) = authenticatedUserAction.async {
    implicit request =>
      val result = service.delete(anho, mes)
      Future.successful(Ok(write(result)))
  }

  def loadData(
      anho: Int,
      mes: Int,
      uuid: String
  ) = authenticatedUserAction.async { implicit request =>
    val empr_id = Utility.extraerEmpresa(request)
    val usua_id = Utility.extraerUsuario(request)
    val thread = new Thread {
      override def run {
        /*
        println("Procesando 1.Usuario")
        service.loadData1Usuario(anho, mes, usua_id.get, uuid)
        println("Procesando 2.Cartera")
        service.loadData2Cartera(anho, mes, usua_id.get, uuid)
        println("Procesando 3.Deposito")
        service.loadData3Deposito(anho, mes, usua_id.get, uuid)
        println("Procesando 4. Aporte")
        service.loadData4Aporte(anho, mes, usua_id.get, uuid)
         */
        service.loadData(anho, mes, usua_id.get, uuid)
      }
    }
    thread.start
    Future.successful(Ok("true"))
  }
}
