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
    id_contrato: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct TipoContrato {
    pub id_contrato: i64,
    pub descripcion: String
}

impl Display for TipoContrato {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {})", self.id_contrato, self.descripcion)
    }
}

impl FromRow for TipoContrato {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_contrato: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl TipoContrato {
    pub fn get(id_contrato: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_contrato: id_contrato
            };
            let _tipo_contrato: Option<TipoContrato> =
                                tr.query_first(
                                                "SELECT gto1.* FROM \"gen$tiposcontrato\" gto1 WHERE gto1.ID_CONTRATO = :id_contrato;",
                                                 _param.clone())?;
            match _tipo_contrato {
                Some(tc) => Ok(tc),
                None => Err(FbError::from("Not Found TipoContrato"))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TipoContrato::default()
            }
        }
    }
}