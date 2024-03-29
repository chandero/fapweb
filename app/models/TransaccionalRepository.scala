package models

// import com.github.t3hnar.bcrypt._

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
import org.mindrot.jbcrypt.BCrypt

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

class TransaccionalRepository @Inject()(dbapi: DBApi, config: Configuration, cService:CreditoRepository)(
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
/*       _hash.isDefined && (contrasena.isBcryptedSafeBounded(_hash.get) match {
        case Success(s) => println("Contraseña es valida:" + contrasena)
                           true
        case Failure(f) => println("Contraseña Error:" + contrasena)
          false
      }) */
      if (_hash.isDefined && BCrypt.checkpw(contrasena, _hash.get)) {
        println("Contraseña es valida:" + contrasena)
        true
      } else {
        println("Contraseña Error:" + contrasena)
        false
      }
    }(ec)

  def registrar(id_identificacion: Int, id_persona: String, email: String) =
    Future {
      var _result = false
      var _msg = ""
      db.withTransaction { implicit connection =>
        // Validar si ya registrado
        val _esRegistrado = SQL("""SELECT COUNT(*) FROM TRAN_USUARIO WHERE TRAN_USUARIO_ID_IDENTIFICACION = {id_identificacion} AND TRAN_USUARIO_ID_PERSONA = {id_persona} AND LOWER(TRAN_USUARIO_EMAIL) = LOWER({email})""").on(
          'id_identificacion -> id_identificacion,
          'id_persona -> id_persona,
          'email -> email
        ).as(SqlParser.scalar[Long].single) > 0
        if (!_esRegistrado) {
          val _nombre =
            SQL("""SELECT FIRST 1 p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO AS NOMBRE FROM "col$colocacion" c
                    INNER JOIN "gen$persona" p ON p.ID_IDENTIFICACION = c.ID_IDENTIFICACION AND p.ID_PERSONA = c.ID_PERSONA
                    WHERE c.ID_IDENTIFICACION = {id_identificacion} AND c.ID_PERSONA = {id_persona} AND c.ID_ESTADO_COLOCACION IN (0,1,2,6,7)
                    AND LOWER(p.EMAIL) = LOWER({email})""")
            .on(
              'id_identificacion -> id_identificacion,
              'id_persona -> id_persona,
              'email -> email
            )
            .as(SqlParser.str("NOMBRE").singleOpt)
          _nombre match {
            case Some(_nombre) => val _uuid = randomUUID().toString
              _result = SQL(
                """INSERT INTO TRAN_ENLACE 
                      (TRAN_ENLACE_UUID, TRAN_ENLACE_ACTIVO, TRAN_ENLACE_FECHA, TRAN_ENLACE_EMAIL, TRAN_ENLACE_ID_IDENTIFICACION, TRAN_ENLACE_ID_PERSONA)
                     VALUES ({uuid}, {activo}, {fecha}, LOWER({email}), {id_identificacion}, {id_persona})"""
              ).on(
                  'uuid -> _uuid,
                  'activo -> 1,
                  'fecha -> new Timestamp(Calendar.getInstance().getTimeInMillis),
                  'email -> email,
                  'id_identificacion -> id_identificacion,
                  'id_persona -> id_persona
                )
                .executeUpdate() > 0
              if (_result) {
                // Enviar correo de enlace
                val enlace = config.get[String]("link.protocol_transaccional") + "/l/" + _uuid
                EmailSender.sendCredentialInfo(email, _nombre, enlace)
              }
            case None =>
              val _nombre = SQL("""SELECT FIRST 1 p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO AS NOMBRE FROM "col$colocacion" c
	  				          INNER JOIN "col$colgarantias" cc ON cc.ID_COLOCACION = c.ID_COLOCACION
		  			          INNER JOIN "gen$persona" p ON p.ID_IDENTIFICACION = cc.ID_IDENTIFICACION AND p.ID_PERSONA = cc.ID_PERSONA
                      WHERE cc.ID_IDENTIFICACION = {id_identificacion} AND cc.ID_PERSONA = {id_persona} AND c.ID_ESTADO_COLOCACION IN (0,1,2,6,7)
                      AND LOWER(p.EMAIL) = LOWER({email})""")
              .on(
                'id_identificacion -> id_identificacion,
                'id_persona -> id_persona,
                'email -> email
              )
              .as(SqlParser.str("NOMBRE").singleOpt)
              _nombre match {
              case Some(_nombre) => val _uuid = randomUUID().toString
                _result = SQL(
                  """INSERT INTO TRAN_ENLACE 
                        (TRAN_ENLACE_UUID, TRAN_ENLACE_ACTIVO, TRAN_ENLACE_FECHA, TRAN_ENLACE_EMAIL, TRAN_ENLACE_ID_IDENTIFICACION, TRAN_ENLACE_ID_PERSONA)
                       VALUES ({uuid}, {activo}, {fecha}, LOWER({email}), {id_identificacion}, {id_persona})"""
                ).on(
                    'uuid -> _uuid,
                    'activo -> 1,
                    'fecha -> new Timestamp(Calendar.getInstance().getTimeInMillis),
                    'email -> email,
                    'id_identificacion -> id_identificacion,
                    'id_persona -> id_persona
                  )
                  .executeUpdate() > 0
                if (_result) {
                  // Enviar correo de enlace
                  _msg = "Usuario Registrado con Exito!"
                  val enlace = config.get[String]("link.protocol_transaccional") + "/l/" + _uuid
                  EmailSender.sendCredentialInfo(email, _nombre, enlace)
                }
              case None =>
                _msg = "Usuario no se encuentra en nuestra base de datos, por favor rectifique!"
                _result = false
              }
            }
          } else {
            _msg = "Usuario ya se encuentra Registrado!"
            _result = false
          }
      }
      (_result, _msg)
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
            var _esActualizado = false
            var _esInsertado = false
            _esActualizado = SQL("""UPDATE TRAN_USUARIO SET TRAN_USUARIO_ULTIMO_INGRESO = {ahora} WHERE TRAN_USUARIO_ID_IDENTIFICACION = {id_identificacion} AND TRAN_USUARIO_ID_PERSONA = {id_persona} AND TRAN_USUARIO_EMAIL = {email}""").on(
              'ahora -> new Timestamp(Calendar.getInstance().getTimeInMillis),
              'id_identificacion -> link._5,
              'id_persona -> link._6,
              'email -> link._4
            ).executeUpdate() > 0
            if (!_esActualizado) {
              _esInsertado = SQL("""INSERT INTO TRAN_USUARIO (TRAN_USUARIO_ID_IDENTIFICACION, TRAN_USUARIO_ID_PERSONA, TRAN_USUARIO_EMAIL, TRAN_USUARIO_ULTIMO_INGRESO) VALUES ({id_identificacion}, {id_persona}, LOWER({email}), {ahora})""").on(
                'id_identificacion -> link._5,
                'id_persona -> link._6,
                'email -> link._4,
                'ahora -> new Timestamp(Calendar.getInstance().getTimeInMillis)
              ).executeInsert().get > 0
            }
            if (_esActualizado || _esInsertado) {
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

  def recuperar(id_identificacion: Int, id_persona: String, email: String): Future[Boolean] = Future {
    val fecha: LocalDateTime =
      new LocalDateTime(Calendar.getInstance().getTimeInMillis())
    val hora: LocalDateTime =
      new LocalDateTime(Calendar.getInstance().getTimeInMillis())
    db.withConnection { implicit connection =>
      val _nombre = SQL(
        """SELECT (gp.NOMBRE || ' ' || gp.PRIMER_APELLIDO || ' ' || gp.SEGUNDO_APELLIDO) AS NOMBRE FROM TRAN_USUARIO tu
           INNER JOIN "gen$persona" gp ON gp.ID_IDENTIFICACION = tu.TRAN_USUARIO_ID_IDENTIFICACION AND gp.ID_PERSONA = tu.TRAN_USUARIO_ID_PERSONA AND LOWER(gp.EMAIL) = LOWER(tu.TRAN_USUARIO_EMAIL)
          WHERE TRAN_USUARIO_ID_IDENTIFICACION = {id_identificacion} AND TRAN_USUARIO_ID_PERSONA = {id_persona} AND LOWER(TRAN_USUARIO_EMAIL) = LOWER({email})"""
      ).on(
          'email -> email,
          'id_identificacion -> id_identificacion,
          'id_persona -> id_persona
        )
        .as(SqlParser.scalar[String].singleOpt)

      _nombre match {
        case Some(nombre) =>
          val _uuid = randomUUID().toString
          val _result = SQL(
          """INSERT INTO TRAN_ENLACE 
              (TRAN_ENLACE_UUID, TRAN_ENLACE_ACTIVO, TRAN_ENLACE_FECHA, TRAN_ENLACE_EMAIL, TRAN_ENLACE_ID_IDENTIFICACION, TRAN_ENLACE_ID_PERSONA)
                VALUES ({uuid}, {activo}, {fecha}, LOWER({email}), {id_identificacion}, {id_persona})"""
          ).on(
            'uuid -> _uuid,
            'activo -> 1,
            'fecha -> new Timestamp(Calendar.getInstance().getTimeInMillis),
            'email -> email,
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .executeUpdate() > 0
          if (_result) {
                  // Enviar correo de enlace
            val enlace = config.get[String]("link.protocol_transaccional") + "/l/" + _uuid
            EmailSender.sendPasswordRecovery(email, nombre, enlace)
          }
          true

        case None =>
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
    val contrasena = BCrypt.hashpw(clave, BCrypt.gensalt());
    /* val contrasena = clave.bcryptSafeBounded match {
      case Success(s) => s
      case Failure(f) => ""
    } */
    db.withConnection { implicit connection =>
      val _parser = str("TRAN_ENLACE_EMAIL") ~ int("TRAN_ENLACE_ID_IDENTIFICACION") ~ str("TRAN_ENLACE_ID_PERSONA") map {
            case email ~ id_identificacion ~ id_persona => (email, id_identificacion, id_persona)
          }
      val _dataEnlace =
        SQL(
          "SELECT TRAN_ENLACE_EMAIL, TRAN_ENLACE_ID_IDENTIFICACION, TRAN_ENLACE_ID_PERSONA FROM TRAN_ENLACE WHERE TRAN_ENLACE_UUID = {enla_uuid}"
        ).on(
            'enla_uuid -> link
          )
          .as(_parser.singleOpt)
      val _result = _dataEnlace match {
        case Some(usuario) =>
          SQL(
          "UPDATE TRAN_USUARIO SET TRAN_USUARIO_CLAVE  = {contrasena} WHERE TRAN_USUARIO_ID_IDENTIFICACION = {id_identificacion} AND TRAN_USUARIO_ID_PERSONA = {id_persona} AND LOWER(TRAN_USUARIO_EMAIL) = LOWER({email})"
          ).on(
              'id_identificacion -> usuario._2,
              'id_persona -> usuario._3,
              'email -> usuario._1,
              'contrasena -> contrasena
            )
            .executeUpdate() > 0
            true
        case None => false
      }
      if (_result) {
         SQL(
            "UPDATE TRAN_ENLACE SET TRAN_ENLACE_ACTIVO = {enla_activo} WHERE TRAN_ENLACE_UUID = {enla_uuid}"
          ).on(
            'enla_activo -> 0,
            'enla_uuid -> link
          )
         .executeUpdate()
      }
      _result
    }
  }

  def registrarEventoWompi(evento: WompiEvent): Future[Boolean] = Future[Boolean] {
    db.withTransaction { implicit connection =>
      println("Evento: Actualizando Evento Wompi")
      val _actualizado: Boolean = SQL(
        """UPDATE TRAN_TRANSACCION SET
          TRAN_TRAN_REFERENCE = {tran_reference},
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
          'tran_reference -> evento.tran_reference,
          'tran_status -> evento.tran_status,
          'tran_created_at -> evento.tran_created_at,
          'tran_finalized_at -> evento.tran_finalized_at,
          'tran_amount_in_cents -> evento.tran_amount_in_cents,
          'tran_customer_email -> evento.tran_customer_email,
          'tran_currency -> evento.tran_currency,
          'tran_payment_method_type -> evento.tran_payment_method_type,
          'tran_payment_method -> Option.empty[String], //evento.tran_payment_method,
          'tran_status_message -> evento.tran_status_message,
          'tran_shipping_address -> evento.tran_shipping_address,
          'tran_redirect_url -> evento.tran_redirect_url,
          'tran_payment_source_id -> evento.tran_payment_source_id,
          'tran_payment_link_id -> evento.tran_payment_link_id,
          'tran_customer_data -> Option.empty[String], //evento.tran_customer_data,
          'tran_billing_data -> evento.tran_billing_data,
          'tran_sent_at -> evento.tran_sent_at,
          'tran_json -> Option.empty[String] //evento.tran_json
        )
        .executeUpdate() > 0

      if (!_actualizado) {
        println("Evento: Insertando Evento Wompi")
        val _insertado = SQL("""INSERT INTO TRAN_TRANSACCION (
          TRAN_TRAN_REFERENCE,
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
          {tran_reference},
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
            'tran_reference -> evento.tran_reference,
            'tran_id -> evento.tran_id,
            'tran_status -> evento.tran_status,
            'tran_created_at -> evento.tran_created_at,
            'tran_finalized_at -> evento.tran_finalized_at,
            'tran_amount_in_cents -> evento.tran_amount_in_cents,
            'tran_customer_email -> evento.tran_customer_email,
            'tran_currency -> evento.tran_currency,
            'tran_payment_method_type -> evento.tran_payment_method_type,
            'tran_payment_method -> Option.empty[String], //evento.tran_payment_method
            'tran_status_message -> evento.tran_status_message,
            'tran_shipping_address -> evento.tran_shipping_address,
            'tran_redirect_url -> evento.tran_redirect_url,
            'tran_payment_source_id -> evento.tran_payment_source_id,
            'tran_payment_link_id -> evento.tran_payment_link_id,
            'tran_customer_data -> Option.empty[String], //evento.tran_customer_data,
            'tran_billing_data -> evento.tran_billing_data,
            'tran_sent_at -> evento.tran_sent_at,
            'tran_json -> Option.empty[String] // evento.tran_json
          )
          .executeUpdate() > 0
      }
      println("Evento: Evento Wompi Procesado")
      evento.tran_status match {
        case Some(status) =>
          if (status == "APPROVED") {
            val _liquidacion = SQL("""SELECT * FROM LIQUIDACION WHERE UPPER(REFERENCIA) = UPPER({referencia}) AND APLICADA = 0""").on('referencia -> evento.tran_reference).as(Liquidacion._set.singleOpt)
            _liquidacion match {
              case Some(_liquidacion) => _liquidacion.aplicada match {
                case Some(0) => cService.confirmarLiquidacionWompi(_liquidacion.referencia)
                case _ => None
              }
              case None => None
            }
          }
        case None => None
      }
    }
    true
  }

  def obtenerRegistroWompi(id: String): Future[Transaccion] = Future {
    val _transaction = db.withTransaction {implicit connection =>
        SQL("""SELECT * FROM TRAN_TRANSACCION WHERE TRAN_TRAN_ID = {id}""")
        .on(
          'id -> id
        ).as(Transaccion._set.singleOpt)
    }
    _transaction match {
      case Some(transaction) => transaction
      case None => Transaccion(None, Some(id), None, None, None, None, None, None, None, None, None, None, None, None, None, None, None, None, None)
    }
  }
}
