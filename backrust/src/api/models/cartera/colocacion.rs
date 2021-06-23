#![allow(unused_variables)]
use crate::api::models::connection::factory::Factory;
use rsfbclient::{prelude::*, FbError, SimpleConnection, FromRow, Column, ColumnToVal};
use serde::{ Serialize, Deserialize };
use chrono::{NaiveDate, NaiveDateTime};
use std::result::Result;

use crate::api::models::tipos::TipoCuota;
use crate::api::models::global::Oficina;
use super::EnteAprobador;
use super::Respaldo;
use super::Inversion;
use super::Clasificacion;
use super::Garantia;
use super::Categoria;
use super::Linea;
use super::TasaVariable;
use super::EstadoColocacion;

use crate::api::models::custom_date_format;

#[derive(Clone, IntoParams)]
struct Param {
    id_colocacion: String,
    id_identificacion: i64,
    id_persona: String
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Colocacion {
    pub id_agencia: Oficina,
    pub id_colocacion: String,
    pub id_identificacion: i64,
    pub id_persona: String,
    pub id_clasificacion: Clasificacion,
    pub id_linea: Linea,
    pub id_inversion: Inversion,
    pub id_respaldo: Respaldo,
    pub id_garantia: Garantia,
    pub id_categoria: Categoria,
    pub id_evaluacion: Categoria,
    #[serde(with = "custom_date_format")]
    pub fecha_desembolso: NaiveDateTime,
    pub valor_desembolso: f64,
    pub plazo_colocacion: i64,
    #[serde(with = "custom_date_format")]
    pub fecha_vencimiento: NaiveDateTime,
    pub tipo_interes: String,
    pub id_interes: TasaVariable,
    pub tasa_interes_corriente: f64,
    pub tasa_interes_mora: f64,
    pub puntos_interes: f64,
    pub id_tipo_cuota: TipoCuota,
    pub amortizacion_capital: i64,
    pub amortizacion_interes: i64,
    pub periodo_gracia: i64,
    pub dias_prorrogados: i64,
    pub valor_cuota: f64,
    pub abonos_capital: f64,
    #[serde(with = "custom_date_format")]
    pub fecha_capital: NaiveDateTime,
    #[serde(with = "custom_date_format")]
    pub fecha_interes: NaiveDateTime,
    pub id_estado_colocacion: EstadoColocacion,
    pub id_ente_aprobador: EnteAprobador,
    pub id_empleado: String,
    pub nota_contable: String,
    pub numero_cuenta: i64,
    pub es_anormal: bool,
    pub dias_pago: i64,
    pub reciprocidad: f64,
    #[serde(with = "custom_date_format")]    
    pub fecha_saldado: NaiveDateTime,
}

impl Default for Colocacion {
    fn default() -> Colocacion {
        Colocacion {
            id_agencia: Oficina::default(),
            id_colocacion: String::default(),
            id_identificacion: i64::default(),
            id_persona: String::default(),
            id_clasificacion: Clasificacion::default(),
            id_linea: Linea::default(),
            id_inversion: Inversion::default(),
            id_respaldo: Respaldo::default(),
            id_garantia: Garantia::default(),
            id_categoria: Categoria::default(),
            id_evaluacion: Categoria::default(),
            fecha_desembolso: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            valor_desembolso: f64::default(),
            plazo_colocacion: i64::default(),
            fecha_vencimiento: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            tipo_interes: String::default(),
            id_interes: TasaVariable::default(),
            tasa_interes_corriente: f64::default(),
            tasa_interes_mora: f64::default(),
            puntos_interes: f64::default(),
            id_tipo_cuota: TipoCuota::default(),
            amortizacion_capital: i64::default(),
            amortizacion_interes: i64::default(),
            periodo_gracia: i64::default(),
            dias_prorrogados: i64::default(),
            valor_cuota: f64::default(),
            abonos_capital: f64::default(),
            fecha_capital: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            fecha_interes: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            id_estado_colocacion: EstadoColocacion::default(),
            id_ente_aprobador: EnteAprobador::default(),
            id_empleado: String::default(),
            nota_contable: String::default(),
            numero_cuenta: i64::default(),
            es_anormal: bool::default(),
            dias_pago: i64::default(),
            reciprocidad: f64::default(),
            fecha_saldado: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
        }
    }
}

impl FromRow for Colocacion {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_agencia: match row[0].clone().to_val() { Ok(v) => Oficina::get(v),  Err(_) => Oficina::default() },
            id_colocacion: match row[1].clone().to_val() { Ok(v) => v,  Err(_) => String::default() },
            id_identificacion: match row[2].clone().to_val() { Ok(v) => v,  Err(_) => 0 },
            id_persona: match row[3].clone().to_val() { Ok(v) => v,  Err(_) => String::default() },
            id_clasificacion: match row[4].clone().to_val() { Ok(v) => Clasificacion::get(v),  Err(_) => Clasificacion::default() },
            id_linea: match row[5].clone().to_val() { Ok(v) => Linea::get(v),  Err(_) => Linea::default() },
            id_inversion: match row[6].clone().to_val() { Ok(v) => Inversion::get(v),  Err(_) => Inversion::default() },
            id_respaldo: match row[7].clone().to_val() { Ok(v) => Respaldo::get(v),  Err(_) => Respaldo::default() },
            id_garantia: match row[8].clone().to_val() { Ok(v) => Garantia::get(v),  Err(_) => Garantia::default() },
            id_categoria: match row[9].clone().to_val() { Ok(v) => Categoria::get(v),  Err(_) => Categoria::default() },
            id_evaluacion: match row[10].clone().to_val() { Ok(v) => Categoria::get(v),  Err(_) => Categoria::default() },
            fecha_desembolso: match row[11].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            valor_desembolso: match row[12].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            plazo_colocacion: match row[13].clone().to_val() { Ok(v) => v,  Err(_) => 0 },
            fecha_vencimiento: match row[14].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            tipo_interes: match row[15].clone().to_val() { Ok(v) => v,  Err(_) => String::default() },
            id_interes: match row[16].clone().to_val() { Ok(v) => TasaVariable::get(v),  Err(_) => TasaVariable::default() },
            tasa_interes_corriente: match row[17].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            tasa_interes_mora: match row[18].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            puntos_interes: match row[19].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            id_tipo_cuota: match row[20].clone().to_val() { Ok(v) => TipoCuota::get(v),  Err(_) => TipoCuota::default() },
            amortizacion_capital: match row[21].clone().to_val() { Ok(v) => v,  Err(_) => 0 },
            amortizacion_interes: match row[22].clone().to_val() { Ok(v) => v,  Err(_) => 0 },
            periodo_gracia: match row[23].clone().to_val() { Ok(v) => v,  Err(_) => 0 },
            dias_prorrogados: match row[24].clone().to_val() { Ok(v) => v,  Err(_) => 0 },
            valor_cuota: match row[25].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            abonos_capital: match row[26].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            fecha_capital: match row[27].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            fecha_interes: match row[28].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            id_estado_colocacion: match row[29].clone().to_val() { Ok(v) => EstadoColocacion::get(v),  Err(_) => EstadoColocacion::default() },
            id_ente_aprobador: match row[30].clone().to_val() { Ok(v) => EnteAprobador::get(v),  Err(_) => EnteAprobador::default() },
            id_empleado: match row[31].clone().to_val() { Ok(v) => v,  Err(_) => String::default() },
            nota_contable: match row[32].clone().to_val() { Ok(v) => v,  Err(_) => String::default() },
            numero_cuenta: match row[33].clone().to_val() { Ok(v) => v,  Err(_) => 0 },
            es_anormal: match row[33].clone().to_val() { Ok(v) => match v { 1 => true, _ => false },  Err(_) => false },
            dias_pago: match row[35].clone().to_val() { Ok(v) => v,  Err(_) => 0 },
            reciprocidad: match row[36].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            fecha_saldado: match row[37].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
        })
    }
}

impl Colocacion {
    pub fn get(id_colocacion: String) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _params = Param {
                id_colocacion: id_colocacion.clone(),
                id_identificacion: 0,
                id_persona: String::default()
            };
            let _colocacion: Option<Colocacion> = 
                                            tr.query_first(
                                                "SELECT cc1.* FROM \"col$colocacion\" cc1 WHERE cc1.ID_COLOCACION = :id_colocacion;",
                                                 _params)?;
            match _colocacion {
                Some(s) => Ok(s),
                None => Err(FbError::from(format!("Not Found Colocacion id_colocacion: {}", id_colocacion)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        Colocacion::default()
            }
        }
    }

    pub fn get_by_persona(id_identificacion: i64, id_persona: String) -> Vec<Colocacion> {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _params = Param {
                id_colocacion: String::default(),
                id_identificacion: id_identificacion,
                id_persona: id_persona.clone()
            };
            let _rows: Vec<Colocacion> = 
                                            tr.query(
                                                "SELECT cc1.* FROM \"col$colocacion\" cc1 WHERE cc1.ID_IDENTIFICACION = :id_identificacion AND cc1.ID_PERSONA = :id_persona;",
                                                 _params)?;
            Ok(_rows)
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        Vec::new()
            }
        }
    }    
}