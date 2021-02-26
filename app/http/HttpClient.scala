package http

import javax.inject.Inject

import requests._
import play.api.libs.ws._
import play.api.http.HttpEntity
import play.api.libs.json._
import play.api.Configuration

import scala.concurrent.{ExecutionContext, Future}

import net.liftweb.json._
import net.liftweb.json.Serialization.write

import models._

class HttpClient @Inject()(ws: WSClient, service:FacturaRepository, conf: Configuration)(implicit ec: ExecutionContext) {

    def doGet(url: String, parametros: Map[String,String] ): Future[WSResponse] = {
        //val r = requests.get(url, params = parametros)
        ws.url(url).get().map { response =>
            response
        }
    }

    def doPost(url: String, json: String): Future[WSResponse] = {
        ws.url(url).addHttpHeaders("Content-Type" -> "application/json").post(json).map { response =>
            response
        }                                 
    }

    def setDocument(f: Long) = {
        service.enviarFactura(f).map { rootInterface =>
            // var jsonString = Json.stringify(Json.toJson(rootInterface))
            implicit val formats = DefaultFormats
            var jsonString = write(rootInterface)
            jsonString = "{\"Documento\":" + jsonString + "}"
            println("json factura: " + jsonString)
            jsonString
        }
    }

    def setNotaDebito(nd: Long) = {
        service.enviarNotaDebito(nd).map { rootInterface =>
            // var jsonString = Json.stringify(Json.toJson(rootInterface))
            implicit val formats = DefaultFormats
            var jsonString = write(rootInterface)
            jsonString = "{\"Documento\":" + jsonString + "}"
            println("json nota debito: " + jsonString)
            jsonString
        }
    }
    
    def setNotaCredito(nc: Long) = {
        service.enviarNotaCredito(nc).map { rootInterface =>
            // var jsonString = Json.stringify(Json.toJson(rootInterface))
            implicit val formats = DefaultFormats
            var jsonString = write(rootInterface)
            jsonString = "{\"Documento\":" + jsonString + "}"
            println("json nota crédito: " + jsonString)
            jsonString
        }
    }    

    def setDocumentJson(f: Long) = {
         setDocument(f).map { jsonString =>
            var url = conf.get[String]("urlFacturacion") + "/SetDocument"
            var params = collection.immutable.Map[String, String]() 
            println("Cadena a Enviar:" + jsonString)
            doPost(url, jsonString).map { response =>
                response
            }
         }
    }

    def setNotaDebitoJson(nd: Long) = {
         setNotaDebito(nd).map { jsonString =>
            var url = conf.get[String]("urlFacturacion") + "/SetDocument"
            var params = collection.immutable.Map[String, String]() 
            println("Cadena ND a Enviar:" + jsonString)
            doPost(url, jsonString).map { response =>
                response
            }
         }
    }

    def setNotaCreditoJson(nc: Long) = {
         setNotaCredito(nc).map { jsonString =>
            var url = conf.get[String]("urlFacturacion") + "/SetDocument"
            var params = collection.immutable.Map[String, String]() 
            println("Cadena NC a Enviar:" + jsonString)
            doPost(url, jsonString).map { response =>
                response
            }
         }
    }    

    def enviarDocumento(f: Long) = {
        setDocumentJson(f).flatMap { response =>
            response
        }
    }

    def enviarNotaDebito(nd: Long) = {
        setNotaDebitoJson(nd).flatMap { response =>
            response
        }
    }

    def enviarNotaCredito(nc: Long) = {
        setNotaDebitoJson(nc).flatMap { response =>
            response
        }
    }

 }