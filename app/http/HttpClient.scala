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

import play.api.libs.ws.ahc.AhcWSResponse
import play.api.libs.ws.ahc.cache.CacheableHttpResponseStatus
import play.shaded.ahc.org.asynchttpclient.Response
import play.shaded.ahc.io.netty.handler.codec.http.DefaultHttpHeaders
import play.api.libs.ws.ahc.cache.CacheableHttpResponseBodyPart
import play.shaded.ahc.org.asynchttpclient.uri.Uri

class HttpClient @Inject()(ws: WSClient, service:FacturaRepository, conf: Configuration)(implicit ec: ExecutionContext) {

    def testResponse(jsonString: String) = {
        val respBuilder = new Response.ResponseBuilder()
        respBuilder.accumulate(new CacheableHttpResponseStatus(Uri.create("http://localhost:9000/api/service"), 202, "status text", "json"))
        respBuilder.accumulate(new DefaultHttpHeaders().add("Content-Type", "application/json"))
        respBuilder.accumulate(new CacheableHttpResponseBodyPart(jsonString.getBytes(), true))
        val resp = new AhcWSResponse(respBuilder.build())
        resp
    }

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
            /* Por el Momento No Enviar
            var url = conf.get[String]("urlFacturacion") + "/SetDocument"
            var params = collection.immutable.Map[String, String]() 
            println("Cadena a Enviar:" + jsonString)
            doPost(url, jsonString).map { response =>
                response
            }
            */
            Future.successful(testResponse(jsonString))
         }
    }

    def setNotaDebitoJson(nd: Long) = {
         setNotaDebito(nd).map { jsonString =>
            // Por el momento No Enviar
            /*
            var url = conf.get[String]("urlFacturacion") + "/SetDocument"
            var params = collection.immutable.Map[String, String]() 
            println("Cadena ND a Enviar:" + jsonString)
            doPost(url, jsonString).map { response =>
                response
            }
            */
            Future.successful(testResponse(jsonString))
         }
    }

    def setNotaCreditoJson(nc: Long) = {
         setNotaCredito(nc).map { jsonString =>
            // Por el momento No Enviar
            /*            
            var url = conf.get[String]("urlFacturacion") + "/SetDocument"
            var params = collection.immutable.Map[String, String]() 
            println("Cadena NC a Enviar:" + jsonString)
            doPost(url, jsonString).map { response =>
                response
            }
            */
            Future.successful(testResponse(jsonString))
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
        setNotaCreditoJson(nc).flatMap { response =>
            response
        }
    }

 }