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
/*
* Estados
* 0: Activo
* 2: Normalizado
* 8: Anulado
* 9: Eliminado
*/

class ColPeriodoGraciaRepository @Inject()(dbapi: DBApi, _g: GlobalesCol)(
    implicit ec: DatabaseExecutionContext
) {
  private val db = dbapi.database("default")

    def todos():Future[Iterable[ColPeriodoGraciaVista]] = Future[Iterable[ColPeriodoGraciaVista]] {
        db.withConnection { implicit connection => 
            SQL(""" SELECT g.*, (p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO) AS nombre, (c.VALOR_DESEMBOLSO - c.ABONOS_CAPITAL) AS saldo, c.VALOR_CUOTA AS cuota FROM COL_PERIODO_GRACIA g
                INNER JOIN "col$colocacion" c ON c.ID_COLOCACION = g.ID_COLOCACION
                INNER JOIN "gen$persona" p ON p.ID_IDENTIFICACION = c.ID_IDENTIFICACION and p.ID_PERSONA = c.ID_PERSONA """).
            as(ColPeriodoGraciaVista._set *)
        }
    }

    def agregar(id_colocacion: String, fecha: Long, dias: Int): Future[Boolean] = Future {
        val _fecha = new DateTime(fecha)
        db.withTransaction { implicit connection => 
          // marcar como finalizado periodo activo previo
          SQL("""UPDATE COL_PERIODO_GRACIA SET ESTADO = 8, FECHA_CANCELADO = {fecha}
                WHERE ID_COLOCACION = {id_colocacion} AND ESTADO = 0""").
            on(
              'id_colocacion -> id_colocacion,
              'fecha -> new DateTime()
            ).executeUpdate()
          // Ingresar nuevo registro
          val result = SQL("""INSERT INTO COL_PERIODO_GRACIA SELECT c.ID_COLOCACION, c.FECHA_CAPITAL, c.FECHA_INTERES,
                  {fecha}, {dias}, 1, NULL, 0 FROM "col$colocacion" c 
                  WHERE c.ID_COLOCACION = {id_colocacion} """).
          on(
            'id_colocacion -> id_colocacion,
            'fecha -> _fecha,
            'dias -> dias
          ).executeUpdate() > 0
          result
        }
    }
  
    def normalizarColocacionPeriodoGracia(id_colocacion: String): Future[Boolean] = Future {
        val c = db.withConnection { implicit connection => 
            SQL("""SELECT c.*, 'D' AS tipo FROM "col$colocacion" c WHERE c.ID_COLOCACION = {id_colocacion}""").
            on(
                'id_colocacion -> id_colocacion
            ).as(Credito._set.single)
        }
        println("ya pase colocacion")
        val cpg = db.withConnection { implicit connection =>
            SQL("""SELECT FIRST 1 g.* 
                FROM COL_PERIODO_GRACIA g 
                WHERE g.ID_COLOCACION = {id_colocacion} AND g.ESTADO = 0""").
            on(
              'id_colocacion -> id_colocacion
            ).as(ColPeriodoGracia._set.singleOpt)
        }
        println("ya pase el select col_periodo_gracia")
        cpg match {
            case Some(p) =>
                val _amortiza_capital = c._5._2
                val _amortiza_interes = c._5._3
                val _fecha_interes = c._6._4
                var _fecha_capital = c._6._4

                val _dias_pago:Int = p.dias.get + c._8._1.get
                val hoy = new DateTime()

                var _result = db.withTransaction { implicit connection =>
                    def sql = SQL("""UPDATE "col$colocacion" SET FECHA_CAPITAL = {fecha_capital}, DIAS_PAGO = {dias_pago} WHERE ID_COLOCACION = {id_colocacion} """).
                    on(
                        'id_colocacion -> id_colocacion,
                        'fecha_capital -> _fecha_capital,
                        'dias_pago -> _dias_pago
                    )
                    sql.executeUpdate() > 0
                }
                println("ya pase el update colocacion")
                _result = _result && _g.reajustarTablaPeriodoGracia(id_colocacion, p.dias.get)
                _result = _result && db.withTransaction { implicit connection =>
                    SQL("""UPDATE COL_PERIODO_GRACIA SET ESTADO = 2, FECHA_CANCELADO = {hoy}
                                        WHERE ID_COLOCACION = {id_colocacion} AND ESTADO = 0""").
                                    on(
                                        'id_colocacion -> id_colocacion,
                                        'hoy -> hoy
                                    ).executeUpdate() > 0
                            }
                println("ya pase el update COL_PERIODO_GRACIA")
                _result
            case None => false
        }
    }

    def normalizarreversoColocacionPeriodoGracia(id_colocacion: String): Future[Boolean] = Future {
        val c = db.withConnection { implicit connection => 
            SQL("""SELECT c.*, 'D' AS tipo FROM "col$colocacion" c WHERE c.ID_COLOCACION = {id_colocacion}""").
            on(
                'id_colocacion -> id_colocacion
            ).as(Credito._set.single)
        }
        println("ya pase colocacion")
        val cpg = db.withConnection { implicit connection =>
            SQL("""SELECT FIRST 1 g.* 
                FROM COL_PERIODO_GRACIA g 
                WHERE g.ID_COLOCACION = {id_colocacion} AND g.ESTADO = 2
                ORDER BY FECHA_REGISTRO DESC""").
            on(
              'id_colocacion -> id_colocacion
            ).as(ColPeriodoGracia._set.singleOpt)
        }
        println("ya pase el select col_periodo_gracia")
        cpg match {
            case Some(p) =>
                val _amortiza_capital = c._5._2
                val _amortiza_interes = c._5._3
                val _fecha_interes = c._6._4
                var _fecha_capital = c._6._4

                val _dias_pago:Int = c._8._1.get - p.dias.get
                val hoy = new DateTime()

                var _result = db.withTransaction { implicit connection =>
                    def sql = SQL("""UPDATE "col$colocacion" SET FECHA_CAPITAL = {fecha_capital}, DIAS_PAGO = {dias_pago} WHERE ID_COLOCACION = {id_colocacion} """).
                    on(
                        'id_colocacion -> id_colocacion,
                        'fecha_capital -> _fecha_capital,
                        'dias_pago -> _dias_pago
                    )
                    sql.executeUpdate() > 0
                }
                println("ya pase el update colocacion")
                _result = _result && _g.reajustarTablaPeriodoGracia(id_colocacion, -p.dias.get)
                _result = _result && db.withTransaction { implicit connection =>
                    SQL("""UPDATE COL_PERIODO_GRACIA SET ESTADO = 0, FECHA_CANCELADO = {hoy}
                                        WHERE ID_COLOCACION = {id_colocacion} AND ESTADO = 2""").
                                    on(
                                        'id_colocacion -> id_colocacion,
                                        'hoy -> hoy
                                    ).executeUpdate() > 0
                            }
                println("ya pase el update COL_PERIODO_GRACIA")
                _result
            case None => false
        }
    }

    def marcarEliminado(id_colocacion: String): Future[Boolean] = Future[Boolean] {
        db.withTransaction { implicit connection => 
            SQL("""UPDATE COL_PERIODO_GRACIA SET ESTADO = 9 
                    WHERE ID_COLOCACION = {id_colocacion} AND ESTADO = 0""").
            on(
              'id_colocacion -> id_colocacion
            ).executeUpdate() > 0
        }
    }

    def actualizar(id_colocacion: String, dias: Int): Future[Boolean] = Future[Boolean] {
        db.withTransaction { implicit connection => 
            SQL("""UPDATE COL_PERIODO_GRACIA SET DIAS = {dias} 
                    WHERE ID_COLOCACION = {id_colocacion} AND ESTADO = 0""").
            on(
              'id_colocacion -> id_colocacion,
              'dias -> dias
            ).executeUpdate() > 0
        }
    }    
}