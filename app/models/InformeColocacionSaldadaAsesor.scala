package models

import java.nio.file.{Files, Paths}
import javax.inject.Inject
import java.util.Calendar
import java.sql.Timestamp
import java.security.MessageDigest
import java.util.{Map, HashMap, Date}
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import scala.util.{Failure, Success}
import anorm._
import anorm.SqlParser.{get, str, int, date, flatten, scalar}
import anorm.JodaParameterMetaData._
import play.api.db.DBApi
import scala.concurrent.Future
import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._
import play.api.Configuration
import notifiers._
import scala.collection.mutable.ListBuffer

// Excel Export
import com.norbitltd.spoiwo.model._
import com.norbitltd.spoiwo.model.enums.CellStyleInheritance
import com.norbitltd.spoiwo.natures.xlsx.Model2XlsxConversions._
import com.norbitltd.spoiwo.model.enums.{
  CellBorderStyle,
  CellFill,
  Pane,
  CellHorizontalAlignment => HA,
  CellVerticalAlignment => VA
}

import utilities._

case class ColocacionSaldadaAsesor(
    id_persona: Option[String],
    nombre: Option[String],
    primer_apellido: Option[String],
    segudo_apellido: Option[String],
    direccion: Option[String],
    municipio: Option[String],
    telefono1: Option[String],
    telefono2: Option[String],
    colocacion: Option[Int]
)

object ColocacionSaldadaAsesor {
  val _set = {
    get[Option[String]]("id_persona") ~
      get[Option[String]]("nombre") ~
      get[Option[String]]("primer_apellido") ~
      get[Option[String]]("segundo_apellido") ~
      get[Option[String]]("direccion") ~
      get[Option[String]]("municipio") ~
      get[Option[String]]("telefono1") ~
      get[Option[String]]("telefono2") ~
      get[Option[Int]]("colocacion") map {
      case a01 ~
            b02 ~
            c03 ~
            d04 ~
            e05 ~
            f06 ~
            g07 ~
            h08 ~
            i09 =>
        new ColocacionSaldadaAsesor(
          a01,
          b02,
          c03,
          d04,
          e05,
          f06,
          g07,
          h08,
          i09
        )
    }
  }
}

class InformeColocacionSaldadaAsesorRepository @Inject()(
    dbapi: DBApi,
    config: Configuration,
    empresaService: EmpresaRepository
)(implicit ec: DatabaseExecutionContext) {

  def generarXlsx(ases_id: Long, empr_id: scala.Long): Array[Byte] = {

    val base = "default"
    val db = dbapi.database(base)
    db.withTransaction { implicit connection =>
      val empresa = empresaService.buscarPorId(empr_id)
      empresa match {
        case Some(empresa) =>
          val asesorQuery = """SELECT TRIM(ge1.NOMBRE || ' ' || ge1.PRIMER_APELLIDO || ' ' || ge1.SEGUNDO_APELLIDO) AS ASESOR FROM ASESOR as1
                               INNER JOIN "gen$empleado" ge1 ON ge1.ID_EMPLEADO  = as1.ID_EMPLEADO
                               WHERE as1.ASES_ID = {ases_id} """
          val diasmoraQuery = """SELECT MAX(DIAS_APLICADOS) FROM "col$extractodet" e 
                                     WHERE e.ID_COLOCACION IN (SELECT c.ID_COLOCACION FROM "col$colocacion" c
                                     WHERE c.ID_IDENTIFICACION = {id_identificacion} and c.ID_PERSONA = {id_persona})
                                     AND e.CODIGO_PUC IN ('415020020100000000', '415021020100000000', '415022010000000000')
                          """
          val asesor = SQL(asesorQuery).on('ases_id -> ases_id).as(SqlParser.scalar[String].single)
          val query =
            """SELECT gp1.ID_PERSONA, gp1.NOMBRE, gp1.PRIMER_APELLIDO, gp1.SEGUNDO_APELLIDO, gd1.DIRECCION, gd1.MUNICIPIO, gd1.TELEFONO1, gd1.TELEFONO2, COUNT(*) AS COLOCACION FROM ASESOR as1
                    INNER JOIN COLOCACIONASESOR ca1 ON ca1.ASES_ID  = as1.ASES_ID 
                    INNER JOIN "col$colocacion" cc1 ON cc1.ID_COLOCACION  = ca1.ID_COLOCACION 
                    INNER JOIN "gen$persona" gp1 ON gp1.ID_IDENTIFICACION  = cc1.ID_IDENTIFICACION  AND gp1.ID_PERSONA = cc1.ID_PERSONA 
                    INNER JOIN "gen$direccion" gd1 ON gd1.ID_IDENTIFICACION = gp1.ID_IDENTIFICACION AND gd1.ID_PERSONA = gp1.ID_PERSONA AND gd1.ID_DIRECCION = 1
                    WHERE cc1.ID_ESTADO_COLOCACION = 7 AND
                        (SELECT COUNT(*) FROM "col$colocacion" cc2 WHERE cc2.ID_IDENTIFICACION = gp1.ID_IDENTIFICACION AND cc2.ID_PERSONA = gp1.ID_PERSONA AND cc2.ID_ESTADO_COLOCACION IN (0,1,2) ) = 0
                        AND as1.ASES_ID = {ases_id}
                    GROUP BY 1,2,3,4,5,6,7,8"""
          val resultSet = SQL(query).on('ases_id -> ases_id).as(ColocacionSaldadaAsesor._set *)
          val sheet1 = Sheet(name="Saldadas",
            rows = {
              val titleRow = com.norbitltd.spoiwo.model.Row().withCellValues(empresa.empr_descripcion)
              val title02Row = com.norbitltd.spoiwo.model.Row().withCellValues("PERSONAS CREDITOS SALDADOS - ASESOR")
              val title03Row = com.norbitltd.spoiwo.model.Row().withCellValues("ASESOR:", asesor)
              val headerRow = com.norbitltd.spoiwo.model.Row().withCellValues("DOCUMENTO", "NOMBRE", "PRIMER APELLIDO", "SEGUNDO APELLIDO", "DIRECCION", "MUNICIPIO", "TELEFONO1", "TELEFONO2", "COLOCACIONES")
              var j = 4
              val rows = resultSet.map { i => j+=1

                com.norbitltd.spoiwo.model.Row(
                  StringCell(i.id_persona match { case Some(v) => v case None => "" },Some(0), style = Some(CellStyle( dataFormat = CellDataFormat("@"))), CellStyleInheritance.CellThenRowThenColumnThenSheet),
                  StringCell(i.nombre match { case Some(v) => v case None => "" },Some(1), style = Some(CellStyle( dataFormat = CellDataFormat("@"))), CellStyleInheritance.CellThenRowThenColumnThenSheet),
                  StringCell(i.primer_apellido match { case Some(v) => v case None => "" },Some(2), style = Some(CellStyle( dataFormat = CellDataFormat("@"))), CellStyleInheritance.CellThenRowThenColumnThenSheet),
                  StringCell(i.segudo_apellido match { case Some(v) => v case None => "" },Some(3), style = Some(CellStyle( dataFormat = CellDataFormat("@"))), CellStyleInheritance.CellThenRowThenColumnThenSheet),
                  StringCell(i.direccion match { case Some(v) => v case None => "" },Some(4), style = Some(CellStyle( dataFormat = CellDataFormat("@"))), CellStyleInheritance.CellThenRowThenColumnThenSheet),
                  StringCell(i.municipio match { case Some(v) => v case None => "" },Some(5), style = Some(CellStyle( dataFormat = CellDataFormat("@"))), CellStyleInheritance.CellThenRowThenColumnThenSheet),
                  StringCell(i.telefono1 match { case Some(v) => v case None => "" },Some(6), style = Some(CellStyle( dataFormat = CellDataFormat("@"))), CellStyleInheritance.CellThenRowThenColumnThenSheet),
                  StringCell(i.telefono2 match { case Some(v) => v case None => "" },Some(7), style = Some(CellStyle( dataFormat = CellDataFormat("@"))), CellStyleInheritance.CellThenRowThenColumnThenSheet),
                  NumericCell(i.colocacion match { case Some(v) => v case None => 0 },Some(8), style = Some(CellStyle( dataFormat = CellDataFormat("#,##0"))), CellStyleInheritance.CellThenRowThenColumnThenSheet)
                )
              }
              titleRow :: title02Row :: title03Row :: headerRow :: rows.toList
            }
          )
          var os:ByteArrayOutputStream = new ByteArrayOutputStream()
          Workbook(sheet1).writeToOutputStream(os) 
          os.toByteArray 
        case None => var os:ByteArrayOutputStream = new ByteArrayOutputStream()
                     os.toByteArray
      }
    }
  }
}
