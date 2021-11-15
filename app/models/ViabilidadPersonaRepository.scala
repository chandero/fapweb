package models

import javax.inject.Inject
import java.util.Calendar
import java.sql.Timestamp
import java.security.MessageDigest
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import scala.util.{ Failure, Success, Random }
import anorm._
import anorm.SqlParser.{ get, str }
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

case class ViabilidadPeticion (
    id_identificacion: Int,
    id_persona: String,
    fecha_expedicion: Long,
    id_linea: Long,
    ingresos: Double,
    gastos: Double,
    otros_egresos: Double,
    monto_solicitado: Double,
    plazo_solicitado: Int,
    numero_celular: String,
    email: String
)

class ViabilidadRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {
    private val db = dbapi.database("default")

    def viabilidad(v: ViabilidadPeticion): Boolean = {
        db.withConnection { implicit connection =>
            val fecha = new LocalDateTime
            val fecha_actual = fecha.toString("yyyy-MM-dd HH:mm:ss")
            val fecha_actual_date = fecha.toString("yyyy-MM-dd")
            val fecha_actual_time = fecha.toString("HH:mm:ss")

            SQL("""""")
            val r = new Random()
            val bool = r.nextBoolean()
            bool
        }
    }
}