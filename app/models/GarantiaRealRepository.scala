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
import scala.collection.mutable

case class GarantiaReal(
  id_agencia: Option[Long],
  matricula: Option[String],
  fecha_matricula: Option[DateTime],
  numero_escritura: Option[String],
  fecha_escritura: Option[DateTime],
  nombre_notaria: Option[String],
  ciudad_escritura: Option[String],
  avaluo: Option[BigDecimal],
  fecha_avaluo: Option[DateTime],
  cuentas_de_orden: Option[BigDecimal],
  poliza_incendio: Option[String],
  valor_asegurado: Option[BigDecimal],
  fecha_inicial_poliza: Option[DateTime],
  fecha_final_poliza: Option[DateTime],
  codigo_aseguradora: Option[String],
  estado: Option[Int],
  modelo_vehiculo: Option[Int],
  marca_vehiculo: Option[String],
  tipo_vehiculo: Option[String],
  linea_vehiculo: Option[String],
  tipo_servicio: Option[String],
  color_vehiculo: Option[String]
)

object GarantiaReal {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val wWrites = new Writes[GarantiaReal] {
    def writes(e: GarantiaReal) = Json.obj(
      "id_agencia" -> e.id_agencia,
      "matricula" -> e.matricula,
      "fecha_matricula" -> e.fecha_matricula,
      "numero_escritura" -> e.numero_escritura,
      "fecha_escritura" -> e.fecha_escritura,
      "nombre_notaria" -> e.nombre_notaria,
      "ciudad_escritura" -> e.ciudad_escritura,
      "avaluo" -> e.avaluo,
      "fecha_avaluo" -> e.fecha_avaluo,
      "cuentas_de_orden" -> e.cuentas_de_orden,
      "poliza_incendio" -> e.poliza_incendio,
      "valor_asegurado" -> e.valor_asegurado,
      "fecha_inicial_poliza" -> e.fecha_inicial_poliza,
      "fecha_final_poliza" -> e.fecha_final_poliza,
      "codigo_aseguradora" -> e.codigo_aseguradora,
      "estado" -> e.estado,
      "modelo_vehiculo" -> e.modelo_vehiculo,
      "marca_vehiculo" -> e.marca_vehiculo,
      "tipo_vehiculo" -> e.tipo_vehiculo,
      "linea_vehiculo" -> e.linea_vehiculo,
      "tipo_servicio" -> e.tipo_servicio,
      "color_vehiculo" -> e.color_vehiculo
    )
  }

  implicit val rReads: Reads[GarantiaReal] = (
    (__ \ "id_agencia").readNullable[Long] and
    (__ \ "matricula").readNullable[String] and 
    (__ \ "fecha_matricula").readNullable[DateTime] and 
    (__ \ "numero_escritura").readNullable[String] and 
    (__ \ "fecha_escritura").readNullable[DateTime] and
    (__ \ "nombre_notaria").readNullable[String] and
    (__ \ "ciudad_escritura").readNullable[String] and
    (__ \ "avaluo").readNullable[BigDecimal] and 
    (__ \ "fecha_avaluo").readNullable[DateTime] and 
    (__ \ "cuentas_de_orden").readNullable[BigDecimal] and 
    (__ \ "poliza_incendio").readNullable[String] and
    (__ \ "valor_asegurado").readNullable[BigDecimal] and
    (__ \ "fecha_inicial_poliza").readNullable[DateTime] and
    (__ \ "fecha_final_poliza").readNullable[DateTime] and 
    (__ \ "codigo_aseguradora").readNullable[String] and 
    (__ \ "estado").readNullable[Int] and 
    (__ \ "modelo_vehiculo").readNullable[Int] and
    (__ \ "marca_vehiculo").readNullable[String] and
    (__ \ "tipo_vehiculo").readNullable[String] and
    (__ \ "linea_vehiculo").readNullable[String] and 
    (__ \ "tipo_servicio").readNullable[String] and 
    (__ \ "color_vehiculo").readNullable[String]        
  )(GarantiaReal.apply _)

  val _set = {
          get[Option[Long]]("id_agencia") ~
          get[Option[String]]("matricula") ~
          get[Option[DateTime]]("fecha_matricula") ~
          get[Option[String]]("numero_escritura") ~
          get[Option[DateTime]]("fecha_escritura") ~
          get[Option[String]]("nombre_notaria") ~
          get[Option[String]]("ciudad_escritura") ~
          get[Option[BigDecimal]]("avaluo") ~
          get[Option[DateTime]]("fecha_avaluo") ~
          get[Option[BigDecimal]]("cuentas_de_orden") ~
          get[Option[String]]("poliza_incendio") ~
          get[Option[BigDecimal]]("valor_asegurado") ~
          get[Option[DateTime]]("fecha_inicial_poliza") ~
          get[Option[DateTime]]("fecha_final_poliza") ~
          get[Option[String]]("codigo_aseguradora") ~
          get[Option[Int]]("estado") ~
          get[Option[Int]]("modelo_vehiculo") ~
          get[Option[String]]("marca_vehiculo") ~
          get[Option[String]]("tipo_vehiculo") ~
          get[Option[String]]("linea_vehiculo") ~
          get[Option[String]]("tipo_servicio") ~
          get[Option[String]]("color_vehiculo") map {
          case 
            id_agencia ~
            matricula             ~
            fecha_matricula       ~
            numero_escritura      ~
            fecha_escritura       ~
            nombre_notaria        ~
            ciudad_escritura      ~
            avaluo                ~
            fecha_avaluo          ~
            cuentas_de_orden      ~
            poliza_incendio       ~
            valor_asegurado       ~
            fecha_inicial_poliza  ~
            fecha_final_poliza    ~
            codigo_aseguradora    ~
            estado                ~
            modelo_vehiculo       ~
            marca_vehiculo        ~
            tipo_vehiculo         ~
            linea_vehiculo        ~
            tipo_servicio         ~
            color_vehiculo        => GarantiaReal(
                    id_agencia            ,
                    matricula             ,
                    fecha_matricula       ,
                    numero_escritura      ,
                    fecha_escritura       ,
                    nombre_notaria        ,
                    ciudad_escritura      ,
                    avaluo                ,
                    fecha_avaluo          ,
                    cuentas_de_orden      ,
                    poliza_incendio       ,
                    valor_asegurado       ,
                    fecha_inicial_poliza  ,
                    fecha_final_poliza    ,
                    codigo_aseguradora    ,
                    estado                ,
                    modelo_vehiculo       ,
                    marca_vehiculo        ,
                    tipo_vehiculo         ,
                    linea_vehiculo        ,
                    tipo_servicio         ,
                    color_vehiculo        ,
                 )
      }
  }  
}

class GarantiaRealRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

    def obtener(id_colocacion: String): Future[Iterable[GarantiaReal]] = Future[Iterable[GarantiaReal]] {
        db.withConnection { implicit connection =>
            SQL("""SELECT * FROM "col$datogarantia" d
                  INNER JOIN "col$garantiacol" g ON (g.ID_AGENCIA = d.ID_AGENCIA) AND (g.MATRICULA = d.MATRICULA)
                  WHERE g.ID_COLOCACION ={id_colocacion}""").
                on(
                    'id_colocacion -> id_colocacion
                ).as(GarantiaReal._set *)
        }
    }

}