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
    id_ocupacion: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct TipoOcupacion {
    pub id_ocupacion: i64,
    pub descripcion: String
}

impl Display for TipoOcupacion {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {})", self.id_ocupacion, self.descripcion)
    }
}

impl FromRow for TipoOcupacion {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_ocupacion: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl TipoOcupacion {
    pub fn get(id_ocupacion: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_ocupacion: id_ocupacion
            };
            let _tipo_ocupacion: Option<TipoOcupacion> =
                                tr.query_first(
                                                "SELECT gto1.* FROM \"gen$tiposocupacion\" gto1 WHERE gto1.ID_OCUPACION = :id_ocupacion;",
                                                 _param.clone())?;
            match _tipo_ocupacion {
                Some(to) => Ok(to),
                None => Err(FbError::from(format!("Not Found TipoOcupacion {}", id_ocupacion)))
            }                                                 
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TipoOcupacion::default()
            }
        }
    }
}