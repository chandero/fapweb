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

case class Municipio(cod_municipio: Option[Long], nombre: Option[String], dpto: Option[String])

object Municipio {
    implicit val yourJodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    implicit val yourJodaDateWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val municipioWrites = new Writes[Municipio] {
        def writes(municipio: Municipio) = Json.obj(
            "cod_municipio" -> municipio.cod_municipio,
            "nombre" -> municipio.nombre,
            "dpto" -> municipio.dpto,
        )
    }

    implicit val municipioReads: Reads[Municipio] = (
        (__ \ "cod_municipio").readNullable[Long] and
          (__ \ "nombre").readNullable[String] and
          (__ \ "dpto").readNullable[String]
    )(Municipio.apply _)
}

class MunicipioRepository @Inject()(dbapi: DBApi)(implicit ec:DatabaseExecutionContext){
   private val db = dbapi.database("default")

    /**
    *    Parsear un Municipio desde un ResultSet
    */
    private val simple = {
        get[Option[Long]]("cod_municipio") ~
          get[Option[String]]("nombre") ~
          get[Option[String]]("dpto") map {
              case cod_municipio ~ nombre ~ dpto => Municipio(cod_municipio, nombre, dpto)
          }
    }

    /**
    * Recuperar un Municipio por su muni_id
    * @param muni_id: Long
    */
    def buscarPorId(cod_municipio: Long) : Option[Municipio] = {
        db.withConnection { implicit connection => 
            SQL("SELECT * FROM \"gen$municipio\" WHERE cod_municipio = {cod_municipio}").
            on(
                'cod_municipio -> cod_municipio
            ).
            as(simple.singleOpt)
        }
    }

    /**
    *  Recuperar un Municipio por su muni_descripcion
    */
    def buscarPorDescripcion(muni_descripcion: String) : Future[Iterable[Municipio]] = Future[Iterable[Municipio]] {
        db.withConnection { implicit connection =>
            SQL("SELECT * FROM \"gen$municipio\" WHERE nombre LIKE %{muni_descripcion}% ORDER BY nombre").
            on(
                'muni_descripcion -> muni_descripcion
            ).as(simple *)
        }
    }

    /**
    *  Recupear todos los Municipio de un Departamento usando el depa_id
    *  @param depa_id: Long
    */
    def buscarPorDepartamento(depa_id: String):Future[Iterable[Municipio]] = Future[Iterable[Municipio]] {
        db.withConnection { implicit connection => 
            SQL("SELECT * FROM \"gen$municipios\" WHERE DPTO = {depa_id} ORDER BY NOMBRE ASC").
            on(
                'depa_id -> depa_id
            ).
            as(simple *)            
        }
    }
    /**
    * Recuperar todos los Municipio
    */
    def todos():Future[Iterable[Municipio]] = Future[Iterable[Municipio]] {
        db.withConnection { implicit connection => 
            SQL("SELECT * FROM \"gen$municipios\" ORDER BY nombre").
            as(simple *)            
        }
    }

}   
