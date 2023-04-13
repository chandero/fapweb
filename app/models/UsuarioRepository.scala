package models

import javax.inject.Inject
import java.util.Calendar
import java.sql.Timestamp
import java.security.MessageDigest
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import scala.util.{Failure, Success}
import anorm._
import anorm.SqlParser.{get, str}
import anorm.JodaParameterMetaData._
import play.api.db.DBApi
import scala.concurrent.Future
import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._
import notifiers._

import java.util.UUID.randomUUID

import utilities._

case class Usuario(
    id_empleado: Option[String],
    documento: Option[String],
    primer_apellido: Option[String],
    segundo_apellido: Option[String],
    nombre: Option[String],
    numero_cuenta: Option[String],
    ultimo_cambio_pasabordo: Option[DateTime],
    ag: Option[Int],
    contrasena: Option[String],
    tipo: Option[Int],
    email: Option[String],
    ultima_sesion: Option[DateTime],
    empr_id: Option[Long],
    perf_id: Option[Long],
    id: Option[Long]
)

case class Enlace(
    enla_id: Option[Long],
    enla_uuid: Option[String],
    enla_activo: Option[Int],
    enla_fecha: Option[DateTime],
    usua_email: Option[String]
)

object Usuario {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val usuarioWrites = new Writes[Usuario] {
    def writes(usuario: Usuario) = Json.obj(
      "id_empleado" -> usuario.id_empleado,
      "documento" -> usuario.documento,
      "primer_apellido" -> usuario.primer_apellido,
      "segundo_apellido" -> usuario.segundo_apellido,
      "nombre" -> usuario.nombre,
      "numero_cuenta" -> usuario.numero_cuenta,
      "ultimo_cambio_pasabordo" -> usuario.ultimo_cambio_pasabordo,
      "ag" -> usuario.ag,
      "contrasena" -> usuario.contrasena,
      "tipo" -> usuario.tipo,
      "email" -> usuario.email,
      "ultima_sesion" -> usuario.ultima_sesion,
      "empr_id" -> usuario.empr_id,
      "perf_id" -> usuario.perf_id,
      "id" -> usuario.id
    )
  }

  implicit val usuarioReads: Reads[Usuario] = (
    (__ \ "id_empleado").readNullable[String] and
      (__ \ "documento").readNullable[String] and
      (__ \ "primer_apellido").readNullable[String] and
      (__ \ "segundo_apellido").readNullable[String] and
      (__ \ "nombre").readNullable[String] and
      (__ \ "numero_cuenta").readNullable[String] and
      (__ \ "ultimo_cambio_pasabordo").readNullable[DateTime] and
      (__ \ "ag").readNullable[Int] and
      (__ \ "contrasena").readNullable[String] and
      (__ \ "tipo").readNullable[Int] and
      (__ \ "email").readNullable[String] and
      (__ \ "ultima_sesion").readNullable[DateTime] and
      (__ \ "empr_id").readNullable[Long] and
      (__ \ "perf_id").readNullable[Long] and
      (__ \ "id").readNullable[Long]
  )(Usuario.apply _)

  val usuarioRS = {
    get[Option[String]]("gen$empleado.ID_EMPLEADO") ~
      get[Option[String]]("gen$empleado.DOCUMENTO") ~
      get[Option[String]]("gen$empleado.PRIMER_APELLIDO") ~
      get[Option[String]]("gen$empleado.SEGUNDO_APELLIDO") ~
      get[Option[String]]("gen$empleado.NOMBRE") ~
      get[Option[String]]("gen$empleado.NUMERO_CUENTA") ~
      get[Option[DateTime]]("gen$empleado.ULTIMO_CAMBIO_PASABORDO") ~
      get[Option[Int]]("gen$empleado.AG") ~
      get[Option[String]]("gen$empleado.CONTRASENA") ~
      get[Option[Int]]("gen$empleado.TIPO") ~
      get[Option[String]]("gen$empleado.EMAIL") ~
      get[Option[DateTime]]("gen$empleado.ULTIMA_SESION") ~
      get[Option[Long]]("gen$empleado.EMPR_ID") ~
      get[Option[Long]]("gen$empleado.PERF_ID") ~
      get[Option[Long]]("gen$empleado.ID") map {
      case id_empleado ~
            documento ~
            primer_apellido ~
            segundo_apellido ~
            nombre ~
            numero_cuenta ~
            ultimo_cambio_pasabordo ~
            ag ~
            contrasena ~
            tipo ~
            email ~
            ultima_sesion ~
            empr_id ~
            perf_id ~
            id =>
        Usuario(
          id_empleado,
          documento,
          primer_apellido,
          segundo_apellido,
          nombre,
          numero_cuenta,
          ultimo_cambio_pasabordo,
          ag,
          contrasena,
          tipo,
          email,
          ultima_sesion,
          empr_id,
          perf_id,
          id
        )
    }
  }

  /**
    * Parsear el enlace
    */
  val enlaceRS = {
    get[Option[Long]]("ENLA_ID") ~
      get[Option[String]]("ENLA_UUID") ~
      get[Option[Int]]("ENLA_ACTIVO") ~
      get[Option[DateTime]]("ENLA_FECHA") ~
      get[Option[String]]("ENLA_EMAIL") map {
      case enla_id ~ enla_uuid ~ enla_activo ~ enla_fecha ~ enla_email =>
        Enlace(enla_id, enla_uuid, enla_activo, enla_fecha, enla_email)
    }
  }

}

class UsuarioRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  /**
    * Recuperar un usuario por su usua_id
    */
  def buscarPorId(id: Long): Future[Option[Usuario]] = Future {
    db.withConnection { implicit connection =>
      SQL("SELECT * FROM \"gen$empleado\" u WHERE u.id = {id}")
        .on('id -> id)
        .as(Usuario.usuarioRS.singleOpt)
    }
  }

  def buscarPorIdDirecto(id: Long): Option[Usuario] = {
    db.withConnection { implicit connection =>
      SQL("SELECT * FROM \"gen$empleado\" u WHERE u.id = {id}")
        .on('id -> id)
        .as(Usuario.usuarioRS.singleOpt)
    }
  }

  /**
    * Recuperar un usuario por su usua_email
    */
  def buscarPorEmail(email: String): Future[Option[Usuario]] =
    Future {
      db.withConnection { implicit connection =>
        var result = SQL("SELECT * FROM \"gen$empleado\" WHERE email = {email}")
          .on('email -> email)
          .as(Usuario.usuarioRS.singleOpt)
        println("Usuario: " + result)
        result
      }
    }(ec)

  def autenticar(email: String, contrasena: String): Future[Boolean] =
    Future {
      db.withConnection { implicit connection =>
        SQL(
          "UPDATE \"gen$empleado\" SET ultima_sesion = CURRENT_TIMESTAMP WHERE email = {email} AND contrasena = {contrasena} AND tipo <> 99"
        ).on("email" -> email, "contrasena" -> Conversion.sha1(contrasena))
          .executeUpdate() > 0
      }
    }(ec)

  def validarEnlace(enla_uuid: String): Future[Boolean] = Future {
    val fecha: LocalDateTime =
      new LocalDateTime(Calendar.getInstance().getTimeInMillis())
    db.withConnection { implicit connection =>
      val link: Option[Enlace] = SQL(
        "SELECT * FROM enlace WHERE enla_uuid = {enla_uuid} and enla_activo = {enla_activo}"
      ).on(
          'enla_uuid -> enla_uuid,
          'enla_activo -> 1
        )
        .as(Usuario.enlaceRS.singleOpt)
      link match {
        case None => {
          false
        }
        case Some(link) => {
          val duration = fecha.toDate().getTime() - link.enla_fecha.get
            .toDate()
            .getTime()
          if (duration < 86400000) {
            true
          } else {
            SQL(
              "UPDATE enlace SET enla_activo = {enla_activo} WHERE enla_uuid = {enla_uuid}"
            ).on(
                'enla_activo -> 0,
                'enla_uuid -> enla_uuid
              )
              .executeUpdate()
            false
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
        val enlace = hosturl + "/#/r/" + uuid
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
    val contrasena = Conversion.sha1(clave)
    db.withConnection { implicit connection =>
      val email =
        SQL("SELECT ENLA_EMAIL FROM ENLACE WHERE ENLA_UUID = {enla_uuid}")
          .on(
            'enla_uuid -> link
          )
          .as(SqlParser.scalar[String].single)

      val result: Boolean = SQL(
        "UPDATE \"gen$empleado\" SET contrasena = {contrasena} WHERE email = {email}"
      ).on(
          'email -> email,
          'contrasena -> contrasena
        )
        .executeUpdate() > 0

      val empleado = SQL("SELECT * FROM \"gen$empleado\" WHERE email = {email}")
        .on(
          'email -> email
        )
        .as(Usuario.usuarioRS.singleOpt)

      SQL(
        "INSERT INTO auditoria(audi_fecha, audi_hora, usua_id, audi_tabla, audi_uid, audi_campo, audi_valorantiguo, audi_valornuevo, audi_evento) VALUES ({audi_fecha}, {audi_hora}, {usua_id}, {audi_tabla}, {audi_uid}, {audi_campo}, {audi_valorantiguo}, {audi_valornuevo}, {audi_evento})"
      ).on(
          'audi_fecha -> fecha,
          'audi_hora -> hora,
          'usua_id -> empleado.get.id,
          'audi_tabla -> "gen$empleado",
          'audi_uid -> empleado.get.id,
          'audi_campo -> "contrasena",
          'audi_valorantiguo -> '*',
          'audi_valornuevo -> '*',
          'audi_evento -> "A"
        )
        .executeInsert()
      result
    }
  }

  def crear(usuario: Usuario): Future[Long] = Future {
    db.withConnection { implicit connection =>
      val fecha: LocalDate =
        new LocalDate(Calendar.getInstance().getTimeInMillis())
      val hora: LocalDateTime =
        new LocalDateTime(Calendar.getInstance().getTimeInMillis())
      val id: Long = SQL(
        "INSERT INTO \"gen$empleado\" (id_empleado, documento, primer_apellido, segundo_apellido, nombre, numero_cuenta, ultimo_cambio_pasabordo, ag, contrasena, tipo, ultima_sesion, email, empr_id, perf_id, id) VALUES({id_empleado}, {documento}, {primer_apellido}, {segundo_apellido}, {nombre}, {numero_cuenta}, {ultimo_cambio_pasabordo}, {ag}, {contrasena}, {tipo}, {ultima_sesion}, {email}, {empr_id}, {perf_id}, {id}) RETURNING id"
      ).on(
          'id_empleado -> usuario.id_empleado,
          'documento -> usuario.documento,
          'primer_apellido -> usuario.primer_apellido,
          'segundo_apellido -> usuario.segundo_apellido,
          'nombre -> usuario.nombre,
          'numero_cuenta -> usuario.numero_cuenta,
          'ultimo_cambio_pasabordo -> usuario.ultimo_cambio_pasabordo,
          'ag -> usuario.ag,
          'contrasena -> usuario.contrasena,
          'tipo -> usuario.tipo,
          'ultima_sesion -> usuario.ultima_sesion,
          'email -> usuario.email,
          'empr_id -> usuario.empr_id,
          'perf_id -> usuario.perf_id
        )
        .executeInsert()
        .get

      SQL(
        "INSERT INTO usuario_empresa (id_empleado, empr_id, perf_id) VALUES ({id_empleado}, {empr_id}, {perf_id})"
      ).on(
          'id_empleado -> id,
          'empr_id -> usuario.empr_id,
          'perf_id -> usuario.perf_id
        )
        .executeInsert()

      SQL(
        "INSERT INTO auditoria(audi_fecha, audi_hora, usua_id, audi_tabla, audi_uid, audi_campo, audi_valorantiguo, audi_valornuevo, audi_evento) VALUES ({audi_fecha}, {audi_hora}, {usua_id}, {audi_tabla}, {audi_uid}, {audi_campo}, {audi_valorantiguo}, {audi_valornuevo}, {audi_evento})"
      ).on(
          'audi_fecha -> fecha,
          'audi_hora -> hora,
          'usua_id -> id,
          'audi_tabla -> "gen$empleado",
          'audi_uid -> id,
          'audi_campo -> "email",
          'audi_valorantiguo -> "",
          'audi_valornuevo -> usuario.email,
          'audi_evento -> "I"
        )
        .executeInsert()

      id
    }
  }

  def actualizar(id_empleado: String, contrasena: String): Future[Boolean] =
    Future {
      db.withConnection { implicit connection =>
        SQL(
          "UPDATE \"gen$empleado\" SET ultima_sesion = now() WHERE id_empleado = {id_empleado} AND contrasena = {contrasena})"
        ).on(
            "id_empleado" -> id_empleado,
            "contrasena" -> contrasena
          )
          .executeUpdate() > 0
      }
    }(ec)

  def actualizar(usuario: Usuario, email: String): Future[Boolean] = {
    buscarPorEmail(email).map { usuario_ant =>
      db.withConnection { implicit connection =>
        val fecha: LocalDate =
          new LocalDate(Calendar.getInstance().getTimeInMillis())
        val hora: LocalDateTime =
          new LocalDateTime(Calendar.getInstance().getTimeInMillis())
        val count: Long = SQL(
          "UPDATE \"gen$empleado\" SET contrasena = {contrasena}, nombre = {nombre}, primer_apellido = {primer_apellido}, segundo_apellido = {segundo_apellido}, tipo = {tipo}, ultima_sesion = {ultima_sesion} WHERE email = {email}"
        ).on(
            'contrasena -> Conversion.sha1(usuario.contrasena.get),
            'nombre -> usuario.nombre,
            'primer_apellido -> usuario.primer_apellido,
            'segundo_apellido -> usuario.segundo_apellido,
            'tipo -> usuario.tipo,
            'ultima_sesion -> usuario.ultima_sesion,
            'email -> usuario.email
          )
          .executeUpdate()

        if (usuario_ant != None) {
          if (usuario_ant.get.contrasena != usuario.contrasena) {
            SQL(
              "INSERT INTO auditoria(audi_fecha, audi_hora, usua_id, audi_tabla, audi_uid, audi_campo, audi_valorantiguo, audi_valornuevo, audi_evento) VALUES ({audi_fecha}, {audi_hora}, {usua_id}, {audi_tabla}, {audi_uid}, {audi_campo}, {audi_valorantiguo}, {audi_valornuevo}, {audi_evento})"
            ).on(
                'audi_fecha -> fecha,
                'audi_hora -> hora,
                'usua_id -> usuario.id,
                'audi_tabla -> "gen$empleado",
                'audi_uid -> usuario.id,
                'audi_campo -> "contrasena",
                'audi_valorantiguo -> usuario_ant.get.contrasena,
                'audi_valornuevo -> usuario.contrasena,
                'audi_evento -> "A"
              )
              .executeInsert()
          }

          if (usuario_ant.get.nombre != usuario.nombre) {
            SQL(
              "INSERT INTO auditoria(audi_fecha, audi_hora, usua_id, audi_tabla, audi_uid, audi_campo, audi_valorantiguo, audi_valornuevo, audi_evento) VALUES ({audi_fecha}, {audi_hora}, {usua_id}, {audi_tabla}, {audi_uid}, {audi_campo}, {audi_valorantiguo}, {audi_valornuevo}, {audi_evento})"
            ).on(
                'audi_fecha -> fecha,
                'audi_hora -> hora,
                'usua_id -> usuario.email,
                'audi_tabla -> "gen$empleado",
                'audi_uid -> usuario.email,
                'audi_campo -> "nombre",
                'audi_valorantiguo -> usuario_ant.get.nombre,
                'audi_valornuevo -> usuario.nombre,
                'audi_evento -> "A"
              )
              .executeInsert()
          }

          if (usuario_ant.get.primer_apellido != usuario.primer_apellido) {
            SQL(
              "INSERT INTO auditoria(audi_fecha, audi_hora, usua_id, audi_tabla, audi_uid, audi_campo, audi_valorantiguo, audi_valornuevo, audi_evento) VALUES ({audi_fecha}, {audi_hora}, {usua_id}, {audi_tabla}, {audi_uid}, {audi_campo}, {audi_valorantiguo}, {audi_valornuevo}, {audi_evento})"
            ).on(
                'audi_fecha -> fecha,
                'audi_hora -> hora,
                'usua_id -> usuario.email,
                'audi_tabla -> "gen$empleado",
                'audi_uid -> usuario.email,
                'audi_campo -> "primer_apellido",
                'audi_valorantiguo -> usuario_ant.get.primer_apellido,
                'audi_valornuevo -> usuario.primer_apellido,
                'audi_evento -> "A"
              )
              .executeInsert()
          }

          if (usuario_ant.get.tipo != usuario.tipo) {
            SQL(
              "INSERT INTO auditoria(audi_fecha, audi_hora, usua_id, audi_tabla, audi_uid, audi_campo, audi_valorantiguo, audi_valornuevo, audi_evento) VALUES ({audi_fecha}, {audi_hora}, {usua_id}, {audi_tabla}, {audi_uid}, {audi_campo}, {audi_valorantiguo}, {audi_valornuevo}, {audi_evento})"
            ).on(
                'audi_fecha -> fecha,
                'audi_hora -> hora,
                'usua_id -> usuario.email,
                'audi_tabla -> "gen$empleado",
                'audi_uid -> usuario.email,
                'audi_campo -> "tipo",
                'audi_valorantiguo -> usuario_ant.get.tipo,
                'audi_valornuevo -> usuario.tipo,
                'audi_evento -> "A"
              )
              .executeInsert()
          }

          if (usuario_ant.get.ultima_sesion != usuario.ultima_sesion) {
            SQL(
              "INSERT INTO auditoria(audi_fecha, audi_hora, usua_id, audi_tabla, audi_uid, audi_campo, audi_valorantiguo, audi_valornuevo, audi_evento) VALUES ({audi_fecha}, {audi_hora}, {usua_id}, {audi_tabla}, {audi_uid}, {audi_campo}, {audi_valorantiguo}, {audi_valornuevo}, {audi_evento})"
            ).on(
                'audi_fecha -> fecha,
                'audi_hora -> hora,
                'usua_id -> usuario.email,
                'audi_tabla -> "gen$empleado",
                'audi_uid -> usuario.email,
                'audi_campo -> "ultima_sesion",
                'audi_valorantiguo -> usuario_ant.get.ultima_sesion,
                'audi_valornuevo -> usuario.ultima_sesion,
                'audi_evento -> "A"
              )
              .executeInsert()
          }
        }

        count > 0
      }
    }
  }

  /**
    * Recuperar total de registros
    * @return total
    */
  def cuenta(empr_id: Long): Long = {
    db.withConnection { implicit connection =>
      val result = SQL(
        "SELECT COUNT(*) AS c FROM \"gen$empleado\" u WHERE empr_id = {empr_id}"
      ).on(
          'empr_id -> empr_id
        )
        .as(SqlParser.scalar[Long].single)
      result
    }
  }

  def todos(
      empr_id: Long,
      page_size: Long,
      current_page: Long
  ): Future[Iterable[Usuario]] = Future[Iterable[Usuario]] {
    db.withConnection { implicit connection =>
      SQL(
        "SELECT FIRST {page_size} SKIP ({page_size} * ({current_page} - 1)) * FROM \"gen$empleado\" u WHERE empr_id = {empr_id} ORDER BY primer_apellido"
      ).on(
          'empr_id -> empr_id,
          'page_size -> page_size,
          'current_page -> current_page
        )
        .as(Usuario.usuarioRS *)
    }
  }

}
