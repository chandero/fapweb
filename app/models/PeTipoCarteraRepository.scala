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

class PeTipoCarteraRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {
  private val pe = dbapi.database("pebase")

  def getAll = Future {
    pe.withConnection { implicit connection =>
      SQL("SELECT * FROM TIPO_CARTERA ORDER BY TIPO_CARTERA_ID").as(
        PE_TIPO_CARTERA._set *
      )
    }
  }
}
