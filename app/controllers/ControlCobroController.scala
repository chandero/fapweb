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

import com.deepoove.poi.data.RenderData;
import com.deepoove.poi.data.TableRenderData;
import com.deepoove.poi.data.TextRenderData;
import java.util.ArrayList
import scala.collection.immutable.HashMap
import java.{util => ju}
import java.nio.file.Files
import java.nio.file.Paths

@Singleton
class ControlCobroController @Inject()(
    cService: ControlCobroRepository,
    cc: ControllerComponents,
    authenticatedUserAction: AuthenticatedUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ImplicitJsonFormats {
  implicit val formats = Serialization.formats(NoTypeHints) ++ List(
    DateTimeSerializer
  )

  def obtenerCreditosPorEstado(
      estado: Int,
      dias_ini: Int,
      dias_fin: Int,
      ases_id: Int
  ) = authenticatedUserAction.async { implicit request: Request[AnyContent] =>
    // val usua_id = Utility.extraerUsuario(request)
    cService.obtenerCreditos(estado, dias_ini, dias_fin, ases_id).map { p =>
      Ok(Json.toJson(p))
    }
  }

  def obtenerCreditosPorDocumento(id_identificacion: Int, id_persona: String) =
    authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      // val usua_id = Utility.extraerUsuario(request)
      cService.obtenerCreditos(id_identificacion, id_persona).map { p =>
        Ok(Json.toJson(p))
      }
    }

  def obtenerCarteraPorDocumentoAction(
      id_identificacion: Int,
      id_persona: String
  ) = Action.async { implicit request: Request[AnyContent] =>
    // val usua_id = Utility.extraerUsuario(request)
    cService.obtenerCartera(id_identificacion, id_persona).map { p =>
      Ok(Json.toJson(p))
    }
  }

  def obtenerHistoriaPorDocumentoAction(
      id_identificacion: Int,
      id_persona: String
  ) = Action.async { implicit request: Request[AnyContent] =>
    // val usua_id = Utility.extraerUsuario(request)
    cService.obtenerHistoria(id_identificacion, id_persona).map { p =>
      Ok(Json.toJson(p))
    }
  }

  def obtenerFianzaPorDocumentoAction(
      id_identificacion: Int,
      id_persona: String
  ) = Action.async { implicit request: Request[AnyContent] =>
    // val usua_id = Utility.extraerUsuario(request)
    cService.obtenerFianza(id_identificacion, id_persona).map { p =>
      Ok(Json.toJson(p))
    }
  }

  def obtenerCreditoPorColocacion(id_colocacion: String) =
    authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      // val usua_id = Utility.extraerUsuario(request)
      cService.obtenerCreditoPorColocacion(id_colocacion).map { p =>
        Ok(Json.toJson(p))
      }
    }

  def obtenerDireccion(id_identificacion: Int, id_persona: String) =
    authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      cService.obtenerDireccion(id_identificacion, id_persona).map { d =>
        Ok(Json.toJson(d))
      }
    }

  def obtenerControlCobro(id_colocacion: String) =
    authenticatedUserAction.async { implicit request: Request[AnyContent] =>
      cService.obtenerControlCobro(id_colocacion).map { c =>
        Ok(Json.toJson(c))
      }
    }

  def agregar = authenticatedUserAction.async {
    implicit request: Request[AnyContent] =>
      val json = request.body.asJson.get
      val cc = json.as[ControlCobro]
      val usua_id = Utility.extraerUsuario(request) match {
        case Some(u) => u
        case None    => 0
      }
      cService.agregar(cc, usua_id).map { result =>
        if (result) {
          Ok
        } else {
          NotAcceptable
        }
      }
  }

  def formatoPazYSalvo(id_colocacion: String) = authenticatedUserAction.async {
    implicit request: Request[AnyContent] =>
      cService.getPazYSalvoData(id_colocacion).map { _deudores =>
        val _data = new java.util.HashMap[String, Object]()
        var _listDeudor = new ArrayList[ju.HashMap[String, Object]]()
        _deudores.map { _deudor =>
          val _map = new ju.HashMap[String, Object]()
          _map.put("nombre", _deudor._1)
          _map.put("documento", _deudor._2)
          _map.put("tipo", _deudor._3)
          _listDeudor.add(_map)
        }
        println("Deudores: " + _listDeudor)
        _data.put("deudores", _listDeudor)
        _data.put("id_colocacion", id_colocacion)
        val os = PdfCreator.pazYSalvoCreator(id_colocacion, _listDeudor)

/*         val os = DocxGenerator.generateDocxFileFromTemplate2(
          "FaP_Carta_Paz_y_Salvo.docx",
          _data
        )
        val filename: String = "/tmp/fap_paz_y_salvo.docx"
        Files.write(Paths.get(filename), os)
        val pdf = DocxGenerator.convertDocxToPdf(os) */
        val filename: String = "fap_paz_y_salvo.pdf"
        val attach = "attachment; filename=" + filename
        Ok(os)
          .as("application/pdf")
            .withHeaders("Content-Disposition" -> attach )
      }
  }

  def cartaPrimerAviso() = authenticatedUserAction.async {
    implicit request: Request[AnyContent] =>
      val json = request.body.asJson.get
      val credito = (json \ "credito").as[String]
      val deudor = (json \ "deudor").as[String]
      val codeudor = (json \ "codeudor").as[String]
      val tipo = (json \ "tipo").as[String]
      val email = (json \ "email").as[String]
      val _result = cService
        .cartaPrimerAviso(
          (credito, deudor, codeudor, "", "", "", "", "", tipo, email)
        )
      Future.successful(Ok(write(_result)))
  }
}
