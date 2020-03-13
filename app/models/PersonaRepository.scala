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

case class Persona_a(
    id_identificacion: Option[Int], // 1
    id_persona: Option[String], // 2
    lugar_expedicion: Option[String], //3
    fecha_expedicion: Option[DateTime], // 4
    nombre: Option[String], // 5
    primer_apellido: Option[String], //6
    segundo_apellido: Option[String], // 7
    id_tipo_persona: Option[Int], // 8
    sexo: Option[String], // 9
    fecha_nacimiento: Option[DateTime], // 10
    lugar_nacimiento: Option[String], // 11
    provincia_nacimiento: Option[String], // 12
    depto_nacimiento: Option[String], // 13
    pais_nacimiento: Option[String], // 14
    id_tipo_estado_civil: Option[Int], // 15
    id_conyuge: Option[String], // 16
    id_identificacion_conyuge: Option[Int], // 17
    nombre_conyuge: Option[String], // 18
    primer_apellido_conyuge: Option[String], // 19
    segundo_apellido_conyuge: Option[String] // 20
)

case class Persona_b(
    id_apoderado: Option[String], // 1
    id_identificacion_apoderado: Option[Int], // 2
    nombre_apoderado: Option[String], // 3
    primer_apellido_apoderado: Option[String], // 4
    segundo_apellido_apoderado: Option[String], // 5
    profesion: Option[String], // 6
    id_estado: Option[Int], // 7
    id_tipo_relacion: Option[Int], // 8
    id_ciiu: Option[Int], // 9
    empresa_labora: Option[String], // 10
    fecha_ingreso_empresa: Option[DateTime], // 11
    cargo_actual: Option[String], // 12
    declaracion: Option[String], // 13
    ingresos_a_principal: Option[BigDecimal], // 14
    ingresos_otros: Option[BigDecimal], // 15
    ingresos_conyuge: Option[BigDecimal], // 16
    ingresos_conyuge_otros: Option[BigDecimal], // 17
    desc_ingr_otros: Option[String] // 18
)

case class Persona_c(
    egresos_alquiler: Option[BigDecimal], // 1
    egresos_servicios: Option[BigDecimal], // 2
    egresos_transporte: Option[BigDecimal], // 3
    egresos_alimentacion: Option[BigDecimal], // 4
    egresos_deudas: Option[BigDecimal], // 5
    egresos_otros: Option[BigDecimal], // 6
    desc_egre_otros: Option[String], // 7
    egresos_conyuge: Option[BigDecimal], // 8
    otros_egresos_conyuge: Option[BigDecimal], // 9
    total_activos: Option[BigDecimal], // 10
    total_pasivos: Option[BigDecimal], // 11
    educacion: Option[Int], // 12
    retefuente: Option[Int], // 13
    acta: Option[String], // 14
    fecha_registro: Option[DateTime], // 15
    foto: Option[Array[Byte]], // 16
    firma: Option[Array[Byte]], // 17
    escritura_constitucion: Option[String] // 18
)

case class Persona_d(
    duracion_sociedad: Option[Int], // 1
    capital_social: Option[BigDecimal], // 2
    matricula_mercantil: Option[String], // 3
    foto_huella: Option[Array[Byte]], // 4
    datos_huella: Option[Array[Byte]], // 5
    email: Option[String], // 6
    id_empleado: Option[String], // 7
    fecha_actualizacion: Option[DateTime] // 8
)

case class Persona_e(
    numero_hijos: Option[Int],
    id_ocupacion: Option[Int],
    id_tipocontrato: Option[Int],
    descripcion_contrato: Option[String],
    id_sector: Option[Int],
    descripcion_sector: Option[String],
    venta_anual: Option[BigDecimal],
    fecha_ultimo_balance: Option[DateTime],
    numero_empleados: Option[Int],
    declara_renta: Option[Int],
    personas_a_cargo: Option[Int],
    id_estrato: Option[Int],
    cabezafamilia: Option[Int],
    id_estudio: Option[Int],
    id_tipovivienda: Option[Int]
)

case class Persona_f(
    es_proveedor: Option[Int],
    numero_rut: Option[String]
)

case class Direccion(
    consecutivo: Option[Int],
    id_direccion: Option[scala.Long],
    direccion: Option[String],
    barrio: Option[String],
    cod_municipio: Option[scala.Long],
    municipio: Option[String],
    telefono1: Option[String],
    telefono2: Option[String],
    telefono3: Option[String],
    telefono4: Option[String]
)

case class Referencia(
    consecutivo_referencia: Option[Int],
    primer_apellido_referencia: Option[String],
    segundo_apellido_referencia: Option[String],
    nombre_referencia: Option[String],
    direccion_referencia: Option[String],
    telefono_referencia: Option[String],
    tipo_referencia: Option[scala.Long],
    parentesco_referencia: Option[scala.Long]
)

case class Beneficiario(consecutivo: Option[Int],
                        primer_apellido: Option[String],
                        segundo_apellido: Option[String],
                        nombre: Option[String],
                        id_parentesco: Option[scala.Long],
                        porcentaje: Option[Double],
                        auxilio: Option[Int]
                       )

case class Hijo(consecutivo_hijo: Option[Int],
                primer_apellido: Option[String],
                segundo_apellido: Option[String],
                nombre: Option[String],
                fecha_nacimiento: Option[DateTime]  
               )

case class Persona(
    a: Option[Persona_a],
    b: Option[Persona_b],
    c: Option[Persona_c],
    d: Option[Persona_d],
    e: Option[Persona_e],
    f: Option[Persona_f],
    direcciones: List[Direccion],
    referencias: List[Referencia],
    beneficiarios: List[Beneficiario],
    hijos: List[Hijo]
)

object Hijo {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

  implicit val dWrites = new Writes[Hijo] {
    def writes(d: Hijo) = Json.obj(
      "consecutivo_hijo" -> d.consecutivo_hijo,
      "primer_apellido" -> d.primer_apellido,
      "segundo_apellido" -> d.segundo_apellido,
      "nombre" -> d.nombre,
      "fecha_nacimiento" -> d.fecha_nacimiento
    )
  }

  implicit val dReads: Reads[Hijo] = (
    (__ \ "consecutivo_hijo").readNullable[Int] and
      (__ \ "primer_apellido").readNullable[String] and
      (__ \ "segundo_apellido").readNullable[String] and
      (__ \ "nombre").readNullable[String] and
      (__ \ "fecha_nacimiento").readNullable[DateTime]
  )(Hijo.apply _)

  val _set = {
    get[Option[Int]]("consecutivo_hijo") ~
      get[Option[String]]("primer_apellido") ~
      get[Option[String]]("segundo_apellido") ~
      get[Option[String]]("nombre") ~
      get[Option[DateTime]]("fecha_nacimiento") map {
      case 
      consecutivo_hijo ~
      primer_apellido ~
      segundo_apellido ~
      nombre ~
      fecha_nacimiento =>
        Hijo(
          consecutivo_hijo,
          primer_apellido,
          segundo_apellido,
          nombre,
          fecha_nacimiento
        )
    }
  }
}

object Beneficiario {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

  implicit val dWrites = new Writes[Beneficiario] {
    def writes(d: Beneficiario) = Json.obj(
      "consecutivo" -> d.consecutivo,
      "primer_apellido" -> d.primer_apellido,
      "segundo_apellido" -> d.segundo_apellido,
      "nombre" -> d.nombre,
      "id_parentesco" -> d.id_parentesco,
      "porcentaje" -> d.porcentaje,
      "auxilio" -> d.auxilio,
    )
  }

  implicit val dReads: Reads[Beneficiario] = (
    (__ \ "consecutivo").readNullable[Int] and
      (__ \ "primer_apellido").readNullable[String] and
      (__ \ "segundo_apellido").readNullable[String] and
      (__ \ "nombre").readNullable[String] and
      (__ \ "id_parentesco").readNullable[scala.Long] and
      (__ \ "porcentaje").readNullable[Double] and
      (__ \ "auxilio").readNullable[Int]
  )(Beneficiario.apply _)

  val _set = {
    get[Option[Int]]("consecutivo") ~
      get[Option[String]]("primer_apellido") ~
      get[Option[String]]("segundo_apellido") ~
      get[Option[String]]("nombre") ~
      get[Option[scala.Long]]("id_parentesco") ~
      get[Option[Double]]("porcentaje") ~
      get[Option[Int]]("auxilio") map {
      case 
      consecutivo ~
      primer_apellido ~
      segundo_apellido ~
      nombre ~
      id_parentesco ~
      porcentaje ~
      auxilio =>
        Beneficiario(
          consecutivo,
          primer_apellido,
          segundo_apellido,
          nombre,
          id_parentesco,
          porcentaje,
          auxilio
        )
    }
  }  
}



object Referencia {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

  implicit val dWrites = new Writes[Referencia] {
    def writes(d: Referencia) = Json.obj(
      "consecutivo_referencia" -> d.consecutivo_referencia,
      "primer_apellido_referencia" -> d.primer_apellido_referencia,
      "segundo_apellido_referencia" -> d.segundo_apellido_referencia,
      "nombre_referencia" -> d.nombre_referencia,
      "direccion_referencia" -> d.direccion_referencia,
      "telefono_referencia" -> d.telefono_referencia,
      "tipo_referencia" -> d.tipo_referencia,
      "parentesco_referencia" -> d.parentesco_referencia
    )
  }

  implicit val dReads: Reads[Referencia] = (
    (__ \ "consecutivo_referencia").readNullable[Int] and
      (__ \ "primer_apellido_referencia").readNullable[String] and
      (__ \ "segundo_apellido_referencia").readNullable[String] and
      (__ \ "nombre_referencia").readNullable[String] and
      (__ \ "direccion_referencia").readNullable[String] and
      (__ \ "telefono_referencia").readNullable[String] and
      (__ \ "tipo_referencia").readNullable[scala.Long] and
      (__ \ "parentesco_referencia").readNullable[scala.Long]
  )(Referencia.apply _)

  val _set = {
    get[Option[Int]]("consecutivo_referencia") ~
      get[Option[String]]("primer_apellido_referencia") ~
      get[Option[String]]("segundo_apellido_referencia") ~
      get[Option[String]]("nombre_referencia") ~
      get[Option[String]]("direccion_referencia") ~
      get[Option[String]]("telefono_referencia") ~
      get[Option[scala.Long]]("tipo_referencia") ~
      get[Option[scala.Long]]("parentesco_referencia") map {
      case 
      consecutivo_referencia ~
      primer_apellido_referencia ~
      segundo_apellido_referencia ~
      nombre_referencia ~
      direccion_referencia ~
      telefono_referencia ~
      tipo_referencia ~
      parentesco_referencia =>
        Referencia(
          consecutivo_referencia,
          primer_apellido_referencia,
          segundo_apellido_referencia,
          nombre_referencia,
          direccion_referencia,
          telefono_referencia,
          tipo_referencia,
          parentesco_referencia
        )
    }
  }
}

object Direccion {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

  implicit val dWrites = new Writes[Direccion] {
    def writes(d: Direccion) = Json.obj(
      "consecutivo" -> d.consecutivo,
      "id_direccion" -> d.id_direccion,
      "direccion" -> d.direccion,
      "barrio" -> d.barrio,
      "cod_municipio" -> d.cod_municipio,
      "municipio" -> d.municipio,
      "telefono1" -> d.telefono1,
      "telefono2" -> d.telefono2,
      "telefono3" -> d.telefono3,
      "telefono4" -> d.telefono4
    )
  }

  implicit val dReads: Reads[Direccion] = (
    (__ \ "consecutivo").readNullable[Int] and
      (__ \ "id_direccion").readNullable[scala.Long] and
      (__ \ "direccion").readNullable[String] and
      (__ \ "barrio").readNullable[String] and
      (__ \ "cod_municipio").readNullable[scala.Long] and
      (__ \ "municipio").readNullable[String] and
      (__ \ "telefono1").readNullable[String] and
      (__ \ "telefono2").readNullable[String] and
      (__ \ "telefono3").readNullable[String] and
      (__ \ "telefono4").readNullable[String]
  )(Direccion.apply _)

  val _set = {
    get[Option[Int]]("consecutivo") ~
      get[Option[scala.Long]]("id_direccion") ~
      get[Option[String]]("direccion") ~
      get[Option[String]]("barrio") ~
      get[Option[scala.Long]]("cod_municipio") ~
      get[Option[String]]("municipio") ~
      get[Option[String]]("telefono1") ~
      get[Option[String]]("telefono2") ~
      get[Option[String]]("telefono3") ~
      get[Option[String]]("telefono4") map {
      case consecutivo ~
            id_direccion ~
            direccion ~
            barrio ~
            cod_municipio ~
            municipio ~
            telefono1 ~
            telefono2 ~
            telefono3 ~
            telefono4 =>
        Direccion(
          consecutivo,
          id_direccion,
          direccion,
          barrio,
          cod_municipio,
          municipio,
          telefono1,
          telefono2,
          telefono3,
          telefono4
        )
    }
  }
}

object Persona_a {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val paWrites = new Writes[Persona_a] {
    def writes(pa: Persona_a) = Json.obj(
      "id_identificacion" -> pa.id_identificacion,
      "id_persona" -> pa.id_persona,
      "lugar_expedicion" -> pa.lugar_expedicion,
      "fecha_expedicion" -> pa.fecha_expedicion,
      "nombre" -> pa.nombre,
      "primer_apellido" -> pa.primer_apellido,
      "segundo_apellido" -> pa.segundo_apellido,
      "id_tipo_persona" -> pa.id_tipo_persona,
      "sexo" -> pa.sexo,
      "fecha_nacimiento" -> pa.fecha_nacimiento,
      "lugar_nacimiento" -> pa.lugar_nacimiento,
      "provincia_nacimiento" -> pa.provincia_nacimiento,
      "depto_nacimiento" -> pa.depto_nacimiento,
      "pais_nacimiento" -> pa.pais_nacimiento,
      "id_tipo_estado_civil" -> pa.id_tipo_estado_civil,
      "id_conyuge" -> pa.id_conyuge,
      "id_identificacion_conyuge" -> pa.id_identificacion_conyuge,
      "nombre_conyuge" -> pa.nombre_conyuge,
      "primer_apellido_conyuge" -> pa.primer_apellido_conyuge,
      "segundo_apellido_conyuge" -> pa.segundo_apellido_conyuge
    )
  }

  implicit val paReads: Reads[Persona_a] = (
    (__ \ "id_identificacion").readNullable[Int] and
      (__ \ "id_persona").readNullable[String] and
      (__ \ "lugar_expedicion").readNullable[String] and
      (__ \ "fecha_expedicion").readNullable[DateTime] and
      (__ \ "nombre").readNullable[String] and
      (__ \ "primer_apellido").readNullable[String] and
      (__ \ "segundo_apellido").readNullable[String] and
      (__ \ "id_tipo_persona").readNullable[Int] and
      (__ \ "sexo").readNullable[String] and
      (__ \ "fecha_nacimiento").readNullable[DateTime] and
      (__ \ "lugar_nacimiento").readNullable[String] and
      (__ \ "provincia_nacimiento").readNullable[String] and
      (__ \ "depto_nacimiento").readNullable[String] and
      (__ \ "pais_nacimiento").readNullable[String] and
      (__ \ "id_tipo_estado_civil").readNullable[Int] and
      (__ \ "id_conyuge").readNullable[String] and
      (__ \ "id_identificacion_conyuge").readNullable[Int] and
      (__ \ "nombre_conyuge").readNullable[String] and
      (__ \ "primer_apellido_conyuge").readNullable[String] and
      (__ \ "segundo_apellido_conyuge").readNullable[String]
  )(Persona_a.apply _)

  val _set = {
    get[Option[Int]]("id_identificacion") ~
      get[Option[String]]("id_persona") ~
      get[Option[String]]("lugar_expedicion") ~
      get[Option[DateTime]]("fecha_expedicion") ~
      get[Option[String]]("nombre") ~
      get[Option[String]]("primer_apellido") ~
      get[Option[String]]("segundo_apellido") ~
      get[Option[Int]]("id_tipo_persona") ~
      get[Option[String]]("sexo") ~
      get[Option[DateTime]]("fecha_nacimiento") ~
      get[Option[String]]("lugar_nacimiento") ~
      get[Option[String]]("provincia_nacimiento") ~
      get[Option[String]]("depto_nacimiento") ~
      get[Option[String]]("pais_nacimiento") ~
      get[Option[Int]]("id_tipo_estado_civil") ~
      get[Option[String]]("id_conyuge") ~
      get[Option[Int]]("id_identificacion_conyuge") ~
      get[Option[String]]("nombre_conyuge") ~
      get[Option[String]]("primer_apellido_conyuge") ~
      get[Option[String]]("segundo_apellido_conyuge") map {
      case id_identificacion ~
            id_persona ~
            lugar_expedicion ~
            fecha_expedicion ~
            nombre ~
            primer_apellido ~
            segundo_apellido ~
            id_tipo_persona ~
            sexo ~
            fecha_nacimiento ~
            lugar_nacimiento ~
            provincia_nacimiento ~
            depto_nacimiento ~
            pais_nacimiento ~
            id_tipo_estado_civil ~
            id_conyuge ~
            id_identificacion_conyuge ~
            nombre_conyuge ~
            primer_apellido_conyuge ~
            segundo_apellido_conyuge =>
        Persona_a(
          id_identificacion,
          id_persona,
          lugar_expedicion,
          fecha_expedicion,
          nombre,
          primer_apellido,
          segundo_apellido,
          id_tipo_persona,
          sexo,
          fecha_nacimiento,
          lugar_nacimiento,
          provincia_nacimiento,
          depto_nacimiento,
          pais_nacimiento,
          id_tipo_estado_civil,
          id_conyuge,
          id_identificacion_conyuge,
          nombre_conyuge,
          primer_apellido_conyuge,
          segundo_apellido_conyuge
        )
    }
  }

}

object Persona_b {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val pbWrites = new Writes[Persona_b] {
    def writes(pb: Persona_b) = Json.obj(
      "id_apoderado" -> pb.id_apoderado,
      "id_identificacion_apoderado" -> pb.id_identificacion_apoderado,
      "nombre_apoderado" -> pb.nombre_apoderado,
      "primer_apellido_apoderado" -> pb.primer_apellido_apoderado,
      "segundo_apellido_apoderado" -> pb.segundo_apellido_apoderado,
      "profesion" -> pb.profesion,
      "id_estado" -> pb.id_estado,
      "id_tipo_relacion" -> pb.id_tipo_relacion,
      "id_ciiu" -> pb.id_ciiu,
      "empresa_labora" -> pb.empresa_labora,
      "fecha_ingreso_empresa" -> pb.fecha_ingreso_empresa,
      "cargo_actual" -> pb.cargo_actual,
      "declaracion" -> pb.declaracion,
      "ingresos_a_principal" -> pb.ingresos_a_principal,
      "ingresos_otros" -> pb.ingresos_otros,
      "ingresos_conyuge" -> pb.ingresos_conyuge,
      "ingresos_conyuge_otros" -> pb.ingresos_conyuge_otros,
      "desc_ingr_otros" -> pb.desc_ingr_otros
    )
  }

  implicit val pbReads: Reads[Persona_b] = (
    (__ \ "id_apoderado").readNullable[String] and
      (__ \ "id_identificacion_apoderado").readNullable[Int] and
      (__ \ "nombre_apoderado").readNullable[String] and
      (__ \ "primer_apellido_apoderado").readNullable[String] and
      (__ \ "segundo_apellido_apoderado").readNullable[String] and
      (__ \ "profesion").readNullable[String] and
      (__ \ "id_estado").readNullable[Int] and
      (__ \ "id_tipo_relacion").readNullable[Int] and
      (__ \ "id_ciiu").readNullable[Int] and
      (__ \ "empresa_labora").readNullable[String] and
      (__ \ "fecha_ingreso_empresa").readNullable[DateTime] and
      (__ \ "cargo_actual").readNullable[String] and
      (__ \ "declaracion").readNullable[String] and
      (__ \ "ingresos_a_principal").readNullable[BigDecimal] and
      (__ \ "ingresos_otros").readNullable[BigDecimal] and
      (__ \ "ingresos_conyuge").readNullable[BigDecimal] and
      (__ \ "ingresos_conyuge_otros").readNullable[BigDecimal] and
      (__ \ "desc_ingr_otros").readNullable[String]
  )(Persona_b.apply _)

  val _set = {
    get[Option[String]]("id_apoderado") ~
      get[Option[Int]]("id_identificacion_apoderado") ~
      get[Option[String]]("nombre_apoderado") ~
      get[Option[String]]("primer_apellido_apoderado") ~
      get[Option[String]]("segundo_apellido_apoderado") ~
      get[Option[String]]("profesion") ~
      get[Option[Int]]("id_estado") ~
      get[Option[Int]]("id_tipo_relacion") ~
      get[Option[Int]]("id_ciiu") ~
      get[Option[String]]("empresa_labora") ~
      get[Option[DateTime]]("fecha_ingreso_empresa") ~
      get[Option[String]]("cargo_actual") ~
      get[Option[String]]("declaracion") ~
      get[Option[BigDecimal]]("ingresos_a_principal") ~
      get[Option[BigDecimal]]("ingresos_otros") ~
      get[Option[BigDecimal]]("ingresos_conyuge") ~
      get[Option[BigDecimal]]("ingresos_conyuge_otros") ~
      get[Option[String]]("desc_ingr_otros") map {
      case id_apoderado ~
            id_identificacion_apoderado ~
            nombre_apoderado ~
            primer_apellido_apoderado ~
            segundo_apellido_apoderado ~
            profesion ~
            id_estado ~
            id_tipo_relacion ~
            id_ciiu ~
            empresa_labora ~
            fecha_ingreso_empresa ~
            cargo_actual ~
            declaracion ~
            ingresos_a_principal ~
            ingresos_otros ~
            ingresos_conyuge ~
            ingresos_conyuge_otros ~
            desc_ingr_otros =>
        Persona_b(
          id_apoderado,
          id_identificacion_apoderado,
          nombre_apoderado,
          primer_apellido_apoderado,
          segundo_apellido_apoderado,
          profesion,
          id_estado,
          id_tipo_relacion,
          id_ciiu,
          empresa_labora,
          fecha_ingreso_empresa,
          cargo_actual,
          declaracion,
          ingresos_a_principal,
          ingresos_otros,
          ingresos_conyuge,
          ingresos_conyuge_otros,
          desc_ingr_otros
        )
    }
  }
}

object Persona_c {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val pbWrites = new Writes[Persona_c] {
    def writes(pc: Persona_c) = Json.obj(
      "egresos_alquiler" -> pc.egresos_alquiler,
      "egresos_servicios" -> pc.egresos_servicios,
      "egresos_transporte" -> pc.egresos_transporte,
      "egresos_alimentacion" -> pc.egresos_alimentacion,
      "egresos_deudas" -> pc.egresos_deudas,
      "egresos_otros" -> pc.egresos_otros,
      "desc_egre_otros" -> pc.desc_egre_otros,
      "egresos_conyuge" -> pc.egresos_conyuge,
      "otros_egresos_conyuge" -> pc.otros_egresos_conyuge,
      "total_activos" -> pc.total_activos,
      "total_pasivos" -> pc.total_pasivos,
      "educacion" -> pc.educacion,
      "retefuente" -> pc.retefuente,
      "acta" -> pc.acta,
      "fecha_registro" -> pc.fecha_registro,
      "foto" -> pc.foto,
      "firma" -> pc.firma,
      "escritura_constitucion" -> pc.escritura_constitucion
    )
  }

  implicit val pbReads: Reads[Persona_c] = (
    (__ \ "egresos_alquiler").readNullable[BigDecimal] and
      (__ \ "egresos_servicios").readNullable[BigDecimal] and
      (__ \ "egresos_transporte").readNullable[BigDecimal] and
      (__ \ "egresos_alimentacion").readNullable[BigDecimal] and
      (__ \ "egresos_deudas").readNullable[BigDecimal] and
      (__ \ "egresos_otros").readNullable[BigDecimal] and
      (__ \ "desc_egre_otros").readNullable[String] and
      (__ \ "egresos_conyuge").readNullable[BigDecimal] and
      (__ \ "otros_egresos_conyuge").readNullable[BigDecimal] and
      (__ \ "total_activos").readNullable[BigDecimal] and
      (__ \ "total_pasivos").readNullable[BigDecimal] and
      (__ \ "educacion").readNullable[Int] and
      (__ \ "retefuente").readNullable[Int] and
      (__ \ "acta").readNullable[String] and
      (__ \ "fecha_registro").readNullable[DateTime] and
      (__ \ "foto").readNullable[Array[Byte]] and
      (__ \ "firma").readNullable[Array[Byte]] and
      (__ \ "escritura_constitucion").readNullable[String]
  )(Persona_c.apply _)

  val _set = {
    get[Option[BigDecimal]]("egresos_alquiler") ~
      get[Option[BigDecimal]]("egresos_servicios") ~
      get[Option[BigDecimal]]("egresos_transporte") ~
      get[Option[BigDecimal]]("egresos_alimentacion") ~
      get[Option[BigDecimal]]("egresos_deudas") ~
      get[Option[BigDecimal]]("egresos_otros") ~
      get[Option[String]]("desc_egre_otros") ~
      get[Option[BigDecimal]]("egresos_conyuge") ~
      get[Option[BigDecimal]]("otros_egresos_conyuge") ~
      get[Option[BigDecimal]]("total_activos") ~
      get[Option[BigDecimal]]("total_pasivos") ~
      get[Option[Int]]("educacion") ~
      get[Option[Int]]("retefuente") ~
      get[Option[String]]("acta") ~
      get[Option[DateTime]]("fecha_registro") ~
      get[Option[Array[Byte]]]("foto") ~
      get[Option[Array[Byte]]]("firma") ~
      get[Option[String]]("escritura_constitucion") map {
      case egresos_alquiler ~
            egresos_servicios ~
            egresos_transporte ~
            egresos_alimentacion ~
            egresos_deudas ~
            egresos_otros ~
            desc_egre_otros ~
            egresos_conyuge ~
            otros_egresos_conyuge ~
            total_activos ~
            total_pasivos ~
            educacion ~
            retefuente ~
            acta ~
            fecha_registro ~
            foto ~
            firma ~
            escritura_constitucion =>
        Persona_c(
          egresos_alquiler,
          egresos_servicios,
          egresos_transporte,
          egresos_alimentacion,
          egresos_deudas,
          egresos_otros,
          desc_egre_otros,
          egresos_conyuge,
          otros_egresos_conyuge,
          total_activos,
          total_pasivos,
          educacion,
          retefuente,
          acta,
          fecha_registro,
          foto,
          firma,
          escritura_constitucion
        )
    }
  }
}

object Persona_d {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val pbWrites = new Writes[Persona_d] {
    def writes(pd: Persona_d) = Json.obj(
      "duracion_sociedad" -> pd.duracion_sociedad,
      "capital_social" -> pd.capital_social,
      "matricula_mercantil" -> pd.matricula_mercantil,
      "foto_huella" -> pd.foto_huella,
      "datos_huella" -> pd.datos_huella,
      "email" -> pd.email,
      "id_empleado" -> pd.id_empleado,
      "fecha_actualizacion" -> pd.fecha_actualizacion
    )
  }

  implicit val pbReads: Reads[Persona_d] = (
    (__ \ "duracion_sociedad").readNullable[Int] and
      (__ \ "capital_social").readNullable[BigDecimal] and
      (__ \ "matricula_mercantil").readNullable[String] and
      (__ \ "foto_huella").readNullable[Array[Byte]] and
      (__ \ "datos_huella").readNullable[Array[Byte]] and
      (__ \ "email").readNullable[String] and
      (__ \ "id_empleado").readNullable[String] and
      (__ \ "fecha_actualizacion").readNullable[DateTime]
  )(Persona_d.apply _)

  val _set = {
    get[Option[Int]]("duracion_sociedad") ~
      get[Option[BigDecimal]]("capital_social") ~
      get[Option[String]]("matricula_mercantil") ~
      get[Option[Array[Byte]]]("foto_huella") ~
      get[Option[Array[Byte]]]("datos_huella") ~
      get[Option[String]]("email") ~
      get[Option[String]]("id_empleado") ~
      get[Option[DateTime]]("fecha_actualizacion") map {
      case duracion_sociedad ~
            capital_social ~
            matricula_mercantil ~
            foto_huella ~
            datos_huella ~
            email ~
            id_empleado ~
            fecha_actualizacion =>
        Persona_d(
          duracion_sociedad,
          capital_social,
          matricula_mercantil,
          foto_huella,
          datos_huella,
          email,
          id_empleado,
          fecha_actualizacion
        )
    }
  }
}

object Persona_e {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val peWrites = new Writes[Persona_e] {
    def writes(pe: Persona_e) = Json.obj(
      "numero_hijos" -> pe.numero_hijos,
      "id_ocupacion" -> pe.id_ocupacion,
      "id_tipocontrato" -> pe.id_tipocontrato,
      "descripcion_contrato" -> pe.descripcion_contrato,
      "id_sector" -> pe.id_sector,
      "descripcion_sector" -> pe.descripcion_sector,
      "venta_anual" -> pe.venta_anual,
      "fecha_ultimo_balance" -> pe.fecha_ultimo_balance,
      "numero_empleados" -> pe.numero_empleados,
      "declara_renta" -> pe.declara_renta,
      "personas_a_cargo" -> pe.personas_a_cargo,
      "id_estrato" -> pe.id_estrato,
      "cabezafamilia" -> pe.cabezafamilia,
      "id_estudio" -> pe.id_estudio,
      "id_tipovivienda" -> pe.id_tipovivienda
    )
  }

  implicit val peReads: Reads[Persona_e] = (
    (__ \ "numero_hijos").readNullable[Int] and
      (__ \ "id_ocupacion").readNullable[Int] and
      (__ \ "id_tipocontrato").readNullable[Int] and
      (__ \ "descripcion_contrato").readNullable[String] and
      (__ \ "id_sector").readNullable[Int] and
      (__ \ "descripcion_sector").readNullable[String] and
      (__ \ "venta_anual").readNullable[BigDecimal] and
      (__ \ "fecha_ultimo_balance").readNullable[DateTime] and
      (__ \ "numero_empleados").readNullable[Int] and
      (__ \ "declara_renta").readNullable[Int] and
      (__ \ "personas_a_cargo").readNullable[Int] and
      (__ \ "id_estrato").readNullable[Int] and
      (__ \ "cabezafamilia").readNullable[Int] and
      (__ \ "id_estudio").readNullable[Int] and
      (__ \ "id_tipovivienda").readNullable[Int]
  )(Persona_e.apply _)

  val _set = {
    get[Option[Int]]("numero_hijos") ~
      get[Option[Int]]("id_ocupacion") ~
      get[Option[Int]]("id_tipocontrato") ~
      get[Option[String]]("descripcion_contrato") ~
      get[Option[Int]]("id_sector") ~
      get[Option[String]]("descripcion_sector") ~
      get[Option[BigDecimal]]("venta_anual") ~
      get[Option[DateTime]]("fecha_ultimo_balance") ~
      get[Option[Int]]("numero_empleados") ~
      get[Option[Int]]("declara_renta") ~
      get[Option[Int]]("personas_a_cargo") ~
      get[Option[Int]]("id_estrato") ~
      get[Option[Int]]("cabezafamilia") ~
      get[Option[Int]]("id_estudio") ~
      get[Option[Int]]("id_tipovivienda") map {
      case numero_hijos ~
            id_ocupacion ~
            id_tipo_contrato ~
            descripcion_contrato ~
            id_sector ~
            descripcion_sector ~
            venta_anual ~
            fecha_ultimo_balance ~
            numero_empleados ~
            declara_renta ~
            personas_a_cargo ~
            id_estrato ~
            cabezafamilia ~
            id_estudio ~
            id_tipovivienda =>
        Persona_e(
          numero_hijos,
          id_ocupacion,
          id_tipo_contrato,
          descripcion_contrato,
          id_sector,
          descripcion_sector,
          venta_anual,
          fecha_ultimo_balance,
          numero_empleados,
          declara_renta,
          personas_a_cargo,
          id_estrato,
          cabezafamilia,
          id_estudio,
          id_tipovivienda
        )
    }
  }
}

object Persona_f {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val pbWrites = new Writes[Persona_f] {
    def writes(pd: Persona_f) = Json.obj(
      "es_proveedor" -> pd.es_proveedor,
      "numero_rut" -> pd.numero_rut
    )
  }

  implicit val pbReads: Reads[Persona_f] = (
    (__ \ "es_proveedor").readNullable[Int] and
      (__ \ "numero_rut").readNullable[String]
  )(Persona_f.apply _)

  val _set = {
    get[Option[Int]]("es_proveedor") ~
      get[Option[String]]("numero_rut") map {
      case es_proveedor ~
            numero_rut =>
        Persona_f(es_proveedor, numero_rut)
    }
  }
}

object Persona {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val pWrites = new Writes[Persona] {
    def writes(p: Persona) = Json.obj(
      "a" -> p.a,
      "b" -> p.b,
      "c" -> p.c,
      "d" -> p.d,
      "e" -> p.e,
      "f" -> p.f,
      "direcciones" -> p.direcciones,
      "referencias" -> p.referencias,
      "beneficiarios" -> p.beneficiarios,
      "hijos" -> p.hijos
    )
  }

  implicit val pReads: Reads[Persona] = (
    (__ \ "a").readNullable[Persona_a] and
      (__ \ "b").readNullable[Persona_b] and
      (__ \ "c").readNullable[Persona_c] and
      (__ \ "d").readNullable[Persona_d] and
      (__ \ "e").readNullable[Persona_e] and
      (__ \ "f").readNullable[Persona_f] and
      (__ \ "direcciones").read[List[Direccion]] and
      (__ \ "referencias").read[List[Referencia]] and
      (__ \ "beneficiarios").read[List[Beneficiario]] and
      (__ \ "hijos").read[List[Hijo]]
  )(Persona.apply _)
}

class PersonaRepository @Inject()(dbapi: DBApi)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  /**
    * obtenerListaTipoIdentificacion
    * @return Future[Iterable[TipoIdentificacion]]
    */
  def obtener(
      id_identificacion: Int,
      id_persona: String
  ): Future[Persona] =
    Future[Persona] {
      db.withConnection { implicit connection =>
        val persona_a = SQL(
          """SELECT * FROM \"gen$persona\" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Persona_a._set.singleOpt)

        val persona_b = SQL(
          """SELECT * FROM \"gen$persona\" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Persona_b._set.singleOpt)

        val persona_c = SQL(
          """SELECT * FROM \"gen$persona\" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Persona_c._set.singleOpt)

        val persona_d = SQL(
          """SELECT * FROM \"gen$persona\" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Persona_d._set.singleOpt)

        val persona_e = SQL(
          """SELECT * FROM \"gen$persadicional\" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Persona_e._set.singleOpt)

        val persona_f = SQL(
          """SELECT * FROM \"gen$personaextra\" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Persona_f._set.singleOpt)

        val direcciones = SQL(
          """SELECT d.consecutivo, d.id_direccion, d.direccion, d.barrio, d.cod_municipio, m.NOMBRE AS municipio, d.telefono1, d.telefono2, d.telefono3, d.telefono4 FROM \"gen$direccion\" d 
             INNER JOIN \"gen$municipios" m ON m.COD_MUNICIPIO = d.COD_MUNICIPIO
             WHERE d.ID_IDENTIFICACION = {id_identificacion} AND d.ID_PERSONA = {id_persona}
             ORDER BY d.ID_DIRECCION ASC"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Direccion._set *)

        val referencias = SQL(
          """SELECT * FROM \"gen$referencias\" d WHERE d.TIPO_ID_REFERENCIA = {id_identificacion} AND d.ID_REFERENCIA = {id_persona}"""
        ).on(
              'id_identificacion -> id_identificacion,
              'id_persona -> id_persona
          )
         .as(Referencia._set *)          

        val beneficiarios = SQL(
          """SELECT * FROM \"gen$beneficiario\" b WHERE b.ID_IDENTIFICACION = {id_identificacion} AND b.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Beneficiario._set *)

        val hijos = SQL(
           """SELECT * FROM \"gen$hijo\" h WHERE h.ID_IDENTIFICACION = {id_identificacion} AND h.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
        ).as(Hijo._set *)          

        var persona = new Persona(
          persona_a,
          persona_b,
          persona_c,
          persona_d,
          persona_e,
          persona_f,
          direcciones,
          referencias,
          beneficiarios,
          hijos
        )

        persona
      }
    }

  /**
    * obtenerListaTipoIdentificacion
    * @return Future[Iterable[TipoIdentificacion]]
    */
  def obtenerDirecto(
      id_identificacion: Int,
      id_persona: String
  ): Persona =
    {
      db.withConnection { implicit connection =>
        val persona_a = SQL(
          """SELECT * FROM \"gen$persona\" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Persona_a._set.singleOpt)

        val persona_b = SQL(
          """SELECT * FROM \"gen$persona\" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Persona_b._set.singleOpt)

        val persona_c = SQL(
          """SELECT * FROM \"gen$persona\" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Persona_c._set.singleOpt)

        val persona_d = SQL(
          """SELECT * FROM \"gen$persona\" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Persona_d._set.singleOpt)

        val persona_e = SQL(
          """SELECT * FROM \"gen$persadicional\" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Persona_e._set.singleOpt)

        val persona_f = SQL(
          """SELECT * FROM \"gen$personaextra\" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Persona_f._set.singleOpt)

        val direcciones = SQL(
          """SELECT d.consecutivo, d.id_direccion, d.direccion, d.barrio, d.cod_municipio, m.NOMBRE AS municipio, d.telefono1, d.telefono2, d.telefono3, d.telefono4 FROM \"gen$direccion\" d 
             INNER JOIN \"gen$municipios" m ON m.COD_MUNICIPIO = d.COD_MUNICIPIO
             WHERE d.ID_IDENTIFICACION = {id_identificacion} AND d.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Direccion._set *)

        val referencias = SQL(
          """SELECT * FROM \"gen$referencias\" d WHERE d.TIPO_ID_REFERENCIA = {id_identificacion} AND d.ID_REFERENCIA = {id_persona}"""
        ).on(
              'id_identificacion -> id_identificacion,
              'id_persona -> id_persona
          )
         .as(Referencia._set *)          

        val beneficiarios = SQL(
          """SELECT * FROM \"gen$beneficiario\" b WHERE b.ID_IDENTIFICACION = {id_identificacion} AND b.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Beneficiario._set *)

        val hijos = SQL(
           """SELECT * FROM \"gen$hijo\" h WHERE h.ID_IDENTIFICACION = {id_identificacion} AND h.ID_PERSONA = {id_persona}"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
        ).as(Hijo._set *)          

        var persona = new Persona(
          persona_a,
          persona_b,
          persona_c,
          persona_d,
          persona_e,
          persona_f,
          direcciones,
          referencias,
          beneficiarios,
          hijos
        )

        persona
      }
    }

    def guardar(p: Persona): Future[Boolean] = {
      db.withConnection { implicit connection =>
        var a = p.a.get
        var b = p.b.get
        var c = p.c.get
        var d = p.d.get
        var e = p.e.get
        var f = p.f.get
        var direcciones = p.direcciones
        var referencias = p.referencias
        var beneficiarios = p.beneficiarios
        var hijos = p.hijos

        var insertadoP: Boolean = false
        var actualizadoP: Boolean = false

        actualizadoP = SQL("""
            UPDATE \"gen$persona\" SET 
            LUGAR_EXPEDICION = {lugar_expedicion},
            FECHA_EXPEDICION = {fecha_expedicion},
            NOMBRE = {nombre},
            PRIMER_APELLIDO = {primer_apellido},
            SEGUNDO_APELLIDO = {segundo_apellido},
            ID_TIPO_PERSONA = {id_tipo_persona},
            SEXO = {sexo},
            LUGAR_NACIMIENTO = {lugar_nacimiento},
            PROVINCIA_NACIMIENTO = {provincia_nacimiento},
            DEPTO_NACIMIENTO = {depto_nacimiento},
            PAIS_NACIMIENTO = {pais_nacimiento},
            ID_TIPO_ESTADO_CIVIL = {id_tipo_estado_civil},
            ID_CONYUGE = {id_conyuge},
            ID_IDENTIFICACION_CONYUGE = {id_identificacion_conyuge},
            NOMBRE_CONYUGE = {nombre_conyuge},
            PRIMER_APELLIDO_CONYUGE = {primer_apellido_conyuge},
            SEGUNDO_APELLIDO_CONYUGE = {segundo_apellido_conyuge},
            ID_APODERADO = {id_apoderado},
            ID_IDENTIFICACION_APODERADO =  {id_identificacion_apoderado},
            NOMBRE_APODERADO = {nombre_apoderado},
            PRIMER_APELLIDO_APODERADO = {primer_apellido_apoderado},
            SEGUNDO_APELLIDO_APODERADO = {segundo_apellido_apoderado},
            PROFESION = {profesion},
            ID_ESTADO = {id_estado},
            ID_TIPO_RELACION = {id_tipo_relacion},
            ID_CIIU = {id_ciiu},
            EMPRESA_LABORA = {empresa_labora},
            FECHA_INGRESO_EMPRESA = {fecha_ingreso_empresa},
            CARGO_ACTUAL = {cargo_actual},
            DECLARACION = {declaracion},
            INGRESOS_A_PRINCIPAL = {ingresos_a_principal},
            INGRESOS_OTROS = {ingresos_otros},
            INGRESOS_CONYUGE = {ingresos_conyuge},
            INGRESOS_CONYUGE_OTROS = {ingresos_conyuge_otros},
            DESC_INGR_OTROS = {desc_ingr_otros},
            EGRESOS_ALQUILER = {egresos_alquiler},
            EGRESOS_SERVICIOS = {egresos_servicios},
            EGRESOS_TRANSPORTE = {egresos_transporte},
            EGRESOS_ALIMENTACION = {egresos_alimentacion},
            EGRESOS_DEUDAS = {egresos_deudas},
            EGRESOS_OTROS = {egresos_otros},
            DESC_EGRE_OTROS = {desc_egre_otros},
            EGRESOS_CONYUGE = {egresos_conyuge},
            OTROS_EGRESOS_CONYUGE = {otros_egresos_conyuge},
            TOTAL_ACTIVOS = {total_activos},
            TOTAL_PASIVOS = {total_pasivos},
            EDUCACION = {educacion},
            RETEFUENTE = {retefuente},
            ACTA = {acta},
            FECHA_REGISTRO = {fecha_registro},
            ESCRITURA_CONSTITUCION = {escritura_constitucion},
            DURACION_SOCIEDAD = {duracion_sociedad},
            CAPITAL_SOCIAL = {capital_social},
            MATRICULA_MERCANTIL = {matricula_mercantil},
            EMAIL = {email},
            ID_EMPLEADO = {id_empleado},
            FECHA_ACTUALIZACION = {fecha_actualizacion}
            WHERE
            ID_IDENTIFICACION = {id_identificacion} and
            ID_PERSONA = {id_persona}
        """).on(
          'id_identificacion -> a.id_identificacion,
          'id_persona -> a.id_persona,
          'lugar_expedicion -> a.lugar_expedicion,
          'fecha_expedicion -> a.fecha_expedicion,
          'nombre -> a.nombre,
          'primer_apellido -> a.primer_apellido,
          'segundo_apellido -> a.segundo_apellido,
          'id_tipo_persona -> a.id_tipo_persona,
          'sexo -> a.sexo,
          'lugar_nacimiento -> a.lugar_nacimiento,
          'provincia_nacimiento -> a.provincia_nacimiento,
          'depto_nacimiento -> a.depto_nacimiento,
          'pais_nacimiento -> a.pais_nacimiento,
          'id_tipo_estado_civil -> a.id_tipo_estado_civil,
          'id_conyuge -> a.id_conyuge,
          'id_identificacion_conyuge -> a.id_identificacion_conyuge,
          'nombre_conyuge -> a.nombre_conyuge,
          'primer_apellido_conyuge -> a.primer_apellido_conyuge,
          'segundo_apellido_conyuge -> a.segundo_apellido_conyuge,
          'id_apoderado -> b.id_apoderado,
          'id_identificacion_apoderado -> b.id_identificacion_apoderado,
          'nombre_apoderado -> b.nombre_apoderado,
          'primer_apellido_apoderado -> b.primer_apellido_apoderado,
          'segundo_apellido_apoderado -> b.segundo_apellido_apoderado,          
          'profesion -> b.profesion,
          'id_estado -> b.id_estado,
          'id_tipo_relacion -> b.id_tipo_relacion,
          'id_ciiu -> b.id_ciiu,
          'empresa_labora -> b.empresa_labora,
          'fecha_ingreso_empresa -> b.fecha_ingreso_empresa,
          'cargo_actual -> b.cargo_actual,
          'declaracion -> b.declaracion,
          'ingresos_a_principal -> b.ingresos_a_principal,
          'ingresos_otros -> b.ingresos_otros,
          'ingresos_conyuge -> b.ingresos_conyuge,
          'ingresos_conyuge_otros -> b.ingresos_conyuge_otros,
          'desc_ingr_otros -> b.desc_ingr_otros,
          'egresos_alquiler -> c.egresos_alquiler,
          'egresos_servicios -> c.egresos_servicios,
          'egresos_transporte -> c.egresos_transporte,
          'egresos_alimentacion -> c.egresos_alimentacion,
          'egresos_deudas -> c.egresos_deudas,
          'egresos_otros -> c.egresos_otros,
          'desc_egre_otros -> c.desc_egre_otros,
          'egresos_conyuge -> c.egresos_conyuge,
          'otros_egresos_conyuge -> c.otros_egresos_conyuge,
          'total_activos -> c.total_activos,
          'total_pasivos -> c.total_pasivos,
          'educacion -> c.educacion,
          'retefuente -> c.retefuente,
          'acta -> c.acta,
          'fecha_registro -> c.fecha_registro,
          'escritura_constitucion -> c.escritura_constitucion,
          'duracion_sociedad -> d.duracion_sociedad,
          'capital_social -> d.capital_social,
          'matricula_mercantil -> d.matricula_mercantil,
          'email -> d.email,
          'id_empleado -> d.id_empleado,
          'fecha_actualizacion -> d.fecha_actualizacion
        ).executeUpdate() > 0

        if (!actualizadoP) {
          insertadoP = SQL("""
            INSERT INTO \"gen$persona\" (
              ID_IDENTIFICACION, 
              ID_PERSONA, 
              LUGAR_EXPEDICION,
              FECHA_EXPEDICION,
              NOMBRE,
              PRIMER_APELLIDO,
              SEGUNDO_APELLIDO,
              ID_TIPO_PERSONA,
              SEXO,
              LUGAR_NACIMIENTO,
              PROVINCIA_NACIMIENTO,
              DEPTO_NACIMIENTO,
              PAIS_NACIMIENTO,
              ID_TIPO_ESTADO_CIVIL,
              ID_CONYUGE,
              ID_IDENTIFICACION_CONYUGE,
              NOMBRE_CONYUGE,
              PRIMER_APELLIDO_CONYUGE,
              SEGUNDO_APELLIDO_CONYUGE,
              ID_APODERADO,
              ID_IDENTIFICACION_APODERADO,
              NOMBRE_APODERADO,
              PRIMER_APELLIDO_APODERADO,
              SEGUNDO_APELLIDO_APODERADO,
              PROFESION,
              ID_ESTADO,
              ID_TIPO_RELACION,
              ID_CIIU,
              EMPRESA_LABORA,
              FECHA_INGRESO_EMPRESA,
              CARGO_ACTUAL,
              DECLARACION,
              INGRESOS_A_PRINCIPAL,
              INGRESOS_OTROS,
              INGRESOS_CONYUGE,
              INGRESOS_CONYUGE_OTROS,
              DESC_INGR_OTROS,
              EGRESOS_ALQUILER,
              EGRESOS_SERVICIOS,
              EGRESOS_TRANSPORTE,
              EGRESOS_ALIMENTACION,
              EGRESOS_DEUDAS,
              EGRESOS_OTROS,
              DESC_EGRE_OTROS,
              EGRESOS_CONYUGE,
              OTROS_EGRESOS_CONYUGE,
              TOTAL_ACTIVOS,
              TOTAL_PASIVOS,
              EDUCACION,
              RETEFUENTE,
              ACTA,
              FECHA_REGISTRO,
              ESCRITURA_CONSTITUCION,
              DURACION_SOCIEDAD,
              CAPITAL_SOCIAL,
              MATRICULA_MERCANTIL,
              EMAIL,
              ID_EMPLEADO,
              FECHA_ACTUALIZACION
              ) VALUES (
                {id_identificacion},
                {id_persona},
                {lugar_expedicion},
                {fecha_expedicion},
                {nombre},
                {primer_apellido},
                {segundo_apellido},
                {id_tipo_persona},
                {sexo},
                {lugar_nacimiento},
                {provincia_nacimiento},
                {depto_nacimiento},
                {pais_nacimiento},
                {id_tipo_estado_civil},
                {id_conyuge},
                {id_identificacion_conyuge},
                {nombre_conyuge},
                {primer_apellido_conyuge},
                {segundo_apellido_conyuge},
                {id_apoderado},
                {id_identificacion_apoderado},
                {nombre_apoderado},
                {primer_apellido_apoderado},
                {segundo_apellido_apoderado},
                {profesion},
                {id_estado},
                {id_tipo_relacion},
                {id_ciiu},
                {empresa_labora},
                {fecha_ingreso_empresa},
                {cargo_actual},
                {declaracion},
                {ingresos_a_principal},
                {ingresos_otros},
                {ingresos_conyuge},
                {ingresos_conyuge_otros},
                {desc_ingr_otros},
                {egresos_alquiler},
                {egresos_servicios},
                {egresos_transporte},
                {egresos_alimentacion},
                {egresos_deudas},
                {egresos_otros},
                {desc_egre_otros},
                {egresos_conyuge},
                {otros_egresos_conyuge},
                {total_activos},
                {total_pasivos},
                {educacion},
                {retefuente},
                {acta},
                {fecha_registro},
                {escritura_constitucion},
                {duracion_sociedad},
                {capital_social},
                {matricula_mercantil},
                {email},
                {id_empleado},
                {fecha_actualizacion}
              )
          """).on(
            'id_identificacion -> a.id_identificacion,
            'id_persona -> a.id_persona,
            'lugar_expedicion -> a.lugar_expedicion,
            'fecha_expedicion -> a.fecha_expedicion,
            'nombre -> a.nombre,
            'primer_apellido -> a.primer_apellido,
            'segundo_apellido -> a.segundo_apellido,
            'id_tipo_persona -> a.id_tipo_persona,
            'sexo -> a.sexo,
            'lugar_nacimiento -> a.lugar_nacimiento,
            'provincia_nacimiento -> a.provincia_nacimiento,
            'depto_nacimiento -> a.depto_nacimiento,
            'pais_nacimiento -> a.pais_nacimiento,
            'id_tipo_estado_civil -> a.id_tipo_estado_civil,
            'id_conyuge -> a.id_conyuge,
            'id_identificacion_conyuge -> a.id_identificacion_conyuge,
            'nombre_conyuge -> a.nombre_conyuge,
            'primer_apellido_conyuge -> a.primer_apellido_conyuge,
            'segundo_apellido_conyuge -> a.segundo_apellido_conyuge,
            'id_apoderado -> b.id_apoderado,
            'id_identificacion_apoderado -> b.id_identificacion_apoderado,
            'nombre_apoderado -> b.nombre_apoderado,
            'primer_apellido_apoderado -> b.primer_apellido_apoderado,
            'segundo_apellido_apoderado -> b.segundo_apellido_apoderado,
            'profesion -> b.profesion,
            'id_estado -> b.id_estado,
            'id_tipo_relacion -> b.id_tipo_relacion,
            'id_ciiu -> b.id_ciiu,
            'empresa_labora -> b.empresa_labora,
            'fecha_ingreso_empresa -> b.fecha_ingreso_empresa,
            'cargo_actual -> b.cargo_actual,
            'declaracion -> b.declaracion,
            'ingresos_a_principal -> b.ingresos_a_principal,
            'ingresos_otros -> b.ingresos_otros,
            'ingresos_conyuge -> b.ingresos_conyuge,
            'ingresos_conyuge_otros -> b.ingresos_conyuge_otros,
            'desc_ingr_otros -> b.desc_ingr_otros,
            'egresos_alquiler -> c.egresos_alquiler,
            'egresos_servicios -> c.egresos_servicios,
            'egresos_transporte -> c.egresos_transporte,
            'egresos_alimentacion -> c.egresos_alimentacion,
            'egresos_deudas -> c.egresos_deudas,
            'egresos_otros -> c.egresos_otros,
            'desc_egre_otros -> c.desc_egre_otros,
            'egresos_conyuge -> c.egresos_conyuge,
            'otros_egresos_conyuge -> c.otros_egresos_conyuge,
            'total_activos -> c.total_activos,
            'total_pasivos -> c.total_pasivos,
            'educacion -> c.educacion,
            'retefuente -> c.retefuente,
            'acta -> c.acta,
            'fecha_registro -> c.fecha_registro,
            'escritura_constitucion -> c.escritura_constitucion,
            'duracion_sociedad -> d.duracion_sociedad,
            'capital_social -> d.capital_social,
            'matricula_mercantil -> d.matricula_mercantil,
            'email -> d.email,
            'id_empleado -> d.id_empleado,
            'fecha_actualizacion -> d.fecha_actualizacion
          ).executeUpdate() > 0
        }
        if (insertadoP || actualizadoP) {
          // Borrado persadicional
          SQL("""DELETE FROM \"gen$persadicional\" WHERE ID_IDENTIFICACION = {id_identificacion} AND ID_PERSONA = {id_persona}""").
          on(
            'id_identificacion -> a.id_identificacion,
            'id_persona -> a.id_persona
          ).executeUpdate()

          // Insertar datos de persadicional
          SQL("""INSERT INTO \"gen$persadicional\" (
            ID_IDENTIFICACION,
            ID_PERSONA,
            NUMERO_HIJOS,
            ID_OCUPACION,
            ID_TIPOCONTRATO,
            DESCRIPCION_CONTRATO,
            ID_SECTOR,
            DESCRIPCION_SECTOR,
            VENTA_ANUAL,
            FECHA_ULTIMO_BALANCE,
            NUMERO_EMPLEADOS,
            DECLARA_RENTA,
            PERSONAS_A_CARGO,
            ID_ESTRATO,
            CABEZAFAMILIA,
            ID_ESTUDIO,
            ID_TIPOVIVIENDA
          ) VALUES (
            {id_identificacion},
            {id_persona},
            {numero_hijos},
            {id_ocupacion},
            {id_tipocontrato},
            {descripcion_contrato},
            {id_sector},
            {descripcion_sector},
            {venta_anual},
            {fecha_ultimo_balance},
            {numero_empleados},
            {declara_renta},
            {personas_a_cargo},
            {id_estrato},
            {cabezafamilia},
            {id_estudio},
            {id_tipovivienda}
          )""").on(
            'id_identificacion -> a.id_identificacion,
            'id_persona -> a.id_persona,
            'numero_hijos -> e.numero_hijos,
            'id_ocupacion -> e.id_ocupacion,
            'id_tipocontrato -> e.id_tipocontrato,
            'descripcion_contrato -> e.descripcion_contrato,
            'id_sector -> e.id_sector,
            'descripcion_sector -> e.descripcion_sector,
            'venta_anual -> e.venta_anual,
            'fecha_ultimo_balance -> e.fecha_ultimo_balance,
            'numero_empleados -> e.numero_empleados,
            'declara_renta -> e.declara_renta,
            'personas_a_cargo -> e.personas_a_cargo,
            'id_estrato -> e.id_estrato,
            'cabezafamilia -> e.cabezafamilia,
            'id_estudio -> e.id_estudio,
            'id_tipovivienda -> e.id_tipovivienda
          ).executeUpdate()

          // Borrando personaextra
          SQL("""DELETE FROM \"gen$personaextra\" WHERE ID_IDENTIFICACION = {id_identificacion} AND ID_PERSONA = {id_persona}""").
          on(
            'id_identificacion -> a.id_identificacion,
            'id_persona -> a.id_persona
          ).executeUpdate()

          // Insertar personaextra
          SQL("""INSERT INTO \"gen$personaextra\" (
                  ID_IDENTIFICACION, 
                  ID_PERSONA, 
                  ES_PROVEEDOR, 
                  NUMERO_RUT) VALUES (
                    {id_identificacion},
                    {id_persona},
                    {es_proveedor},
                    {numero_rut}
                  )""").on(
                    'id_identificacion -> a.id_identificacion,
                    'id_persona -> a.id_persona,
                    'es_proveedor -> f.es_proveedor,
                    'numero_rut -> f.numero_rut
          ).executeUpdate()
          // Borrando direcciones previas
          SQL("""DELETE FROM \"gen$direccion\" WHERE ID_IDENTIFICACION = {id_identificacion} and ID_PERSONA = {id_persona}""").
          on(
            'id_identificacion -> a.id_identificacion,
            'id_persona -> a.id_persona
          ).executeUpdate()
          // Insertar direcciones
          for (d <- direcciones) {
            SQL("""INSERT INTO \"gen$direccion\" 
                  (ID_IDENTIFICACION,
                   ID_PERSONA,
                   CONSECUTIVO,
                   ID_DIRECCION,
                   DIRECCION,
                   BARRIO,
                   COD_MUNICIPIO,
                   TELEFONO1,
                   TELEFONO2,
                   TELEFONO3,
                   TELEFONO4
                  ) VALUES (
                    {id_identificacion},
                    {id_persona},
                    {consecutivo},
                    {id_direccion},
                    {direccion},
                    {barrio},
                    {cod_municipio},
                    {telefono1},
                    {telefono2},
                    {telefono3},
                    {telefono4}
                  )
            """).on(
              'id_identificacion -> a.id_identificacion,
              'id_persona -> a.id_persona,
              'consecutivo -> d.consecutivo,
              'id_direccion -> d.id_direccion,
              'direccion -> d.direccion,
              'barrio -> d.barrio,
              'cod_municipio -> d.cod_municipio,
              'telefono1 -> d.telefono1,
              'telefono2 -> d.telefono2,
              'telefono3 -> d.telefono3,
              'telefono4 -> d.telefono4
            ).executeUpdate()
          }

          // Borrando Referencias Previas
          SQL("""DELETE FROM \"gen$referencias\" WHERE TIPO_ID_REFERENCIA = {id_identificacion} and ID_REFERENCIA = {id_persona}""").
          on(
            'id_identificacion -> a.id_identificacion,
            'id_persona -> a.id_persona
          ).executeUpdate()
          
          for(r <- referencias) {
            // Insertando nuevas referencias
            SQL("""INSERT INTO \"gen$referencias\" (
                     TIPO_ID_REFERENCIA, 
                     ID_REFERENCIA,
                     CONSECUTIVO_REFERENCIA,
                     PRIMER_APELLIDO_REFERENCIA,
                     SEGUNDO_APELLIDO_REFERENCIA,
                     NOMBRE_REFERENCIA,
                     DIRECCION_REFERENCIA,
                     TELEFONO_REFERENCIA,
                     TIPO_REFERENCIA,
                     PARENTESCO_REFERENCIA) VALUES (
                       {id_identificacion},
                       {id_persona},
                       {consecutivo_referencia},
                       {primer_apellido_referencia},
                       {segundo_apellido_referencia},
                       {nombre_referencia},
                       {direccion_referencia},
                       {telefono_referencia},
                       {tipo_referencia},
                       {parentesco_referencia}
            )""").on(
                       'id_identificacion -> a.id_identificacion,
                       'id_persona -> a.id_persona,
                       'consecutivo_referencia -> r.consecutivo_referencia,
                       'primer_apellido_referencia -> r.primer_apellido_referencia,
                       'segundo_apellido_referencia -> r.segundo_apellido_referencia,
                       'nombre_referencia -> r.nombre_referencia,
                       'direccion_referencia -> r.direccion_referencia,
                       'telefono_referencia -> r.telefono_referencia,
                       'tipo_referencia -> r.tipo_referencia,
                       'parentesco_referencia -> r.parentesco_referencia
            ).executeUpdate()
          }

          // Borrar beneficiarios previos
          SQL("""DELETE FROM \"gen$beneficiario\" WHERE ID_IDENTIFICACION = {id_identificacion} and ID_PERSONA = {id_persona}""").
          on(
              'id_identificacion -> a.id_identificacion,
              'id_persona -> a.id_persona
          ).executeUpdate()

          for(b <- beneficiarios) {
            SQL("""INSERT INTO \"gen$beneficiario\" (
              ID_IDENTIFICACION,
              ID_PERSONA,              
              CONSECUTIVO,
              PRIMER_APELLIDO,
              SEGUNDO_APELLIDO,
              NOMBRE,
              ID_PARENTESCO,
              PORCENTAJE,
              AUXILIO
            ) VALUES (
              {id_identificacion},
              {id_persona},
              {consecutivo},
              {primer_apellido},
              {segundo_apellido},
              {nombre},
              {id_parentesco},
              {porcentaje},
              {auxilio}
            )""").on(
              'id_identificacion -> a.id_identificacion,
              'id_persona -> a.id_persona,
              'consecutivo -> b.consecutivo,
              'primer_apellido -> b.primer_apellido,
              'segundo_apellido -> b.segundo_apellido,
              'nombre -> b.nombre,
              'id_parentesco -> b.id_parentesco,
              'porcentaje -> b.porcentaje,
              'auxilio -> b.auxilio
            ).executeUpdate()
          }

          // Borrar hijos previos
          SQL("""DELETE FROM \"gen$hijo\" WHERE ID_IDENTIFICACION = {id_identificacion} AND ID_PERSONA = {id_persona}""").
          on(
            'id_identificacion -> a.id_identificacion,
            'id_persona -> a.id_persona
          ).executeUpdate()

          for (h <- hijos) {
            SQL("""INSERT INTO \"gen$hijo\" (
              ID_IDENTIFICACION,
              ID_PERSONA,
              CONSECUTIVO_HIJO,
              NOMBRE,
              PRIMER_APELLIDO,
              SEGUNDO_APELLIDO,
              FECHA_NACIMIENTO
            ) VALUES (
              {id_identificacion},
              {id_persona},
              {consecutivo_hijo},
              {nombre},
              {primer_apellido},
              {segundo_apellido},
              {fecha_nacimiento}
            )""").on(
              'id_identificacion -> a.id_identificacion,
              'id_persona -> a.id_persona,
              'consecutivo_hijo -> h.consecutivo_hijo,
              'nombre -> h.nombre,                            
              'primer_apellido -> h.primer_apellido,
              'segundo_apellido -> h.segundo_apellido,
              'fecha_nacimiento -> h.fecha_nacimiento
            ).executeUpdate()
          }
        }
        Future(insertadoP || actualizadoP)
      }
    }
}
