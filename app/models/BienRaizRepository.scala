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

case class BienRaiz(
    id_identificacion: Option[Int],
    id_persona: Option[String],
    id_solicitud: Option[String],
    descripcion_bien: Option[String],
    barrio: Option[String],
    direccion: Option[String],
    ciudad: Option[String],
    valor_comercial: Option[BigDecimal],
    es_hipoteca: Option[Int],
    afavorde: Option[String],
    escritura: Option[String],
    notaria: Option[String],
    matricula: Option[String],
    fecha_escritura: Option[DateTime],
    es_informacion: Option[Int],
    es_garantiareal: Option[Int],
    es_garantiapersonal: Option[Int],
    consecutivo: Option[Int]
)

object BienRaiz {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wWrites = new Writes[BienRaiz] {
    def writes(e: BienRaiz) = Json.obj(
      "id_identificacion" -> e.id_identificacion,
      "id_persona" -> e.id_persona,
      "id_solicitud" -> e.id_solicitud,
      "descripcion_bien" -> e.descripcion_bien,
      "barrio" -> e.barrio,
      "direccion" -> e.direccion,
      "ciudad" -> e.ciudad,
      "valor_comercial" -> e.valor_comercial,
      "es_hipoteca" -> e.es_hipoteca,
      "afavorde" -> e.afavorde,
      "escritura" -> e.escritura,
      "notaria" -> e.notaria,
      "matricula" -> e.matricula,
      "fecha_escritura" -> e.fecha_escritura,
      "es_informacion" -> e.es_informacion,
      "es_garantiareal" -> e.es_garantiareal,
      "es_garantiapersonal" -> e.es_garantiapersonal,
      "consecutivo" -> e.consecutivo
    )
  }

  implicit val rReads: Reads[BienRaiz] = (
    (__ \ "id_identificacion").readNullable[Int] and
      (__ \ "id_persona").readNullable[String] and
      (__ \ "id_solicitud").readNullable[String] and
      (__ \ "descripcion_bien").readNullable[String] and
      (__ \ "barrio").readNullable[String] and
      (__ \ "direccion").readNullable[String] and
      (__ \ "ciudad").readNullable[String] and
      (__ \ "valor_comercial").readNullable[BigDecimal] and
      (__ \ "es_hipoteca").readNullable[Int] and
      (__ \ "afavorde").readNullable[String] and
      (__ \ "escritura").readNullable[String] and
      (__ \ "notaria").readNullable[String] and
      (__ \ "matricula").readNullable[String] and
      (__ \ "fecha_escritura").readNullable[DateTime] and
      (__ \ "es_informacion").readNullable[Int] and
      (__ \ "es_garantiareal").readNullable[Int] and
      (__ \ "es_garantiapersonal").readNullable[Int] and
      (__ \ "consecutivo").readNullable[Int]
  )(BienRaiz.apply _)

  val _set = {
    get[Option[Int]]("id_identificacion") ~
      get[Option[String]]("id_persona") ~
      get[Option[String]]("id_solicitud") ~
      get[Option[String]]("descripcion_bien") ~
      get[Option[String]]("barrio") ~
      get[Option[String]]("direccion") ~
      get[Option[String]]("ciudad") ~
      get[Option[BigDecimal]]("valor_comercial") ~
      get[Option[Int]]("es_hipoteca") ~
      get[Option[String]]("afavorde") ~
      get[Option[String]]("escritura") ~
      get[Option[String]]("notaria") ~
      get[Option[String]]("matricula") ~
      get[Option[DateTime]]("fecha_escritura") ~
      get[Option[Int]]("es_informacion") ~
      get[Option[Int]]("es_garantiareal") ~
      get[Option[Int]]("es_garantiapersonal") ~
      get[Option[Int]]("consecutivo") map {
      case id_identificacion ~
            id_persona ~
            id_solicitud ~
            descripcion_bien ~
            barrio ~
            direccion ~
            ciudad ~
            valor_comercial ~
            es_hipoteca ~
            afavorde ~
            escritura ~
            notaria ~
            matricula ~
            fecha_escritura ~
            es_informacion ~
            es_garantiareal ~
            es_garantiapersonal ~
            consecutivo =>
        BienRaiz(
          id_identificacion,
          id_persona,
          id_solicitud,
          descripcion_bien,
          barrio,
          direccion,
          ciudad,
          valor_comercial,
          es_hipoteca,
          afavorde,
          escritura,
          notaria,
          matricula,
          fecha_escritura,
          es_informacion,
          es_garantiareal,
          es_garantiapersonal,
          consecutivo
        )
    }
  }
}

class BienRaizRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {
  // conexión a la base de datos
  private val db = dbapi.database("default")

  def buscar(id_solicitud: String): Future[Iterable[BienRaiz]] = {
    val result = db.withConnection { implicit connection =>
      SQL(
        """SELECT * FROM \"gen$bienesraices\" WHERE ID_SOLICITUD = {id_solicitud}"""
      ).on(
          'id_solicitud -> id_solicitud
      ).as(BienRaiz._set *)
    }
    Future.successful(result)
  }

  def guardar(id_solicitud: String, lbr: List[BienRaiz]): Boolean = {
    val result = db.withConnection { implicit connection =>
      SQL(
        """DELETE FROM \"gen$bienesraices\" WHERE ID_SOLICITUD = {id_solicitud}"""
      ).on(
          'id_solicitud -> id_solicitud
        )
        .executeUpdate()
      for (br <- lbr) {
        val actualizar = SQL("""INSERT INTO \"gen$bienesraices\" 
                                    (DESCRIPCION_BIEN,
                                    BARRIO, 
                                    DIRECCION,
                                    CIUDAD,
                                    VALOR_COMERCIAL,
                                    ES_HIPOTECA, 
                                    AFAVORDE,
                                    ID_IDENTIFICACION,
                                    ID_PERSONA,
                                    ESCRITURA, 
                                    NOTARIA,  
                                    MATRICULA,
                                    FECHA_ESCRITURA,
                                    ES_INFORMACION, 
                                    ES_GARANTIAREAL,  
                                    ES_GARANTIAPERSONAL,
                                    ID_SOLICITUD,
                                    CONSECUTIVO
                                    ) VALUES ( 
                                    {descripcion_bien},
                                    {barrio},
                                    {direccion},
                                    {ciudad},
                                    {valor_comercial},
                                    {es_hipoteca},
                                    {afavorde},
                                    {id_identificacion},
                                    {id_persona},
                                    {escritura},
                                    {notaria},
                                    {matricula},
                                    {fecha_escritura},
                                    {es_informacion},
                                    {es_garantiareal}, 
                                    {es_garantiapersonal},
                                    {id_solicitud},
                                    {consecutivo}
                                    )
                """)
          .on(
            'descripcion_bien -> br.descripcion_bien,
            'barrio -> br.barrio,
            'direccion -> br.direccion,
            'ciudad -> br.ciudad,
            'valor_comercial -> br.valor_comercial,
            'es_hipoteca -> br.es_hipoteca,
            'afavorde -> br.afavorde,
            'id_identificacion -> br.id_identificacion,
            'id_persona -> br.id_persona,
            'escritura -> br.escritura,
            'notaria -> br.notaria,
            'matricula -> br.matricula,
            'fecha_escritura -> br.fecha_escritura,
            'es_informacion -> br.es_informacion,
            'es_garantiareal -> br.es_garantiareal,
            'es_garantiapersonal -> br.es_garantiapersonal,
            'id_solicitud -> br.id_solicitud,
            'consecutivo -> br.consecutivo
          )
          .executeUpdate() > 0
      }
      true
    }
    result
  }
}
