#![allow(unused_variables)]
use crate::api::models::connection::factory::Factory;
use rsfbclient::{prelude::*, FbError, SimpleConnection, FromRow, Column, ColumnToVal};
use serde::{ Serialize, Deserialize };
use chrono::{NaiveDate, NaiveDateTime};
use std::result::Result;

use crate::api::models::custom_date_format;

use crate::api::models::tipos::TipoCuota;
use crate::api::models::tipos::TipoEstadoSolicitud;
use crate::api::models::global::Oficina;
use super::EnteAprobador;
use super::Respaldo;
use super::Inversion;
use super::Clasificacion;
use super::Garantia;
use super::Linea;
use super::Riesgo;

#[derive(Clone, IntoParams)]
struct Param {
    id_solicitud: String,
    id_identificacion: i64,
    id_persona: String
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Solicitud {
    pub id_solicitud: String,
    pub id_identificacion: i64,
    pub id_persona: String,
    pub valor_solicitado: f64,
    pub plazo: i64,
    pub amortizacion: i64,
    pub garantia: Garantia,
    pub tipo_cuota: TipoCuota,
    pub estado: TipoEstadoSolicitud,
    pub linea: Linea,
    #[serde(with = "custom_date_format")]
    pub fecha_recepcion: NaiveDateTime,
    pub oficina: Oficina,
    pub destino: String,
    #[serde(with = "custom_date_format")]
    pub fecha_concepto: NaiveDateTime,
    pub id_empleado: String,
    pub ente_aprobador: EnteAprobador,
    pub numero_acta: String,
    pub tasa_interes: f64,
    pub plazo_aprobado: i64,
    pub descripcion_garantia: String,
    pub numero_codeudores: i64,
    pub respaldo: Respaldo,
    pub valor_aprobado: f64,
    pub id_analisis: String,
    pub ingresos: f64,
    pub disponible: f64,
    pub deducciones: f64,
    pub valor_cuota: f64,
    pub disponibilidad: f64,
    pub solv_economica: bool,
    pub exp_creditos: String,
    pub inversion: Inversion,
    pub clasificacion: Clasificacion,
    pub es_desembolso_parcial: bool,
    pub pago_interes: i64,
    pub id_copia: bool,
    pub observacion: String,
    pub s_vida: bool,
    pub s_exequial: bool,
    pub es_fundacion: bool,
    pub numero_riesgo: Riesgo,
    #[serde(with = "custom_date_format")]
    pub fecha_analisis: NaiveDateTime
}

impl Default for Solicitud {
    fn default() -> Solicitud {
        Solicitud {
            id_solicitud: String::default(),
            id_identificacion: i64::default(),
            id_persona: String::default(),
            valor_solicitado: f64::default(),
            plazo: i64::default(),
            amortizacion: i64::default(),
            garantia: Garantia::default(),
            tipo_cuota: TipoCuota::default(),
            estado: TipoEstadoSolicitud::default(),
            linea: Linea::default(),
            fecha_recepcion: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            oficina: Oficina::default(),
            destino: String::default(),
            fecha_concepto: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            id_empleado: String::default(),
            ente_aprobador: EnteAprobador::default(),
            numero_acta: String::default(),
            tasa_interes: f64::default(),
            plazo_aprobado: i64::default(),
            descripcion_garantia: String::default(),
            numero_codeudores: i64::default(),
            respaldo: Respaldo::default(),
            valor_aprobado: f64::default(),
            id_analisis: String::default(),
            ingresos: f64::default(),
            disponible: f64::default(),
            deducciones: f64::default(),
            valor_cuota: f64::default(),
            disponibilidad: f64::default(),
            solv_economica: bool::default(),
            exp_creditos: String::default(),
            inversion: Inversion::default(),
            clasificacion: Clasificacion::default(),
            es_desembolso_parcial: bool::default(),
            pago_interes: i64::default(),
            id_copia: bool::default(),
            observacion: String::default(),
            s_vida: bool::default(),
            s_exequial: bool::default(),
            es_fundacion: bool::default(),
            numero_riesgo: Riesgo::default(),
            fecha_analisis: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
        }
    }
}

impl FromRow for Solicitud {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_solicitud: match row[0].clone().to_val() { Ok(v) => v,  Err(_) => "".to_string() },
            id_identificacion: match row[2].clone().to_val() { Ok(v) => v,  Err(_) => 0 },
            id_persona: match row[1].clone().to_val() { Ok(v) => v,  Err(_) => "".to_string() },
            valor_solicitado: match row[3].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            plazo: match row[4].clone().to_val() { Ok(v) => v,  Err(_) => 0 },
            amortizacion: match row[5].clone().to_val() { Ok(v) => v,  Err(_) => 0 },
            garantia: match row[6].clone().to_val() { Ok(v) => Garantia::get(v),  Err(_) => Garantia::default() },
            tipo_cuota: match row[7].clone().to_val() { Ok(v) => TipoCuota::get(v),  Err(_) => TipoCuota::default() },
            estado: match row[8].clone().to_val() { Ok(v) => TipoEstadoSolicitud::get(v),  Err(_) => TipoEstadoSolicitud::default() },
            linea: match row[9].clone().to_val() { Ok(v) => Linea::get(v),  Err(_) => Linea::default() },
            fecha_recepcion: match row[10].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            oficina: match row[11].clone().to_val() { Ok(v) => Oficina::get(v),  Err(_) => Oficina::default() },
            destino: match row[12].clone().to_val() { Ok(v) => v,  Err(_) => "".to_string() },
            fecha_concepto: match row[13].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            id_empleado: match row[14].clone().to_val() { Ok(v) => v,  Err(_) => "".to_string() },
            ente_aprobador: match row[15].clone().to_val() { Ok(v) => EnteAprobador::get(v),  Err(_) => EnteAprobador::default() },
            numero_acta: match row[16].clone().to_val() { Ok(v) => v,  Err(_) => "".to_string() },
            tasa_interes: match row[17].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            plazo_aprobado: match row[18].clone().to_val() { Ok(v) => v,  Err(_) => 0 },
            descripcion_garantia: match row[19].clone().to_val() { Ok(v) => v,  Err(_) => "".to_string() },
            numero_codeudores: match row[20].clone().to_val() { Ok(v) => v,  Err(_) => 0 },
            respaldo: match row[21].clone().to_val() { Ok(v) => Respaldo::get(v),  Err(_) => Respaldo::default() },
            valor_aprobado: match row[22].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            id_analisis: match row[23].clone().to_val() { Ok(v) => v,  Err(_) => String::default() },
            ingresos: match row[24].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            disponible: match row[25].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            deducciones: match row[26].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            valor_cuota: match row[27].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            disponibilidad: match row[28].clone().to_val() { Ok(v) => v,  Err(_) => 0.0 },
            solv_economica: match row[29].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            exp_creditos: match row[30].clone().to_val() { Ok(v) => v,  Err(_) => "".to_string() },
            inversion: match row[31].clone().to_val() { Ok(v) => Inversion::get(v),  Err(_) => Inversion::default() },
            clasificacion: match row[32].clone().to_val() { Ok(v) => Clasificacion::get(v),  Err(_) => Clasificacion::default() },
            es_desembolso_parcial: match row[33].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            pago_interes: match row[34].clone().to_val() { Ok(v) => v,  Err(_) => 0 },
            id_copia: match row[35].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            observacion: match row[36].clone().to_val() { Ok(v) => v,  Err(_) => "".to_string() },
            s_vida: match row[37].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            s_exequial: match row[38].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            es_fundacion: match row[39].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            numero_riesgo: match row[40].clone().to_val() { Ok(v) => Riesgo::get(v),  Err(_) => Riesgo::default() },
            fecha_analisis: match row[41].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
        })
    }
}

impl Solicitud {
    pub fn get(id_solicitud: String) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _params = Param {
                id_solicitud: id_solicitud.clone(),
                id_identificacion: 0,
                id_persona: String::default()
            };
            let _solicitud: Option<Solicitud> = 
                                            tr.query_first(
                                                "SELECT cs1.* FROM \"col$solicitud\" cs1 WHERE cs1.ID_SOLICITUD = :id_solicitud;",
                                                 _params)?;
            match _solicitud {
                Some(s) => Ok(s),
                None => Err(FbError::from(format!("Not Found Solicitud id_solicitud: {}", id_solicitud)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        Solicitud::default()
            }
        }
    }

    pub fn get_by_persona(id_identificacion: i64, id_persona: String) -> Vec<Solicitud> {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _params = Param {
                id_solicitud: String::default(),
                id_identificacion: id_identificacion,
                id_persona: id_persona.clone()
            };
            let _rows: Vec<Solicitud> = 
                                            tr.query(
                                                "SELECT cs1.* FROM \"col$solicitud\" cs1 WHERE cs1.ID_IDENTIFICACION = :id_identificacion AND cs1.ID_PERSONA = :id_persona;",
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