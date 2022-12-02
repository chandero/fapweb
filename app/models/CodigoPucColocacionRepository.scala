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
import scala.collection.mutable.ListBuffer

import org.joda.time.DateTime
import org.joda.time.LocalDate

import utilities._

case class CodigoPucColocacion(
    id_clasificacion: Option[Int],
    id_garantia: Option[Int],
    id_categoria: Option[String],
    dias_iniciales: Option[Int],
    dias_finales: Option[Int],
    cod_capital_cp: Option[String],
    cod_capital_lp: Option[String],
    cod_int_ant: Option[String],
    cod_cxc: Option[String],
    cod_int_mes: Option[String],
    cod_int_mora: Option[String],
    cod_costas: Option[String],
    cod_otras_cxc: Option[String],
    cod_prov_capital: Option[String],
    cod_prov_interes: Option[String],
    cod_prov_costas: Option[String],
    cod_capital_cas: Option[String],
    cod_cxc_cas: Option[String],
    cod_costas_cas: Option[String],
    cod_contingencia: Option[String],
    cod_iprov_capital: Option[String],
    cod_eprov_capital: Option[String]
)

object CodigoPucColocacion {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wWrites = new Writes[CodigoPucColocacion] {
    def writes(e: CodigoPucColocacion) = Json.obj(
      "id_clasificacion" -> e.id_clasificacion,
      "id_garantia" -> e.id_garantia,
      "id_categoria" -> e.id_categoria,
      "dias_iniciales" -> e.dias_iniciales,
      "dias_finales" -> e.dias_finales,
      "cod_capital_cp" -> e.cod_capital_cp,
      "cod_capital_lp" -> e.cod_capital_lp,
      "cod_int_ant" -> e.cod_int_ant,
      "cod_cxc" -> e.cod_cxc,
      "cod_int_mes" -> e.cod_int_mes,
      "cod_int_mora" -> e.cod_int_mora,
      "cod_costas" -> e.cod_costas,
      "cod_otras_cxc" -> e.cod_otras_cxc,
      "cod_prov_capital" -> e.cod_prov_capital,
      "cod_prov_interes" -> e.cod_prov_interes,
      "cod_prov_costas" -> e.cod_prov_costas,
      "cod_capital_cas" -> e.cod_capital_cas,
      "cod_cxc_cas" -> e.cod_cxc_cas,
      "cod_costas_cas" -> e.cod_costas_cas,
      "cod_contingencia" -> e.cod_contingencia,
      "cod_iprov_capital" -> e.cod_iprov_capital,
      "cod_eprov_capital" -> e.cod_eprov_capital
    )
  }

  implicit val rReads: Reads[CodigoPucColocacion] = (
      (__ \ "id_clasificacion").readNullable[Int] and
      (__ \ "id_garantia").readNullable[Int] and
      (__ \ "id_categoria").readNullable[String] and
      (__ \ "dias_iniciales").readNullable[Int] and
      (__ \ "dias_finales").readNullable[Int] and
      (__ \ "cod_capital_cp").readNullable[String] and
      (__ \ "cod_capital_lp").readNullable[String] and
      (__ \ "cod_int_ant").readNullable[String] and
      (__ \ "cod_cxc").readNullable[String] and
      (__ \ "cod_int_mes").readNullable[String] and
      (__ \ "cod_int_mora").readNullable[String] and
      (__ \ "cod_costas").readNullable[String] and
      (__ \ "cod_otras_cxc").readNullable[String] and
      (__ \ "cod_prov_capital").readNullable[String] and
      (__ \ "cod_prov_interes").readNullable[String] and
      (__ \ "cod_prov_costas").readNullable[String] and
      (__ \ "cod_capital_cas").readNullable[String] and
      (__ \ "cod_cxc_cas").readNullable[String] and
      (__ \ "cod_costas_cas").readNullable[String] and
      (__ \ "cod_contingencia").readNullable[String] and
      (__ \ "cod_iprov_capital").readNullable[String] and
      (__ \ "cod_eprov_capital").readNullable[String]
  )(CodigoPucColocacion.apply _)

  val _set = {
    get[Option[Int]]("id_clasificacion") ~
      get[Option[Int]]("id_garantia") ~
      get[Option[String]]("id_categoria") ~
      get[Option[Int]]("dias_iniciales") ~
      get[Option[Int]]("dias_finales") ~
      get[Option[String]]("cod_capital_cp") ~
      get[Option[String]]("cod_capital_lp") ~
      get[Option[String]]("cod_int_ant") ~
      get[Option[String]]("cod_cxc") ~
      get[Option[String]]("cod_int_mes") ~
      get[Option[String]]("cod_int_mora") ~
      get[Option[String]]("cod_costas") ~
      get[Option[String]]("cod_otras_cxc") ~
      get[Option[String]]("cod_prov_capital") ~
      get[Option[String]]("cod_prov_interes") ~
      get[Option[String]]("cod_prov_costas") ~
      get[Option[String]]("cod_capital_cas") ~
      get[Option[String]]("cod_cxc_cas") ~
      get[Option[String]]("cod_costas_cas") ~
      get[Option[String]]("cod_contingencia") ~
      get[Option[String]]("cod_iprov_capital") ~
      get[Option[String]]("cod_eprov_capital") map {
      case id_clasificacion ~
            id_garantia ~
            id_categoria ~
            dias_iniciales ~
            dias_finales ~
            cod_capital_cp ~
            cod_capital_lp ~
            cod_int_ant ~
            cod_cxc ~
            cod_int_mes ~
            cod_int_mora ~
            cod_costas ~
            cod_otras_cxc ~
            cod_prov_capital ~
            cod_prov_interes ~
            cod_prov_costas ~
            cod_capital_cas ~
            cod_cxc_cas ~
            cod_costas_cas ~
            cod_contingencia ~
            cod_iprov_capital ~
            cod_eprov_capital =>
        CodigoPucColocacion(
            id_clasificacion,
            id_garantia,
            id_categoria,
            dias_iniciales,
            dias_finales,
            cod_capital_cp,
            cod_capital_lp,
            cod_int_ant,
            cod_cxc,
            cod_int_mes,
            cod_int_mora,
            cod_costas,
            cod_otras_cxc,
            cod_prov_capital,
            cod_prov_interes,
            cod_prov_costas,
            cod_capital_cas,
            cod_cxc_cas,
            cod_costas_cas,
            cod_contingencia,
            cod_iprov_capital,
            cod_eprov_capital
        )
    }
  }    
}

case class CodigoPucBasico(
  id_clasificacion: Option[Int],
  id_garantia: Option[Int],
  id_categoria: Option[String],
  dias_iniciales: Option[Int],
  dias_finales: Option[Int],
  cod_int_ant: Option[String],
  cod_int_mes: Option[String],
  cod_int_mora: Option[String]
)

object CodigoPucBasico {

    val _set = {
      get[Option[Int]]("id_clasificacion") ~
      get[Option[Int]]("id_garantia") ~
      get[Option[String]]("id_categoria") ~
      get[Option[Int]]("dias_iniciales") ~
      get[Option[Int]]("dias_finales") ~
      get[Option[String]]("cod_int_ant") ~
      get[Option[String]]("cod_int_mes") ~
      get[Option[String]]("cod_int_mora") map {
      case id_clasificacion ~
            id_garantia ~
            id_categoria ~
            dias_iniciales ~
            dias_finales ~
            cod_int_ant ~
            cod_int_mes ~
            cod_int_mora => CodigoPucBasico(
                id_clasificacion,
                id_garantia,
                id_categoria,
                dias_iniciales,
                dias_finales,
                cod_int_ant,
                cod_int_mes,
                cod_int_mora 
            )
      }
    }
}

class CodigoPucColocacionRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")
  
  def obtener(id_clasificacion: Int, id_garantia: Int, id_categoria: String): CodigoPucColocacion = {
    db.withConnection { implicit connection => 
                        SQL("""SELECT * FROM "col$codigospuc" c WHERE 
                                c.ID_CLASIFICACION = {id_clasificacion} and 
                                c.ID_GARANTIA = {id_garantia} and
                                c.ID_CATEGORIA = {id_categoria}""").
                            on(
                                'id_clasificacion -> id_clasificacion,
                                'id_garantia -> id_garantia,
                                'id_categoria -> id_categoria
                            ).as(CodigoPucColocacion._set.single)
    }
  }

  def obtenerFacturado(id_clasificacion: Int, id_garantia: Int, id_categoria: String): CodigoPucBasico = {
    db.withConnection { implicit connection => 
                        SQL("""SELECT * FROM "col$codigospucfacturado" c WHERE 
                                c.ID_CLASIFICACION = {id_clasificacion} and 
                                c.ID_GARANTIA = {id_garantia} and
                                c.ID_CATEGORIA = {id_categoria}""").
                            on(
                                'id_clasificacion -> id_clasificacion,
                                'id_garantia -> id_garantia,
                                'id_categoria -> id_categoria
                            ).as(CodigoPucBasico._set.single)
    }
  }


}