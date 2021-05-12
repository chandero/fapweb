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
import scala.math.BigDecimal

import org.joda.time.DateTime
import org.joda.time.LocalDate
import scala.collection.mutable

case class Puc(
   codigo: Option[String],
   id_agencia: Option[Int],
   nombre: Option[String],
   tipo: Option[String],
   codigomayor: Option[String],
   movimiento: Option[Int],
   esbanco: Option[Int],
   informe: Option[String],
   nivel: Option[Int],
   naturaleza: Option[String],
   centrocosto: Option[String],
   saldoinicial: Option[BigDecimal],
   escaptacion: Option[Int],
   escolocacion: Option[Int] 
)

object Puc {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wWrites = new Writes[Puc] {
    def writes(e: Puc) = Json.obj(
        "codigo" -> e.codigo,
        "id_agencia" -> e.id_agencia,
        "nombre" -> e.nombre,
        "tipo" -> e.tipo,
        "codigomayor" -> e.codigomayor,
        "movimiento" -> e.movimiento,
        "esbanco" -> e.esbanco,
        "informe" -> e.informe,
        "nivel" -> e.nivel,
        "naturaleza" -> e.naturaleza,
        "centrocosto" -> e.centrocosto,
        "saldoinicial" -> e.saldoinicial,
        "escaptacion" -> e.escaptacion,
        "escolocacion" -> e.escolocacion
    )
  }

  implicit val rReads: Reads[Puc] = (
    (__ \ "codigo").readNullable[String] and
    (__ \ "id_agencia").readNullable[Int] and
    (__ \ "nombre").readNullable[String] and
    (__ \ "tipo").readNullable[String] and
    (__ \ "codigomayor").readNullable[String] and
    (__ \ "movimiento").readNullable[Int] and
    (__ \ "esbanco").readNullable[Int] and
    (__ \ "informe").readNullable[String] and
    (__ \ "nivel").readNullable[Int] and
    (__ \ "naturaleza").readNullable[String] and
    (__ \ "centrocosto").readNullable[String] and
    (__ \ "saldoinicial").readNullable[BigDecimal] and
    (__ \ "escaptacion").readNullable[Int] and
    (__ \ "escolocacion").readNullable[Int]    
  )(Puc.apply _)

  val _set = {
   get[Option[String]]("codigo") ~
   get[Option[Int]]("id_agencia") ~
   get[Option[String]]("nombre") ~
   get[Option[String]]("tipo") ~
   get[Option[String]]("codigomayor") ~
   get[Option[Int]]("movimiento") ~
   get[Option[Int]]("esbanco") ~
   get[Option[String]]("informe") ~
   get[Option[Int]]("nivel") ~
   get[Option[String]]("naturaleza") ~
   get[Option[String]]("centrocosto") ~
   get[Option[BigDecimal]]("saldoinicial") ~
   get[Option[Int]]("escaptacion") ~
   get[Option[Int]]("escolocacion") map {
     case
        codigo ~
        id_agencia ~
        nombre ~
        tipo ~
        codigomayor ~
        movimiento ~
        esbanco ~
        informe ~
        nivel ~
        naturaleza ~
        centrocosto ~
        saldoinicial ~
        escaptacion ~
        escolocacion     
       => Puc(
            codigo,
            id_agencia,
            nombre,
            tipo,
            codigomayor,
            movimiento,
            esbanco,
            informe,
            nivel,
            naturaleza,
            centrocosto,
            saldoinicial,
            escaptacion,
            escolocacion 
       ) 
   }
  }
}

class PucRepository @Inject()(dbapi: DBApi, colocacionService: ColocacionRepository, empresaRepository: EmpresaRepository)(
    implicit ec: DatabaseExecutionContext) {
  private val db = dbapi.database("default")

    def obtenerLista(): Future[Iterable[Puc]] = Future {
      db.withTransaction { implicit connection => 
        SQL("""SELECT * FROM "con$puc" ORDER BY CODIGO ASC""").as(Puc._set *)
      }
    }

    def obtenerNombre(codigo: String): Future[String] = Future {
        val nombre = db.withConnection { implicit connection =>
            SQL("""SELECT NOMBRE FROM "con$puc" WHERE CODIGO = {codigo}""").
            on(
                'codigo -> codigo
            ).as(SqlParser.scalar[String].singleOpt)
        }
        nombre match {
            case Some(n) => n
            case None => "NO EXISTE EL CODIGO"
         }
    }

    def obtener(codigo: String): Future[Option[Puc]] = Future {
        val puc = db.withConnection { implicit connection =>
            SQL("""SELECT * FROM "con$puc" WHERE CODIGO = {codigo}""").
            on(
                'codigo -> codigo
            ).as(Puc._set.singleOpt)
        }
        puc
    }
}