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

case class _AutorizacionFactura (
  AutFechaFinal: Option[String],
  AutFechaInicio: Option[String],
  AutNumAutorizacion: Option[String],
  AutPrefijo: Option[String],
  AutSecuenciaFinal: Option[String],
  AutSecuenciaInicio: Option[String]
)

case class _CompradorFactura (
  CompradorApellidos: Option[String],
  CompradorCiudad: Option[String],
  CompradorCodCiudad: Option[String],
  CompradorCodDepartamento: Option[String],
//  CompradorCodPostal: Option[String],
  CompradorCorreoElectronico: Option[String],
  CompradorDVIdentificacion: Option[String],
  CompradorDepartamento: Option[String],
  CompradorDireccion: Option[String],
  CompradorEnviarCorreo: Boolean,
  CompradorIdentificacion: Option[String],
//  CompradorImpuesto: Option[String],
  CompradorNombreCompleto: Option[String],
  CompradorNombrePais: Option[String],
  CompradorNotaCont: Option[String],
  CompradorPais: Option[String],
  CompradorPrimerNombre: Option[String],
  CompradorRazonSocial: Option[String],
//  CompradorRespFiscal: Option[String],
  CompradorSector: Option[String],
  CompradorSegundoNombre: Option[String],
  CompradorTelefonoCont: Option[String],
  CompradorTipoIdentificacion: Option[String],
  CompradorTipoPersona: Option[String],
  CompradorTipoRegimen: Option[String]
)

case class _EmisorData (
  EmiDVIdentificacion: Option[String],
  EmiIdentificacion: Option[String],
  EmiTipoIdentificacion: Int,
  EmiTipoPersona: Int
)

case class _EncabezadoData (
  FacCodOperacion: Option[String],
  FacFechaContingencia: Option[String],
  FacFechaFin: Option[String],
  FacFechaHoraFactura: Option[String],
  FacFechaIni: Option[String],
  FacRefContigencia: Option[String],
  FacTipoFactura: Option[String],
  FacTipoRefContigencia: Option[String]
)

case class _InfoMonetarioData (
  FacCodMoneda: Option[String],
  FacTotalAnticipos: Option[String],
  FacTotalBaseImponible: Option[String],
  FacTotalBrutoMasImp: Option[String],
  FacTotalCargos: Option[String],
  FacTotalDescuentos: Option[String],
  FacTotalFactura: Option[String],
  FacTotalImporteBruto: Option[String]
)

case class _LsAnticipos (
  FacAnticipoFecha: Option[String],
  FacAnticipoSec: Option[String],
  FacAnticipoTotal: Option[String]
)

case class _LsCargos (
  FacCargoBase: Option[String],
  FacCargoPorc: Option[String],
  FacCargoRazon: Option[String],
  FacCargoSecuencia: Option[String],
  FacCargoTipo: Option[String],
  FacCargoTotal: Option[String],
  FacCodDescuento: Option[String]
)

case class _LsDetalle (
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

case class _LsDetalleCargos (
  DetSecuencia: Option[String],
  FacCargoBase: Option[String],
  FacCargoPorc: Option[String],
  FacCargoRazon: Option[String],
  FacCargoSecuencia: Option[String],
  FacCargoTipo: Option[String],
  FacCargoTotal: Option[String]
)

case class _LsDetalleImpuesto (
  BaseImponible: Option[String],
  CodigoImpuesto: Option[String],
  EsRetencionImpuesto: Boolean,
  NombreImpuesto: Option[String],
  Porcentaje: Option[String],
  Secuencia: Int,
  ValorImpuesto: Option[String]
)

case class _LsFormaPago (
  FacFormaPago: Option[String],
  FacMetodoPago: Option[String],
  FacVencimientoFac: Option[String]
)

case class _LsImpuestos (
  BaseImponible: Option[String],
  CodigoImpuesto: Option[String],
  EsRetencionImpuesto: Boolean,
  NombreImpuesto: Option[String],
  Porcentaje: Option[String],
  ValorImpuesto: Option[String]
)

case class _SoftwareSeguridad (
  ClaveTecnica: Option[String],
  CodigoErp: Option[String],
  GuidEmpresa: Option[String],
  GuidOrigen: Option[String],
  HashSeguridad: Option[String],
  NumeroDocumento: Option[String],
  TipoDocumento: Option[String]
)

case class _RootInterface (
  AutorizacionFactura: Option[_AutorizacionFactura],
  CompradorFactura: Option[_CompradorFactura],
  EmisorData: Option[_EmisorData],
  EncabezadoData: Option[_EncabezadoData],
  InfoMonetarioData: Option[_InfoMonetarioData],
  LsDetalle: Option[Seq[_LsDetalle]],
  LsDetalleCargos: Option[Seq[_LsDetalleCargos]],
  LsDetalleImpuesto: Option[Seq[_LsDetalleImpuesto]],
  LsImpuestos: Option[Seq[_LsImpuestos]],
  ReferenciaFactura: Option[String],
  SoftwareSeguridad: Option[_SoftwareSeguridad],
  lsAnticipos: Option[Seq[_LsAnticipos]],
  lsCargos: Option[Seq[_LsCargos]],
  lsFormaPago: Option[Seq[_LsFormaPago]],
  lsNotas: Option[Seq[String]]
)

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
//      "CompradorCodPostal" -> r.CompradorCodPostal,
      "CompradorCorreoElectronico" -> r.CompradorCorreoElectronico,
      "CompradorDVIdentificacion" -> r.CompradorDVIdentificacion,
      "CompradorDepartamento" -> r.CompradorDepartamento,
      "CompradorDireccion" -> r.CompradorDireccion,
      "CompradorEnviarCorreo" -> r.CompradorEnviarCorreo,
      "CompradorIdentificacion" -> r.CompradorIdentificacion,
//      "CompradorImpuesto" -> r.CompradorImpuesto,
      "CompradorNombreCompleto" -> r.CompradorNombreCompleto,
      "CompradorNombrePais" -> r.CompradorNombrePais,
      "CompradorNotaCont" -> r.CompradorNotaCont,
      "CompradorPais" -> r.CompradorPais,
      "CompradorPrimerNombre" -> r.CompradorPrimerNombre,
      "CompradorRazonSocial" -> r.CompradorRazonSocial,
//    "CompradorRespFiscal" -> r.CompradorRespFiscal,
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
//    (__ \ "CompradorCodPostal").readNullable[String] and
    (__ \ "CompradorCorreoElectronico").readNullable[String] and
    (__ \ "CompradorDVIdentificacion").readNullable[String] and
    (__ \ "CompradorDepartamento").readNullable[String] and
    (__ \ "CompradorDireccion").readNullable[String] and
    (__ \ "CompradorEnviarCorreo").readNullable[Boolean] and
    (__ \ "CompradorIdentificacion").readNullable[String] and
//    (__ \ "CompradorImpuesto").readNullable[String] and
    (__ \ "CompradorNombreCompleto").readNullable[String] and
    (__ \ "CompradorNombrePais").readNullable[String] and
    (__ \ "CompradorNotaCont").readNullable[String] and
    (__ \ "CompradorPais").readNullable[String] and
    (__ \ "CompradorPrimerNombre").readNullable[String] and
    (__ \ "CompradorRazonSocial").readNullable[String] and
//    (__ \ "CompradorRespFiscal").readNullable[String] and
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
      "EmiDVIdentificacion" -> r.FacCodOperacion,
      "EmiIdentificacion" -> r.FacFechaContingencia,
      "EmiTipoIdentificacion" -> r.FacFechaFin,
      "EmiTipoPersona" -> r.FacFechaHoraFactura
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
      "FacCargoTotal" -> r.FacCargoTotal                            
    )
  }

  implicit val Reads: Reads[_LsCargos] = (
    (__ \ "FacCargoBase").readNullable[String] and
    (__ \ "FacCargoPorc").readNullable[String] and
    (__ \ "FacCargoRazon").readNullable[String] and
    (__ \ "FacCargoSecuencia").readNullable[String] and
    (__ \ "FacCargoTipo").readNullable[String] and
    (__ \ "FacCargoTotal").readNullable[String]
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
    (__ \ "Codigo").readNullable[Boolean] and
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
    (__ \ "FacCargoPorc").readNullable[Boolean] and
    (__ \ "FacCargoRazon").readNullable[String] and
    (__ \ "FacCargoSecuencia").readNullable[String] and
    (__ \ "FacCargoTipo").readNullable[Int] and
    (__ \ "FacCargoTotal").readNullable[String] and
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
    (__ \ "ValorImpuesto").readNullable[String] and
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
    (__ \ "FacVencimientoFac").readNullable[Boolean]
  )(_LsFormaPago.apply _)
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
    (__ \ "ReferenciaFactura").readNullable[_ReferenciaFactura] and
    (__ \ "SoftwareSeguridad").readNullable[_SoftwareSeguridad] and
    (__ \ "lsAnticipos").readNullable[Seq[_LsAnticipos]] and
    (__ \ "lsCargos").readNullable[Seq[_LsCargos]] and
    (__ \ "lsFormaPago").readNullable[Seq[_LsFormaPago]] and
    (__ \ "lsNotas").readNullable[Seq[String]]
  )(_RootInterface.apply _)
}