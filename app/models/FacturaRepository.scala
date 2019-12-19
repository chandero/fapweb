package models

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
  CompradorCodPostal: Option[String],
  CompradorCorreoElectronico: Option[String],
  CompradorDVIdentificacion: Option[String],
  CompradorDepartamento: Option[String],
  CompradorDireccion: Option[String],
  CompradorEnviarCorreo: Boolean,
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
  DetFacConsecutivo: Int,
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