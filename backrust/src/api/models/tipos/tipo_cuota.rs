#![allow(unused_variables)]
use crate::api::models::connection::factory::Factory;
use rsfbclient::{prelude::*, FbError, SimpleConnection, FromRow, Column, ColumnToVal};
use serde::{ Serialize, Deserialize };
use chrono::{NaiveDate, NaiveDateTime};
use std::convert::Into;
use std::result::Result;
use std::clone::Clone;
use std::fmt::{Display, Formatter, Result as FmtResult};

#[derive(Clone, IntoParams)]
struct Param {
    id_tipos_cuota: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct TipoCuota {
    pub id_tipos_cuota: i64,
    pub descripcion_tipo_cuota: String,
    pub capital: String,
    pub interes: String,
    pub tipo_cuota: String,
    pub es_variable: bool,
    pub es_fija: bool
}

impl Display for TipoCuota {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {}, capital: {}, interes: {}, tipo_cuota: {}, es_variable: {}, es_fija: {})", self.id_tipos_cuota, self.descripcion_tipo_cuota, self.capital, self.interes, self.tipo_cuota, self.es_variable, self.es_fija)
    }
}

impl FromRow for TipoCuota {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_tipos_cuota: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_tipo_cuota: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            capital: match row[2].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            interes: match row[3].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            tipo_cuota: match row[4].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            es_variable: match row[5].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            es_fija: match row[6].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
        })
    }
}

impl TipoCuota {
    pub fn get(id_tipos_cuota: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_tipos_cuota: id_tipos_cuota
            };
            let _tipo_cuota: Option<TipoCuota> =
                                tr.query_first(
                                                "SELECT ctc1.* FROM \"col$tiposcuota\" ctc1 WHERE ctc1.id_tipos_cuota = :id_tipos_cuota;",
                                                 _param.clone())?;
            match _tipo_cuota {
                Some(tc) => Ok(tc),
                None => Err(FbError::from(format!("Not Found TipoCuota {}", id_tipos_cuota)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TipoCuota::default()
            }
        }
    }
}