#![allow(unused_variables)]
use crate::api::models::connection::factory::Factory;
use rsfbclient::{prelude::*, FbError, SimpleConnection, FromRow, Column, ColumnToVal};
use serde::{ Serialize, Deserialize };
use std::result::Result;
use std::clone::Clone;
use std::fmt::{Display, Formatter, Result as FmtResult};

#[derive(Clone, IntoParams)]
struct Param {
    id_respaldo: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct Respaldo {
    pub id_respaldo: i64,
    pub descripcion_respaldo: String,
}

impl Display for Respaldo {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {})", self.id_respaldo, self.descripcion_respaldo)
    }
}

impl FromRow for Respaldo {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_respaldo: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_respaldo: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl Respaldo {
    pub fn get(id_respaldo: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_respaldo: id_respaldo
            };
            let _respaldo: Option<Respaldo> = 
                                tr.query_first(
                                                "SELECT cr1.* FROM \"col$respaldo\" cr1 WHERE cr1.id_respaldo = :id_respaldo;",
                                                 _param.clone())?;
            match _respaldo {
                Some(te) => Ok(te),
                None => Err(FbError::from(format!("Not Found Respaldo id_respaldo: {}", id_respaldo)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        Respaldo::default()
            }
        }
    }
}