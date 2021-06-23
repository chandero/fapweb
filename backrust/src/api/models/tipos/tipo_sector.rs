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
    id_sector: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct TipoSector {
    pub id_sector: i64,
    pub descripcion: String
}

impl Display for TipoSector {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {})", self.id_sector, self.descripcion)
    }
}

impl FromRow for TipoSector {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_sector: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl TipoSector {
    pub fn get(id_sector: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_sector: id_sector
            };
            let _tipo_sector: Option<TipoSector> =
                                tr.query_first(
                                                "SELECT gtsc1.* FROM \"gen$tipossectorcomercial\" gtsc1 WHERE gtsc1.ID_SECTOR = :id_sector;",
                                                 _param.clone())?;
            match _tipo_sector {
                Some(ts) => Ok(ts),
                None => Err(FbError::from(format!("Not Found TipoSector {}", id_sector)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TipoSector::default()
            }
        }
    }
}