package models

import javax.inject.Inject
import java.util.Calendar

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

import play.api.db.DBApi

import anorm._
import anorm.SqlParser.{ get, str }
import anorm.JodaParameterMetaData._

import scala.util.{ Failure, Success }
import scala.concurrent.{ Await, Future }

import org.joda.time.DateTime
import org.joda.time.LocalDate

case class Perfil(perf_id: Option[Long], perf_descripcion: String, perf_abreviatura: String, perf_estado: Int)

object Perfil {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val perfilWrites = new Writes[Perfil] {
        def writes(perfil: Perfil) = Json.obj(
            "perf_id" -> perfil.perf_id,
            "perf_descripcion" -> perfil.perf_descripcion,
            "perf_abreviatura" -> perfil.perf_abreviatura,
            "perf_estado" -> perfil.perf_estado
        )
    }

    implicit val perfilReads: Reads[Perfil] = (
        (__ \ "perf_id").readNullable[Long] and
        (__ \ "perf_descripcion").read[String] and
        (__ \ "perf_abreviatura").read[String] and
        (__ \ "perf_estado").read[Int]
    )(Perfil.apply _)    
}

class PerfilRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext){
    private val db = dbapi.database("default")

    /**
    * Parsear un Perfil desde un ResultSet
    */
    private val simple = {
        get[Option[Long]]("perfil.perf_id") ~
        get[String]("perfil.perf_descripcion") ~
        get[String]("perfil.perf_abreviatura") ~
        get[Int]("perfil.perf_estado") map {
            case perf_id ~ perf_descripcion ~ perf_abreviatura ~ perf_estado => Perfil(perf_id, perf_descripcion, perf_abreviatura, perf_estado)
        }
    }

    /**
    * Recuperar un Perfil dado su perf_id
    * @param perf_id: Long
    */
    def buscarPorId(perf_id: Long) : Option[Perfil] = {
        db.withConnection { implicit connection => 
            SQL("SELECT * FROM perfil WHERE perf_id = {perf_id}").
            on(
                'perf_id -> perf_id
            ).as(simple.singleOpt)
        }
    }

    /**
    * Recuperar un Perfil dado su empl_id
    * @param perf_id: Long
    */
    def buscarPorEmpleadoId(empl_id: Long) : List[Perfil] = {
        db.withConnection { implicit connection => 
            SQL("""SELECT * FROM GEN_EMPLEADO_PERFIL e 
                    LEFT JOIN perfil p ON p.PERF_ID = e.PERF_ID
                    WHERE EMPL_ID = {empl_id}""").
            on(
                'empl_id -> empl_id
            ).as(simple *)
        }
    }


    /**
    * Recuperar Perfil dado su perf_descripcion
    * @param perf_descripcion: String
    * @param empr_id: Long
    */
    def buscarPorDescripcion(perf_descripcion: String, empr_id:Long) : Future[Iterable[Perfil]] = Future[Iterable[Perfil]] {
        db.withConnection { implicit connection =>
            SQL("SELECT * FROM perfil WHERE perf_descripcion LIKE %{perf_descripcion}% and perf_estado <> 9").
            on(
                'perf_descripcion -> perf_descripcion
            ).as(simple *)
        }
    }

  def cuenta(): Long =  {
    db.withConnection{ implicit connection =>
      val result = SQL("SELECT COUNT(*) AS c FROM perfil WHERE perf_estado <> 9").
      as(SqlParser.scalar[Long].single)
      result
    }
  }  

    /**
    * Recuperar todos los Perfil de la empresa
    * @param empr_id: Long 
    */
    def todos(page_size: Long, current_page: Long): Future[Iterable[Perfil]] = Future[Iterable[Perfil]] {
        db.withConnection { implicit connection => 
            SQL("SELECT * FROM perfil WHERE perf_estado <> 9 ORDER BY perf_descripcion LIMIT {page_size} OFFSET {page_size} * ({current_page} - 1)").
            on(
                'page_size -> page_size,
                'current_page -> current_page
            ).
            as(simple *)
        }
    }

    /**
    * Crear perfil
    * @param perfil: Perfil
    */
    def crear(perfil: Perfil) : Future[Long] = Future {
        db.withConnection { implicit connection => 
            val fecha: LocalDate = new LocalDate(Calendar.getInstance().getTimeInMillis())
            val hora: LocalDate = fecha
            val id: Long = SQL("INSERT INTO perfil (perf_descripcion, perf_estado, perf_abreviatura) VALUES ({perf_descripcion},{perf_estado},{perf_abreviatura}) WHERE perf_id = {perf_id}").
            on(
                'perf_id -> perfil.perf_id,
                'perf_descripcion -> perfil.perf_descripcion,
                'perf_estado -> perfil.perf_estado,
                'perf_abreviatura -> perfil.perf_abreviatura,
            ).executeInsert().get

            SQL("INSERT INTO auditoria(audi_fecha, audi_hora, usua_id, audi_tabla, audi_uid, audi_campo, audi_valorantiguo, audi_valornuevo, audi_evento, audi_alias) VALUES ({audi_fecha}, {audi_hora}, {usua_id}, {audi_tabla}, {audi_uid}, {audi_campo}, {audi_valorantiguo}, {audi_valornuevo}, {audi_evento}, {audi_alias})").
            on(
                'audi_fecha -> fecha,
                'audi_hora -> hora,
                //'usua_id -> perfil.usua_id,
                'audi_tabla -> "perfil", 
                'audi_uid -> id,
                'audi_campo -> "perf_descripcion", 
                'audi_valorantiguo -> "",
                'audi_valornuevo -> perfil.perf_descripcion,
                'audi_evento -> "I").
                executeInsert()
            
            id            
        }
    }

    /**
    * Actualizar Perfil
    * @param perfil: Perfil
    */
    def actualizar(perfil: Perfil) : Long = {
        val perfil_ant: Option[Perfil] = buscarPorId(perfil.perf_id.get)

        db.withConnection { implicit connection =>
            val fecha: LocalDate = new LocalDate(Calendar.getInstance().getTimeInMillis())
            val hora: LocalDate = fecha
            val count: Long = SQL("UPDATE perfil SET perf_descripcion = {perf_descripcion}, perf_estado = {perf_estado}, perf_abreviatura = {perf_abreviatura} WHERE perf_id = {perf_id}").
            on(
                'perf_descripcion -> perfil.perf_descripcion,
                'perf_estado -> perfil.perf_estado,
                'perf_abreviatura -> perfil.perf_abreviatura,
            ).executeUpdate()

            if (perfil_ant != None) {
                if (perfil_ant.get.perf_descripcion != perfil.perf_descripcion){
                SQL("INSERT INTO auditoria(audi_fecha, audi_hora, usua_id, audi_tabla, audi_uid, audi_campo, audi_valorantiguo, audi_valornuevo, audi_evento, audi_alias) VALUES ({audi_fecha}, {audi_hora}, {usua_id}, {audi_tabla}, {audi_uid}, {audi_campo}, {audi_valorantiguo}, {audi_valornuevo}, {audi_evento}, {audi_alias})").
                on(
                    'audi_fecha -> fecha,
                    'audi_hora -> hora,
                    //'usua_id -> perfil.usua_id,
                    'audi_tabla -> "perfil", 
                    'audi_uid -> perfil.perf_id,
                    'audi_campo -> "perf_descripcion", 
                    'audi_valorantiguo -> perfil_ant.get.perf_descripcion,
                    'audi_valornuevo -> perfil.perf_descripcion,
                    'audi_evento -> "A").
                    executeInsert()                    
                }
                if (perfil_ant.get.perf_estado != perfil.perf_estado){
                SQL("INSERT INTO auditoria(audi_fecha, audi_hora, usua_id, audi_tabla, audi_uid, audi_campo, audi_valorantiguo, audi_valornuevo, audi_evento, audi_alias) VALUES ({audi_fecha}, {audi_hora}, {usua_id}, {audi_tabla}, {audi_uid}, {audi_campo}, {audi_valorantiguo}, {audi_valornuevo}, {audi_evento}, {audi_alias})").
                on(
                    'audi_fecha -> fecha,
                    'audi_hora -> hora,
                    //'usua_id -> perfil.usua_id,
                    'audi_tabla -> "perfil", 
                    'audi_uid -> perfil.perf_id,
                    'audi_campo -> "perf_estado", 
                    'audi_valorantiguo -> perfil_ant.get.perf_estado,
                    'audi_valornuevo -> perfil.perf_estado,
                    'audi_evento -> "A").
                    executeInsert()                    
                }
                if (perfil_ant.get.perf_abreviatura != perfil.perf_abreviatura){
                SQL("INSERT INTO auditoria(audi_fecha, audi_hora, usua_id, audi_tabla, audi_uid, audi_campo, audi_valorantiguo, audi_valornuevo, audi_evento, audi_alias) VALUES ({audi_fecha}, {audi_hora}, {usua_id}, {audi_tabla}, {audi_uid}, {audi_campo}, {audi_valorantiguo}, {audi_valornuevo}, {audi_evento}, {audi_alias})").
                on(
                    'audi_fecha -> fecha,
                    'audi_hora -> hora,
                    //'usua_id -> perfil.usua_id,
                    'audi_tabla -> "perfil", 
                    'audi_uid -> perfil.perf_id,
                    'audi_campo -> "perf_abreviatura",
                    'audi_valorantiguo -> perfil_ant.get.perf_abreviatura,
                    'audi_valornuevo -> perfil.perf_abreviatura,
                    'audi_evento -> "A").
                    executeInsert()                    
                }         
            }

            count
        }
    }

    /**
    * Eliminar Perfil
    * @param perfil: Perfil
    */
    def eliminar(perfil: Perfil) : Boolean = {
        db.withConnection { implicit connection =>
            val fecha: LocalDate = new LocalDate(Calendar.getInstance().getTimeInMillis())
            val hora: LocalDate = fecha        
            val count:Long = SQL("UPDATE perfil SET perf_estado = 9 WHERE perf_id = {perf_id}").
            on(
                'perf_id -> perfil.perf_id
            ).executeUpdate()

            SQL("INSERT INTO auditoria(audi_fecha, audi_hora, usua_id, audi_tabla, audi_uid, audi_campo, audi_valorantiguo, audi_valornuevo, audi_evento, audi_alias) VALUES ({audi_fecha}, {audi_hora}, {usua_id}, {audi_tabla}, {audi_uid}, {audi_campo}, {audi_valorantiguo}, {audi_valornuevo}, {audi_evento}, {audi_alias})").
                on(
                    'audi_fecha -> fecha,
                    'audi_hora -> hora,
                    //'usua_id -> perfil.usua_id,
                    'audi_tabla -> "perfil", 
                    'audi_uid -> perfil.perf_id,
                    'audi_campo -> "", 
                    'audi_valorantiguo -> "",
                    'audi_valornuevo -> "",
                    'audi_evento -> "E").
                    executeInsert()

            count > 0            
        }
    }
}