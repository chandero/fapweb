package models

import org.joda.time.DateTime

import play.api.libs.functional.syntax._

import anorm._
import anorm.SqlParser.{get, str}
import anorm.JodaParameterMetaData._

case class PE_VAR_INDEPENDIENTE(
    VAR_INDEPENDIENTE_ID: Option[Int],
    VAR_INDEPENDIENTE_CODIGO: Option[String],
    VAR_INDEPENDIENTE_COEFICIENTE: Option[Double],
    VAR_INDEPENDIENTE_ACTIVA: Option[Boolean],
    TIPO_CARTERA_ID: Option[Int],
    VAR_INDEPENDIENTE_LIBRANZA: Option[Boolean],
    VAR_INDEPENDIENTE_ORDEN: Option[Int]
)

object PE_VAR_INDEPENDIENTE {
  val _set = {
    get[Option[Int]]("VAR_INDEPENDIENTE_ID") ~
      get[Option[String]]("VAR_INDEPENDIENTE_CODIGO") ~
      get[Option[Double]]("VAR_INDEPENDIENTE_COEFICIENTE") ~
      get[Option[Int]]("VAR_INDEPENDIENTE_ACTIVA") ~
      get[Option[Int]]("TIPO_CARTERA_ID") ~
      get[Option[Int]]("VAR_INDEPENDIENTE_LIBRANZA") ~
      get[Option[Int]]("VAR_INDEPENDIENTE_ORDEN") map {
      case id ~
            codigo ~
            coeficiente ~
            activa ~
            cartera ~
            libranza ~
            orden =>
        PE_VAR_INDEPENDIENTE(
          id,
          codigo,
          coeficiente,
          if (activa.get == 0) Some(false) else Some(true),
          cartera,
          if (libranza.get == 0) Some(false) else Some(true),
          orden
        )
    }
  }
}

case class PE_TIPO_CARTERA(
    TIPO_CARTERA_ID: Option[Int],
    TIPO_CARTERA_DESCRIPCION: Option[String]
)

object PE_TIPO_CARTERA {
  val _set = {
    get[Option[Int]]("TIPO_CARTERA_ID") ~
      get[Option[String]]("TIPO_CARTERA_DESCRIPCION") map {
      case id ~ descripcion => PE_TIPO_CARTERA(id, descripcion)
    }
  }
}

case class PE_GARANTIA(
    GARANTIA_ID: Option[Int],
    GARANTIA_HIPOTECARIA: Option[Boolean],
    GARANTIA_MORA_MIN: Option[Int],
    GARANTIA_MORA_MAX: Option[Int],
    GARANTIA_PORCENTAJE: Option[Double]
)

object PE_GARANTIA {
  val _set = {
    get[Option[Int]]("GARANTIA_ID") ~
      get[Option[Int]]("GARANTIA_HIPOTECARIA") ~
      get[Option[Int]]("GARANTIA_MORA_MIN") ~
      get[Option[Int]]("GARANTIA_MORA_MAX") ~
      get[Option[Double]]("GARANTIA_PORCENTAJE") map {
      case a ~ b ~ c ~ d ~ e =>
        PE_GARANTIA(a, { if (b.get == 0) Some(false) else Some(true) }, c, d, e)
    }
  }
}

case class PE_TIPO_GARANTIA(
    TIPO_GARANTIA_ID: Option[Int],
    TIPO_GARANTIA_DESCRIPCION: Option[String],
    TIPO_GARANTIA_ES_IDONEA: Option[Boolean]
)

object PE_TIPO_GARANTIA {
  val _set = {
    get[Option[Int]]("TIPO_GARANTIA_ID") ~
      get[Option[String]]("TIPO_GARANTIA_DESCRIPCION") ~
      get[Option[Int]]("TIPO_GARANTIA_ES_IDONEA") map {
      case a ~ b ~ c =>
        PE_TIPO_GARANTIA(a, b, { if (c.get == 0) Some(false) else Some(true) })
    }
  }
}

case class PE_EDAD(
    EDAD_ID: Option[Int],
    EDAD_CODIGO: Option[String],
    EDAD_DESCRIPCION: Option[String]
)

object PE_EDAD {
  val _set = {
    get[Option[Int]]("EDAD_ID") ~
      get[Option[String]]("EDAD_CODIGO") ~
      get[Option[String]]("EDAD_DESCRIPCION") map {
      case a ~ b ~ c => PE_EDAD(a, b, c)
    }
  }
}

case class PE_DETERIORO(
    DETERIORO_ID: Option[Int],
    TIPO_CARTERA_ID: Option[Int],
    DETERIORO_EDAD: Option[String],
    DETERIORO_TASA: Option[Double],
    DETERIORO_FECHA_REGISTRO: Option[DateTime]
)

object PE_DETERIORO {
  val _set = {
    get[Option[Int]]("DETERIORO_ID") ~
      get[Option[Int]]("TIPO_CARTERA_ID") ~
      get[Option[String]]("DETERIORO_EDAD") ~
      get[Option[Double]]("DETERIORO_TASA") ~
      get[Option[DateTime]]("DETERIORO_FECHA_REGISTRO") map {
      case a ~ b ~ c ~ d ~ e => PE_DETERIORO(a, b, c, d, e)
    }
  }
}

case class PE_USUARIO(
    USUARIO_TIPO_IDENTIFICACION: Option[String],
    USUARIO_ID_PERSONA: Option[String],
    USUARIO_PRIMER_APELLIDO: Option[String],
    USUARIO_SEGUNDO_APELLIDO: Option[String],
    USUARIO_NOMBRES: Option[String],
    USUARIO_FECHA_VINCULACION: Option[DateTime],
    USUARIO_TELEFONO: Option[String],
    USUARIO_DIRECCION: Option[String],
    USUARIO_ASOCIADO: Option[Int],
    USUARIO_ACTIVO: Option[Int],
    USUARIO_CIIU: Option[Int],
    USUARIO_MUNICIPIO: Option[Int],
    USUARIO_EMAIL: Option[String],
    USUARIO_GENERO: Option[Int],
    USUARIO_EMPLEADO: Option[Int],
    USUARIO_TIPO_CONTRATO: Option[Int],
    USUARIO_NIVEL_ESCOLARIDAD: Option[Int],
    USUARIO_ESTRATO: Option[Int],
    USUARIO_NIVEL_INGRESOS: Option[Int],
    USUARIO_FECHA_NACIMIENTO: Option[DateTime],
    USUARIO_ESTADO_CIVIL: Option[Int],
    USUARIO_CABEZA_FAMILIA: Option[Int],
    USUARIO_OCUPACION: Option[Int],
    USUARIO_SECTOR_ECONOMICO: Option[Int],
    USUARIO_JORNADA_LABORAL: Option[Int],
    USUARIO_FECHA_RETIRO: Option[DateTime],
    USUARIO_ASAMBLEA: Option[Int],
    CREATED_AT: Option[DateTime],
    UPDATED_AT: Option[DateTime],
    DELETED_AT: Option[DateTime]
)

object PE_USUARIO {
  val _set = {
    get[Option[String]]("USUARIO_TIPO_IDENTIFICACION") ~
      get[Option[String]]("USUARIO_ID_PERSONA") ~
      get[Option[String]]("USUARIO_PRIMER_APELLIDO") ~
      get[Option[String]]("USUARIO_SEGUNDO_APELLIDO") ~
      get[Option[String]]("USUARIO_NOMBRES") ~
      get[Option[DateTime]]("USUARIO_FECHA_VINCULACION") ~
      get[Option[String]]("USUARIO_TELEFONO") ~
      get[Option[String]]("USUARIO_DIRECCION") ~
      get[Option[Int]]("USUARIO_ASOCIADO") ~
      get[Option[Int]]("USUARIO_ACTIVO") ~
      get[Option[Int]]("USUARIO_CIIU") ~
      get[Option[Int]]("USUARIO_MUNICIPIO") ~
      get[Option[String]]("USUARIO_EMAIL") ~
      get[Option[Int]]("USUARIO_GENERO") ~
      get[Option[Int]]("USUARIO_EMPLEADO") ~
      get[Option[Int]]("USUARIO_TIPO_CONTRATO") ~
      get[Option[Int]]("USUARIO_NIVEL_ESCOLARIDAD") ~
      get[Option[Int]]("USUARIO_ESTRATO") ~
      get[Option[Int]]("USUARIO_NIVEL_INGRESOS") ~
      get[Option[DateTime]]("USUARIO_FECHA_NACIMIENTO") ~
      get[Option[Int]]("USUARIO_ESTADO_CIVIL") ~
      get[Option[Int]]("USUARIO_CABEZA_FAMILIA") ~
      get[Option[Int]]("USUARIO_OCUPACION") ~
      get[Option[Int]]("USUARIO_SECTOR_ECONOMICO") ~
      get[Option[Int]]("USUARIO_JORNADA_LABORAL") ~
      get[Option[DateTime]]("USUARIO_FECHA_RETIRO") ~
      get[Option[Int]]("USUARIO_ASAMBLEA") ~
      get[Option[DateTime]]("CREATED_AT") ~
      get[Option[DateTime]]("UPDATED_AT") ~
      get[Option[DateTime]]("DELETED_AT") map {
      case a ~ b ~ c ~ d ~ e ~ f ~ g ~ h ~ i ~ j ~ k ~ l ~ m ~ n ~ o ~ p ~ q ~ r ~ s ~ t ~ u ~ v ~ w ~ x ~ y ~ z ~ a1 ~ a2 ~ a3 ~ a4 =>
        PE_USUARIO(
          a,
          b,
          c,
          d,
          e,
          f,
          g,
          h,
          i,
          j,
          k,
          l,
          m,
          n,
          o,
          p,
          q,
          r,
          s,
          t,
          u,
          v,
          w,
          x,
          y,
          z,
          a1,
          a2,
          a3,
          a4
        )
    }
  }
}

case class PE_CARTERA(
    CARTERA_TIPO_IDENTIFICACION: Option[String],
    CARTERA_ID_PERSONA: Option[String],
    CARTERA_CODIGO_CONTABLE: Option[String],
    CARTERA_MODIFICACION_CREDITO: Option[Int],
    CARTERA_ID_COLOCACION: Option[String],
    CARTERA_FECHA_DESEMBOLSO: Option[DateTime],
    CARTERA_FECHA_VENCIMIENTO: Option[DateTime],
    CARTERA_MOROSIDAD: Option[Int],
    CARTERA_TIPO_CUOTA: Option[Int],
    CARTERA_ALTURA_CUOTA: Option[Int],
    CARTERA_AMORTI_INTERES: Option[Int],
    CARTERA_MODALIDAD: Option[Int],
    CARTERA_TASA_NOMINAL: Option[Double],
    CARTERA_TASA_EFECTIVA: Option[Double],
    CARTERA_VALOR_PRESTAMO: Option[Double],
    CARTERA_VALOR_CUOTA: Option[Double],
    CARTERA_SALDO_CAPITAL: Option[Double],
    CARTERA_SALDO_INTERES: Option[Double],
    CARTERA_OTROS_SALDOS: Option[Double],
    CARTERA_GARANTIA: Option[Double],
    CARTERA_FECHA_AVALUO: Option[DateTime],
    CARTERA_PROVISION_CAPITAL: Option[Double],
    CARTERA_PROVISION_INTERES: Option[Double],
    CARTERA_CONTINGENCIA: Option[Double],
    CARTERA_VALOR_CUOTA_EXTRA: Option[Double],
    CARTERA_MESES_CUOTA_EXTRA: Option[Int],
    CARTERA_FECHA_ULTIMO_PAGO: Option[DateTime],
    CARTERA_CLASE_GARANTIA: Option[Int],
    CARTERA_DESTINO_CREDITO: Option[String],
    CARTERA_CODIGO_OFICINA: Option[String],
    CARTERA_AMORTIZA_CAPITAL: Option[Int],
    CARTERA_VALOR_MORA: Option[Double],
    CARTERA_TIPO_VIVIENDA: Option[String],
    CARTERA_VIS: Option[String],
    CARTERA_RANGO_TIPO: Option[String],
    CARTERA_ENTIDAD_REDESCUENTO: Option[String],
    CARTERA_MARGEN_REDESCUENTO: Option[String],
    CARTERA_SUBSIDIO: Option[Double],
    CARTERA_DESEMBOLSO: Option[Int],
    CARTERA_MONEDA: Option[Int],
    CARTERA_FECHA_REESTRUCTURACION: Option[DateTime],
    CARTERA_CATE_REESTRUCURACION: Option[String],
    CARTERA_APORTE_SOCIAL: Option[Double],
    CARTERA_LINEA: Option[Int],
    CARTERA_NUMERO_MODIFICACION: Option[Int],
    CARTERA_ESTADO_CREDITO: Option[Int],
    CARTERA_NIT_PATRONAL: Option[String],
    CARTERA_NOMBRE_PATRONAL: Option[String],
    CARTERA_MODCRECE1120: Option[Int],
    CARTERA_TIPOMODCE1120: Option[Int],
    CARTERA_FECHAMODCE1120: Option[DateTime],
    CARTERA_CALIFANTEMODCE1120: Option[String],
    CARTERA_PERIODO_GRACIA: Option[String],
    CARTERA_TARJCREDCUPRO: Option[Int],
    CARTERA_ENTOTOGARANT: Option[Int],
    CARTERA_MODCREDCE1720: Option[Int],
    CARTERA_FECHA_CORTE: Option[String],
    CREATED_AT: Option[DateTime],
    UPDATED_AT: Option[DateTime],
    DELETED_AT: Option[DateTime]
)

object PE_CARTERA {
  val _set = {
    get[Option[String]]("CARTERA_TIPO_IDENTIFICACION") ~
      get[Option[String]]("CARTERA_ID_PERSONA") ~
      get[Option[String]]("CARTERA_CODIGO_CONTABLE") ~
      get[Option[Int]]("CARTERA_MODIFICACION_CREDITO") ~
      get[Option[String]]("CARTERA_ID_COLOCACION") ~
      get[Option[DateTime]]("CARTERA_FECHA_DESEMBOLSO") ~
      get[Option[DateTime]]("CARTERA_FECHA_VENCIMIENTO") ~
      get[Option[Int]]("CARTERA_MOROSIDAD") ~
      get[Option[Int]]("CARTERA_TIPO_CUOTA") ~
      get[Option[Int]]("CARTERA_ALTURA_CUOTA") ~
      get[Option[Int]]("CARTERA_AMORTI_INTERES") ~
      get[Option[Int]]("CARTERA_MODALIDAD") ~
      get[Option[Double]]("CARTERA_TASA_NOMINAL") ~
      get[Option[Double]]("CARTERA_TASA_EFECTIVA") ~
      get[Option[Double]]("CARTERA_VALOR_PRESTAMO") ~
      get[Option[Double]]("CARTERA_VALOR_CUOTA") ~
      get[Option[Double]]("CARTERA_SALDO_CAPITAL") ~
      get[Option[Double]]("CARTERA_SALDO_INTERES") ~
      get[Option[Double]]("CARTERA_OTROS_SALDOS") ~
      get[Option[Double]]("CARTERA_GARANTIA") ~
      get[Option[DateTime]]("CARTERA_FECHA_AVALUO") ~
      get[Option[Double]]("CARTERA_PROVISION_CAPITAL") ~
      get[Option[Double]]("CARTERA_PROVISION_INTERES") ~
      get[Option[Double]]("CARTERA_CONTINGENCIA") ~
      get[Option[Double]]("CARTERA_VALOR_CUOTA_EXTRA") ~
      get[Option[Int]]("CARTERA_MESES_CUOTA_EXTRA") ~
      get[Option[DateTime]]("CARTERA_FECHA_ULTIMO_PAGO") ~
      get[Option[Int]]("CARTERA_CLASE_GARANTIA") ~
      get[Option[String]]("CARTERA_DESTINO_CREDITO") ~
      get[Option[String]]("CARTERA_CODIGO_OFICINA") ~
      get[Option[Int]]("CARTERA_AMORTIZA_CAPITAL") ~
      get[Option[Double]]("CARTERA_VALOR_MORA") ~
      get[Option[String]]("CARTERA_TIPO_VIVIENDA") ~
      get[Option[String]]("CARTERA_VIS") ~
      get[Option[String]]("CARTERA_RANGO_TIPO") ~
      get[Option[String]]("CARTERA_ENTIDAD_REDESCUENTO") ~
      get[Option[String]]("CARTERA_MARGEN_REDESCUENTO") ~
      get[Option[Double]]("CARTERA_SUBSIDIO") ~
      get[Option[Int]]("CARTERA_DESEMBOLSO") ~
      get[Option[Int]]("CARTERA_MONEDA") ~
      get[Option[DateTime]]("CARTERA_FECHA_REESTRUCTURACION") ~
      get[Option[String]]("CARTERA_CATE_REESTRUCURACION") ~
      get[Option[Double]]("CARTERA_APORTE_SOCIAL") ~
      get[Option[Int]]("CARTERA_LINEA") ~
      get[Option[Int]]("CARTERA_NUMERO_MODIFICACION") ~
      get[Option[Int]]("CARTERA_ESTADO_CREDITO") ~
      get[Option[String]]("CARTERA_NIT_PATRONAL") ~
      get[Option[String]]("CARTERA_NOMBRE_PATRONAL") ~
      get[Option[Int]]("CARTERA_MODCRECE1120") ~
      get[Option[Int]]("CARTERA_TIPOMODCE1120") ~
      get[Option[DateTime]]("CARTERA_FECHAMODCE1120") ~
      get[Option[String]]("CARTERA_CALIFANTEMODCE1120") ~
      get[Option[String]]("CARTERA_PERIODO_GRACIA") ~
      get[Option[Int]]("CARTERA_TARJCREDCUPRO") ~
      get[Option[Int]]("CARTERA_ENTOTOGARANT") ~
      get[Option[Int]]("CARTERA_MODCREDCE1720") ~
      get[Option[String]]("CARTERA_FECHA_CORTE") ~
      get[Option[DateTime]]("CREATED_AT") ~
      get[Option[DateTime]]("UPDATED_AT") ~
      get[Option[DateTime]]("DELETED_AT") map {
      case tipo_identificacion ~
            id_persona ~
            codigo_contable ~
            modificacion_credito ~
            id_colocacion ~
            fecha_desembolso ~
            fecha_vencimiento ~
            morosidad ~
            tipo_cuota ~
            altura_cuota ~
            amorti_interes ~
            modalidad ~
            tasa_nominal ~
            tasa_efectiva ~
            valor_prestamo ~
            valor_cuota ~
            saldo_capital ~
            saldo_interes ~
            otros_saldos ~
            garantia ~
            fecha_avaluo ~
            provision_capital ~
            provision_interes ~
            contingencia ~
            valor_cuota_extra ~
            meses_cuota_extra ~
            fecha_ultimo_pago ~
            clase_garantia ~
            destino_credito ~
            codigo_oficina ~
            amortiza_capital ~
            valor_mora ~
            tipo_vivienda ~
            vis ~
            rango_tipo ~
            entidad_redescuento ~
            margen_redescuento ~
            subsidio ~
            desembolso ~
            moneda ~
            fecha_reestructuracion ~
            cate_reestructuracion ~
            aporte_social ~
            linea ~
            numero_modificacion ~
            estado_credito ~
            nit_patronal ~
            nombre_patronal ~
            modcrece1120 ~
            tipomodce1120 ~
            fechamodce1120 ~
            califantemodce1120 ~
            periodo_gracia ~
            tarjcredcupro ~
            entotogarant ~
            modcredce1720 ~
            fecha_corte ~
            created ~
            updated ~
            deleted =>
        PE_CARTERA(
          tipo_identificacion,
          id_persona,
          codigo_contable,
          modificacion_credito,
          id_colocacion,
          fecha_desembolso,
          fecha_vencimiento,
          morosidad,
          tipo_cuota,
          altura_cuota,
          amorti_interes,
          modalidad,
          tasa_nominal,
          tasa_efectiva,
          valor_prestamo,
          valor_cuota,
          saldo_capital,
          saldo_interes,
          otros_saldos,
          garantia,
          fecha_avaluo,
          provision_capital,
          provision_interes,
          contingencia,
          valor_cuota_extra,
          meses_cuota_extra,
          fecha_ultimo_pago,
          clase_garantia,
          destino_credito,
          codigo_oficina,
          amortiza_capital,
          valor_mora,
          tipo_vivienda,
          vis,
          rango_tipo,
          entidad_redescuento,
          margen_redescuento,
          subsidio,
          desembolso,
          moneda,
          fecha_reestructuracion,
          cate_reestructuracion,
          aporte_social,
          linea,
          numero_modificacion,
          estado_credito,
          nit_patronal,
          nombre_patronal,
          modcrece1120,
          tipomodce1120,
          fechamodce1120,
          califantemodce1120,
          periodo_gracia,
          tarjcredcupro,
          entotogarant,
          modcredce1720,
          fecha_corte,
          created,
          updated,
          deleted
        )
    }
  }
}

case class PE_DEPOSITO(
    USUARIO_TIPO_IDENTIFICACION: Option[String],
    USUARIO_ID_PERSONA: Option[String],
    DEPOSITO_CODIGO_CONTABLE: Option[String],
    DEPOSITO_NOMBRE: Option[String],
    DEPOSITO_TIPO_AHORRO: Option[Int],
    DEPOSITO_AMORTIZACION: Option[Int],
    DEPOSITO_FECHA_APERTURA: Option[DateTime],
    DEPOSITO_PLAZO: Option[Int],
    DEPOSITO_FECHA_VENCIMIENTO: Option[DateTime],
    DEPOSITO_MODALIDAD: Option[Int],
    DEPOSITO_TASA_NOMINAL: Option[Double],
    DEPOSITO_TASA_EFECTIVA: Option[Double],
    DEPOSITO_INTERES_CAUSADO: Option[Double],
    DEPOSITO_SALDO: Option[Double],
    DEPOSITO_INICIAL: Option[Double],
    DEPOSITO_NUMERO_CUENTA: Option[String],
    DEPOSITO_EXCENTA_GMF: Option[Int],
    DEPOSITO_FECHA_ACEPTACION: Option[DateTime],
    DEPOSITO_ESTADO: Option[Int],
    DEPOSITO_BAJO_MONTO: Option[Int],
    DEPOSITO_COTITULARES: Option[Int],
    DEPOSITO_CONJUNTA: Option[Int],
    DEPOSITO_FECHA_CORTE: Option[String],
    CREATED_AT: Option[DateTime],
    UPDATED_AT: Option[DateTime],
    DELETED_AT: Option[DateTime]
)

object PE_DEPOSITO {
  val _set = {
    get[Option[String]]("USUARIO_TIPO_IDENTIFICACION") ~
      get[Option[String]]("USUARIO_ID_PERSONA") ~
      get[Option[String]]("DEPOSITO_CODIGO_CONTABLE") ~
      get[Option[String]]("DEPOSITO_NOMBRE") ~
      get[Option[Int]]("DEPOSITO_TIPO_AHORRO") ~
      get[Option[Int]]("DEPOSITO_AMORTIZACION") ~
      get[Option[DateTime]]("DEPOSITO_FECHA_APERTURA") ~
      get[Option[Int]]("DEPOSITO_PLAZO") ~
      get[Option[DateTime]]("DEPOSITO_FECHA_VENCIMIENTO") ~
      get[Option[Int]]("DEPOSITO_MODALIDAD") ~
      get[Option[Double]]("DEPOSITO_TASA_NOMINAL") ~
      get[Option[Double]]("DEPOSITO_TASA_EFECTIVA") ~
      get[Option[Double]]("DEPOSITO_INTERES_CAUSADO") ~
      get[Option[Double]]("DEPOSITO_SALDO") ~
      get[Option[Double]]("DEPOSITO_INICIAL") ~
      get[Option[String]]("DEPOSITO_NUMERO_CUENTA") ~
      get[Option[Int]]("DEPOSITO_EXCENTA_GMF") ~
      get[Option[DateTime]]("DEPOSITO_FECHA_ACEPTACION") ~
      get[Option[Int]]("DEPOSITO_ESTADO") ~
      get[Option[Int]]("DEPOSITO_BAJO_MONTO") ~
      get[Option[Int]]("DEPOSITO_COTITULARES") ~
      get[Option[Int]]("DEPOSITO_CONJUNTA") ~
      get[Option[String]]("DEPOSITO_FECHA_CORTE") ~
      get[Option[DateTime]]("CREATED_AT") ~
      get[Option[DateTime]]("UPDATED_AT") ~
      get[Option[DateTime]]("DELETED_AT") map {
      case a ~ b ~ c ~ d ~ e ~ f ~ g ~ h ~ i ~ j ~ k ~ l ~ m ~ n ~ o ~ p ~ q ~ r ~ s ~ t ~ u ~ v ~ w ~ x ~ y ~ z =>
        PE_DEPOSITO(
          a,
          b,
          c,
          d,
          e,
          f,
          g,
          h,
          i,
          j,
          k,
          l,
          m,
          n,
          o,
          p,
          q,
          r,
          s,
          t,
          u,
          v,
          w,
          x,
          y,
          z
        )
    }
  }
}

case class PE_APORTE(
    USUARIO_TIPO_IDENTIFICACION: Option[String],
    USUARIO_ID_IDENTIFICACION: Option[String],
    APORTE_SALDO: Option[Double],
    APORTE_MENSUAL: Option[Double],
    APORTE_ORDINARIO: Option[Double],
    APORTE_EXTRAORDINARIO: Option[Double],
    APORTE_REVALORIZACION: Option[Double],
    APORTE_PROMEDIO: Option[Double],
    APORTE_ULTIMA_FECHA: Option[DateTime]
)

object PE_APORTE {
  val _set = {
    get[Option[String]]("USUARIO_TIPO_IDENTIFICACION") ~
      get[Option[String]]("USUARIO_ID_IDENTIFICACION") ~
      get[Option[Double]]("APORTE_SALDO") ~
      get[Option[Double]]("APORTE_MENSUAL") ~
      get[Option[Double]]("APORTE_ORDINARIO") ~
      get[Option[Double]]("APORTE_EXTRAORDINARIO") ~
      get[Option[Double]]("APORTE_REVALORIZACION") ~
      get[Option[Double]]("APORTE_PROMEDIO") ~
      get[Option[DateTime]]("APORTE_ULTIMA_FECHA") map {
      case a ~ b ~ c ~ d ~ e ~ f ~ g ~ h ~ i =>
        PE_APORTE(a, b, c, d, e, f, g, h, i)
    }
  }
}

case class CONTROL_CARGA(
    CONTROL_CARGA_ID: Option[Int],
    CONTROL_CARGA_ANHO: Option[Int],
    CONTROL_CARGA_PERIODO: Option[Int],
    CONTROL_CARGA_FECHA: Option[DateTime],
    CONTROL_CARGA_TIPO: Option[Int],
    CONTROL_CARGA_ESTADO: Option[Int],
    CONTROL_CARGA_REGISTROS: Option[Int],
    ID_EMPLEADO: Option[String]
)

object CONTROL_CARGA {
  val _set = {
    get[Option[Int]]("CONTROL_CARGA_ID") ~
      get[Option[Int]]("CONTROL_CARGA_ANHO") ~
      get[Option[Int]]("CONTROL_CARGA_PERIODO") ~
      get[Option[DateTime]]("CONTROL_CARGA_FECHA") ~
      get[Option[Int]]("CONTROL_CARGA_TIPO") ~
      get[Option[Int]]("CONTROL_CARGA_ESTADO") ~
      get[Option[Int]]("CONTROL_CARGA_REGISTROS") ~
      get[Option[String]]("ID_EMPLEADO") map {
      case id ~
            anho ~
            periodo ~
            fecha ~
            tipo ~
            estado ~
            registros ~
            empleado =>
        CONTROL_CARGA(
          id,
          anho,
          periodo,
          fecha,
          tipo,
          estado,
          registros,
          empleado
        )
    }
  }
}
