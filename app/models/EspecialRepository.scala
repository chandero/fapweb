package models

import javax.inject.Inject

import play.api.db.DBApi

import anorm._
import anorm.SqlParser.{get, str}
import anorm.JodaParameterMetaData._

import org.joda.time.DateTime

import notifiers.EmailSender

import java.util.regex.Pattern
import java.sql.Connection

class EspecialRepository @Inject()(
    dbapi: DBApi,
) (
    implicit ec: DatabaseExecutionContext
) {
    private val db = dbapi.database("default")


    def enviarCorreosLey2300() {
        val _parser = get[Option[Int]]("ID_IDENTIFICACION") ~ get[Option[String]]("ID_PERSONA") ~ get[Option[String]]("NOMBRE") ~ get[Option[String]]("PRIMER_APELLIDO") ~ get[Option[String]]("SEGUNDO_APELLIDO") ~ get[Option[String]]("EMAIL") map {
            case a ~ b ~ c ~ d ~ e ~ f => (a, b, c, d, e, f)
        }
        println("Enviando correos Ley 2300")
        val _fecha_inicial = new DateTime().withTime(0,0,0,0).toString("yyyy-MM-dd HH:mm:ss")
        val _fecha_final = new DateTime().withTime(23,59,59,999).toString("yyyy-MM-dd HH:mm:ss")
        println("Fecha inicial: " + _fecha_inicial)
        println("Fecha final: " + _fecha_final)
        db.withConnection { implicit connection =>
            val _conteo_dia = SQL("""SELECT COUNT(*) AS CONTEO FROM LEY2300CONTROL WHERE FECHA_ENVIO BETWEEN {FECHA_INICIAL} AND {FECHA_FINAL}""").on(
                'FECHA_INICIAL -> _fecha_inicial,
                'FECHA_FINAL -> _fecha_final
            ).as(SqlParser.scalar[Int].single)
            println("Conteo del día: " + _conteo_dia)
            if (_conteo_dia >= 500) {
                println("Ya se enviaron los correos de hoy")
                return
            }
            println("No se han enviado todos los correos hoy")
            val _personas = SQL("""SELECT FIRST 100 DISTINCT o.ID_IDENTIFICACION, o.ID_PERSONA, o.NOMBRE, o.PRIMER_APELLIDO, o.SEGUNDO_APELLIDO, o.EMAIL FROM (
  SELECT gp.ID_IDENTIFICACION, gp.ID_PERSONA, gp.NOMBRE, gp.PRIMER_APELLIDO, gp.SEGUNDO_APELLIDO, LOWER(gp.EMAIL) AS EMAIL FROM "col$colocacion" cc 
  INNER JOIN "gen$persona" gp ON gp.ID_IDENTIFICACION = cc.ID_IDENTIFICACION AND gp.ID_PERSONA = cc.ID_PERSONA
  WHERE cc.ID_ESTADO_COLOCACION IN (0,1,2) and cc.ID_PERSONA NOT IN (SELECT ID_PERSONA FROM LEY2300CONTROL)
  UNION ALL
  SELECT gp.ID_IDENTIFICACION, gp.ID_PERSONA, gp.NOMBRE, gp.PRIMER_APELLIDO, gp.SEGUNDO_APELLIDO, LOWER(gp.EMAIL) AS EMAIL FROM "col$colgarantias" cc2
  INNER JOIN "col$colocacion" cc1 ON cc1.ID_COLOCACION = cc2.ID_COLOCACION
  INNER JOIN "gen$persona" gp ON gp.ID_IDENTIFICACION = cc1.ID_IDENTIFICACION AND gp.ID_PERSONA = cc1.ID_PERSONA
  WHERE cc1.ID_ESTADO_COLOCACION IN (0,1,2) and cc1.ID_PERSONA NOT IN (SELECT ID_PERSONA FROM LEY2300CONTROL)
) AS o""").as(_parser *)
            val _queryInsertControl = """INSERT INTO LEY2300CONTROL (ID_PERSONA, ID_IDENTIFICACION, ENVIADO, EMAIL, FECHA_ENVIO, EMAIL_ID) VALUES ({ID_PERSONA}, {ID_IDENTIFICACION}, {ENVIADO}, {EMAIL}, {FECHA_ENVIO}, {EMAIL_ID})"""
                _personas.foreach { persona =>
                    var enviado = false
                    var id = ""
                    val correo = persona._6.getOrElse("")
                    println("Persona: " + persona._2 + ", Correo: " + correo)
                    if (validaCorreo(correo)) {
                    if (!correo.contains("@fundacionapoyo.com")) {
                        id = enviarCorreoLey2300(persona)
                        if (id > "") {
                                SQL(_queryInsertControl).on(
                                    "ID_PERSONA" -> persona._2,
                                    "ID_IDENTIFICACION" -> persona._1,
                                    "ENVIADO" -> 1,
                                    "EMAIL" -> correo,
                                    "FECHA_ENVIO" -> new DateTime(),
                                    "EMAIL_ID" -> id
                                ).executeUpdate()
                            println("Correo enviado a " + correo + ", con id " + id)
                        } else {
                                SQL(_queryInsertControl).on(
                                    'ID_PERSONA -> persona._2,
                                    'ID_IDENTIFICACION -> persona._1,
                                    'ENVIADO -> 0,
                                    'EMAIL -> correo,
                                    'FECHA_ENVIO -> new DateTime(),
                                    'EMAIL_ID -> id
                                ).executeUpdate()                        
                            println("Error al enviar correo a " + correo)
                        }
                    } else {
                                SQL(_queryInsertControl).on(
                                    'ID_PERSONA -> persona._2,
                                    'ID_IDENTIFICACION -> persona._1,
                                    'ENVIADO -> 0,
                                    'EMAIL -> correo,
                                    'FECHA_ENVIO -> new DateTime(),
                                    'EMAIL_ID -> id
                                ).executeUpdate()                        
                        println("Correo no válido: " + correo)
                    }
                } else {
                        SQL(_queryInsertControl).on(
                            'ID_PERSONA -> persona._2,
                            'ID_IDENTIFICACION -> persona._1,
                            'ENVIADO -> 0,
                            'EMAIL -> correo,
                            'FECHA_ENVIO -> new DateTime(),
                            'EMAIL_ID -> id
                        ).executeUpdate()
                        println("Correo no válido: " + correo)
                }
            }
        }
    }

    def enviarCorreoLey2300(persona: (Option[Int], Option[String], Option[String], Option[String], Option[String], Option[String])): String = {
        val _correo_destino = persona._6.getOrElse("")
        val _primer_nombre_destino = persona._3.getOrElse("")
        val _primer_apellido_destino = persona._4.getOrElse("")
        val _segundo_apellido_destino = persona._5.getOrElse("")
        val _nombre_completo = _primer_nombre_destino + " " + _primer_apellido_destino + " " + _segundo_apellido_destino
        val id = EmailSender.sendEmailLey2300(_correo_destino, _nombre_completo)
        id
    }

    def validaCorreo(correo: String): Boolean = {
        val _correo = correo.trim
        if (_correo.length > 0) {
            // email validator regex
            val _email_regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            val _pattern = Pattern.compile(_email_regex)
            val _matcher = _pattern.matcher(_correo)
            return _matcher.matches()
        }
        return false
    }
}