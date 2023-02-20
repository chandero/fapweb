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
import scala.collection.mutable.ListBuffer

import org.joda.time.DateTime
import org.joda.time.LocalDate
import scala.collection.mutable

case class Vehiculo(
    id_vehiculo: Option[Int],
    numero_placa: Option[String],
    descripcion: Option[String],
    pignorado: Option[Int],
    valor_comercial: Option[BigDecimal],
    id_identificacion: Option[Int],
    id_persona: Option[String],
    marca: Option[String],
    modelo: Option[String]
)

object Vehiculo {
    implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

    implicit val wWrites = new Writes[Vehiculo] {
        def writes(e: Vehiculo) = Json.obj(
            "id_vehiculo" -> e.id_vehiculo,            
            "numero_placa" -> e.numero_placa,
            "descripcion" -> e.descripcion,
            "pignorado" -> e.pignorado,
            "valor_comercial" -> e.valor_comercial,
            "id_identificacion" -> e.id_identificacion,
            "id_persona" -> e.id_persona,  
            "marca" -> e.marca,
            "modelo" -> e.modelo
        )  
    }

    implicit val rReads: Reads[Vehiculo] = (
        (__ \ "id_vehiculo").readNullable[Int] and
        (__ \ "numero_placa").readNullable[String] and
        (__ \ "descripcion").readNullable[String] and
        (__ \ "pignorado").readNullable[Int] and
        (__ \ "valor_comercial").readNullable[BigDecimal] and
        (__ \ "id_identificacion").readNullable[Int] and
        (__ \ "id_persona").readNullable[String] and
        (__ \ "marca").readNullable[String] and
        (__ \ "modelo").readNullable[String]
    )(Vehiculo.apply _)

    val _set = {
        get[Option[Int]]("id_vehiculo") ~
        get[Option[String]]("numero_placa") ~
        get[Option[String]]("descripcion") ~
        get[Option[Int]]("pignorado") ~
        get[Option[BigDecimal]]("valor_comercial") ~        
        get[Option[Int]]("id_identificacion") ~
        get[Option[String]]("id_persona") ~
        get[Option[String]]("marca") ~
        get[Option[String]]("modelo") map {
            case 
                id_vehiculo ~
                numero_placa ~
                descripcion ~
                pignorado ~
                valor_comercial ~
                id_identificacion ~
                id_persona ~
                marca ~
                modelo => Vehiculo(
                    id_vehiculo,
                    numero_placa,
                    descripcion,
                    pignorado,
                    valor_comercial,
                    id_identificacion,
                    id_persona,
                    marca,
                    modelo
                )
        }
    }
}

class VehiculoRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {
    // conexión a la base de datos
    private val db = dbapi.database("default")

    def buscar(id_solicitud: Int) : Future[Iterable[Vehiculo]] = {
        val result = db.withConnection { implicit connection =>
            SQL("""SELECT * FROM \"gen$vehiculo\" WHERE ID_VEHICULO = {id_solicitud}""").
            on(
                'id_solicitud -> id_solicitud
            ).as(Vehiculo._set *)
        }
        Future.successful(result)
    }

    def guardar(id_solicitud: Int, lv: List[Vehiculo]) : Boolean = {
        val result = db.withConnection { implicit connection =>
            SQL(
              """DELETE FROM \"gen$vehiculo\" WHERE ID_VEHICULO = {id_solicitud}"""
            ).on(
                'id_solicitud -> id_solicitud
            )
              .executeUpdate()
            
            for (v <- lv) {
                SQL("""INSERT INTO \"gen$vehiculo\" (
                    ID_VEHICULO,
                    NUMERO_PLACA,
                    DESCRIPCION,
                    PIGNORADO,
                    VALOR_COMERCIAL,
                    ID_IDENTIFICACION,
                    ID_PERSONA,
                    MARCA,
                    MODELO
                ) VALUES (
                    {id_vehiculo},
                    {numero_placa},
                    {descripcion},
                    {pignorado},
                    {valor_comercial},
                    {id_identificacion},
                    {id_persona},
                    {marca},
                    {modelo}
                )""").on(
                    'id_vehiculo -> v.id_vehiculo,
                    'numero_placa -> v.numero_placa,
                    'descripcion -> v.descripcion,
                    'pignorado -> v.pignorado,
                    'valor_comercial -> v.valor_comercial,
                    'id_identificacion -> v.id_identificacion,
                    'id_persona -> v.id_persona,
                    'marca -> v.marca,
                    'modelo -> v.modelo
                ).executeUpdate()
            }
            true
        }
        result
    }
}