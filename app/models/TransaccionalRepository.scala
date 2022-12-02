package models

import com.github.t3hnar.bcrypt._

import javax.inject.Inject
import java.util.Calendar
import java.util.UUID.randomUUID
import java.sql.Timestamp

import play.api.db.DBApi
import play.api.Configuration
import scala.concurrent.Future

import anorm._
import anorm.SqlParser.{get, str, int, date}
import anorm.JodaParameterMetaData._

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

import scala.util.{Try, Success, Failure}

import notifiers._

case class Cliente(
    cliente_id: Option[Long],
    cliente_email: Option[String],
    cliente_nombre: Option[String],
    cliente_primer_apellido: Option[String]
)

class TransaccionalRepository @Inject()(dbapi: DBApi, config: Configuration)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  def autenticar(
      id_identificacion: Int,
      id_persona: String,
      contrasena: String
  ): Future[Boolean] =
    Future {
      val _hash = db.withConnection { implicit connection =>
        SQL(
          "SELECT TRAN_USUARIO_CLAVE FROM TRAN_USUARIO WHERE TRAN_USUARIO_ID_IDENTIFICACION = {id_identificacion} AND TRAN_USUARIO_ID_PERSONA = {id_persona}"
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(SqlParser.str("TRAN_USUARIO_CLAVE").singleOpt)
      }
      _hash.isDefined && (contrasena.isBcryptedSafeBounded(_hash.get) match {
        case Success(s) => true
        case Failure(f) => false
      })
    }(ec)

  def registrar(id_identificacion: Int, id_persona: String, email: String) =
    Future {
      var _result = false
      db.withTransaction { implicit connection =>
        val _nombre =
          SQL("""SELECT p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO AS NOMBRE FROM "col$colocacion" c 
                    INNER JOIN "gen$persona" p ON p.ID_IDENTIFICACION = c.ID_IDENTIFICACION AND p.ID_PERSONA = c.ID_PERSONA
                    WHERE c.ID_IDENTIFICACION = {id_identificacion} AND c.ID_PERSONA = {id_persona} AND c.ID_ESTADO_COLOCACION IN (0,1,2)
                    AND p.EMAIL = {email}""")
            .on(
              'id_identificacion -> id_identificacion,
              'id_persona -> id_persona,
              'email -> email
            )
            .as(SqlParser.str("NOMBRE").single)
        if (!_nombre.isEmpty()) {
          val _esInserted = SQL(
            "INSERT INTO TRAN_USUARIO (TRAN_USUARIO_ID_IDENTIFICACION, TRAN_USUARIO_ID_PERSONA, TRAN_USUARIO_CLAVE, TRAN_USUARIO_EMAIL) VALUES ({id_identificacion}, {id_persona}, {clave}, {email})"
          ).on(
              'id_identificacion -> id_identificacion,
              'id_persona -> id_persona,
              'clave -> randomUUID().toString.boundedBcrypt(12),
              'email -> email
            )
            .executeInsert()
            .get > 0
          if (_esInserted) {
            val _uuid = randomUUID().toString
            _result = SQL(
              """INSERT INTO TRAN_ENLACE 
                    (TRAN_ENLACE_UUID, TRAN_ENLACE_ACTIVO, TRAN_ENLACE_FECHA, TRAN_ENLACE_EMAIL) 
                   VALUES ({uuid}, {activo}, {fecha}, {email})"""
            ).on(
                'uuid -> _uuid,
                'activo -> true,
                'fecha -> new Timestamp(Calendar.getInstance().getTimeInMillis),
                'email -> email
              )
              .executeInsert()
              .get > 0
            if (_result) {
              // Enviar correo de enlace
              val enlace = config.get[String]("link.protocol_transaccional") + "/link/" + _uuid
              EmailSender.sendCredentialInfo(email, _nombre, enlace)
            }
          }
        }
      }
      _result
    }(ec)

  def ultimoIngreso(
      id_identificacion: Int,
      id_persona: String
  ): Future[Boolean] =
    Future {
      db.withConnection(implicit connection => {
        SQL(
          """UPDATE TRAN_USUARIO SET TRAN_USUARIO_ULTIMO_INGRESO = {ahora} WHERE TRAN_USUARIO_ID_IDENTIFICACION = {id_identificacion} AND TRAN_USUARIO_ID_PERSONA = {id_persona}"""
        ).on(
            'ahora -> new Timestamp(Calendar.getInstance().getTimeInMillis),
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .executeUpdate() > 0
      })
    }(ec)

  def buscarPorDocumento(
      id_identificacion: Int,
      id_persona: String
  ): Future[Option[(String, String, String, String)]] =
    Future {
      val _parser = str("NOMBRE") ~ str("PRIMER_APELLIDO") ~ str(
        "SEGUNDO_APELLIDO"
      ) ~ str("EMAIL") map {
        case n ~ p ~ s ~ e => (n, p, s, e)
      }
      db.withConnection { implicit connection =>
        SQL(
          """SELECT NOMBRE, PRIMER_APELLIDO, SEGUNDO_APELLIDO, EMAIL FROM "gen$persona" p1 WHERE p1.ID_IDENTIFICACION = {id_identificacion} AND p1.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(_parser.singleOpt)
      }
    }(ec)

  def validarEnlace(enla_uuid: String): Future[(Boolean, String)] = Future {
    val fecha: LocalDateTime =
      new LocalDateTime(Calendar.getInstance().getTimeInMillis())
    val _enlaceRS = {
      get[Option[Long]]("TRAN_ENLACE_ID") ~
        get[Option[String]]("TRAN_ENLACE_UUID") ~
        get[Option[Int]]("TRAN_ENLACE_ACTIVO") ~
        get[Option[DateTime]]("TRAN_ENLACE_FECHA") ~
        get[Option[String]]("TRAN_ENLACE_EMAIL") map {
        case enla_id ~ enla_uuid ~ enla_activo ~ enla_fecha ~ enla_email =>
          Enlace(enla_id, enla_uuid, enla_activo, enla_fecha, enla_email)
      }
    }
    db.withConnection { implicit connection =>
      var _nombre = ""
      val _linkParse = int("TRAN_ENLACE_ACTIVO") ~ date("TRAN_ENLACE_FECHA") ~ str(
        "NOMBRE"
      ) map {
        case activo ~ fecha ~ nombre => (activo, fecha, nombre)
      }
      val link = SQL(
        """SELECT te.TRAN_ENLACE_ACTIVO, te.TRAN_ENLACE_FECHA, gp.NOMBRE || ' ' || gp.PRIMER_APELLIDO || ' ' || gp.SEGUNDO_APELLIDO AS NOMBRE FROM TRAN_ENLACE te
          INNER JOIN TRAN_USUARIO tu ON tu.TRAN_USUARIO_EMAIL = te.TRAN_ENLACE_EMAIL
          INNER JOIN "gen$persona" gp ON gp.ID_IDENTIFICACION = tu.TRAN_USUARIO_ID_IDENTIFICACION AND gp.ID_PERSONA = tu.TRAN_USUARIO_ID_PERSONA
          WHERE te.TRAN_ENLACE_UUID = {uuid} AND te.TRAN_ENLACE_ACTIVO = {activo}"""
      ).on(
          'uuid -> enla_uuid,
          'activo -> 1
        )
        .as(_linkParse.singleOpt)
      link match {
        case None => {
          (false, "")
        }
        case Some(link) => {
          val duration = fecha.toDate().getTime() - link._2.getTime()
          _nombre = link._3
          if (duration < 86400000) {
            (true, _nombre)
          } else {
            SQL(
              "UPDATE TRAN_ENLACE SET TRAN_ENLACE_ACTIVO = {enla_activo} WHERE TRAN_ENLACE_UUID = {enla_uuid}"
            ).on(
                'enla_activo -> 0,
                'enla_uuid -> enla_uuid
              )
              .executeUpdate()
            (false, "")
          }
        }
      }
    }
  }

  def recuperar(hosturl: String, email: String): Future[Boolean] = Future {
    val fecha: LocalDateTime =
      new LocalDateTime(Calendar.getInstance().getTimeInMillis())
    val hora: LocalDateTime =
      new LocalDateTime(Calendar.getInstance().getTimeInMillis())

    db.withConnection { implicit connection =>
      val usuario = SQL(
        "SELECT * FROM \"gen$empleado\" WHERE email = {email} and tipo <> 99"
      ).on(
          'email -> email
        )
        .as(Usuario.usuarioRS.singleOpt)
        .get

      if (usuario != null) {
        // enviar correo con el enlace
        var uuid: String = randomUUID().toString()
        val enlace = hosturl + "/#/tr/" + uuid
        val sender = new EmailSender()
        sender.sendPasswordRecovery(
          usuario.email.get,
          usuario.nombre.get + " " + usuario.primer_apellido.get,
          enlace
        )
        SQL(
          "INSERT INTO ENLACE (ENLA_UUID, ENLA_ACTIVO, ENLA_FECHA, ENLA_EMAIL) VALUES ({enla_uuid}, {enla_activo}, {enla_fecha}, {enla_email})"
        ).on(
            'enla_uuid -> uuid,
            'enla_activo -> 1,
            'enla_fecha -> fecha,
            'enla_email -> usuario.email
          )
          .executeInsert(SqlParser.scalar[String].singleOpt)
        true
      } else {
        false
      }
    }

  }

  /*
   * Cambiar clave
   * @param link:String
   * @param clave: String
   */

  def cambiarClave(link: String, clave: String): Boolean = {
    val fecha: LocalDate = new LocalDate(
      Calendar.getInstance().getTimeInMillis()
    )
    val hora: LocalDateTime = new LocalDateTime(
      Calendar.getInstance().getTimeInMillis()
    )
    val contrasena = clave.bcryptSafeBounded match {
      case Success(s) => s
      case Failure(f) => ""
    }
    db.withConnection { implicit connection =>
      val email =
        SQL(
          "SELECT TRAN_ENLACE_EMAIL FROM TRAN_ENLACE WHERE TRAN_ENLACE_UUID = {enla_uuid}"
        ).on(
            'enla_uuid -> link
          )
          .as(SqlParser.scalar[String].single)

      val _parser = int("ID_IDENTIFICACION") ~ str("ID_PERSONA") map {
        case id_identificacion ~ id_persona => (id_identificacion, id_persona)
      }

      val usuario = SQL(
        """SELECT ID_IDENTIFICACION, ID_PERSONA FROM "gen$persona" WHERE EMAIL = {email}"""
      ).on(
          'email -> email
        )
        .as(_parser.single)

      val _result: Boolean = SQL(
        "UPDATE TRAN_USUARIO SET TRAN_USUA_CLAVE  = {contrasena} WHERE TRAN_USUARIO_ID_IDENTIFICACION = {id_identificacion} AND TRAN_USUARIO_ID_PERSONA = {id_persona}"
      ).on(
          'id_identificacion -> usuario._1,
          'id_persona -> usuario._2,
          'contrasena -> contrasena
        )
        .executeUpdate() > 0
      if (_result) {
        SQL(
          "UPDATE TRAN_ENLACE SET TRAN_ENLACE_ACTIVO = {enla_activo} WHERE TRAN_ENLACE_UUID = {enla_uuid}"
        ).on(
            'enla_activo -> 0,
            'enla_uuid -> link
          )
          .executeUpdate()

        SQL(
          "INSERT INTO auditoria(audi_fecha, audi_hora, usua_id, audi_tabla, audi_uid, audi_campo, audi_valorantiguo, audi_valornuevo, audi_evento) VALUES ({audi_fecha}, {audi_hora}, {usua_id}, {audi_tabla}, {audi_uid}, {audi_campo}, {audi_valorantiguo}, {audi_valornuevo}, {audi_evento})"
        ).on(
            'audi_fecha -> fecha,
            'audi_hora -> hora,
            'usua_id -> usuario._2,
            'audi_tabla -> "TRAN_USUARIO",
            'audi_uid -> usuario._2,
            'audi_campo -> "contrasena",
            'audi_valorantiguo -> '*',
            'audi_valornuevo -> '*',
            'audi_evento -> "A"
          )
          .executeInsert()
      }
      _result
    }
  }

}
