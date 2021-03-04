package models

import javax.inject.Inject
import java.util.Calendar
import java.text.SimpleDateFormat

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

import play.api.db.DBApi

import anorm._
import anorm.SqlParser.{get, str, date, int}
import anorm.JodaParameterMetaData._

import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}
import scala.collection.mutable.ListBuffer

import org.joda.time.DateTime
import org.joda.time.LocalDate

case class FacturaItem(
    fait_id: Option[Long],
    fact_numero: Option[Long],
    fait_detalle: Option[String],
    fait_cantidad: Option[Int],
    fait_valorunitario: Option[Double],
    fait_tasaiva: Option[Double],
    fait_valoriva: Option[Double],
    fait_total: Option[Double]
)

case class Factura(
    fact_numero: Option[Long],
    fact_fecha: Option[DateTime],
    fact_descripcion: Option[String],
    tipo_comprobante: Option[String],
    id_comprobante: Option[Int],
    fecha_comprobante: Option[DateTime],
    id_identificacion: Option[Int],
    id_persona: Option[String],
    id_empleado: Option[String],
    fact_estado: Option[Int],
    items: Option[Seq[FacturaItem]],
    primer_apellido: Option[String],
    segundo_apellido: Option[String],
    nombre: Option[String],    
    fact_total: Option[Double]
)

case class FacturaNotaItem(
    fanoit_id: Option[Long],
    fact_nota_tipo: Option[String],
    fact_nota_numero: Option[Long],
    fanoit_detalle: Option[String],
    fanoit_cantidad: Option[Int],
    fanoit_valorunitario: Option[Double],
    fanoit_tasaiva: Option[Double],
    fanoit_valoriva: Option[Double],
    fanoit_total: Option[Double]
)

case class FacturaNota(
    fact_nota_tipo: Option[String],
    fact_nota_numero: Option[Long],
    fact_nota_fecha: Option[DateTime],
    fact_nota_descripcion: Option[String],
    fact_numero: Option[Int],
    fact_cufe: Option[String],
    id_identificacion: Option[Int],
    id_persona: Option[String],
    id_empleado: Option[String],
    fact_nota_estado: Option[Int],
    items: Option[Seq[FacturaNotaItem]],
    primer_apellido: Option[String],
    segundo_apellido: Option[String],
    nombre: Option[String],
    fact_nota_total: Option[Double]
)

case class _AutorizacionFactura(
    AutFechaFinal: Option[String],
    AutFechaInicio: Option[String],
    AutNumAutorizacion: Option[String],
    AutPrefijo: Option[String],
    AutSecuenciaFinal: Option[String],
    AutSecuenciaInicio: Option[String]
)

case class _CompradorFactura(
    CompradorApellidos: Option[String],
    CompradorCiudad: Option[String],
    CompradorCodCiudad: Option[String],
    CompradorCodDepartamento: Option[String],
    CompradorCodPostal: Option[String],
    CompradorCorreoElectronico: Option[String],
    CompradorDVIdentificacion: Option[String],
    CompradorDepartamento: Option[String],
    CompradorDireccion: Option[String],
    CompradorEnviarCorreo: Option[Boolean],
    CompradorIdentificacion: Option[String],
    CompradorImpuesto: Option[String],
    CompradorNombreCompleto: Option[String],
    CompradorNombrePais: Option[String],
    CompradorNotaCont: Option[String],
    CompradorPais: Option[String],
    CompradorPrimerNombre: Option[String],
    CompradorRazonSocial: Option[String],
    CompradorRespFiscal: Option[String],
    CompradorSector: Option[String],
    CompradorSegundoNombre: Option[String],
    CompradorTelefonoCont: Option[String],
    CompradorTipoIdentificacion: Option[String],
    CompradorTipoPersona: Option[String],
    CompradorTipoRegimen: Option[String]
)

case class _EmisorData(
    EmiDVIdentificacion: Option[String],
    EmiIdentificacion: Option[String],
    EmiTipoIdentificacion: Option[Int],
    EmiTipoPersona: Option[Int]
)

case class _EncabezadoData(
    FacCodOperacion: Option[String],
    FacFechaContingencia: Option[String],
    FacFechaFin: Option[String],
    FacFechaHoraFactura: Option[String],
    FacFechaIni: Option[String],
    FacRefContigencia: Option[String],
    FacTipoFactura: Option[String],
    FacTipoRefContigencia: Option[String]
)

case class _InfoMonetarioData(
    FacCodMoneda: Option[String],
    FacTotalAnticipos: Option[String],
    FacTotalBaseImponible: Option[String],
    FacTotalBrutoMasImp: Option[String],
    FacTotalCargos: Option[String],
    FacTotalDescuentos: Option[String],
    FacTotalFactura: Option[String],
    FacTotalImporteBruto: Option[String]
)

case class _LsAnticipos(
    FacAnticipoFecha: Option[String],
    FacAnticipoSec: Option[String],
    FacAnticipoTotal: Option[String]
)

case class _LsCargos(
    FacCargoBase: Option[String],
    FacCargoPorc: Option[String],
    FacCargoRazon: Option[String],
    FacCargoSecuencia: Option[String],
    FacCargoTipo: Option[String],
    FacCargoTotal: Option[String],
    FacCodDescuento: Option[String]
)

case class _LsDetalle(
    Cantidad: Option[String],
    Codificacion: Option[String],
    Codigo: Option[String],
    CodigoEstandar: Option[String],
    Descripcion: Option[String],
    DetFacConsecutivo: Option[Int],
    PrecioSinImpuestos: Option[String],
    PrecioTotal: Option[String],
    PrecioUnitario: Option[String],
    TamañoPaquete: Option[String],
    UnidadMedida: Option[String]
)

case class _LsDetalleCargos(
    DetSecuencia: Option[String],
    FacCargoBase: Option[String],
    FacCargoPorc: Option[String],
    FacCargoRazon: Option[String],
    FacCargoSecuencia: Option[String],
    FacCargoTipo: Option[String],
    FacCargoTotal: Option[String]
)

case class _LsDetalleImpuesto(
    BaseImponible: Option[String],
    CodigoImpuesto: Option[String],
    EsRetencionImpuesto: Option[Boolean],
    NombreImpuesto: Option[String],
    Porcentaje: Option[String],
    Secuencia: Option[Int],
    ValorImpuesto: Option[String]
)

case class _LsFormaPago(
    FacFormaPago: Option[String],
    FacMetodoPago: Option[String],
    FacVencimientoFac: Option[String]
)

case class _LsImpuestos(
    BaseImponible: Option[String],
    CodigoImpuesto: Option[String],
    EsRetencionImpuesto: Option[Boolean],
    NombreImpuesto: Option[String],
    Porcentaje: Option[String],
    ValorImpuesto: Option[String]
)

case class _LsNota(
    DescripcionCabecera: Option[String],
    Consecutivo: Option[Int]
)

case class _ReferenciaFactura(
    ConceptoNota: Option[Int],
    DescNatCorreccion: Option[String],
    NumeroFactura: Option[String],
    CufeFactura: Option[String],
    FechaFactura: Option[String]
)

case class _SoftwareSeguridad(
    ClaveTecnica: Option[String],
    CodigoErp: Option[String],
    GuidEmpresa: Option[String],
    GuidOrigen: Option[String],
    HashSeguridad: Option[String],
    NumeroDocumento: Option[String],
    TipoDocumento: Option[String]
)

case class _RootInterface(
    AutorizacionFactura: Option[_AutorizacionFactura],
    CompradorFactura: Option[_CompradorFactura],
    EmisorData: Option[_EmisorData],
    EncabezadoData: Option[_EncabezadoData],
    InfoMonetarioData: Option[_InfoMonetarioData],
    LsDetalle: Option[Seq[_LsDetalle]],
    LsDetalleCargos: Option[Seq[_LsDetalleCargos]],
    LsDetalleImpuesto: Option[Seq[_LsDetalleImpuesto]],
    LsImpuestos: Option[Seq[_LsImpuestos]],
    ReferenciaFactura: Option[_ReferenciaFactura],
    SoftwareSeguridad: Option[_SoftwareSeguridad],
    lsAnticipos: Option[Seq[_LsAnticipos]],
    lsCargos: Option[Seq[_LsCargos]],
    lsFormaPago: Option[Seq[_LsFormaPago]],
    lsNotas: Option[Seq[_LsNota]]
)

/*
object _AutorizacionFactura {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_AutorizacionFactura] {
    def writes(r: _AutorizacionFactura) = Json.obj(
      "AutFechaFinal" -> r.AutFechaFinal,
      "AutFechaInicio" -> r.AutFechaInicio,
      "AutNumAutorizacion" -> r.AutNumAutorizacion,
      "AutPrefijo" -> r.AutPrefijo,
      "AutSecuenciaFinal" -> r.AutSecuenciaFinal,
      "AutSecuenciaInicio" -> r.AutSecuenciaInicio
    )
  }

  implicit val Reads: Reads[_AutorizacionFactura] = (
    (__ \ "AutFechaFinal").readNullable[String] and
    (__ \ "AutFechaInicio").readNullable[String] and
    (__ \ "AutNumAutorizacion").readNullable[String] and
    (__ \ "AutPrefijo").readNullable[String] and
    (__ \ "AutSecuenciaFinal").readNullable[String] and
    (__ \ "AutSecuenciaInicio").readNullable[String]
  )(_AutorizacionFactura.apply _)
}


object _CompradorFactura {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_CompradorFactura] {
    def writes(r: _CompradorFactura) = Json.obj(
      "CompradorApellidos" -> r.CompradorApellidos,
      "CompradorCiudad" -> r.CompradorCiudad,
      "CompradorCodCiudad" -> r.CompradorCodCiudad,
      "CompradorCodDepartamento" -> r.CompradorCodDepartamento,
      "CompradorCodPostal" -> r.CompradorCodPostal,
      "CompradorCorreoElectronico" -> r.CompradorCorreoElectronico,
      "CompradorDVIdentificacion" -> r.CompradorDVIdentificacion,
      "CompradorDepartamento" -> r.CompradorDepartamento,
      "CompradorDireccion" -> r.CompradorDireccion,
      "CompradorEnviarCorreo" -> r.CompradorEnviarCorreo,
      "CompradorIdentificacion" -> r.CompradorIdentificacion,
      "CompradorImpuesto" -> r.CompradorImpuesto,
      "CompradorNombreCompleto" -> r.CompradorNombreCompleto,
      "CompradorNombrePais" -> r.CompradorNombrePais,
      "CompradorNotaCont" -> r.CompradorNotaCont,
      "CompradorPais" -> r.CompradorPais,
      "CompradorPrimerNombre" -> r.CompradorPrimerNombre,
      "CompradorRazonSocial" -> r.CompradorRazonSocial,
      "CompradorRespFiscal" -> r.CompradorRespFiscal,
      "CompradorSector" -> r.CompradorSector,
      "CompradorSegundoNombre" -> r.CompradorSegundoNombre,
      "CompradorTelefonoCont" -> r.CompradorTelefonoCont,
      "CompradorTipoIdentificacion" -> r.CompradorTipoIdentificacion,
      "CompradorTipoPersona" -> r.CompradorTipoPersona,
      "CompradorTipoRegimen" -> r.CompradorTipoRegimen
    )
  }

  implicit val Reads: Reads[_CompradorFactura] = (
    (__ \ "CompradorApellidos").readNullable[String] and
    (__ \ "CompradorCiudad").readNullable[String] and
    (__ \ "CompradorCodCiudad").readNullable[String] and
    (__ \ "CompradorCodDepartamento").readNullable[String] and
    (__ \ "CompradorCodPostal").readNullable[String] and
    (__ \ "CompradorCorreoElectronico").readNullable[String] and
    (__ \ "CompradorDVIdentificacion").readNullable[String] and
    (__ \ "CompradorDepartamento").readNullable[String] and
    (__ \ "CompradorDireccion").readNullable[String] and
    (__ \ "CompradorEnviarCorreo").readNullable[Boolean] and
    (__ \ "CompradorIdentificacion").readNullable[String] and
    (__ \ "CompradorImpuesto").readNullable[String] and
    (__ \ "CompradorNombreCompleto").readNullable[String] and
    (__ \ "CompradorNombrePais").readNullable[String] and
    (__ \ "CompradorNotaCont").readNullable[String] and
    (__ \ "CompradorPais").readNullable[String] and
    (__ \ "CompradorPrimerNombre").readNullable[String] and
    (__ \ "CompradorRazonSocial").readNullable[String] and
    (__ \ "CompradorRespFiscal").readNullable[String] and
    (__ \ "CompradorSector").readNullable[String] and
    (__ \ "CompradorSegundoNombre").readNullable[String] and
    (__ \ "CompradorTelefonoCont").readNullable[String] and
    (__ \ "CompradorTipoIdentificacion").readNullable[String] and
    (__ \ "CompradorTipoPersona").readNullable[String] and
    (__ \ "CompradorTipoRegimen").readNullable[String]
  )(_CompradorFactura.apply _)
}

object _EmisorData {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_EmisorData] {
    def writes(r: _EmisorData) = Json.obj(
      "EmiDVIdentificacion" -> r.EmiDVIdentificacion,
      "EmiIdentificacion" -> r.EmiIdentificacion,
      "EmiTipoIdentificacion" -> r.EmiTipoIdentificacion,
      "EmiTipoPersona" -> r.EmiTipoPersona
    )
  }

  implicit val Reads: Reads[_EmisorData] = (
    (__ \ "EmiDVIdentificacion").readNullable[String] and
    (__ \ "EmiIdentificacion").readNullable[String] and
    (__ \ "EmiTipoIdentificacion").readNullable[Int] and
    (__ \ "EmiTipoPersona").readNullable[Int]
  )(_EmisorData.apply _)
}

object _EncabezadoData {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_EncabezadoData] {
    def writes(r: _EncabezadoData) = Json.obj(
      "FacCodOperacion" -> r.FacCodOperacion,
      "FacFechaContingencia" -> r.FacFechaContingencia,
      "FacFechaFin" -> r.FacFechaFin,
      "FacFechaHoraFactura" -> r.FacFechaHoraFactura,
      "FacFechaIni" -> r.FacFechaIni,
      "FacRefContigencia" -> r.FacRefContigencia,
      "FacTipoFactura" -> r.FacTipoFactura,
      "FacTipoRefContigencia" -> r.FacTipoRefContigencia
    )
  }

  implicit val Reads: Reads[_EncabezadoData] = (
    (__ \ "FacCodOperacion").readNullable[String] and
    (__ \ "FacFechaContingencia").readNullable[String] and
    (__ \ "FacFechaFin").readNullable[String] and
    (__ \ "FacFechaHoraFactura").readNullable[String] and
    (__ \ "FacFechaIni").readNullable[String] and
    (__ \ "FacRefContigencia").readNullable[String] and
    (__ \ "FacTipoFactura").readNullable[String] and
    (__ \ "FacTipoRefContigencia").readNullable[String]
  )(_EncabezadoData.apply _)
}

object _InfoMonetarioData {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_InfoMonetarioData] {
    def writes(r: _InfoMonetarioData) = Json.obj(
      "FacCodMoneda" -> r.FacCodMoneda,
      "FacTotalAnticipos" -> r.FacTotalAnticipos,
      "FacTotalBaseImponible" -> r.FacTotalBaseImponible,
      "FacTotalBrutoMasImp" -> r.FacTotalBrutoMasImp,
      "FacTotalCargos" -> r.FacTotalCargos,
      "FacTotalDescuentos" -> r.FacTotalDescuentos,
      "FacTotalFactura" -> r.FacTotalFactura,
      "FacTotalImporteBruto" -> r.FacTotalImporteBruto
    )
  }

  implicit val Reads: Reads[_InfoMonetarioData] = (
    (__ \ "FacCodMoneda").readNullable[String] and
    (__ \ "FacTotalAnticipos").readNullable[String] and
    (__ \ "FacTotalBaseImponible").readNullable[String] and
    (__ \ "FacTotalBrutoMasImp").readNullable[String] and
    (__ \ "FacTotalCargos").readNullable[String] and
    (__ \ "FacTotalDescuentos").readNullable[String] and
    (__ \ "FacTotalFactura").readNullable[String] and
    (__ \ "FacTotalImporteBruto").readNullable[String]
  )(_InfoMonetarioData.apply _)
}

object _LsAnticipos {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_LsAnticipos] {
    def writes(r: _LsAnticipos) = Json.obj(
      "FacAnticipoFecha" -> r.FacAnticipoFecha,
      "FacAnticipoSec" -> r.FacAnticipoSec,
      "FacAnticipoTotal" -> r.FacAnticipoTotal
    )
  }

  implicit val Reads: Reads[_LsAnticipos] = (
    (__ \ "FacAnticipoFecha").readNullable[String] and
    (__ \ "FacAnticipoSec").readNullable[String] and
    (__ \ "FacAnticipoTotal").readNullable[String]
  )(_LsAnticipos.apply _)
}


object _LsCargos {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_LsCargos] {
    def writes(r: _LsCargos) = Json.obj(
      "FacCargoBase" -> r.FacCargoBase,
      "FacCargoPorc" -> r.FacCargoPorc,
      "FacCargoRazon" -> r.FacCargoRazon,
      "FacCargoSecuencia" -> r.FacCargoSecuencia,
      "FacCargoTipo" -> r.FacCargoTipo,
      "FacCargoTotal" -> r.FacCargoTotal,
      "FacCodDescuento" -> r.FacCodDescuento
    )
  }

  implicit val Reads: Reads[_LsCargos] = (
    (__ \ "FacCargoBase").readNullable[String] and
    (__ \ "FacCargoPorc").readNullable[String] and
    (__ \ "FacCargoRazon").readNullable[String] and
    (__ \ "FacCargoSecuencia").readNullable[String] and
    (__ \ "FacCargoTipo").readNullable[String] and
    (__ \ "FacCargoTotal").readNullable[String] and
    (__ \ "FacCodDescuento").readNullable[String]
  )(_LsCargos.apply _)
}

object _LsDetalle {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_LsDetalle] {
    def writes(r: _LsDetalle) = Json.obj(
      "Cantidad" -> r.Cantidad,
      "Codificacion" -> r.Codificacion,
      "Codigo" -> r.Codigo,
      "CodigoEstandar" -> r.CodigoEstandar,
      "Descripcion" -> r.Descripcion,
      "DetFacConsecutivo" -> r.DetFacConsecutivo,
      "PrecioSinImpuestos" -> r.PrecioSinImpuestos,
      "PrecioTotal" -> r.PrecioTotal,
      "PrecioUnitario" -> r.PrecioUnitario,
      "TamañoPaquete" -> r.TamañoPaquete,
      "UnidadMedida" -> r.UnidadMedida
    )
  }

  implicit val Reads: Reads[_LsDetalle] = (
    (__ \ "Cantidad").readNullable[String] and
    (__ \ "Codificacion").readNullable[String] and
    (__ \ "Codigo").readNullable[String] and
    (__ \ "CodigoEstandar").readNullable[String] and
    (__ \ "Descripcion").readNullable[String] and
    (__ \ "DetFacConsecutivo").readNullable[Int] and
    (__ \ "PrecioSinImpuestos").readNullable[String] and
    (__ \ "PrecioTotal").readNullable[String] and
    (__ \ "PrecioUnitario").readNullable[String] and
    (__ \ "TamañoPaquete").readNullable[String] and
    (__ \ "UnidadMedida").readNullable[String]
  )(_LsDetalle.apply _)
}

object _LsDetalleCargos {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_LsDetalleCargos] {
    def writes(r: _LsDetalleCargos) = Json.obj(
      "DetSecuencia" -> r.DetSecuencia,
      "FacCargoBase" -> r.FacCargoBase,
      "FacCargoPorc" -> r.FacCargoPorc,
      "FacCargoRazon" -> r.FacCargoRazon,
      "FacCargoSecuencia" -> r.FacCargoSecuencia,
      "FacCargoTipo" -> r.FacCargoTipo,
      "FacCargoTotal" -> r.FacCargoTotal
    )
  }

  implicit val Reads: Reads[_LsDetalleCargos] = (
    (__ \ "DetSecuencia").readNullable[String] and
    (__ \ "FacCargoBase").readNullable[String] and
    (__ \ "FacCargoPorc").readNullable[String] and
    (__ \ "FacCargoRazon").readNullable[String] and
    (__ \ "FacCargoSecuencia").readNullable[String] and
    (__ \ "FacCargoTipo").readNullable[String] and
    (__ \ "FacCargoTotal").readNullable[String]
  )(_LsDetalleCargos.apply _)
}

object _LsDetalleImpuesto {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_LsDetalleImpuesto] {
    def writes(r: _LsDetalleImpuesto) = Json.obj(
      "BaseImponible" -> r.BaseImponible,
      "CodigoImpuesto" -> r.CodigoImpuesto,
      "EsRetencionImpuesto" -> r.EsRetencionImpuesto,
      "NombreImpuesto" -> r.NombreImpuesto,
      "Porcentaje" -> r.Porcentaje,
      "Secuencia" -> r.Secuencia,
      "ValorImpuesto" -> r.ValorImpuesto
    )
  }

  implicit val Reads: Reads[_LsDetalleImpuesto] = (
    (__ \ "BaseImponible").readNullable[String] and
    (__ \ "CodigoImpuesto").readNullable[String] and
    (__ \ "EsRetencionImpuesto").readNullable[Boolean] and
    (__ \ "NombreImpuesto").readNullable[String] and
    (__ \ "Porcentaje").readNullable[String] and
    (__ \ "Secuencia").readNullable[Int] and
    (__ \ "ValorImpuesto").readNullable[String]
  )(_LsDetalleImpuesto.apply _)
}

object _LsFormaPago {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_LsFormaPago] {
    def writes(r: _LsFormaPago) = Json.obj(
      "FacFormaPago" -> r.FacFormaPago,
      "FacMetodoPago" -> r.FacMetodoPago,
      "FacVencimientoFac" -> r.FacVencimientoFac
    )
  }

  implicit val Reads: Reads[_LsFormaPago] = (
    (__ \ "FacFormaPago").readNullable[String] and
    (__ \ "FacMetodoPago").readNullable[String] and
    (__ \ "FacVencimientoFac").readNullable[String]
  )(_LsFormaPago.apply _)
}

object _LsNota {

  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_LsNota] {
    def writes(r: _LsNota) = Json.obj(
      "DescripcionCabecera" -> r.DescripcionCabecera,
      "Consecutivo" -> r.Consecutivo
    )
  }

  implicit val Reads: Reads[_LsNota] = (
    (__ \ "DescripcionCabecera").readNullable[String] and
    (__ \ "Consecutivo").readNullable[Int]
  )(_LsNota.apply _)

}

object _LsImpuestos {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_LsImpuestos] {
    def writes(r: _LsImpuestos) = Json.obj(
      "BaseImponible" -> r.BaseImponible,
      "CodigoImpuesto" -> r.CodigoImpuesto,
      "EsRetencionImpuesto" -> r.EsRetencionImpuesto,
      "NombreImpuesto" -> r.NombreImpuesto,
      "Porcentaje" -> r.Porcentaje,
      "ValorImpuesto" -> r.ValorImpuesto
    )
  }

  implicit val Reads: Reads[_LsImpuestos] = (
    (__ \ "BaseImponible").readNullable[String] and
    (__ \ "CodigoImpuesto").readNullable[String] and
    (__ \ "EsRetencionImpuesto").readNullable[Boolean] and
    (__ \ "NombreImpuesto").readNullable[String] and
    (__ \ "Porcentaje").readNullable[String] and
    (__ \ "ValorImpuesto").readNullable[String]
  )(_LsImpuestos.apply _)
}


object _SoftwareSeguridad {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_SoftwareSeguridad] {
    def writes(r: _SoftwareSeguridad) = Json.obj(
      "ClaveTecnica" -> r.ClaveTecnica,
      "CodigoErp" -> r.CodigoErp,
      "GuidEmpresa" -> r.GuidEmpresa,
      "GuidOrigen" -> r.GuidOrigen,
      "HashSeguridad" -> r.HashSeguridad,
      "NumeroDocumento" -> r.NumeroDocumento,
      "TipoDocumento" -> r.TipoDocumento
    )
  }

  implicit val Reads: Reads[_SoftwareSeguridad] = (
    (__ \ "ClaveTecnica").readNullable[String] and
    (__ \ "CodigoErp").readNullable[String] and
    (__ \ "GuidEmpresa").readNullable[String] and
    (__ \ "GuidOrigen").readNullable[String] and
    (__ \ "HashSeguridad").readNullable[String] and
    (__ \ "NumeroDocumento").readNullable[String] and
    (__ \ "TipoDocumento").readNullable[String]
  )(_SoftwareSeguridad.apply _)
}

object _RootInterface {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[_RootInterface] {
    def writes(r: _RootInterface) = Json.obj(
      "AutorizacionFactura" -> r.AutorizacionFactura,
      "CompradorFactura" -> r.CompradorFactura,
      "EmisorData" -> r.EmisorData,
      "EncabezadoData" -> r.EncabezadoData,
      "InfoMonetarioData" -> r.InfoMonetarioData,
      "LsDetalle" -> r.LsDetalle,
      "LsDetalleCargos" -> r.LsDetalleCargos,
      "LsDetalleImpuesto" -> r.LsDetalleImpuesto,
      "ReferenciaFactura" -> r.ReferenciaFactura,
      "SoftwareSeguridad" -> r.SoftwareSeguridad,
      "lsAnticipos" -> r.lsAnticipos,
      "lsCargos" -> r.lsCargos,
      "lsFormaPago" -> r.lsFormaPago,
      "lsNotas" -> r.lsNotas
    )
  }

  implicit val Reads: Reads[_RootInterface] = (
    (__ \ "AutorizacionFactura").readNullable[_AutorizacionFactura] and
    (__ \ "CompradorFactura").readNullable[_CompradorFactura] and
    (__ \ "EmisorData").readNullable[_EmisorData] and
    (__ \ "EncabezadoData").readNullable[_EncabezadoData] and
    (__ \ "InfoMonetarioData").readNullable[_InfoMonetarioData] and
    (__ \ "LsDetalle").readNullable[Seq[_LsDetalle]] and
    (__ \ "LsDetalleCargos").readNullable[Seq[_LsDetalleCargos]] and
    (__ \ "LsDetalleImpuesto").readNullable[Seq[_LsDetalleImpuesto]] and
    (__ \ "LsImpuestos").readNullable[Seq[_LsImpuestos]] and
    (__ \ "ReferenciaFactura").readNullable[String] and
    (__ \ "SoftwareSeguridad").readNullable[_SoftwareSeguridad] and
    (__ \ "lsAnticipos").readNullable[Seq[_LsAnticipos]] and
    (__ \ "lsCargos").readNullable[Seq[_LsCargos]] and
    (__ \ "lsFormaPago").readNullable[Seq[_LsFormaPago]] and
    (__ \ "lsNotas").readNullable[Seq[_LsNota]]
  )(_RootInterface.apply _)
}
 */

object FacturaItem {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[FacturaItem] {
    def writes(r: FacturaItem) = Json.obj(
      "fait_id" -> r.fait_id,
      "fact_numero" -> r.fact_numero,
      "fait_detalle" -> r.fait_detalle,
      "fait_cantidad" -> r.fait_cantidad,
      "fait_valorunitario" -> r.fait_valorunitario,
      "fait_tasaiva" -> r.fait_tasaiva,
      "fait_valoriva" -> r.fait_valoriva,
      "fait_total" -> r.fait_total
    )
  }

  implicit val Reads: Reads[FacturaItem] = (
    (__ \ "fait_id").readNullable[Long] and
      (__ \ "fact_numero").readNullable[Long] and
      (__ \ "fait_detalle").readNullable[String] and
      (__ \ "fait_cantidad").readNullable[Int] and
      (__ \ "fait_valorunitario").readNullable[Double] and
      (__ \ "fait_tasaiva").readNullable[Double] and
      (__ \ "fait_valoriva").readNullable[Double] and
      (__ \ "fait_total").readNullable[Double]
  )(FacturaItem.apply _)

  val _set = {
    get[Option[Long]]("fait_id") ~
      get[Option[Long]]("fact_numero") ~
      get[Option[String]]("fait_detalle") ~
      get[Option[Int]]("fait_cantidad") ~
      get[Option[Double]]("fait_valorunitario") ~
      get[Option[Double]]("fait_tasaiva") ~
      get[Option[Double]]("fait_valoriva") ~
      get[Option[Double]]("fait_total") map {
      case fait_id ~
            fact_numero ~
            fait_detalle ~
            fait_cantidad ~
            fait_valorunitario ~
            fait_tasaiva ~
            fait_valoriva ~
            fait_total =>
        FacturaItem(
          fait_id,
          fact_numero,
          fait_detalle,
          fait_cantidad,
          fait_valorunitario,
          fait_tasaiva,
          fait_valoriva,
          fait_total
        )
    }
  }

}

object Factura {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  implicit val Writes = new Writes[Factura] {
    def writes(r: Factura) = Json.obj(
      "fact_numero" -> r.fact_numero,
      "fact_fecha" -> r.fact_fecha,
      "fact_descripcion" -> r.fact_descripcion,
      "tipo_comprobante" -> r.tipo_comprobante,
      "id_comprobante" -> r.id_comprobante,
      "fecha_comprobante" -> r.fecha_comprobante,
      "id_identificacion" -> r.id_identificacion,
      "id_persona" -> r.id_persona,
      "id_empleado" -> r.id_empleado,
      "fact_estado" -> r.fact_estado,
      "items" -> r.items
    )
  }

  implicit val Reads: Reads[Factura] = (
    (__ \ "fact_numero").readNullable[Long] and
      (__ \ "fact_fecha").readNullable[DateTime] and
      (__ \ "fact_descripcion").readNullable[String] and
      (__ \ "tipo_comprobante").readNullable[String] and
      (__ \ "id_comprobante").readNullable[Int] and
      (__ \ "fecha_comprobante").readNullable[DateTime] and
      (__ \ "id_identificacion").readNullable[Int] and
      (__ \ "id_persona").readNullable[String] and
      (__ \ "id_empleado").readNullable[String] and
      (__ \ "fact_estado").readNullable[Int] and
      (__ \ "items").readNullable[Seq[FacturaItem]] and
      (__ \ "primer_apellido").readNullable[String] and
      (__ \ "segundo_apellido").readNullable[String] and
      (__ \ "nombre").readNullable[String] and      
      (__ \ "fact_total").readNullable[Double]
  )(Factura.apply _)

  val _set = {
    get[Option[Long]]("fact_numero") ~
      get[Option[DateTime]]("fact_fecha") ~
      get[Option[String]]("fact_descripcion") ~
      get[Option[String]]("tipo_comprobante") ~
      get[Option[Int]]("id_comprobante") ~
      get[Option[DateTime]]("fecha_comprobante") ~
      get[Option[Int]]("id_identificacion") ~
      get[Option[String]]("id_persona") ~
      get[Option[String]]("id_empleado") ~
      get[Option[Int]]("fact_estado") ~ 
      get[Option[String]]("primer_apellido") ~
      get[Option[String]]("segundo_apellido") ~
      get[Option[String]]("nombre") ~
      get[Option[Double]]("fact_total") map {
      case fact_numero ~
            fact_fecha ~
            fact_descripcion ~
            tipo_comprobante ~
            id_comprobante ~
            fecha_comprobante ~
            id_identificacion ~
            id_persona ~
            id_empleado ~
            fact_estado ~
            primer_apellido ~
            segundo_apellido ~
            nombre ~
            fact_total =>
        Factura(
          fact_numero,
          fact_fecha,
          fact_descripcion,
          tipo_comprobante,
          id_comprobante,
          fecha_comprobante,
          id_identificacion,
          id_persona,
          id_empleado,
          fact_estado,
          None,
          primer_apellido,
          segundo_apellido,
          nombre,
          fact_total
        )
    }
  }
}

object FacturaNota {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  val _set = {
    get[Option[String]]("fact_nota_tipo") ~
      get[Option[Long]]("fact_nota_numero") ~
      get[Option[DateTime]]("fact_nota_fecha") ~
      get[Option[String]]("fact_nota_descripcion") ~
      get[Option[Int]]("fact_numero") ~
      get[Option[String]]("fact_cufe") ~
      get[Option[Int]]("id_identificacion") ~
      get[Option[String]]("id_persona") ~
      get[Option[String]]("id_empleado") ~
      get[Option[Int]]("fact_nota_estado") ~
      get[Option[String]]("primer_apellido") ~
      get[Option[String]]("segundo_apellido") ~
      get[Option[String]]("nombre") ~
      get[Option[Double]]("fact_nota_total") map {
      case fact_nota_tipo ~
            fact_nota_numero ~
            fact_nota_fecha ~
            fact_nota_descripcion ~
            fact_numero ~
            fact_cufe ~
            id_identificacion ~
            id_persona ~
            id_empleado ~
            fact_nota_estado ~
            primer_apellido ~
            segundo_apellido ~
            nombre ~
            fact_nota_total =>
        FacturaNota(
          fact_nota_tipo,
          fact_nota_numero,
          fact_nota_fecha,
          fact_nota_descripcion,
          fact_numero,
          fact_cufe,
          id_identificacion,
          id_persona,
          id_empleado,
          fact_nota_estado,
          None,
          primer_apellido,
          segundo_apellido,
          nombre,
          fact_nota_total
        )
    }
  }
}

object FacturaNotaItem {
  implicit val yourJodaDateReads =
    JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val yourJodaDateWrites =
    JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ'")

  val _set = {
    get[Option[Long]]("fanoit_id") ~
      get[Option[String]]("fact_nota_tipo") ~
      get[Option[Long]]("fact_nota_numero") ~
      get[Option[String]]("fanoit_detalle") ~
      get[Option[Int]]("fanoit_cantidad") ~
      get[Option[Double]]("fanoit_valorunitario") ~
      get[Option[Double]]("fanoit_tasaiva") ~
      get[Option[Double]]("fanoit_valoriva") ~
      get[Option[Double]]("fanoit_total") map {
      case fanoit_id ~
            fact_nota_tipo ~
            fact_nota_numero ~
            fanoit_detalle ~
            fanoit_cantidad ~
            fanoit_valorunitario ~
            fanoit_tasaiva ~
            fanoit_valoriva ~
            fanoit_total =>
        FacturaNotaItem(
          fanoit_id,
          fact_nota_tipo,
          fact_nota_numero,
          fanoit_detalle,
          fanoit_cantidad,
          fanoit_valorunitario,
          fanoit_tasaiva,
          fanoit_valoriva,
          fanoit_total
        )
    }
  }

}

class FacturaRepository @Inject()(
    dbapi: DBApi,
    personaService: PersonaRepository,
    usuarioService: UsuarioRepository
)(implicit ec: DatabaseExecutionContext) {
  private val db = dbapi.database("default")

  /**
    Recuperar una Factura usando su FACT_NUMERO
    @param fact_numero: Long
    */
  def buscarPorNumero(fact_numero: Long): Future[Option[Factura]] =
    Future[Option[Factura]] {
      db.withConnection { implicit connection =>
        val f = SQL(
          """SELECT  f1.*, gp1.PRIMER_APELLIDO, gp1.SEGUNDO_APELLIDO, gp1.NOMBRE, (SELECT SUM(FAIT_TOTAL) FROM FACTURA_ITEM fi1 WHERE fi1.FACT_NUMERO = f1.FACT_NUMERO) AS FACT_TOTAL FROM FACTURA f1
                     LEFT JOIN "gen$persona" gp1 ON gp1.ID_IDENTIFICACION = f1.ID_IDENTIFICACION AND gp1.ID_PERSONA = f1.ID_PERSONA
                     WHERE f1.FACT_ESTADO <> 9 AND f1.FACT_NUMERO = {fact_numero}"""
        ).on(
            'fact_numero -> fact_numero
          )
          .as(Factura._set.singleOpt)
        f match {
          case Some(f) =>
            val fis = SQL(
              "SELECT * FROM FACTURA_ITEM WHERE FACT_NUMERO = {fact_numero}"
            ).on('fact_numero -> f.fact_numero).as(FacturaItem._set *)
            var factura = f.copy(items = Some(fis))
            Some(factura)
          case None => None
        }
      }
    }

  def cuentaFactura(): Long = {
    db.withConnection { implicit connection =>
      SQL("""SELECT COUNT(*) FROM FACTURA f1 WHERE f1.FACT_ESTADO <> 9""").as(
        SqlParser.scalar[Long].single
      )
    }
  }

  def todosFactura(
      page_size: scala.Long,
      current_page: scala.Long,
      empr_id: scala.Long,
      orderby: String,
      filter: String
  ): Future[Iterable[Factura]] = Future[Iterable[Factura]] {
    var _list = new ListBuffer[Factura]()
    db.withConnection { implicit connection =>
      var query = """SELECT FIRST {page_size} SKIP ({page_size} * ({current_page} - 1)) f1.*, gp1.PRIMER_APELLIDO, gp1.SEGUNDO_APELLIDO, gp1.NOMBRE, (SELECT SUM(FAIT_TOTAL) FROM FACTURA_ITEM fi1 WHERE fi1.FACT_NUMERO = f1.FACT_NUMERO) AS FACT_TOTAL FROM FACTURA f1
                     LEFT JOIN "gen$persona" gp1 ON gp1.ID_IDENTIFICACION = f1.ID_IDENTIFICACION AND gp1.ID_PERSONA = f1.ID_PERSONA
                     WHERE f1.FACT_ESTADO <> 9"""
      if (!filter.isEmpty) {
        query = query + " and " + filter
      }
      if (!orderby.isEmpty) {
        query = query + s" ORDER BY $orderby"
      } else {
        query = query + s" ORDER BY FACT_NUMERO DESC"
      }      

      val facts = SQL(query).on(
          'page_size -> page_size,
          'current_page -> current_page
        )
        .as(Factura._set *)
      for(fact <- facts) {
        val items = SQL("""SELECT * FROM FACTURA_ITEM fi1 WHERE fi1.FACT_NUMERO = {fact_numero}""").
        on(
          'fact_numero -> fact.fact_numero
        ).as(FacturaItem._set *)
        val factura = fact.copy(items = Some(items))
        _list += factura
      }
      _list.toList
    }
  }

 // NOTA DEBITO
  def cuentaNotaDebito(): Long = {
    db.withConnection { implicit connection =>
      SQL("""SELECT COUNT(*) FROM FACTURA_NOTA fn1 WHERE fn1.FACT_NOTA_TIPO = 'D' AND fn1.FACT_NOTA_ESTADO <> 9""").as(
        SqlParser.scalar[Long].single
      )
    }
  }

  def todosNotaDebito(
      page_size: scala.Long,
      current_page: scala.Long,
      empr_id: scala.Long,
      orderby: String,
      filter: String
  ): Future[Iterable[FacturaNota]] = Future[Iterable[FacturaNota]] {
    var _list = new ListBuffer[FacturaNota]()
    db.withConnection { implicit connection =>
      var query = """SELECT FIRST {page_size} SKIP ({page_size} * ({current_page} - 1)) fn1.*, gp1.ID_IDENTIFICACION, gp1.ID_PERSONA, gp1.PRIMER_APELLIDO, gp1.SEGUNDO_APELLIDO, gp1.NOMBRE, (SELECT SUM(FANOIT_TOTAL) FROM FACTURA_NOTA_ITEM fni1 WHERE fni1.FACT_NOTA_TIPO = 'D' AND fni1.FACT_NOTA_NUMERO = fn1.FACT_NOTA_NUMERO) AS FACT_NOTA_TOTAL FROM FACTURA_NOTA fn1
                     LEFT JOIN FACTURA f1 ON f1.FACT_NUMERO = fn1.FACT_NUMERO
                     LEFT JOIN "gen$persona" gp1 ON gp1.ID_IDENTIFICACION = f1.ID_IDENTIFICACION AND gp1.ID_PERSONA = f1.ID_PERSONA
                     WHERE fn1.FACT_NOTA_ESTADO <> 9 AND fn1.FACT_NOTA_TIPO = 'D'"""
      if (!filter.isEmpty) {
        query = query + " AND " + filter
      }
      if (!orderby.isEmpty) {
        query = query + s" ORDER BY $orderby"
      } else {
        query = query + s" ORDER BY fn1.FACT_NOTA_NUMERO DESC"
      }
      val notas = SQL(query).on(
          'page_size -> page_size,
          'current_page -> current_page
        )
        .as(FacturaNota._set *)
      for(nota <- notas) {
        val items = SQL("""SELECT * FROM FACTURA_NOTA_ITEM fni1 WHERE fni1.FACT_NOTA_TIPO = 'D' AND fni1.FACT_NOTA_NUMERO = {fact_nota_numero}""").
        on(
          'fact_nota_numero -> nota.fact_nota_numero
        ).as(FacturaNotaItem._set *)
        val fnota = nota.copy(items = Some(items))
        _list += fnota
      }
      _list.toList
    }
  }

 // NOTA CREDITO
  def cuentaNotaCredito(): Long = {
    db.withConnection { implicit connection =>
      SQL("""SELECT COUNT(*) FROM FACTURA_NOTA fn1 WHERE fn1.FACT_NOTA_TIPO = 'C' AND fn1.FACT_NOTA_ESTADO <> 9""").as(
        SqlParser.scalar[Long].single
      )
    }
  }

  def todosNotaCredito(
      page_size: scala.Long,
      current_page: scala.Long,
      empr_id: scala.Long,
      orderby: String,
      filter: String
  ): Future[Iterable[FacturaNota]] = Future[Iterable[FacturaNota]] {
    var _list = new ListBuffer[FacturaNota]()
    db.withConnection { implicit connection =>
      var query = """SELECT FIRST {page_size} SKIP ({page_size} * ({current_page} - 1)) fn1.*, gp1.ID_IDENTIFICACION, gp1.ID_PERSONA, gp1.PRIMER_APELLIDO, gp1.SEGUNDO_APELLIDO, gp1.NOMBRE, (SELECT SUM(FANOIT_TOTAL) FROM FACTURA_NOTA_ITEM fni1 WHERE fni1.FACT_NOTA_TIPO = 'C' AND fni1.FACT_NOTA_NUMERO = fn1.FACT_NOTA_NUMERO) AS FACT_NOTA_TOTAL FROM FACTURA_NOTA fn1
                     LEFT JOIN FACTURA f1 ON f1.FACT_NUMERO = fn1.FACT_NUMERO
                     LEFT JOIN "gen$persona" gp1 ON gp1.ID_IDENTIFICACION = f1.ID_IDENTIFICACION AND gp1.ID_PERSONA = f1.ID_PERSONA
                     WHERE fn1.FACT_NOTA_ESTADO <> 9 AND fn1.FACT_NOTA_TIPO = 'C'"""
      if (!filter.isEmpty) {
        query = query + " AND " + filter
      }
      if (!orderby.isEmpty) {
        query = query + s" ORDER BY $orderby"
      } else {
        query = query + s" ORDER BY FACT_NOTA_NUMERO DESC"
      }      

      val notas = SQL(query).on(
          'page_size -> page_size,
          'current_page -> current_page
        )
        .as(FacturaNota._set *)
      for(nota <- notas) {
        val items = SQL("""SELECT * FROM FACTURA_NOTA_ITEM fni1 WHERE fni1.FACT_NOTA_TIPO = 'C' AND fni1.FACT_NOTA_NUMERO = {fact_nota_numero}""").
        on(
          'fact_nota_numero -> nota.fact_nota_numero
        ).as(FacturaNotaItem._set *)
        val fnota = nota.copy(items = Some(items))
        _list += fnota
      }
      _list.toList
    }
  }
 //
  /**
    Recuperar una Factura usando su FACT_NUMERO
    @param fact_numero: Long
    */
  def buscarPorNumeroDirecto(fact_numero: Long): Factura = {
    db.withConnection { implicit connection =>
      println("Buscando Factura No.:" + fact_numero)
      val f = SQL(
        """SELECT  f1.*, gp1.PRIMER_APELLIDO, gp1.SEGUNDO_APELLIDO, gp1.NOMBRE, (SELECT SUM(FAIT_TOTAL) FROM FACTURA_ITEM fi1 WHERE fi1.FACT_NUMERO = f1.FACT_NUMERO) AS FACT_TOTAL FROM FACTURA f1
                     LEFT JOIN "gen$persona" gp1 ON gp1.ID_IDENTIFICACION = f1.ID_IDENTIFICACION AND gp1.ID_PERSONA = f1.ID_PERSONA
                     WHERE f1.FACT_ESTADO <> 9 AND f1.FACT_NUMERO = {fact_numero}"""
      ).on(
          'fact_numero -> fact_numero
        )
        .as(Factura._set.singleOpt)
      println("Factura Encontrada: " + f)
      f match {
        case Some(f) =>
          println("Buscando Items")
          val fis = SQL(
            "SELECT * FROM FACTURA_ITEM WHERE FACT_NUMERO = {fact_numero}"
          ).on('fact_numero -> f.fact_numero).as(FacturaItem._set *)
          var factura = f.copy(items = Some(fis))
          factura
        case None =>
          var factura = new Factura(
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None
          )
          factura
      }
    }
  }

  /**
    Recuperar una Factura usando su FACT_NUMERO
    @param fact_nota_numero: Long
    */
  def buscarNotaDebitoPorNumeroDirecto(fact_nota_numero: Long): FacturaNota = {
    db.withConnection { implicit connection =>
      println("Buscando Nota Debito No.:" + fact_nota_numero)
      val nd = SQL(
        """SELECT  fn1.*, gp1.ID_IDENTIFICACION, gp1.ID_PERSONA, gp1.PRIMER_APELLIDO, gp1.SEGUNDO_APELLIDO, gp1.NOMBRE, (SELECT SUM(FANOIT_TOTAL) FROM FACTURA_NOTA_ITEM fni1 WHERE fni1.FACT_NOTA_NUMERO = fn1.FACT_NOTA_NUMERO) AS FACT_NOTA_TOTAL FROM FACTURA_NOTA fn1
                     LEFT JOIN FACTURA f1 ON f1.FACT_NUMERO = fn1.FACT_NUMERO
                     LEFT JOIN "gen$persona" gp1 ON gp1.ID_IDENTIFICACION = f1.ID_IDENTIFICACION AND gp1.ID_PERSONA = f1.ID_PERSONA
                     WHERE fn1.FACT_NOTA_ESTADO <> 9 AND fn1.FACT_NOTA_TIPO = 'D' AND fn1.FACT_NOTA_NUMERO = {fact_nota_numero}"""
      ).on(
          'fact_nota_numero -> fact_nota_numero
        )
        .as(FacturaNota._set.singleOpt)
      println("Nota Debito Encontrada: " + nd)
      nd match {
        case Some(nd) =>
          println("Buscando Items")
          val fnis = SQL(
            "SELECT * FROM FACTURA_NOTA_ITEM WHERE FACT_NOTA_TIPO = 'D' AND FACT_NOTA_NUMERO = {fact_nota_numero}"
          ).on('fact_nota_numero -> nd.fact_nota_numero)
            .as(FacturaNotaItem._set *)
          var nota = nd.copy(items = Some(fnis))
          nota
        case None =>
          var nota = new FacturaNota(
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None
          )
          nota
      }
    }
  }

  /**
    Recuperar una Factura usando su FACT_NUMERO
    @param fact_nota_numero: Long
    */
  def buscarNotaCreditoPorNumeroDirecto(fact_nota_numero: Long): FacturaNota = {
    db.withConnection { implicit connection =>
      println("Buscando Nota Credito No.:" + fact_nota_numero)
      val nc = SQL(
        """SELECT  fn1.*, gp1.ID_IDENTIFICACION, gp1.ID_PERSONA, gp1.PRIMER_APELLIDO, gp1.SEGUNDO_APELLIDO, gp1.NOMBRE, (SELECT SUM(FANOIT_TOTAL) FROM FACTURA_NOTA_ITEM fni1 WHERE fni1.FACT_NOTA_NUMERO = fn1.FACT_NOTA_NUMERO) AS FACT_NOTA_TOTAL FROM FACTURA_NOTA fn1
                     LEFT JOIN FACTURA f1 ON f1.FACT_NUMERO = fn1.FACT_NUMERO
                     LEFT JOIN "gen$persona" gp1 ON gp1.ID_IDENTIFICACION = f1.ID_IDENTIFICACION AND gp1.ID_PERSONA = f1.ID_PERSONA
                     WHERE fn1.FACT_NOTA_ESTADO <> 9 AND fn1.FACT_NOTA_TIPO = 'C' AND fn1.FACT_NOTA_NUMERO = {fact_nota_numero}"""
      ).on(
          'fact_nota_numero -> fact_nota_numero
        )
        .as(FacturaNota._set.singleOpt)
      println("Nota Credito Encontrada: " + nc)
      nc match {
        case Some(nc) =>
          println("Buscando Items")
          val fnis = SQL(
            "SELECT * FROM FACTURA_NOTA_ITEM WHERE FACT_NOTA_TIPO = 'C' AND FACT_NOTA_NUMERO = {fact_nota_numero}"
          ).on('fact_nota_numero -> nc.fact_nota_numero)
            .as(FacturaNotaItem._set *)
          var nota = nc.copy(items = Some(fnis))
          nota
        case None =>
          var nota = new FacturaNota(
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None
          )
          nota
      }
    }
  }

  def enviarFactura(fact_numero: Long): Future[_RootInterface] =
    Future[_RootInterface] {
      db.withConnection { implicit connection =>
        var _rootInterface = new _RootInterface(
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None
        )
        val f = buscarPorNumeroDirecto(fact_numero) /* SQL("""SELECT * FROM FACTURA f
                            WHERE f.FACT_NUMERO = {fact_numero}""").on('fact_numero -> fact_numero).as(Factura._set.singleOpt) */
        println("Factura: " + f)
        val persona = personaService.obtenerDirecto(
          f.id_identificacion.get,
          f.id_persona.get
        )
        val direccion = persona.direcciones(0)
        val cod_municipio = direccion.cod_municipio.get
        val codigo_municipio = "%05d".format(cod_municipio);
        val depa_id = codigo_municipio.toString.substring(0, 2)
        println("cod_municipio: " + cod_municipio)
        println("depa_id :" + depa_id)
        val _parseAutorizacion = int("FAAU_ID") ~
          date("FAAU_FECHAFINAL") ~
          date("FAAU_FECHAINICIO") ~
          str("FAAU_NUMAUTORIZACION") ~
          str("FAAU_PREFIJO").? ~
          int("FAAU_SECUENCIAFINAL") ~
          int("FAAU_SECUENCIAINICIAL") map {
          case a ~ b ~ c ~ d ~ e ~ f ~ g => (a, b, c, d, e, f, g)
        }
        println("Buscando Autorización para Factura No. " + fact_numero)
        val autorizacion =
          SQL("""
                                SELECT * FROM FAC_AUTORIZACION 
                                WHERE {fact_numero} BETWEEN FAAU_SECUENCIAINICIAL AND FAAU_SECUENCIAFINAL
                                ORDER BY FAAU_ID DESC""")
            .on(
              'fact_numero -> fact_numero
            )
            .as(_parseAutorizacion.single)
        println("Autorizacion Recibida: " + autorizacion._4)
        val _parseDepartamento = str("depa_id") ~ str("depa_nombre") map {
          case a ~ b => (a, b)
        }
        val departamento = SQL(
          """SELECT * FROM DEPARTAMENTO WHERE DEPA_ID = {depa_id}"""
        ).on('depa_id -> depa_id).as(_parseDepartamento.singleOpt)
        println("Departamento: " + departamento)
        val _parseTipoIde = int("fati_id") map { case a => (a) }
        val tipoiden = SQL(
          """SELECT FATI_ID FROM FAC_TIPO_IDENTIFICACION WHERE FATI_RELACION = {fati_relacion}"""
        ).on('fati_relacion -> f.id_identificacion).as(_parseTipoIde.single)
        println("Tipo Identificacion: " + tipoiden)
        val _parseTipoPer = int("fatp_id") map { case a => (a) }
        val tipoper = SQL(
          """SELECT FATP_ID FROM FAC_TIPO_PERSONA WHERE FATP_RELACION CONTAINING {fati_relacion}"""
        ).on('fati_relacion -> persona.a.get.id_tipo_persona)
          .as(_parseTipoPer.single)
        println("Tipo Persona")
        val _parseSoftwareSeguridad = str("clave_tecnica") ~ str("guid_empresa") ~ str(
          "guid_origen"
        ) ~ str("hash_seguridad") map { case a ~ b ~ c ~ d => (a, b, c, d) }
        val _sSeguridad =
          SQL("SELECT * FROM FAC_SOFTWARE_SEGURIDAD WHERE FAAU_ID = {faau_id}")
            .on(
              'faau_id -> autorizacion._1
            )
            .as(_parseSoftwareSeguridad.single)
        println("Seguridad")
        val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdd = new SimpleDateFormat("yyyy-MM-dd")
        val prefijo = autorizacion._5 match {
          case Some(p) => p
          case None    => ""
        }

        val _autorizacionData = new _AutorizacionFactura(
          Some(sdd.format(autorizacion._2)),
          Some(sdd.format(autorizacion._3)),
          Some(autorizacion._4),
          Some(prefijo),
          Some(autorizacion._6.toString()),
          Some(autorizacion._7.toString())
        )

        val _encabezadoData = new _EncabezadoData(
          Some("10"),
          None,
          None,
          Some(sdf.format(f.fact_fecha.get.toDate)),
          None,
          None,
          Some("01"),
          None
        )

        var _sendemail = persona.d.get.email.exists(_.trim.nonEmpty)
        var (_ident, _dv) = tipoiden match {
          case 31 =>
            var _id = f.id_persona.get.split("-");
            (_id(0), _id(1))
          case _ => (f.id_persona.get, "")
        }
        val _compradorData = new _CompradorFactura(
          persona.a.get.primer_apellido,
          direccion.municipio,
          Some(direccion.cod_municipio.get.toString),
          Some(depa_id.toString),
          Some(depa_id.toString + "0001"),
          persona.d.get.email,
          Some(_dv),
          Some(departamento.get._2),
          direccion.direccion,
          Some(_sendemail),
          Some(_ident),
          None,
          Some(
            persona.a.get.nombre.get.concat(
              " ".concat(
                persona.a.get.primer_apellido.get
                  .concat(" ".concat(persona.a.get.segundo_apellido.get))
              )
            )
          ),
          Some("COLOMBIA"),
          Some(f.id_comprobante.toString()),
          Some("CO"),
          tipoiden match {
            case 31 => None
            case _  => persona.a.get.nombre
          },
          tipoiden match {
            case 31 => persona.a.get.nombre
            case _  => None
          },
          Some("R-99-PN"),
          direccion.barrio,
          None,
          direccion.telefono1,
          Some(tipoiden.toString),
          Some(tipoper.toString),
          Some("04")
        )
        val _emisorData = new _EmisorData(
          //Some("7"),
          //Some("901180226"),
          Some("5"),
          Some("804015942"),
          Some(31),
          Some(1)
        )
        var _listDetalleData = new ListBuffer[_LsDetalle]()
        var _listDetalleImpuestoData = new ListBuffer[_LsDetalleImpuesto]()
        var _totalFactura = 0D
        var i = 1
        println("Items:" + f.items)
        f.items.foreach { items =>
          items.foreach { item =>
            val _detalleData = new _LsDetalle(
              Some("1"),
              Some("999"),
              Some(item.fait_detalle.get),
              Some(item.fait_detalle.get),
              item.fait_detalle,
              Some(i),
              Some(item.fait_valorunitario.get.toString),
              Some(item.fait_valorunitario.get.toString),
              Some(item.fait_valorunitario.get.toString),
              None,
              Some("ZZ")
            )

            val _detalleImpuestoData = new _LsDetalleImpuesto(
              Some(item.fait_valorunitario.get.toString),
              Some("01"),
              Some(false),
              None,
              Some("0.0"),
              Some(i),
              Some("0.0")
            )

            i += 1
            _totalFactura = _totalFactura + item.fait_valorunitario.get
            _listDetalleData += _detalleData
            _listDetalleImpuestoData += _detalleImpuestoData
          }
        }

        val _infoMonetarioData = new _InfoMonetarioData(
          Some("COP"),
          Some("0"),
          Some(_totalFactura.toString),
          Some(_totalFactura.toString),
          Some("0"),
          Some("0"),
          Some(_totalFactura.toString),
          Some(_totalFactura.toString)
        )

        val _referenciaFactura = new _ReferenciaFactura(
          None,
          None,
          None,
          None,
          None
        )

        val _softwareSeguridadData = new _SoftwareSeguridad(
          Some(_sSeguridad._1),
          Some(prefijo + f.fact_numero.get.toString),
          Some(_sSeguridad._2),
          Some(_sSeguridad._3),
          Some(_sSeguridad._4),
          Some(prefijo + f.fact_numero.get.toString),
          Some("FAC")
        )

        val _formaPagoData = new _LsFormaPago(Some("10"), Some("1"), None)

        var _listNotasData = new ListBuffer[_LsNota]()
        _listNotasData += new _LsNota(Some("OFICINAS"), Some(1))
        _listNotasData += new _LsNota(Some(" "), Some(2))
        _listNotasData += new _LsNota(Some("PRINCIPAL BUCARAMANGA"), Some(3))
        _listNotasData += new _LsNota(
          Some("Carrera 20 No. 36-06 Edificio Sagrada Familia Of. 407"),
          Some(4)
        )
        _listNotasData += new _LsNota(
          Some("Teléfonos: 6701000 - 3162854212"),
          Some(5)
        )
        _listNotasData += new _LsNota(Some("Bucaramanga"), Some(6))
        _listNotasData += new _LsNota(Some(" "), Some(7))
        _listNotasData += new _LsNota(Some("SUCURSAL FLORIDABLANCA"), Some(8))
        _listNotasData += new _LsNota(
          Some("Carrera 8 No. 43-03 Lagos II"),
          Some(9)
        )
        _listNotasData += new _LsNota(
          Some("Teléfonos: 6750757 - 3173836208"),
          Some(10)
        )
        _listNotasData += new _LsNota(Some("Floridablanca"), Some(11))
        _listNotasData += new _LsNota(Some(" "), Some(12))
        _listNotasData += new _LsNota(
          Some("Email: fap@fundacionapoyo.com"),
          Some(13)
        )
        _listNotasData += new _LsNota(Some("www.fundacionapoyo.com"), Some(14))

        val _listFormaPagoData = new ListBuffer[_LsFormaPago]()
        _listFormaPagoData += _formaPagoData
        _rootInterface = new _RootInterface(
          Some(_autorizacionData),
          Some(_compradorData),
          Some(_emisorData),
          Some(_encabezadoData),
          Some(_infoMonetarioData),
          Some(_listDetalleData),
          None,
          Some(_listDetalleImpuestoData),
          None,
          Some(_referenciaFactura),
          Some(_softwareSeguridadData),
          None,
          None,
          Some(_listFormaPagoData),
          Some(_listNotasData)
        )
        println(_rootInterface)
        _rootInterface

      }
    }

  // FIN ENVIAR FACTURA

  // CREAR NOTA

  def crearNota(nota: FacturaNota, usua_id: Long): Future[Long] = {
    val hoy = Calendar.getInstance().getTime()
    db.withConnection { implicit connection =>
      // Buscar Consecutivo
      val fact_nota_numero = nota.fact_nota_tipo match {
        case Some(t) => t match {
          case "D" => SQL("SELECT GEN_ID(GEN_FACTURA_NOTA_DEBITO_NUMERO, 1) FROM RDB$DATABASE;").as(SqlParser.scalar[Long].single)
          case "C" => SQL("SELECT GEN_ID(GEN_FACTURA_NOTA_CREDITO_NUMERO, 1) FROM RDB$DATABASE;").as(SqlParser.scalar[Long].single)
        }
        case None => -1
      }

      val queryNota = """INSERT INTO FACTURA_NOTA (
                    FACT_NOTA_TIPO, 
                    FACT_NOTA_NUMERO, 
                    FACT_NOTA_FECHA, 
                    FACT_NOTA_DESCRIPCION, 
                    FACT_NUMERO, 
                    FACT_CUFE, 
                    FACT_NOTA_ESTADO, 
                    ID_EMPLEADO) VALUES (
                    {FACT_NOTA_TIPO},
                    {FACT_NOTA_NUMERO},
                    {FACT_NOTA_FECHA},
                    {FACT_NOTA_DESCRIPCION},
                    {FACT_NUMERO},
                    {FACT_CUFE},
                    {FACT_NOTA_ESTADO},
                    {ID_EMPLEADO}
                  ) RETURNING FACT_NUMERO"""
      val queryItem = """INSERT INTO FACTURA_NOTA_ITEM (
                      FACT_NOTA_TIPO,
                      FACT_NOTA_NUMERO,
                      FANOIT_DETALLE,
                      FANOIT_CANTIDAD,
                      FANOIT_VALORUNITARIO,
                      FANOIT_TASAIVA,
                      FANOIT_VALORIVA,
                      FANOIT_TOTAL
                     ) VALUES (
                      {FACT_NOTA_TIPO},
                      {FACT_NOTA_NUMERO},
                      {FANOIT_DETALLE},
                      {FANOIT_CANTIDAD},
                      {FANOIT_VALORUNITARIO},
                      {FANOIT_TASAIVA},
                      {FANOIT_VALORIVA},
                      {FANOIT_TOTAL}
                     )"""
        // Buscar Empleado
        val empleado = usuarioService.buscarPorIdDirecto(usua_id)
        Future.successful(empleado match {
            case Some(user) =>
              // Crear Encabezado Nota
              val creado = SQL(queryNota)
              .on(
                'FACT_NOTA_TIPO -> nota.fact_nota_tipo,
                'FACT_NOTA_NUMERO -> fact_nota_numero,
                'FACT_NOTA_FECHA -> hoy,
                'FACT_NOTA_DESCRIPCION -> nota.fact_nota_descripcion,
                'FACT_NUMERO -> nota.fact_numero,
                'FACT_CUFE -> nota.fact_cufe,
                'FACT_NOTA_ESTADO -> 1,
                'ID_EMPLEADO -> user.id_empleado
              ).executeInsert().get > 0

              if (creado) {
                nota.items match { 
                  case Some(items) => items.map { item => 
                                        SQL(queryItem)
                                        .on(
                                          'FACT_NOTA_TIPO -> nota.fact_nota_tipo,
                                          'FACT_NOTA_NUMERO -> fact_nota_numero,
                                          'FANOIT_DETALLE -> item.fanoit_detalle,
                                          'FANOIT_CANTIDAD -> item.fanoit_cantidad,
                                          'FANOIT_VALORUNITARIO -> item.fanoit_valorunitario,
                                          'FANOIT_TASAIVA -> item.fanoit_tasaiva,
                                          'FANOIT_VALORIVA -> item.fanoit_valoriva,
                                          'FANOIT_TOTAL -> item.fanoit_total
                                        ).executeInsert()
                                      }
                  case None => 0L

                }
              }
              fact_nota_numero
            case None => 0L
          })
    }
  }

  // ENVIAR NOTA DEBITO
  def enviarNotaDebito(fact_nota_numero: Long): Future[_RootInterface] =
    Future[_RootInterface] {
      db.withConnection { implicit connection =>
        var _rootInterface = new _RootInterface(
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None
        )
        val nd = buscarNotaDebitoPorNumeroDirecto(fact_nota_numero) /* SQL("""SELECT * FROM FACTURA f
                            WHERE f.FACT_NUMERO = {fact_numero}""").on('fact_numero -> fact_numero).as(Factura._set.singleOpt) */
        val f = buscarPorNumeroDirecto(nd.fact_numero.get)
        println("Nota Débito: " + nd)
        val persona = personaService.obtenerDirecto(
          nd.id_identificacion.get,
          nd.id_persona.get
        )
        val direccion = persona.direcciones(0)
        val cod_municipio = direccion.cod_municipio.get
        val codigo_municipio = "%05d".format(cod_municipio);
        val depa_id = codigo_municipio.toString.substring(0, 2)
        println("cod_municipio: " + cod_municipio)
        println("depa_id :" + depa_id)
        val _parseAutorizacion = int("FAAU_ID") ~
          date("FAAU_FECHAFINAL") ~
          date("FAAU_FECHAINICIO") ~
          str("FAAU_NUMAUTORIZACION") ~
          str("FAAU_PREFIJO").? ~
          int("FAAU_SECUENCIAFINAL") ~
          int("FAAU_SECUENCIAINICIAL") map {
          case a ~ b ~ c ~ d ~ e ~ f ~ g => (a, b, c, d, e, f, g)
        }
        println("Buscando Autorización para Factura No. " + nd.fact_numero)
        val autorizacion =
          SQL("""
                                SELECT * FROM FAC_AUTORIZACION 
                                WHERE {fact_numero} BETWEEN FAAU_SECUENCIAINICIAL AND FAAU_SECUENCIAFINAL
                                ORDER BY FAAU_ID DESC""")
            .on(
              'fact_numero -> nd.fact_numero
            )
            .as(_parseAutorizacion.single)
        println("Autorizacion Recibida: " + autorizacion._4)
        val _parseDepartamento = str("depa_id") ~ str("depa_nombre") map {
          case a ~ b => (a, b)
        }
        val departamento = SQL(
          """SELECT * FROM DEPARTAMENTO WHERE DEPA_ID = {depa_id}"""
        ).on('depa_id -> depa_id).as(_parseDepartamento.singleOpt)
        println("Departamento: " + departamento)
        val _parseTipoIde = int("fati_id") map { case a => (a) }
        val tipoiden = SQL(
          """SELECT FATI_ID FROM FAC_TIPO_IDENTIFICACION WHERE FATI_RELACION = {fati_relacion}"""
        ).on('fati_relacion -> f.id_identificacion).as(_parseTipoIde.single)
        println("Tipo Identificacion: " + tipoiden)
        val _parseTipoPer = int("fatp_id") map { case a => (a) }
        val tipoper = SQL(
          """SELECT FATP_ID FROM FAC_TIPO_PERSONA WHERE FATP_RELACION CONTAINING {fati_relacion}"""
        ).on('fati_relacion -> persona.a.get.id_tipo_persona)
          .as(_parseTipoPer.single)
        println("Tipo Persona")
        val _parseSoftwareSeguridad = str("clave_tecnica") ~ str("guid_empresa") ~ str(
          "guid_origen"
        ) ~ str("hash_seguridad") map { case a ~ b ~ c ~ d => (a, b, c, d) }
        val _sSeguridad =
          SQL("SELECT * FROM FAC_SOFTWARE_SEGURIDAD WHERE FAAU_ID = {faau_id}")
            .on(
              'faau_id -> autorizacion._1
            )
            .as(_parseSoftwareSeguridad.single)
        println("Seguridad")
        val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdd = new SimpleDateFormat("yyyy-MM-dd")
        val prefijo = autorizacion._5 match {
          case Some(p) => p
          case None    => ""
        }

        val _autorizacionData = new _AutorizacionFactura(
          Some(sdd.format(autorizacion._2)),
          Some(sdd.format(autorizacion._3)),
          Some(autorizacion._4),
          Some(prefijo),
          Some(autorizacion._6.toString()),
          Some(autorizacion._7.toString())
        )

        val _encabezadoData = new _EncabezadoData(
          Some("10"),
          None,
          None,
          Some(sdf.format(nd.fact_nota_fecha.get.toDate)),
          None,
          None,
          Some("92"),
          None
        )

        var _sendemail = persona.d.get.email.exists(_.trim.nonEmpty)
        var (_ident, _dv) = tipoiden match {
          case 31 =>
            var _id = f.id_persona.get.split("-");
            (_id(0), _id(1))
          case _ => (f.id_persona.get, "")
        }
        val _compradorData = new _CompradorFactura(
          persona.a.get.primer_apellido,
          direccion.municipio,
          Some(direccion.cod_municipio.get.toString),
          Some(depa_id.toString),
          Some(depa_id.toString + "0001"),
          persona.d.get.email,
          Some(_dv),
          Some(departamento.get._2),
          direccion.direccion,
          Some(_sendemail),
          Some(_ident),
          None,
          Some(
            persona.a.get.nombre.get.concat(
              " ".concat(
                persona.a.get.primer_apellido.get
                  .concat(" ".concat(persona.a.get.segundo_apellido.get))
              )
            )
          ),
          Some("COLOMBIA"),
          Some(f.id_comprobante.toString()),
          Some("CO"),
          tipoiden match {
            case 31 => None
            case _  => persona.a.get.nombre
          },
          tipoiden match {
            case 31 => persona.a.get.nombre
            case _  => None
          },
          Some("R-99-PN"),
          direccion.barrio,
          None,
          direccion.telefono1,
          Some(tipoiden.toString),
          Some(tipoper.toString),
          Some("04")
        )
        val _emisorData = new _EmisorData(
          //Some("7"),
          //Some("901180226"),
          Some("5"),
          Some("804015942"),
          Some(31),
          Some(1)
        )
        var _listDetalleData = new ListBuffer[_LsDetalle]()
        var _listDetalleImpuestoData = new ListBuffer[_LsDetalleImpuesto]()
        var _totalFactura = 0D
        var i = 1
        println("Items:" + nd.items)
        nd.items.foreach { items =>
          items.foreach { item =>
            val _detalleData = new _LsDetalle(
              Some("1"),
              Some("999"),
              Some(item.fanoit_detalle.get),
              Some(item.fanoit_detalle.get),
              item.fanoit_detalle,
              Some(i),
              Some(item.fanoit_valorunitario.get.toString),
              Some(item.fanoit_valorunitario.get.toString),
              Some(item.fanoit_valorunitario.get.toString),
              None,
              Some("ZZ")
            )

            val _detalleImpuestoData = new _LsDetalleImpuesto(
              Some(item.fanoit_valorunitario.get.toString),
              Some("01"),
              Some(false),
              None,
              Some("0.0"),
              Some(i),
              Some("0.0")
            )

            i += 1
            _totalFactura = _totalFactura + item.fanoit_valorunitario.get
            _listDetalleData += _detalleData
            _listDetalleImpuestoData += _detalleImpuestoData
          }
        }

        val _infoMonetarioData = new _InfoMonetarioData(
          Some("COP"),
          Some("0"),
          Some(_totalFactura.toString),
          Some(_totalFactura.toString),
          Some("0"),
          Some("0"),
          Some(_totalFactura.toString),
          Some(_totalFactura.toString)
        )

        val _referenciaFactura = new _ReferenciaFactura(
          Some(1),
          nd.fact_nota_descripcion,
          Some(nd.fact_numero.get.toString()),
          nd.fact_cufe,
          Some(sdd.format(f.fact_fecha.get.toDate))
        )

        val _softwareSeguridadData = new _SoftwareSeguridad(
          Some(_sSeguridad._1),
          Some(nd.fact_nota_numero.get.toString),
          Some(_sSeguridad._2),
          Some(_sSeguridad._3),
          Some(_sSeguridad._4),
          Some("FPND" + nd.fact_nota_numero.get.toString),
          Some("NDB")
        )

        val _formaPagoData = new _LsFormaPago(Some("10"), Some("1"), None)

        var _listNotasData = new ListBuffer[_LsNota]()
        _listNotasData += new _LsNota(Some("OFICINAS"), Some(1))
        _listNotasData += new _LsNota(Some(" "), Some(2))
        _listNotasData += new _LsNota(Some("PRINCIPAL BUCARAMANGA"), Some(3))
        _listNotasData += new _LsNota(
          Some("Carrera 20 No. 36-06 Edificio Sagrada Familia Of. 407"),
          Some(4)
        )
        _listNotasData += new _LsNota(
          Some("Teléfonos: 6701000 - 3162854212"),
          Some(5)
        )
        _listNotasData += new _LsNota(Some("Bucaramanga"), Some(6))
        _listNotasData += new _LsNota(Some(" "), Some(7))
        _listNotasData += new _LsNota(Some("SUCURSAL FLORIDABLANCA"), Some(8))
        _listNotasData += new _LsNota(
          Some("Carrera 8 No. 43-03 Lagos II"),
          Some(9)
        )
        _listNotasData += new _LsNota(
          Some("Teléfonos: 6750757 - 3173836208"),
          Some(10)
        )
        _listNotasData += new _LsNota(Some("Floridablanca"), Some(11))
        _listNotasData += new _LsNota(Some(" "), Some(12))
        _listNotasData += new _LsNota(
          Some("Email: fap@fundacionapoyo.com"),
          Some(13)
        )
        _listNotasData += new _LsNota(Some("www.fundacionapoyo.com"), Some(14))

        val _listFormaPagoData = new ListBuffer[_LsFormaPago]()
        _listFormaPagoData += _formaPagoData
        _rootInterface = new _RootInterface(
          None, //Some(_autorizacionData)
          Some(_compradorData),
          Some(_emisorData),
          Some(_encabezadoData),
          Some(_infoMonetarioData),
          Some(_listDetalleData),
          None,
          Some(_listDetalleImpuestoData),
          None,
          Some(_referenciaFactura),
          Some(_softwareSeguridadData),
          None,
          None,
          Some(_listFormaPagoData),
          Some(_listNotasData)
        )
        println(_rootInterface)
        _rootInterface

      }
    }
  // FIN NOTA DEBITO
  // ENVIAR NOTA CREDITO
  def enviarNotaCredito(fact_nota_numero: Long): Future[_RootInterface] =
    Future[_RootInterface] {
      db.withConnection { implicit connection =>
        var _rootInterface = new _RootInterface(
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None
        )
        val nc = buscarNotaCreditoPorNumeroDirecto(fact_nota_numero) /* SQL("""SELECT * FROM FACTURA f
                            WHERE f.FACT_NUMERO = {fact_numero}""").on('fact_numero -> fact_numero).as(Factura._set.singleOpt) */
        val f = buscarPorNumeroDirecto(nc.fact_numero.get)
        println("Nota Crédito: " + nc)
        val persona = personaService.obtenerDirecto(
          nc.id_identificacion.get,
          nc.id_persona.get
        )
        val direccion = persona.direcciones(0)
        val cod_municipio = direccion.cod_municipio.get
        val codigo_municipio = "%05d".format(cod_municipio);
        val depa_id = codigo_municipio.toString.substring(0, 2)
        println("cod_municipio: " + cod_municipio)
        println("depa_id :" + depa_id)
        val _parseAutorizacion = int("FAAU_ID") ~
          date("FAAU_FECHAFINAL") ~
          date("FAAU_FECHAINICIO") ~
          str("FAAU_NUMAUTORIZACION") ~
          str("FAAU_PREFIJO").? ~
          int("FAAU_SECUENCIAFINAL") ~
          int("FAAU_SECUENCIAINICIAL") map {
          case a ~ b ~ c ~ d ~ e ~ f ~ g => (a, b, c, d, e, f, g)
        }
        println("Buscando Autorización para Factura No. " + nc.fact_numero)
        val autorizacion =
          SQL("""
                                SELECT * FROM FAC_AUTORIZACION 
                                WHERE {fact_numero} BETWEEN FAAU_SECUENCIAINICIAL AND FAAU_SECUENCIAFINAL
                                ORDER BY FAAU_ID DESC""")
            .on(
              'fact_numero -> nc.fact_numero
            )
            .as(_parseAutorizacion.single)
        println("Autorizacion Recibida: " + autorizacion._4)
        val _parseDepartamento = str("depa_id") ~ str("depa_nombre") map {
          case a ~ b => (a, b)
        }
        val departamento = SQL(
          """SELECT * FROM DEPARTAMENTO WHERE DEPA_ID = {depa_id}"""
        ).on('depa_id -> depa_id).as(_parseDepartamento.singleOpt)
        println("Departamento: " + departamento)
        val _parseTipoIde = int("fati_id") map { case a => (a) }
        val tipoiden = SQL(
          """SELECT FATI_ID FROM FAC_TIPO_IDENTIFICACION WHERE FATI_RELACION = {fati_relacion}"""
        ).on('fati_relacion -> f.id_identificacion).as(_parseTipoIde.single)
        println("Tipo Identificacion: " + tipoiden)
        val _parseTipoPer = int("fatp_id") map { case a => (a) }
        val tipoper = SQL(
          """SELECT FATP_ID FROM FAC_TIPO_PERSONA WHERE FATP_RELACION CONTAINING {fati_relacion}"""
        ).on('fati_relacion -> persona.a.get.id_tipo_persona)
          .as(_parseTipoPer.single)
        println("Tipo Persona")
        val _parseSoftwareSeguridad = str("clave_tecnica") ~ str("guid_empresa") ~ str(
          "guid_origen"
        ) ~ str("hash_seguridad") map { case a ~ b ~ c ~ d => (a, b, c, d) }
        val _sSeguridad =
          SQL("SELECT * FROM FAC_SOFTWARE_SEGURIDAD WHERE FAAU_ID = {faau_id}")
            .on(
              'faau_id -> autorizacion._1
            )
            .as(_parseSoftwareSeguridad.single)
        println("Seguridad")
        val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdd = new SimpleDateFormat("yyyy-MM-dd")
        val prefijo = autorizacion._5 match {
          case Some(p) => p
          case None    => ""
        }

        val _autorizacionData = new _AutorizacionFactura(
          Some(sdd.format(autorizacion._2)),
          Some(sdd.format(autorizacion._3)),
          Some(autorizacion._4),
          Some(prefijo),
          Some(autorizacion._6.toString()),
          Some(autorizacion._7.toString())
        )

        val _encabezadoData = new _EncabezadoData(
          Some("10"),
          None,
          None,
          Some(sdf.format(nc.fact_nota_fecha.get.toDate)),
          None,
          None,
          Some("91"),
          None
        )

        var _sendemail = persona.d.get.email.exists(_.trim.nonEmpty)
        var (_ident, _dv) = tipoiden match {
          case 31 =>
            var _id = f.id_persona.get.split("-");
            (_id(0), _id(1))
          case _ => (f.id_persona.get, "")
        }
        val _compradorData = new _CompradorFactura(
          persona.a.get.primer_apellido,
          direccion.municipio,
          Some(direccion.cod_municipio.get.toString),
          Some(depa_id.toString),
          Some(depa_id.toString + "0001"),
          persona.d.get.email,
          Some(_dv),
          Some(departamento.get._2),
          direccion.direccion,
          Some(_sendemail),
          Some(_ident),
          None,
          Some(
            persona.a.get.nombre.get.concat(
              " ".concat(
                persona.a.get.primer_apellido.get
                  .concat(" ".concat(persona.a.get.segundo_apellido.get))
              )
            )
          ),
          Some("COLOMBIA"),
          Some(f.id_comprobante.toString()),
          Some("CO"),
          tipoiden match {
            case 31 => None
            case _  => persona.a.get.nombre
          },
          tipoiden match {
            case 31 => persona.a.get.nombre
            case _  => None
          },
          Some("R-99-PN"),
          direccion.barrio,
          None,
          direccion.telefono1,
          Some(tipoiden.toString),
          Some(tipoper.toString),
          Some("04")
        )
        val _emisorData = new _EmisorData(
          //Some("7"),
          //Some("901180226"),
          Some("5"),
          Some("804015942"),
          Some(31),
          Some(1)
        )
        var _listDetalleData = new ListBuffer[_LsDetalle]()
        var _listDetalleImpuestoData = new ListBuffer[_LsDetalleImpuesto]()
        var _totalFactura = 0D
        var i = 1
        println("Items:" + nc.items)
        nc.items.foreach { items =>
          items.foreach { item =>
            val _detalleData = new _LsDetalle(
              Some("1"),
              Some("999"),
              Some(item.fanoit_detalle.get),
              Some(item.fanoit_detalle.get),
              item.fanoit_detalle,
              Some(i),
              Some(item.fanoit_valorunitario.get.toString),
              Some(item.fanoit_valorunitario.get.toString),
              Some(item.fanoit_valorunitario.get.toString),
              None,
              Some("ZZ")
            )

            val _detalleImpuestoData = new _LsDetalleImpuesto(
              Some(item.fanoit_valorunitario.get.toString),
              Some("01"),
              Some(false),
              None,
              Some("0.0"),
              Some(i),
              Some("0.0")
            )

            i += 1
            _totalFactura = _totalFactura + item.fanoit_valorunitario.get
            _listDetalleData += _detalleData
            _listDetalleImpuestoData += _detalleImpuestoData
          }
        }

        val _infoMonetarioData = new _InfoMonetarioData(
          Some("COP"),
          Some("0"),
          Some(_totalFactura.toString),
          Some(_totalFactura.toString),
          Some("0"),
          Some("0"),
          Some(_totalFactura.toString),
          Some(_totalFactura.toString)
        )

        val _referenciaFactura = new _ReferenciaFactura(
          Some(1),
          nc.fact_nota_descripcion,
          Some(nc.fact_numero.get.toString()),
          nc.fact_cufe,
          Some(sdd.format(f.fact_fecha.get.toDate))
        )

        val _softwareSeguridadData = new _SoftwareSeguridad(
          Some(_sSeguridad._1),
          Some("FPNC" + nc.fact_nota_numero.get.toString),
          Some(_sSeguridad._2),
          Some(_sSeguridad._3),
          Some(_sSeguridad._4),
          Some("FPNC" + nc.fact_nota_numero.get.toString),
          Some("NCR")
        )

        val _formaPagoData = new _LsFormaPago(Some("10"), Some("1"), None)

        var _listNotasData = new ListBuffer[_LsNota]()
        _listNotasData += new _LsNota(Some("OFICINAS"), Some(1))
        _listNotasData += new _LsNota(Some(" "), Some(2))
        _listNotasData += new _LsNota(Some("PRINCIPAL BUCARAMANGA"), Some(3))
        _listNotasData += new _LsNota(
          Some("Carrera 20 No. 36-06 Edificio Sagrada Familia Of. 407"),
          Some(4)
        )
        _listNotasData += new _LsNota(
          Some("Teléfonos: 6701000 - 3162854212"),
          Some(5)
        )
        _listNotasData += new _LsNota(Some("Bucaramanga"), Some(6))
        _listNotasData += new _LsNota(Some(" "), Some(7))
        _listNotasData += new _LsNota(Some("SUCURSAL FLORIDABLANCA"), Some(8))
        _listNotasData += new _LsNota(
          Some("Carrera 8 No. 43-03 Lagos II"),
          Some(9)
        )
        _listNotasData += new _LsNota(
          Some("Teléfonos: 6750757 - 3173836208"),
          Some(10)
        )
        _listNotasData += new _LsNota(Some("Floridablanca"), Some(11))
        _listNotasData += new _LsNota(Some(" "), Some(12))
        _listNotasData += new _LsNota(
          Some("Email: fap@fundacionapoyo.com"),
          Some(13)
        )
        _listNotasData += new _LsNota(Some("www.fundacionapoyo.com"), Some(14))

        val _listFormaPagoData = new ListBuffer[_LsFormaPago]()
        _listFormaPagoData += _formaPagoData
        _rootInterface = new _RootInterface(
          None, //Some(_autorizacionData),
          Some(_compradorData),
          Some(_emisorData),
          Some(_encabezadoData),
          Some(_infoMonetarioData),
          Some(_listDetalleData),
          None,
          Some(_listDetalleImpuestoData),
          None,
          Some(_referenciaFactura),
          Some(_softwareSeguridadData),
          None,
          None,
          Some(_listFormaPagoData),
          Some(_listNotasData)
        )
        println(_rootInterface)
        _rootInterface

      }
    }
  // FIN NOTA CREDITO
}
