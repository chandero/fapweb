package utilities

import javax.inject.Inject

import play.api.db.DBApi

import anorm._
import anorm.SqlParser.{get, str}
import anorm.JodaParameterMetaData._

import models._
import org.joda.time.DateTime

class GlobalesCon @Inject()(dbapi: DBApi, _funcion: Funcion )(implicit ec: DatabaseExecutionContext) {
    private val db = dbapi.database("default")

    def saldo_anterior_codigo_puc(codigo: String, fecha: Long): Long = {
        val ff = new DateTime(fecha)
        db.withTransaction { implicit connection =>
            val saldo_inicial = SQL("""SELECT SALDOINICIAL FROM "con$puc" cp1 WHERE cp1.CODIGO = {codigo}""").on('codigo -> codigo).as(SqlParser.scalar[Long].single)
            val queryMovs = """SELECT 
                                    SUM(a.DEBITO) AS DEBITO,  
                                    SUM(a.CREDITO) AS CREDITO 
                                   FROM "con$comprobante" c
                                   INNER JOIN "con$auxiliar" a ON a.TIPO_COMPROBANTE = c.TIPO_COMPROBANTE and a.ID_COMPROBANTE = c.ID_COMPROBANTE
                                   WHERE c.ESTADO <> {estado} AND a.FECHA BETWEEN {fecha_inicial} AND {fecha_final} AND a.CODIGO = {codigo}"""
            var movs: Long = 0L
            if (ff.year.get() != 0) {
                movs = SQL(queryMovs).
                on(
                    'codigo -> codigo,
                    'fecha_inicial -> new DateTime(ff.year().get(),1,1,0,0,0),
                    'fecha_final -> ff,
                    'estado -> "N"
                ).as(SqlParser.scalar[Long].single)
            }
            saldo_inicial + movs
        }
    }

}