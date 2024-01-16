package models

import javax.inject.Inject
import java.util.Calendar

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

import play.api.db.DBApi

import anorm._
import anorm.SqlParser.{get, str}
import anorm.JodaParameterMetaData._

import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}

import org.joda.time.DateTime
import org.joda.time.LocalDate

case class Departamento(depa_id: String, depa_descripcion: String)


class DepartamentoRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {
    private val db = dbapi.database("default")

    /**
    * Parsear un Departamento desde el ResultSet
    */
    private val simple = {
        get[String]("DPTO") map {
            case depa_id => Departamento(depa_id, depa_id) }
    }

    /*
    * Recuperar un Departamento usando su depa_id
    * @param depa_id: Long
    */
    def buscarPorId(depa_id: String): Option[Departamento] = {
        db.withConnection { implicit connection =>
            SQL("SELECT DPTO FROM \"gen$municipios\" WHERE DPTO = {depa_id}").on(
                'depa_id -> depa_id
            ).as(simple.singleOpt)
        }
    }

    /**
        Recuperar todos los departamentos
    */
    def todos(): Future[Iterable[Departamento]] = Future[Iterable[Departamento]] {
        db.withConnection { implicit connection => 
            SQL("SELECT DISTINCT d.DPTO FROM \"gen$municipios\" d ORDER BY DPTO ASC")
            .as(simple *)
        }
    }
}