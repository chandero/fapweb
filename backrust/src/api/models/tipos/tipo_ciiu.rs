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
    id_ciiu: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct TipoCiiu {
    pub id_ciiu: i64,
    pub id_clase_ciiu: i64,
    pub descripcion_ciiu: String
}

impl Display for TipoCiiu {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, clase: {}, descripcion: {})", self.id_ciiu, self.id_clase_ciiu, self.descripcion_ciiu)
    }
}

impl FromRow for TipoCiiu {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_ciiu: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            id_clase_ciiu: match row[1].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_ciiu: match row[2].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl TipoCiiu {
    pub fn get(id_ciiu: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_ciiu: id_ciiu
            };
            let _tipo_ciiu: Option<TipoCiiu> = 
                                tr.query_first(
                                                "SELECT gtci1.* FROM \"gen$tiposciiu\" gtci1 WHERE gtci1.id_ciiu = :id_ciiu;",
                                                 _param.clone())?;
            match _tipo_ciiu {
                Some(tc) => Ok(tc),
                None => Err(FbError::from("Not Found TipoCiiu"))
            }                                                 
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TipoCiiu::default()
            }
        }
    }
}