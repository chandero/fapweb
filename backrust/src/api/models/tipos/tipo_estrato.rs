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
    id_estrato: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct TipoEstrato {
    pub id_estrato: i64,
    pub descripcion: String
}

impl Display for TipoEstrato {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {})", self.id_estrato, self.descripcion)
    }
}

impl FromRow for TipoEstrato {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_estrato: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl TipoEstrato {
    pub fn get(id_estrato: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_estrato: id_estrato
            };
            let _tipo_estrato: Option<TipoEstrato> = 
                                tr.query_first(
                                                "SELECT ge1.* FROM \"gen$estrato\" ge1 WHERE ge1.id_estrato = :id_estrato;",
                                                 _param.clone())?;
            match _tipo_estrato {
                Some(te) => Ok(te),
                None => Err(FbError::from("Not Found TipoEstrato"))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TipoEstrato::default()
            }
        }
    }
}