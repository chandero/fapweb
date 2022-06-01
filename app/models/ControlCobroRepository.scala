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

import utilities._

class ControlCobroRepository @Inject()(dbapi: DBApi, _f: Funcion, _gcol: GlobalesCol, _uService: UsuarioRepository)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

  def obtenerCreditos(estado: Int, dias_ini: Int, dias_fin: Int, ases_id: Int): Future[Iterable[ControlCobroVista]] = Future[Iterable[ControlCobroVista]] {
      var _lista = new mutable.ListBuffer[ControlCobroVista]()      
      val creditos = db.withConnection { implicit connection =>
            var query = """SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'D' AS tipo FROM \"col$colocacion\" a
              LEFT JOIN COLOCACIONASESOR r ON r.ID_COLOCACION = a.ID_COLOCACION
              WHERE a.ID_ESTADO_COLOCACION IN (0,1,2,3,7,8,9)
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
      db.withConnection { implicit connection =>
        for (c <- creditos) { 
          val nombre = 
                        SQL("""SELECT (p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO) AS NOMBRE FROM "gen$persona" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona} """).
                        on(
                          'id_identificacion -> c._1._3.get,
                          'id_persona -> c._1._4.get
                        ).as(SqlParser.scalar[String].singleOpt)

          val centro_costo = SQL("""SELECT DESCRIPCION_AGENCIA FROM "gen$agencia" WHERE ID_AGENCIA = {id_agencia}""").
            on(
              'id_agencia -> c._1._1.get
            ).as(SqlParser.scalar[String].singleOpt)

          val estadoDesc = SQL("""SELECT DESCRIPCION_ESTADO_COLOCACION FROM "col$estado" WHERE ID_ESTADO_COLOCACION = {id_estado_colocacion}""").
            on(
              'id_estado_colocacion -> c._6._5.get
            ).as(SqlParser.scalar[String].singleOpt)

          val tipo_cuota = SQL("""SELECT DESCRIPCION_TIPO_CUOTA FROM "col$tiposcuota" WHERE ID_TIPOS_CUOTA = {id_tipos_cuota}""").
            on(
              'id_tipos_cuota -> c._5._1.get
            ).as(SqlParser.scalar[String].singleOpt)


          val numero_cuotas = SQL("""SELECT COUNT(*) FROM "col$tablaliquidacion" t WHERE t.ID_COLOCACION = {id_colocacion} and t.PAGADA = 0""").
            on(
            'id_colocacion -> c._1._2.get
          ).as(SqlParser.scalar[Int].singleOpt)

          var valor = c._3._3.get 
          var abono = c._6._2.get
          var deuda = valor - abono
          var tasa_nominal = _f.tasaNominalVencida(c._4._3.get, c._5._3.get)

          var dias_mora = 0
          if (deuda > 0) {
            dias_mora = _gcol.obtenerDiasMora(c._1._2.get)
            if (dias_mora < -1) { dias_mora += 2 }
          }        
          val estCol = c._6._5.get
          estado match {
          case -1 =>
            if (dias_mora >= dias_ini && dias_mora <= dias_fin) {
              var _cc = new ControlCobroVista(
                                        c._1._1,
                                        c._1._2, 
                                        nombre, 
                                        Some(valor),
                                        Some(deuda), 
                                        c._6._1,
                                        c._3._4,
                                        c._5._2,
                                        c._5._3,
                                        Some(tasa_nominal),
                                        numero_cuotas,
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
              var _cc = new ControlCobroVista(
                                        c._1._1,
                                        c._1._2, 
                                        nombre, 
                                        Some(valor),
                                        Some(deuda), 
                                        c._6._1,
                                        c._3._4,
                                        c._5._2,
                                        c._5._3,
                                        Some(tasa_nominal),
                                        numero_cuotas,                                        
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
      }
      _lista.toList
    }

  def obtenerCreditoPorColocacion(id_colocacion: String): Future[Iterable[ControlCobroVista]] = Future[Iterable[ControlCobroVista]] {
      var _lista = new mutable.ListBuffer[ControlCobroVista]()      
      val creditos = db.withConnection { implicit connection =>
            SQL("""SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'D' AS tipo FROM \"col$colocacion\" a
             WHERE a.ID_COLOCACION = {id_colocacion} and a.ID_ESTADO_COLOCACION IN (0,1,2,3,7,8,9)
            """).
            on(
                'id_colocacion -> id_colocacion
            ).as(Credito._set *)
      }
      // Debo verificar el filtro de busqueda
      db.withConnection { implicit connection =>      
        for (c <- creditos) {
          val nombre = SQL("""SELECT (p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO) AS NOMBRE FROM "gen$persona" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona} """).
                        on(
                          'id_identificacion -> c._1._3.get,
                          'id_persona -> c._1._4.get
                        ).as(SqlParser.scalar[String].singleOpt)
 
          val centro_costo = SQL("""SELECT DESCRIPCION_AGENCIA FROM "gen$agencia" WHERE ID_AGENCIA = {id_agencia}""").
            on(
              'id_agencia -> c._1._1.get
            ).as(SqlParser.scalar[String].singleOpt)

          val estadoDesc = SQL("""SELECT DESCRIPCION_ESTADO_COLOCACION FROM "col$estado" WHERE ID_ESTADO_COLOCACION = {id_estado_colocacion}""").
            on(
              'id_estado_colocacion -> c._6._5.get
            ).as(SqlParser.scalar[String].singleOpt)

          val tipo_cuota = SQL("""SELECT DESCRIPCION_TIPO_CUOTA FROM "col$tiposcuota" WHERE ID_TIPOS_CUOTA = {id_tipos_cuota}""").
            on(
              'id_tipos_cuota -> c._5._1.get
            ).as(SqlParser.scalar[String].singleOpt)

          val numero_cuotas = SQL("""SELECT COUNT(*) FROM "col$tablaliquidacion" t WHERE t.ID_COLOCACION = {id_colocacion} and t.PAGADA = 0""").
            on(
              'id_colocacion -> c._1._2.get
            ).as(SqlParser.scalar[Int].singleOpt)

          var valor = c._3._3.get 
          var abono = c._6._2.get
          var deuda = valor - abono
          var tasa_nominal = _f.tasaNominalVencida(c._4._3.get, c._5._3.get)

          var dias_mora = 0
          if (deuda > 0) { 
            dias_mora = _gcol.obtenerDiasMora(c._1._2.get)
            if (dias_mora < -1) { dias_mora += 2 }
          }
          var _cc = new ControlCobroVista(
                                        c._1._1,
                                        c._1._2, 
                                        nombre, 
                                        Some(valor),
                                        Some(deuda), 
                                        c._6._1,
                                        c._3._4,
                                        c._5._2,
                                        c._5._3,
                                        Some(tasa_nominal),
                                        numero_cuotas,                                        
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
      _lista.toList      
  }

  def obtenerCreditos(id_identificacion: Int, id_persona: String): Future[Iterable[ControlCobroVista]] = Future[Iterable[ControlCobroVista]] {
      var _lista = new mutable.ListBuffer[ControlCobroVista]()      
      val creditos = db.withConnection { implicit connection =>
            SQL("""SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'D' AS tipo FROM \"col$colocacion\" a
             WHERE a.ID_IDENTIFICACION = {id_identificacion} and a.ID_PERSONA = {id_persona} and a.ID_ESTADO_COLOCACION IN (0,1,2,3,7,8,9)
             UNION ALL
             SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION, a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, 'F' AS tipo FROM \"col$colocacion\" a
             LEFT JOIN \"col$colgarantias\" g ON g.ID_COLOCACION = a.ID_COLOCACION WHERE g.ID_IDENTIFICACION = {id_identificacion} and g.ID_PERSONA = {id_persona} and a.ID_ESTADO_COLOCACION IN (0,1,2,3,7,8,9)
            """).
            on(
                'id_identificacion -> id_identificacion,
                'id_persona -> id_persona
            ).as(Credito._set *)
      }
      // Debo verificar el filtro de busqueda
      db.withConnection { implicit connection =>      
        for (c <- creditos) {
          val nombre = SQL("""SELECT (p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO) AS NOMBRE FROM "gen$persona" p WHERE p.ID_IDENTIFICACION = {id_identificacion} AND p.ID_PERSONA = {id_persona} """).
                        on(
                          'id_identificacion -> c._1._3.get,
                          'id_persona -> c._1._4.get
                        ).as(SqlParser.scalar[String].singleOpt)

          val centro_costo = SQL("""SELECT DESCRIPCION_AGENCIA FROM "gen$agencia" WHERE ID_AGENCIA = {id_agencia}""").
            on(
              'id_agencia -> c._1._1.get
            ).as(SqlParser.scalar[String].singleOpt)

          val estadoDesc = SQL("""SELECT DESCRIPCION_ESTADO_COLOCACION FROM "col$estado" WHERE ID_ESTADO_COLOCACION = {id_estado_colocacion}""").
            on(
              'id_estado_colocacion -> c._6._5.get
            ).as(SqlParser.scalar[String].singleOpt)

          val tipo_cuota = SQL("""SELECT DESCRIPCION_TIPO_CUOTA FROM "col$tiposcuota" WHERE ID_TIPOS_CUOTA = {id_tipos_cuota}""").
            on(
              'id_tipos_cuota -> c._5._1.get
            ).as(SqlParser.scalar[String].singleOpt)

          val numero_cuotas = SQL("""SELECT COUNT(*) FROM "col$tablaliquidacion" t WHERE t.ID_COLOCACION = {id_colocacion} and t.PAGADA = 0""").
          on(
            'id_colocacion -> c._1._2.get
          ).as(SqlParser.scalar[Int].singleOpt)

          var valor = c._3._3.get 
          var abono = c._6._2.get
          var deuda = valor - abono
          var tasa_nominal = _f.tasaNominalVencida(c._4._3.get, c._5._3.get)

          var dias_mora = 0
          if (deuda > 0) { 
            dias_mora = _gcol.obtenerDiasMora(c._1._2.get)
            if (dias_mora < -1) { dias_mora += 2 }
          }
          var _cc = new ControlCobroVista(
                                        c._1._1,
                                        c._1._2, 
                                        nombre, 
                                        Some(valor),
                                        Some(deuda), 
                                        c._6._1,
                                        c._3._4,
                                        c._5._2,
                                        c._5._3,
                                        Some(tasa_nominal),
                                        numero_cuotas,                                        
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

  def obtenerControlCobro(id_colocacion: String): Future[Iterable[ControlCobro]] = Future[Iterable[ControlCobro]] {
    val lista = db.withConnection { implicit connection =>
      SQL("""SELECT c.ID_AGENCIA,c.ID_COLOCACION,c.FECHA_OBSERVACION,c.OBSERVACION, c.ES_OBSERVACION,c.ES_COMPROMISO, c.FECHA_COMPROMISO, c.ID_USUARIO, e.NOMBRE || ' ' || e.PRIMER_APELLIDO || ' ' || e.SEGUNDO_APELLIDO AS EMPLEADO from "col$controlcobro" c
             INNER JOIN "gen$empleado" e ON (e.ID_EMPLEADO = c.ID_USUARIO)
             WHERE c.ID_COLOCACION = {id_colocacion} ORDER BY FECHA_OBSERVACION DESC""").
             on(
               'id_colocacion -> id_colocacion
             ).as(ControlCobro._set *)
    }
    lista
  }

  def agregar(cc: ControlCobro, usua_id: Long): Future[Boolean] = {
    _uService.buscarPorId(usua_id).map { empleado =>
      var id_empleado:Option[String] = None
      empleado match {
        case Some(e) => id_empleado = e.id_empleado
        case None => None
      }
      db.withConnection { implicit connection =>
        SQL("""INSERT INTO "col$controlcobro" VALUES (
          {id_agencia}, 
          {id_colocacion}, 
          {fecha_observacion}, 
          {observacion}, 
          {es_observacion}, 
          {es_compromiso}, 
          {fecha_compromiso}, 
          {id_empleado}
        )""").
        on(
        'id_agencia -> cc.id_agencia,
        'id_colocacion -> cc.id_colocacion,
        'fecha_observacion -> cc.fecha_observacion,
        'observacion -> cc.observacion,
        'es_observacion -> cc.es_observacion,
        'es_compromiso -> cc.es_compromiso,
        'fecha_compromiso -> cc.fecha_compromiso,
        'id_empleado -> id_empleado
       ).executeUpdate() > 0
      }
    }
  }
}