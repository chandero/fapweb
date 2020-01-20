package controllers

import javax.inject.Inject

import models._

import play.api.mvc._
import play.api.libs.json._
import play.api.Configuration

import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}
import scala.collection

import pdi.jwt.JwtSession

import utilities._
import http._


@Singleton
class HttpClientController  @Inject()(service: FacturaRepository, cc: ControllerComponents, authenticatedUserAction: AuthenticatedUserAction, conf: Configuration)(implicit ec: ExecutionContext) extends AbstractController(cc) {
    def status() = Action { 
        println("Estoy en el metodo status")
        var url = conf.get[String]("urlFacturacion") + "/Status"
        var http = new HttpClient()
        println("Cree el cliente http")
        var params = collection.immutable.Map[String, String]()
        var result = http.doGet(url, params)
        println("Envie el Get")
        println("Result: "+ result)
        Ok(result.text)
    }

    def gettransactionbyid(f: String, p: String, d: String) = Action {
        println("Estoy en el metodo gettransactionbyid")
        var url = conf.get[String]("urlFacturacion") + "/GetTransaccionbyIdentificacionJson/"+ f + "/" + p + "/" + d
        var http = new HttpClient()
        println("Cree el cliente http")
        var params = collection.immutable.Map[String, String]()
        var result = http.doGet(url, params)
        println("Envie el Get")
        println("Result: "+ result)
        Ok(result.text)
    }

    def setdocumentbyjson(f: Long) = Action.async { implicit request =>
        println("Estoy en el metodo setdocumentbyjson")
        var url = conf.get[String]("urlFacturacion") + "/SetDocumentbyjson"
        var http = new HttpClient()
        println("Cree el cliente http")
        var params = collection.immutable.Map[String, String]()
        service.enviarFactura(f).map { rootInterface =>
            var result = http.doPost(url, Json.stringify(Json.toJson(rootInterface)))
            println("Envie el Post")
            println("Result: "+ result)
            Ok(result.text)
        }        
        
    }    

}