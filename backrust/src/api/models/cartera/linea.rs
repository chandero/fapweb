#![allow(unused_variables)]
use crate::api::models::connection::factory::Factory;
use chrono::{NaiveDate, NaiveDateTime};
use rsfbclient::{prelude::*, Column, ColumnToVal, FbError, FromRow, SimpleConnection};
use serde::{Deserialize, Serialize};
use std::clone::Clone;
use std::convert::Into;
use std::fmt::{Display, Formatter, Result as FmtResult};
use std::result::Result;

#[derive(Clone, IntoParams)]
struct Param {
    id_linea: i64,
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct Linea {
    pub id_linea: i64,
    pub descripcion_linea: String,
    pub veces_aportes: f64,
    pub veces_ahorros: f64,
    pub porcentaje_credito: f64,
    pub porcentaje_aportes: f64,
    pub porcentaje_ahorros: f64,
    pub minimo_capitalizar: f64,
    pub capitalizacion_acumulada: bool,
    pub es_vivienda: bool,
    pub es_ordinaria: bool,
    pub tasa: f64,
    pub puntos_adicionales: f64,
    pub estado: i64,
    pub cf_redondeo: bool,
}

impl Display for Linea {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {}, v aportes: {}, v ahorros: {}, %cred: {}, %aporte: {}, %ahorro: {}, min.capitaliza: {}, cap. acumulada: {}, vivienda: {}, ordinaria: {}, tasa: {}, puntos: {}, redondeo: {}, estado: {})", self.id_linea, self.descripcion_linea, self.veces_aportes, self.veces_ahorros, self.porcentaje_credito, self.porcentaje_aportes, self.porcentaje_ahorros, self.minimo_capitalizar, self.capitalizacion_acumulada, self.es_vivienda, self.es_ordinaria, self.tasa, self.puntos_adicionales, self.cf_redondeo, self.estado)
    }
}

impl FromRow for Linea {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_linea: match row[0].clone().to_val() {
                Ok(v) => v,
                Err(_) => 0,
            },
            descripcion_linea: match row[1].clone().to_val() {
                Ok(v) => v,
                Err(_) => "".to_string(),
            },
            veces_aportes: match row[2].clone().to_val() {
                Ok(v) => v,
                Err(_) => 0.0,
            },
            veces_ahorros: match row[3].clone().to_val() {
                Ok(v) => v,
                Err(_) => 0.0,
            },
            porcentaje_credito: match row[4].clone().to_val() {
                Ok(v) => v,
                Err(_) => 0.0,
            },
            porcentaje_aportes: match row[5].clone().to_val() {
                Ok(v) => v,
                Err(_) => 0.0,
            },
            porcentaje_ahorros: match row[6].clone().to_val() {
                Ok(v) => v,
                Err(_) => 0.0,
            },
            minimo_capitalizar: match row[7].clone().to_val() {
                Ok(v) => v,
                Err(_) => 0.0,
            },
            capitalizacion_acumulada: match row[8].clone().to_val() {
                Ok(v) => match v {
                    1 => true,
                    _ => false,
                },
                Err(_) => false,
            },
            es_vivienda: match row[9].clone().to_val() {
                Ok(v) => match v {
                    1 => true,
                    _ => false,
                },
                Err(_) => false,
            },
            es_ordinaria: match row[10].clone().to_val() {
                Ok(v) => match v {
                    1 => true,
                    _ => false,
                },
                Err(_) => false,
            },
            tasa: match row[11].clone().to_val() {
                Ok(v) => v,
                Err(_) => 0.0,
            },
            puntos_adicionales: match row[12].clone().to_val() {
                Ok(v) => v,
                Err(_) => 0.0,
            },
            estado: match row[13].clone().to_val() {
                Ok(v) => v,
                Err(_) => 0,
            },
            cf_redondeo: match row[14].clone().to_val() {
                Ok(v) => match v {
                    1 => true,
                    _ => false,
                },
                Err(_) => false,
            },
        })
    }
}

impl Linea {
    pub fn get(id_linea: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param { id_linea: id_linea };
            let _tipo_linea: Option<Linea> = tr.query_first(
                "SELECT cl1.* FROM \"col$lineas\" cl1 WHERE cl1.id_linea = :id_linea;",
                _param.clone(),
            )?;
            match _tipo_linea {
                Some(tc) => Ok(tc),
                None => Err(FbError::from(format!("Not Found TipoLinea {}", id_linea))),
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => {
                println!("Error: {}", e);
                Linea::default()
            }
        }
    }
}
