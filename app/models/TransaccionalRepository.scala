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

case class EnlaceTransaccional(
    enla_id: Option[Long],
    enla_uuid: Option[String],
    enla_activo: Option[Int],
    enla_fecha: Option[DateTime],
    usua_email: Option[String],
    enla_id_identificacion: Option[Int],
    enla_id_persona: Option[String]
)

case class CustomerData(
  legal_id: Option[String],
  full_name: Option[String],
  phone_number: Option[String],
  legal_id_type: Option[String]
)

case class WompiEvent(
  tran_reference: Option[String],
  tran_id: Option[String],
  tran_status: Option[String],
  tran_created_at: Option[String],
  tran_finalized_at: Option[String],
  tran_amount_in_cents: Option[Long],
  tran_customer_email: Option[String],
  tran_currency: Option[String],
  tran_payment_method_type: Option[String],
  // tran_payment_method: Option[String],
  tran_status_message: Option[String],
  tran_shipping_address: Option[String],
  tran_redirect_url: Option[String],
  tran_payment_source_id: Option[String],
  tran_payment_link_id: Option[String],
  tran_customer_data: Option[CustomerData],
  tran_billing_data: Option[String],
  tran_sent_at: Option[String],
  //tran_json: Option[String]
)

case class WompiEventRow(
  tran_id: Option[String],
  tran_event_received_at: Option[String],
  tran_event_json: Option[String]
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
          SQL("""SELECT FIRST 1 p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO AS NOMBRE FROM "col$colocacion" c
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
            val _uuid = randomUUID().toString
            _result = SQL(
              """INSERT INTO TRAN_ENLACE 
                    (TRAN_ENLACE_UUID, TRAN_ENLACE_ACTIVO, TRAN_ENLACE_FECHA, TRAN_ENLACE_EMAIL, TRAN_ENLACE_ID_IDENTIFICACION, TRAN_ENLACE_ID_PERSONA)
                   VALUES ({uuid}, {activo}, {fecha}, {email}, {id_identificacion}, {id_persona})"""
            ).on(
                'uuid -> _uuid,
                'activo -> 1,
                'fecha -> new Timestamp(Calendar.getInstance().getTimeInMillis),
                'email -> email,
                'id_identificacion -> id_identificacion,
                'id_persona -> id_persona
              )
              .executeInsert()
              .get > 0
            if (_result) {
              // Enviar correo de enlace
              val enlace = config.get[String]("link.protocol_transaccional") + "/l/" + _uuid
              EmailSender.sendCredentialInfo(email, _nombre, enlace)
            }
          } else {
            _result = false
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
/*     val _enlaceRS = {
      get[Option[Long]]("TRAN_ENLACE_ID") ~
        get[Option[String]]("TRAN_ENLACE_UUID") ~
        get[Option[Int]]("TRAN_ENLACE_ACTIVO") ~
        get[Option[DateTime]]("TRAN_ENLACE_FECHA") ~
        get[Option[String]]("TRAN_ENLACE_EMAIL") ~
        get[Option[Int]]("TRAN_ENLACE_ID_IDENTIFICACION") ~
        get[Option[String]]("TRAN_ENLACE_ID_PERSONA") map {
        case enla_id ~ enla_uuid ~ enla_activo ~ enla_fecha ~ enla_email ~ enla_id_identificacion ~ enla_id_persona =>
          EnlaceTransaccional(enla_id, enla_uuid, enla_activo, enla_fecha, enla_email, enla_id_identificacion, enla_id_persona)
      }
    } */
    println("Enlace: Ingreso a buscar si existe el enlace")
    db.withTransaction { implicit connection =>
      var _email = ""
      val _linkParse = int("TRAN_ENLACE_ACTIVO") ~ date("TRAN_ENLACE_FECHA") ~ str(
        "NOMBRE"
      ) ~ str("TRAN_ENLACE_EMAIL") ~ int("TRAN_ENLACE_ID_IDENTIFICACION") ~ str("TRAN_ENLACE_ID_PERSONA") map {
        case activo ~ fecha ~ nombre ~ email ~ id_identificacion ~ id_persona => (activo, fecha, nombre, email, id_identificacion, id_persona)
      }
      val link = SQL(
        """SELECT te.TRAN_ENLACE_ACTIVO, te.TRAN_ENLACE_FECHA, gp.NOMBRE || ' ' || gp.PRIMER_APELLIDO || ' ' || gp.SEGUNDO_APELLIDO AS NOMBRE,
          te.TRAN_ENLACE_EMAIL, te.TRAN_ENLACE_ID_IDENTIFICACION, te.TRAN_ENLACE_ID_PERSONA FROM TRAN_ENLACE te
          INNER JOIN "gen$persona" gp ON gp.ID_IDENTIFICACION = te.TRAN_ENLACE_ID_IDENTIFICACION AND gp.ID_PERSONA = te.TRAN_ENLACE_ID_PERSONA
          WHERE te.TRAN_ENLACE_UUID = {uuid} AND te.TRAN_ENLACE_ACTIVO = {activo}"""
      ).on(
          'uuid -> enla_uuid,
          'activo -> 1
        )
        .as(_linkParse.singleOpt)
      println("Enlace: Link localizado: " + link)
      link match {
        case None => {
          (false, "")
        }
        case Some(link) => {
          val duration = fecha.toDate().getTime() - link._2.getTime()
          _email = link._4
          if (duration < 86400000) {
            SQL(
              "UPDATE TRAN_ENLACE SET TRAN_ENLACE_ACTIVO = {enla_activo} WHERE TRAN_ENLACE_UUID = {enla_uuid}"
            ).on(
                'enla_activo -> 0,
                'enla_uuid -> enla_uuid
              )
              .executeUpdate()
            val _esInsertado = SQL("""INSERT INTO TRAN_USUARIO (TRAN_USUARIO_ID_IDENTIFICACION, TRAN_USUARIO_ID_PERSONA, TRAN_USUARIO_EMAIL, TRAN_USUARIO_ULTIMO_INGRESO) VALUES ({id_identificacion}, {id_persona}, {email}, {ahora})""").on(
              'id_identificacion -> link._5,
              'id_persona -> link._6,
              'email -> link._4,
              'ahora -> new Timestamp(Calendar.getInstance().getTimeInMillis)
            ).executeInsert().get > 0
            if (_esInsertado) {
              (true, _email)
            } else {
              (false, "")
            }
          } else {
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

  def cambiarClave(email: String, clave: String, link: String): Boolean = {
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
        "UPDATE TRAN_USUARIO SET TRAN_USUARIO_CLAVE  = {contrasena} WHERE TRAN_USUARIO_ID_IDENTIFICACION = {id_identificacion} AND TRAN_USUARIO_ID_PERSONA = {id_persona}"
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

/*         SQL(
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
          .executeInsert() */
      }
      _result
    }
  }

  def registrarEventoWompi(evento: WompiEvent): Future[Boolean] = Future[Boolean] {
    db.withConnection { implicit connection =>
      val _actualizado: Boolean = SQL(
        """UPDATE TRAN_TRANSACCION SET
          TRAN_TRAN_STATUS = {tran_status},
          TRAN_TRAN_CREATED_AT = {tran_created_at},
          TRAN_TRAN_FINALIZED_AT = {tran_finalized_at},
          TRAN_TRAN_AMOUNT_IN_CENTS = {tran_amount_in_cents},
          TRAN_TRAN_CUSTOMER_EMAIL = {tran_customer_email},
          TRAN_TRAN_CURRENCY = {tran_currency},
          TRAN_TRAN_PAYMENT_METHOD_TYPE = {tran_payment_method_type},
          TRAN_TRAN_PAYMENT_METHOD = {tran_payment_method},
          TRAN_TRAN_STATUS_MESSAGE = {tran_status_message},
          TRAN_TRAN_SHIPPING_ADDRESS = {tran_shipping_address},
          TRAN_TRAN_REDIRECT_URL = {tran_redirect_url},
          TRAN_TRAN_PAYMENT_SOURCE_ID = {tran_payment_source_id},
          TRAN_TRAN_PAYMENT_LINK_ID = {tran_payment_link_id},
          TRAN_TRAN_CUSTOMER_DATA = {tran_customer_data},
          TRAN_TRAN_BILLING_DATA = {tran_billing_data},
          TRAN_TRAN_SENT_AT = {tran_sent_at},
          TRAN_TRAN_JSON = {tran_json}
          WHERE TRAN_TRAN_ID = {tran_id}"""
      ).on(
          'tran_id -> evento.tran_id,
          'tran_status -> evento.tran_status,
          'tran_created_at -> evento.tran_created_at,
          'tran_finalized_at -> evento.tran_finalized_at,
          'tran_amount_in_cents -> evento.tran_amount_in_cents,
          'tran_customer_email -> evento.tran_customer_email,
          'tran_currency -> evento.tran_currency,
          'tran_payment_method_type -> evento.tran_payment_method_type,
          'tran_payment_method -> None, //evento.tran_payment_method,
          'tran_status_message -> evento.tran_status_message,
          'tran_shipping_address -> evento.tran_shipping_address,
          'tran_redirect_url -> evento.tran_redirect_url,
          'tran_payment_source_id -> evento.tran_payment_source_id,
          'tran_payment_link_id -> evento.tran_payment_link_id,
          'tran_customer_data -> None, //evento.tran_customer_data,
          'tran_billing_data -> evento.tran_billing_data,
          'tran_sent_at -> evento.tran_sent_at,
          'tran_json -> None //evento.tran_json
        )
        .executeUpdate() > 0

      if (!_actualizado) {
        val _insertado = SQL("""INSERT INTO TRAN_TRANSACCION (
          TRAN_TRAN_ID,
          TRAN_TRAN_STATUS,
          TRAN_TRAN_CREATED_AT,
          TRAN_TRAN_FINALIZED_AT,
          TRAN_TRAN_AMOUNT_IN_CENTS,
          TRAN_TRAN_CUSTOMER_EMAIL,
          TRAN_TRAN_CURRENCY,
          TRAN_TRAN_PAYMENT_METHOD_TYPE,
          TRAN_TRAN_PAYMENT_METHOD,
          TRAN_TRAN_STATUS_MESSAGE,
          TRAN_TRAN_SHIPPING_ADDRESS,
          TRAN_TRAN_REDIRECT_URL,
          TRAN_TRAN_PAYMENT_SOURCE_ID,
          TRAN_TRAN_PAYMENT_LINK_ID,
          TRAN_TRAN_CUSTOMER_DATA,
          TRAN_TRAN_BILLING_DATA,
          TRAN_TRAN_SENT_AT,
          TRAN_TRAN_JSON
        ) VALUES (
          {tran_id},
          {tran_status},
          {tran_created_at},
          {tran_finalized_at},
          {tran_amount_in_cents},
          {tran_customer_email},
          {tran_currency},
          {tran_payment_method_type},
          {tran_payment_method},
          {tran_status_message},
          {tran_shipping_address},
          {tran_redirect_url},
          {tran_payment_source_id},
          {tran_payment_link_id},
          {tran_customer_data},
          {tran_billing_data},
          {tran_sent_at},
          {tran_json}
        )""").on(
            'tran_id -> evento.tran_id,
            'tran_status -> evento.tran_status,
            'tran_created_at -> evento.tran_created_at,
            'tran_finalized_at -> evento.tran_finalized_at,
            'tran_amount_in_cents -> evento.tran_amount_in_cents,
            'tran_customer_email -> evento.tran_customer_email,
            'tran_currency -> evento.tran_currency,
            'tran_payment_method_type -> evento.tran_payment_method_type,
            'tran_payment_method -> None, //evento.tran_payment_method
            'tran_status_message -> evento.tran_status_message,
            'tran_shipping_address -> evento.tran_shipping_address,
            'tran_redirect_url -> evento.tran_redirect_url,
            'tran_payment_source_id -> evento.tran_payment_source_id,
            'tran_payment_link_id -> evento.tran_payment_link_id,
            'tran_customer_data -> None, //evento.tran_customer_data,
            'tran_billing_data -> evento.tran_billing_data,
            'tran_sent_at -> evento.tran_sent_at,
            'tran_json -> None // evento.tran_json
          )
          .executeInsert().get > 0
      }
      return Future.successful(true)
    }
  }

  def obtenerRegistroWompi(referencia: String): Future[Transaccion] = Future {
    db.withTransaction {implicit connection =>
        SQL("""SELECT * FROM TRAN_TRANSACCIONAL WHERE TRAN_TRAN_REFERENCE = {referencia}""")
        .on(
          'referencia -> referencia
        ).as(Transaccion._set.singleOpt).getOrElse(Transaccion(Some(referencia), None, None, None, None, None, None, None, None, None, None, None, None, None, None, None, None, None, None))
    }
  }
}
