package notifiers

import notifiers._
import views._

class EmailSender {
  def sendPasswordRecovery(usua_email: String, nombre: String, enlace: String) {
    val mail = new MailloyContext with Mailloy
    mail.setSubject("Recuperación de Contraseña")
    mail.addRecipient(usua_email)
    mail.addFrom("Fundación Apoyo <comunicaciones@fundacionapoyo.com>")
    mail.send(
      views.html.recovery(nombre, enlace).body,
      views.html.recovery(nombre, enlace).body
    )
  }

  def sendCredentialInfo(usua_email: String, nombre: String, enlace: String) {
    val mail = new MailloyContext with Mailloy
    mail.setSubject("Contraseña de Ingreso a Mi Usuario Fundación Apoyo")
    mail.addRecipient(usua_email)
    mail.addFrom("Fundación Apoyo <comunicaciones@fundacionapoyo.com>")
    mail.send(
      views.html.register(nombre, enlace).body,
      views.html.register(nombre, enlace).body
    )
  }

  def sendSolicitudInviableInfo(usua_email: String, nombre: String) {
    val mail = new MailloyContext with Mailloy
    mail.setSubject("Respuesta Solicitud de Crédito")
    mail.addRecipient(usua_email)
    mail.addFrom("Fundación Apoyo <comunicaciones@fundacionapoyo.com>")
    mail.send(
      views.html.presolicitudinviable(nombre).body,
      views.html.presolicitudinviable(nombre).body
    )
  }
  def sendHtml(email: String, subject: String, body: String) {
    val mail = new MailloyContext with Mailloy
    mail.setSubject(subject)
    mail.addRecipient(email)
    mail.addFrom("Fundación Apoyo <comunicaciones@fundacionapoyo.com>")
    mail.send(
      body,
      body
    )
  }
}

object EmailSender extends EmailSender
