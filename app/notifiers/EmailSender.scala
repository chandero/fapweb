package notifiers

import notifiers._
import views._

class EmailSender {
  def sendPasswordRecovery(usua_email: String, nombre: String, enlace: String) {
    val mail = new MailloyContext with Mailloy
    mail.setSubject("Recuperación de Contraseña")
    mail.addRecipient(usua_email)
    mail.addFrom("Fundación Apoyo <comunicaciones@fundacionapoyo.com>")
    try {
      mail.send(
        views.html.recovery(nombre, enlace).body,
        views.html.recovery(nombre, enlace).body
      )
    } catch {
      case e: Exception => {
        println("Error al enviar correo")
      }
    }
  }

  def sendCredentialInfo(usua_email: String, nombre: String, enlace: String) {
    val mail = new MailloyContext with Mailloy
    mail.setSubject("Contraseña de Ingreso a Mi Usuario Fundación Apoyo")
    mail.addRecipient(usua_email)
    mail.addFrom("Fundación Apoyo <comunicaciones@fundacionapoyo.com>")
    try {
      mail.send(
        views.html.register(nombre, enlace).body,
        views.html.register(nombre, enlace).body
      )
    } catch {
      case e: Exception => {
        println("Error al enviar correo")
      }
    }
  }

  def sendSolicitudInviableInfo(usua_email: String, nombre: String) {
    val mail = new MailloyContext with Mailloy
    mail.setSubject("Respuesta Solicitud de Crédito")
    mail.addRecipient(usua_email)
    mail.addFrom("Fundación Apoyo <comunicaciones@fundacionapoyo.com>")
    try {
      mail.send(
        views.html.presolicitudinviable(nombre).body,
        views.html.presolicitudinviable(nombre).body
      )
    } catch {
      case e: Exception => {
        println("Error al enviar correo")
      }
    }
  }

  def sendHtml(email: String, subject: String, body: String) {
    val mail = new MailloyContext with Mailloy
    mail.setSubject(subject)
    mail.addRecipient(email)
    mail.addFrom("Fundación Apoyo <comunicaciones@fundacionapoyo.com>")
    try {
      mail.send(
        body,
        body
      )
    } catch {
      case e: Exception => {
        println("Error al enviar correo")
      }
    }
  }

  def sendEmailLey2300(email: String, nombre: String) = {
    val mail = new MailloyContext with Mailloy
    mail.setSubject("FUNDACION APOYO - Conozca Información importante Ley 2300 de 2023")
    mail.addRecipient(email)
    mail.addFrom("Fundación Apoyo <notificaciones@fundacionapoyo.com>")
    try {
      mail.send(
        views.html.ley2300(nombre).body,
        views.html.ley2300(nombre).body
      )
    } catch {
      case e: Exception => {
        println("Error al enviar correo")
      }
      ""
    }
  }
}

object EmailSender extends EmailSender
