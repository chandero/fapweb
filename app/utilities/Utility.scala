package utilities

import play.api.mvc._

import pdi.jwt.JwtSession

class Utility {
    
    def extraerUsuario(request: Request[AnyContent]): Option[Long] = {
      val token = request.headers.get("Authorization")
      token match {
        case None => {
         return None
        }
        case Some(token) => {
          val session = JwtSession.deserialize(token)
          val usuaId = session.get("usua_id")
          println("session: " + session)
          usuaId match {
            case None => {
              None
            }
            case Some(usuaId) => {
              Option(usuaId.as[Long])
            }
          }
        }
      }
    }

    def extraerEmpresa(request: Request[AnyContent]): Option[Long] = {
      val token = request.headers.get("Authorization")
      token match {
        case None => {
         return None
        }
        case Some(token) => {
          val session = JwtSession.deserialize(token)
          val emprId = session.get("empr_id")
          emprId match {
            case None => {
              None
            }
            case Some(emprId) => {
              Option(emprId.as[Long])
            }
          }
        }
      }
    }
    
    def obtenerMes(periodo: Int) = {
      periodo match {
        case 1 => "Enero"
        case 1 => "Febrero"
        case 1 => "Marzo"
        case 1 => "Abril"
        case 1 => "Mayo"
        case 1 => "Junio"
        case 1 => "Julio"
        case 1 => "Agosto"
        case 1 => "Septiembre"
        case 1 => "Octubre"
        case 1 => "Noviembre"
        case 1 => "Diciembre"
        case _ => "Indefinido"
      }
    }
}

object Utility extends Utility