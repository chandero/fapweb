#![allow(unused_variables)]
use crate::api::models::connection::factory::Factory;
use rsfbclient::{prelude::*, FbError, SimpleConnection, FromRow, Column, ColumnToVal};
use serde::{ Serialize, Deserialize };
use chrono::{NaiveDate, NaiveDateTime};
use std::convert::Into;
use std::result::Result;
use std::clone::Clone;
use std::fmt::{Display, Formatter, Result as FmtResult};

use crate::api::models::custom_date_format;

use crate::api::models::global::Oficina;

#[derive(Clone, IntoParams)]
struct Param {
    id_colocacion: Option<String>,
    fecha_extracto: Option<NaiveDateTime>,
    id_cbte_colocacion: Option<i64>,   
}

#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct Extracto {
    pub id_agencia: Oficina,
    pub id_cbte_colocacion: i64,
    pub id_colocacion: String,
    #[serde(with = "custom_date_format")]
    pub fecha_extracto: NaiveDateTime,
    #[serde(with = "custom_date_format")]
    pub hora_extracto: NaiveDateTime,
    pub cuota_extracto: i64,
    pub tipo_operacion: i64,
    pub saldo_anterior_extracto: f64,
    pub abono_capital: f64,
    pub abono_cxc: f64,
    pub abono_anticipado: f64,
    pub abono_servicios: f64,
    pub abono_mora: f64,
    pub abono_seguro: f64,
    pub abono_pagxcli: f64,
    pub abono_honorarios: f64,
    pub abono_otros: f64,
    pub tasa_interes_liquidacion: f64,
    pub id_empleado: String,
    #[serde(with = "custom_date_format")]
    pub interes_pago_hasta: NaiveDateTime,
    #[serde(with = "custom_date_format")]
    pub capital_pago_hasta: NaiveDateTime,
    pub tipo_abono: i64,
    pub detallado: Vec<ExtractoDetallado>
}

#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct ExtractoDetallado {
    pub id_agencia: Oficina,
    pub id_cbte_colocacion: i64,
    pub id_colocacion: String,
    #[serde(with = "custom_date_format")]
    pub fecha_extracto: NaiveDateTime,
    #[serde(with = "custom_date_format")]
    pub hora_extracto: NaiveDateTime,
    pub codigo_puc: String,
    #[serde(with = "custom_date_format")]
    pub fecha_inicial: NaiveDateTime,
    #[serde(with = "custom_date_format")]
    pub fecha_final: NaiveDateTime,
    pub dias_aplicados: i64,
    pub tasa_liquidacion: f64,
    pub valor_debito: f64,
    pub valor_credito: f64
}

impl Default for Extracto {
    fn default() -> Self { 
        Self {
            id_agencia: Oficina::default(),
            id_cbte_colocacion: i64::default(),
            id_colocacion: String::default(),
            fecha_extracto: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            hora_extracto: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            cuota_extracto: i64::default(),
            tipo_operacion: i64::default(),
            saldo_anterior_extracto: f64::default(),
            abono_capital: f64::default(),
            abono_cxc: f64::default(),
            abono_anticipado: f64::default(),
            abono_servicios: f64::default(),
            abono_mora: f64::default(),
            abono_seguro: f64::default(),
            abono_pagxcli: f64::default(),
            abono_honorarios: f64::default(),
            abono_otros: f64::default(),
            tasa_interes_liquidacion: f64::default(),
            id_empleado: String::default(),
            interes_pago_hasta: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            capital_pago_hasta: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            tipo_abono: i64::default(),
            detallado: Vec::new()
        }
    }
}

impl Default for ExtractoDetallado {
    fn default() -> Self {
        Self {
            id_agencia: Oficina::default(),
            id_cbte_colocacion: i64::default(),
            id_colocacion: String::default(),
            fecha_extracto: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            hora_extracto: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            codigo_puc: String::default(),
            fecha_inicial: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            fecha_final: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            dias_aplicados: i64::default(),
            tasa_liquidacion: f64::default(),
            valor_debito: f64::default(),
            valor_credito: f64::default()   
        }
    }
}

impl FromRow for Extracto {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_agencia: match row[0].clone().to_val() { Ok(v) => Oficina::get(v), Err(_) => Oficina::default() },
            id_cbte_colocacion: match row[1].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            id_colocacion: match row[2].clone().to_val() { Ok(v) => v, Err(_) => String::default() },
            fecha_extracto: match row[3].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            hora_extracto: match row[4].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            cuota_extracto: match row[5].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            tipo_operacion: match row[6].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            saldo_anterior_extracto: match row[7].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            abono_capital: match row[8].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            abono_cxc: match row[9].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            abono_anticipado: match row[10].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            abono_servicios: match row[11].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            abono_mora: match row[12].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            abono_seguro: match row[13].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            abono_pagxcli: match row[14].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            abono_honorarios: match row[15].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            abono_otros: match row[16].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            tasa_interes_liquidacion: match row[17].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            id_empleado: match row[18].clone().to_val() { Ok(v) => v, Err(_) => String::default() },
            interes_pago_hasta: match row[19].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            capital_pago_hasta: match row[20].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            tipo_abono: match row[21].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            detallado: ExtractoDetallado::get(row[2].clone().to_val()?, row[1].clone().to_val()?, row[3].clone().to_val()?)
        })
    }
}

impl FromRow for ExtractoDetallado {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_agencia: match row[0].clone().to_val() { Ok(v) => Oficina::get(v), Err(_) => Oficina::default() },
            id_cbte_colocacion: match row[1].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            id_colocacion: match row[2].clone().to_val() { Ok(v) => v, Err(_) => String::default() },
            fecha_extracto: match row[3].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            hora_extracto: match row[4].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            codigo_puc: match row[5].clone().to_val() { Ok(v) => v, Err(_) => String::default() },
            fecha_inicial: match row[6].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            fecha_final: match row[7].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            dias_aplicados: match row[8].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            tasa_liquidacion: match row[9].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            valor_debito: match row[10].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            valor_credito: match row[11].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
        })
    }    
}

impl Extracto {
    pub fn get(id_colocacion: String) -> Vec<Extracto> {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_colocacion: Some(id_colocacion),
                fecha_extracto: None,
                id_cbte_colocacion: None, 
            };
            let extracto: Vec<Extracto> =
                                tr.query(
                                        "SELECT ce1.* FROM \"col$extracto\" ce1 WHERE ce1.id_colocacion = :id_colocacion;",
                                                 _param.clone())?;
            Ok(extracto)
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

impl ExtractoDetallado {
    pub fn get(id_colocacion: String, id_cbte_colocacion: i64, fecha_extracto: NaiveDateTime) -> Vec<ExtractoDetallado> {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_colocacion: Some(id_colocacion),
                fecha_extracto: Some(fecha_extracto),
                id_cbte_colocacion: Some(id_cbte_colocacion), 
            };
            let _extracto_detallado: Vec<ExtractoDetallado> =
                                tr.query(
                                        "SELECT ced1.* FROM \"col$extractodet\" ced1 WHERE ced1.id_colocacion = :id_colocacion AND ced1.id_cbte_colocacion = :id_cbte_colocacion AND ced1.fecha_extracto = :fecha_extracto;",
                                                 _param.clone())?;
            Ok(_extracto_detallado)
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