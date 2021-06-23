#![allow(unused_variables)]
use crate::api::models::connection::factory::Factory;
use rsfbclient::{prelude::*, FbError, SimpleConnection, FromRow, Column, ColumnToVal};
use serde::{ Serialize, Deserialize };
use std::result::Result;
use std::clone::Clone;
use std::fmt::{Display, Formatter, Result as FmtResult};

#[derive(Clone, IntoParams)]
struct Param {
    id_riesgo: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct Riesgo {
    pub id_riesgo: i64,
    pub descripcion: String,
    pub tipo: i64
}

impl Display for Riesgo {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {}, tipo: {})", self.id_riesgo, self.descripcion, self.tipo)
    }
}

impl FromRow for Riesgo {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_riesgo: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            tipo: match row[3].clone().to_val() { Ok(v) => v, Err(_) => 0 },
        })
    }
}

impl Riesgo {
    pub fn get(id_riesgo: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_riesgo: id_riesgo
            };
            let _riesgo: Option<Riesgo> = 
                                tr.query_first(
                                                "SELECT gre1.* FROM \"gen$riesgosequivida\" gre1 WHERE gre1.id_riesgo = :id_riesgo;",
                                                 _param.clone())?;
            match _riesgo {
                Some(te) => Ok(te),
                None => Err(FbError::from(format!("Not Found Riesgo id_riesgo: {}", id_riesgo)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        Riesgo::default()
            }
        }
    }
}