#![allow(unused_variables)]
use crate::api::models::connection::factory::Factory;
use rsfbclient::{prelude::*, FbError, SimpleConnection, FromRow, Column, ColumnToVal};
use serde::{ Serialize, Deserialize };
use std::result::Result;
use std::clone::Clone;
use std::fmt::{Display, Formatter, Result as FmtResult};

#[derive(Clone, IntoParams)]
struct Param {
    id_interes: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct TasaVariable {
    pub id_interes: i64,
    pub descripcion_tasa: String,
    pub valor_actual_tasa: f64
}

impl Display for TasaVariable {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {}, tasa: {})", self.id_interes, self.descripcion_tasa, self.valor_actual_tasa)
    }
}

impl FromRow for TasaVariable {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_interes: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_tasa: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            valor_actual_tasa: match row[2].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
        })
    }
}

impl TasaVariable {
    pub fn get(id_interes: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_interes: id_interes
            };
            let _tasa: Option<TasaVariable> = 
                                tr.query_first(
                                                "SELECT ctv1.* FROM \"col$tasavariables\" ctv1 WHERE ctv1.id_interes = :id_interes;",
                                                 _param.clone())?;
            match _tasa {
                Some(te) => Ok(te),
                None => Err(FbError::from(format!("Not Found TasaVariable id_interes: {}", id_interes)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TasaVariable::default()
            }
        }
    }
}