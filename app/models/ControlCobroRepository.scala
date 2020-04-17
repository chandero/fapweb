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

import utilities.Funcion

case class ControlCobro(
    id_colocacion: Option[String],
    nombre: Option[String],
    saldo: Option[BigDecimal],
    cuota: Option[BigDecimal],
    fecha_capital: Option[DateTime],
    fecha_interes: Option[DateTime],
    estado: Option[String],
    dias_mora: Option[Int],
    id_identificacion: Option[Int],
    id_persona: Option[String],
    tipo_cuota: Option[String],
    tiene_compromiso: Option[String],
    centro_costo: Option[String]
)

case class Compromiso(
    comp_id: Option[Long],
    id_colocacion: Option[String],
    comp_descripcion: Option[String],
    comp_activo: Option[Int],
    comp_fecha: Option[DateTime],
    comp_fechacompromiso: Option[DateTime],
    comp_repetircada: Option[String],
    id_empleado: Option[String]
)

object ControlCobro {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")
    
  implicit val write = new Writes[ControlCobro] {
    def writes(e: ControlCobro) = Json.obj(
        "id_colocacion" -> e.id_colocacion,
        "nombre" -> e.nombre,
        "saldo" -> e.saldo,
        "cuota" -> e.cuota,
        "fecha_capital" -> e.fecha_capital,
        "fecha_interes" -> e.fecha_interes,
        "estado" -> e.estado,
        "dias_mora" -> e.dias_mora,
        "id_identificacion" -> e.id_identificacion,
        "id_persona" -> e.id_persona,
        "tipo_cuota" -> e.tipo_cuota,
        "tiene_compromiso" -> e.tiene_compromiso,
        "centro_costo" -> e.centro_costo
    )
  }

  implicit val rReads: Reads[ControlCobro] = (
    (__ \ "id_colocacion").readNullable[String] and
    (__ \ "nombre").readNullable[String] and
    (__ \ "saldo").readNullable[BigDecimal] and
    (__ \ "cuota").readNullable[BigDecimal] and
    (__ \ "fecha_capital").readNullable[DateTime] and
    (__ \ "fecha_interes").readNullable[DateTime] and
    (__ \ "estado").readNullable[String] and
    (__ \ "dias_mora").readNullable[Int] and
    (__ \ "id_identificacion").readNullable[Int] and
    (__ \ "id_persona").readNullable[String] and
    (__ \ "tipo_cuota").readNullable[String] and
    (__ \ "tiene_compromiso").readNullable[String] and
    (__ \ "centro_costo").readNullable[String]
  )(ControlCobro.apply _)    
}

object Compromiso {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val write = new Writes[Compromiso] {
    def writes(e: Compromiso) = Json.obj(
        "comp_id" -> e.comp_id,
        "id_colocacion" -> e.id_colocacion,
        "comp_descripcion" -> e.comp_descripcion,
        "comp_activo" -> e.comp_activo,
        "comp_fecha" -> e.comp_fecha,
        "comp_fechacompromiso" -> e.comp_fechacompromiso,
        "comp_repetircada" -> e.comp_repetircada,
        "id_empleado" -> e.id_empleado
    )
  }

  implicit val rReads: Reads[Compromiso] = (
    (__ \ "comp_id").readNullable[Long] and
    (__ \ "id_colocacion").readNullable[String] and
    (__ \ "comp_descripcion").readNullable[String] and
    (__ \ "comp_activo").readNullable[Int] and
    (__ \ "comp_fecha").readNullable[DateTime] and
    (__ \ "comp_fechacompromiso").readNullable[DateTime] and
    (__ \ "comp_repetircada").readNullable[String] and
    (__ \ "id_empleado").readNullable[String]
  )(Compromiso.apply _) 
}

class ControlCobroRepository @Inject()(dbapi: DBApi, _f: Funcion)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  def obtenerCreditos(estado: Int, dias_ini: Int, dias_fin: Int, ases_id: Int): Future[Iterable[ControlCobro]] = Future[Iterable[ControlCobro]] {
      var _lista = new mutable.ListBuffer[ControlCobro]()      
      val creditos = db.withConnection { implicit connection =>
            var query = """SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'D' AS tipo FROM \"col$colocacion\" a
              LEFT JOIN COLOCACIONASESOR r ON r.ID_COLOCACION = a.ID_COLOCACION
              WHERE a.ID_ESTADO_COLOCACION IN (0,1,2,3,8,9)
            """
            if (ases_id > 0) {
              query += " and r.ASES_ID = {ases_id}"
            }
            if (ases_id > 0) {
              SQL(query).on('ases_id -> ases_id).as(Credito._set *)
            } else {
              SQL(query).as(Credito._set *)
            }
      }
      // Debo verificar el filtro de busqueda
      for (c <- creditos) { 
        val nombre = db.withConnection { implicit connection =>
                        SQL("""SELECT (p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO) AS NOMBRE FROM "gen$persona" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona} """).
                        on(
                          'id_identificacion -> c._1._3.get,
                          'id_persona -> c._1._4.get
                        ).as(SqlParser.scalar[String].singleOpt)
        }

        val centro_costo = db.withConnection { implicit connection => 
            SQL("""SELECT DESCRIPCION_AGENCIA FROM "gen$agencia" WHERE ID_AGENCIA = {id_agencia}""").
            on(
              'id_agencia -> c._1._1.get
            ).as(SqlParser.scalar[String].singleOpt)
        }

        val estadoDesc = db.withConnection { implicit connection => 
          SQL("""SELECT DESCRIPCION_ESTADO_COLOCACION FROM "col$estado" WHERE ID_ESTADO_COLOCACION = {id_estado_colocacion}""").
          on(
            'id_estado_colocacion -> c._6._5.get
          ).as(SqlParser.scalar[String].singleOpt)
        }

        val tipo_cuota = db.withConnection { implicit connection =>
          SQL("""SELECT DESCRIPCION_TIPO_CUOTA FROM "col$tiposcuota" WHERE ID_TIPOS_CUOTA = {id_tipos_cuota}""").
          on(
            'id_tipos_cuota -> c._5._1.get
          ).as(SqlParser.scalar[String].singleOpt)
        }

        var valor = c._3._3.get 
        var abono = c._6._2.get
        var deuda = valor - abono

        var dias_mora = 0
        if (deuda > 0) {
          dias_mora = _f.obtenerDiasMora(c._1._2.get)
          if (dias_mora < -1) { dias_mora += 2 }
        }        
        val estCol = c._6._5.get
        estado match {
        case -1 =>
          if (dias_mora >= dias_ini && dias_mora <= dias_fin) {
            var _cc = new ControlCobro(
                                        c._1._2, 
                                        nombre, 
                                        Some(deuda), 
                                        c._6._1, 
                                        c._6._3, 
                                        c._6._4, 
                                        estadoDesc,
                                        Some(dias_mora),
                                        c._1._3,
                                        c._1._4,
                                        tipo_cuota,
                                        Some(""), 
                                        centro_costo)
            _lista += _cc                                        
          }
        case estCol =>
            if (dias_mora >= dias_ini && dias_mora <= dias_fin) {
              var _cc = new ControlCobro(
                                        c._1._2, 
                                        nombre, 
                                        Some(deuda), 
                                        c._6._1, 
                                        c._6._3, 
                                        c._6._4, 
                                        estadoDesc,
                                        Some(dias_mora),
                                        c._1._3,
                                        c._1._4,
                                        tipo_cuota,
                                        Some(""), 
                                        centro_costo)
            _lista += _cc
          }
        }
      }
      _lista.toList
    }

  def obtenerCreditos(id_identificacion: Int, id_persona: String): Future[Iterable[ControlCobro]] = Future[Iterable[ControlCobro]] {
      var _lista = new mutable.ListBuffer[ControlCobro]()      
      val creditos = db.withConnection { implicit connection =>
            SQL("""SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'D' AS tipo FROM \"col$colocacion\" a
             WHERE a.ID_IDENTIFICACION = {id_identificacion} and a.ID_PERSONA = {id_persona} and a.ID_ESTADO_COLOCACION IN (0,1,2,3,8,9)
             UNION ALL
             SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'F' AS tipo FROM \"col$colocacion\" a
             LEFT JOIN \"col$colgarantias\" g ON g.ID_COLOCACION = a.ID_COLOCACION WHERE g.ID_IDENTIFICACION = {id_identificacion} and g.ID_PERSONA = {id_persona} and a.ID_ESTADO_COLOCACION IN (0,1,2,3,8,9)
            """).
            on(
                'id_identificacion -> id_identificacion,
                'id_persona -> id_persona
            ).as(Credito._set *)
      }
      // Debo verificar el filtro de busqueda
      for (c <- creditos) {
        val nombre = db.withConnection { implicit connection =>
                        SQL("""SELECT (p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO) AS NOMBRE FROM "gen$persona" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona} """).
                        on(
                          'id_identificacion -> c._1._3.get,
                          'id_persona -> c._1._4.get
                        ).as(SqlParser.scalar[String].singleOpt)
        }

        val centro_costo = db.withConnection { implicit connection => 
            SQL("""SELECT DESCRIPCION_AGENCIA FROM "gen$agencia" WHERE ID_AGENCIA = {id_agencia}""").
            on(
              'id_agencia -> c._1._1.get
            ).as(SqlParser.scalar[String].singleOpt)
        }

        val estadoDesc = db.withConnection { implicit connection => 
          SQL("""SELECT DESCRIPCION_ESTADO_COLOCACION FROM "col$estado" WHERE ID_ESTADO_COLOCACION = {id_estado_colocacion}""").
          on(
            'id_estado_colocacion -> c._6._5.get
          ).as(SqlParser.scalar[String].singleOpt)
        }

        val tipo_cuota = db.withConnection { implicit connection =>
          SQL("""SELECT DESCRIPCION_TIPO_CUOTA FROM "col$tiposcuota" WHERE ID_TIPOS_CUOTA = {id_tipos_cuota}""").
          on(
            'id_tipos_cuota -> c._5._1.get
          ).as(SqlParser.scalar[String].singleOpt)
        }


        var valor = c._3._3.get 
        var abono = c._6._2.get
        var deuda = valor - abono

        var dias_mora = 0
        if (deuda > 0) { 
          dias_mora = _f.obtenerDiasMora(c._1._2.get)
          if (dias_mora < -1) { dias_mora += 2 }
        }
        var _cc = new ControlCobro(
                                        c._1._2, 
                                        nombre, 
                                        Some(deuda), 
                                        c._6._1, 
                                        c._6._3, 
                                        c._6._4, 
                                        estadoDesc,
                                        Some(dias_mora),
                                        c._1._3,
                                        c._1._4,
                                        tipo_cuota,
                                        Some(""), 
                                        centro_costo)
        _lista += _cc 
      }
      _lista.toList      
  }
  
  def obtenerDireccion(id_identificacion: Int, id_persona: String): Future[Iterable[Direccion]] = Future[Iterable[Direccion]] {
    val direcciones = db.withConnection { implicit connection =>
      SQL(
          """SELECT d.consecutivo, d.id_direccion, d.direccion, d.barrio, d.cod_municipio, m.NOMBRE AS municipio, d.telefono1, d.telefono2, d.telefono3, d.telefono4 FROM \"gen$direccion\" d 
             INNER JOIN \"gen$municipios" m ON m.COD_MUNICIPIO = d.COD_MUNICIPIO
             WHERE d.ID_IDENTIFICACION = {id_identificacion} AND d.ID_PERSONA = {id_persona}
             ORDER BY d.ID_DIRECCION ASC"""
        ).on(
            'id_identificacion -> id_identificacion,
            'id_persona -> id_persona
          )
          .as(Direccion._set *)
    }
    direcciones
  }
}