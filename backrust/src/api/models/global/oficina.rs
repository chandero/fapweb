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
    id_agencia: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct Oficina {
    pub id_agencia: i64,
    pub descripcion_agencia: String,
    pub direccion_agencia: String,
    pub telefono_agencia: String,
    pub codigo_contable: String,
    pub codigo_municipio: String,
    pub host: String,
    pub puerto: i64,
    pub activa: bool,
    pub port: i64,
    pub ciudad: String,

}

impl Display for Oficina {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {})", self.id_agencia, self.descripcion_agencia)
    }
}

impl FromRow for Oficina {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_agencia: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_agencia: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            direccion_agencia: match row[2].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            telefono_agencia: match row[3].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            codigo_contable: match row[4].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            codigo_municipio: match row[5].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            host: match row[6].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            puerto: match row[7].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            activa: match row[8].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            port: match row[9].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            ciudad: match row[10].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl Oficina {
    pub fn get(id_agencia: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_agencia: id_agencia
            };
            let _oficina: Option<Oficina> =
                                tr.query_first(
                                                "SELECT ga1.* FROM \"gen$agencia\" ga1 WHERE ga1.id_agencia = :id_agencia;",
                                                 _param.clone())?;
            match _oficina {
                Some(ts) => Ok(ts),
                None => Err(FbError::from(format!("Not Found Oficina {}", id_agencia)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        Oficina::default()
            }
        }
    }
}