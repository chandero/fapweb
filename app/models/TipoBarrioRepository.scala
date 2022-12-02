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

case class TipoBarrio(tiba_id:Option[Long], tiba_descripcion: String, tiba_estado: Int)

object TipoBarrio {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val tipobarrioWrites = new Writes[TipoBarrio] {
        def writes(tipobarrio: TipoBarrio) = Json.obj(
            "tiba_id" -> tipobarrio.tiba_id,
            "tiba_descripcion" -> tipobarrio.tiba_descripcion,
            "tiba_estado" -> tipobarrio.tiba_estado
        )
    }

    implicit val tipobarrioReads: Reads[TipoBarrio] = (
        (__ \ "tiba_id").readNullable[Long] and
        (__ \ "tiba_descripcion").read[String] and
        (__ \ "tiba_estado").read[Int]
    )(TipoBarrio.apply _)
}

class TipoBarrioRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext){
    private val db = dbapi.database("default")

    /**
    * Parsear un TipoBarrio desde un ResultSet
    */
    private val simple = {
        get[Option[Long]]("tipobarrio.tiba_id") ~
        get[String]("tipobarrio.tiba_descripcion") ~ 
        get[Int]("tipobarrio.tiba_estado") map {
            case tiba_id ~ tiba_descripcion ~ tiba_estado => TipoBarrio(tiba_id, tiba_descripcion, tiba_estado)
        }
    }

    /**
    * Recuperar un TipoBarrio dado su tiba_id
    * @param tiba_id: Long
    */
    def buscarPorId(tiba_id: Long) : Option[TipoBarrio] = {
        db.withConnection { implicit connection =>
            SQL("SELECT * FROM siap.tipobarrio WHERE tiba_id = {tiba_id}").
            on(
                'tiba_id -> tiba_id
            ).
            as(simple.singleOpt)
        }
    }

    /**
    * Recuperar todos los TipoBarrio activos
    */
    def todos(): Future[Iterable[TipoBarrio]] = Future[Iterable[TipoBarrio]] {
        db.withConnection { implicit connection =>
            SQL("SELECT * FROM siap.tipobarrio WHERE tiba_estado = 1").
            as(simple *)
        }        
    }

    /**
    * Crear TipoBarrio
    * @param tipobarrio: TipoBarrio
    */
    def crear(tipobarrio: TipoBarrio) : Future[Long] = Future {
        db.withConnection { implicit connection =>
            val id: Long = SQL("INSERT INTO siap.tipobarrio (tiba_descripcion) VALUES ({tiba_descripcion})").
            on(
                'tiba_descripcion -> tipobarrio.tiba_descripcion
            ).executeInsert().get

            id
        }
    }

    /**
    * Actualizar TipoBarrio
    * @param tipobarrio: TipoBarrio
    */
    def actualizar(tipobarrio: TipoBarrio) : Long = {
        db.withConnection { implicit connection =>
            val count: Long = SQL("UPDATE siap.tipobarrio SET tiba_descripcion = {tiba_descripcion}, tiba_estado = {tiba_estado} WHERE tiba_id = {tiba_id}").
            on(
                'tiba_id -> tipobarrio.tiba_id,
                'tiba_descripcion -> tipobarrio.tiba_descripcion,
                'tiba_estado -> tipobarrio.tiba_estado
            ).executeUpdate()

            count
        }
    }

    /**
    * Elimnar TipoBarrio
    * @param tipobarrio: TipoBarrio
    */
    def eliminar(tipobarrio:TipoBarrio): Boolean = {
        db.withConnection { implicit connection => 
            val count: Long = SQL("UPDATE siap.tipobarrio SET tiba_estado = 9 WHERE tiba_id = {tiba_id}").
            on(
                'tiba_id -> tipobarrio.tiba_id
            ).executeUpdate()

            count > 0
        }
    }
}