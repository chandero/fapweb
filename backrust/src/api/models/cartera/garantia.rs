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
    id_garantia: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct Garantia {
    pub id_garantia: i64,
    pub descripcion_garantia: String
}

impl Display for Garantia {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {})", self.id_garantia, self.descripcion_garantia)
    }
}

impl FromRow for Garantia {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_garantia: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_garantia: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl Garantia {
    pub fn get(id_garantia: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_garantia: id_garantia
            };
            let _tipo_garantia: Option<Garantia> =
                                tr.query_first(
                                                "SELECT cg1.* FROM \"col$garantia\" cg1 WHERE cg1.id_garantia = :id_garantia;",
                                                 _param.clone())?;
            match _tipo_garantia {
                Some(tc) => Ok(tc),
                None => Err(FbError::from(format!("Not Found Garantia {}", id_garantia)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        Garantia::default()
            }
        }
    }
}