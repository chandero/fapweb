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
    id_nivel: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct TipoNivelEstudio {
    pub id_nivel: i64,
    pub descripcion_nivel: String
}

impl Display for TipoNivelEstudio {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion_nivel: {})", self.id_nivel, self.descripcion_nivel)
    }
}

impl FromRow for TipoNivelEstudio {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_nivel: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_nivel: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl TipoNivelEstudio {
    pub fn get(id_nivel: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_nivel: id_nivel
            };
            let _tipo_nivel_estudio: Option<TipoNivelEstudio> =
                                tr.query_first(
                                                "SELECT gne1.* FROM \"gen$nivelestudio\" gne1 WHERE gne1.ID_NIVEL = :id_nivel;",
                                                 _param.clone())?;
            match _tipo_nivel_estudio {
                Some(ts) => Ok(ts),
                None => Err(FbError::from(format!("Not Found TipoNivelEstudio {}", id_nivel)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TipoNivelEstudio::default()
            }
        }
    }
}