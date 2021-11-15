package controllers

import javax.inject.Inject

import models._

import play.api.mvc._
import play.api.libs.json._
import play.api.Configuration

import com.google.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}
import scala.collection
import scala.util.{Failure, Success}

import pdi.jwt.JwtSession

import utilities._
import http._


@Singleton
class HttpClientController  @Inject()(service: FacturaRepository, http: HttpClient, cc: ControllerComponents, authenticatedUserAction: AuthenticatedUserAction, conf: Configuration)(implicit ec: ExecutionContext) extends AbstractController(cc) {
    def status() = Action.async { 
        println("Estoy en el metodo status")
        var url = conf.get[String]("urlFacturacion") + "/Status"
        println("Cree el cliente http")
        var params = collection.immutable.Map[String, String]()
        http.doGet(url, params).map { response =>
            println("Result: "+ response)
            val contentType = response.headers
                                .get("Content-Type")
                                .flatMap(_.headOption)
                                .getOrElse("application/html")
            Ok.chunked(response.bodyAsSource).as(contentType)
        }
    }

    def gettransactionbyid(f: String, p: String, d: String) = Action.async {
        println("Estoy en el metodo gettransactionbyid")
        var url = conf.get[String]("urlFacturacion") + "/GetTransaccionbyIdentificacion/"+ f + "/" + p + "/" + d
        println("Cree el cliente http")
        var params = collection.immutable.Map[String, String]()
        http.doGet(url, params).map { response =>
            println("Result: "+ response)
            val contentType = response.headers
                                .get("Content-Type")
                                .flatMap(_.headOption)
                                .getOrElse("application/html")
            Ok.chunked(response.bodyAsSource).as(contentType)
        }
    }

    def setdocumentbyjson(f: Long) = Action.async { implicit request =>
        println("Estoy en el metodo setdocumentbyjson")
       
        http.enviarDocumento(f).flatMap { response =>
//            response.flatMap { response =>
                println("Result: "+ response)
                val contentType = response.headers
                                .get("Content-Type")
                                .flatMap(_.headOption)
                                .getOrElse("application/html")
                Future.successful(Ok.chunked(response.bodyAsSource).as(contentType))
        }
    }
    
    def setnotadebitobyjson(nd: Long) = Action.async { implicit request =>
        http.enviarNotaDebito(nd).flatMap { response =>
//            response.flatMap { response =>
                println("Result: "+ response)
                val contentType = response.headers
                                .get("Content-Type")
                                .flatMap(_.headOption)
                                .getOrElse("application/html")
                Future.successful(Ok.chunked(response.bodyAsSource).as(contentType))
        }
    }    

    def setnotacreditobyjson(nc: Long) = Action.async { implicit request =>
        http.enviarNotaCredito(nc).flatMap { response =>
//            response.flatMap { response =>
                println("Result: "+ response)
                val contentType = response.headers
                                .get("Content-Type")
                                .flatMap(_.headOption)
                                .getOrElse("application/html")
                Future.successful(Ok.chunked(response.bodyAsSource).as(contentType))
        }
    }    

}