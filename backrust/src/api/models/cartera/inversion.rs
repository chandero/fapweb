#![allow(unused_variables)]
use crate::api::models::connection::factory::Factory;
use rsfbclient::{prelude::*, FbError, SimpleConnection, FromRow, Column, ColumnToVal};
use serde::{ Serialize, Deserialize };
use std::result::Result;
use std::clone::Clone;
use std::fmt::{Display, Formatter, Result as FmtResult};

#[derive(Clone, IntoParams)]
struct Param {
    id_inversion: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct Inversion {
    pub id_inversion: i64,
    pub descripcion_inversion: String,
}

impl Display for Inversion {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {})", self.id_inversion, self.descripcion_inversion)
    }
}

impl FromRow for Inversion {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_inversion: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_inversion: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl Inversion {
    pub fn get(id_inversion: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_inversion: id_inversion
            };
            let _inversion: Option<Inversion> = 
                                tr.query_first(
                                                "SELECT ci1.* FROM \"col$inversion\" ci1 WHERE ci1.id_inversion = :id_inversion;",
                                                 _param.clone())?;
            match _inversion {
                Some(te) => Ok(te),
                None => Err(FbError::from(format!("Not Found Inversion id_inversion: {}", id_inversion)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        Inversion::default()
            }
        }
    }
}