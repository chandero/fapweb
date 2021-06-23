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
    id_tipovivienda: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct TipoVivienda {
    pub id_tipovivienda: i64,
    pub descripcion_tipo: String
}

impl Display for TipoVivienda {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion_tipo: {})", self.id_tipovivienda, self.descripcion_tipo)
    }
}

impl FromRow for TipoVivienda {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_tipovivienda: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_tipo: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl TipoVivienda {
    pub fn get(id_tipovivienda: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_tipovivienda: id_tipovivienda
            };
            let _tipo_vivienda: Option<TipoVivienda> =
                                tr.query_first(
                                                "SELECT gtv1.* FROM \"gen$tipovivienda\" gtv1 WHERE gtv1.id_tipovivienda = :id_tipovivienda;",
                                                 _param.clone())?;
            match _tipo_vivienda {
                Some(ts) => Ok(ts),
                None => Err(FbError::from(format!("Not Found TipoVivienda {}", id_tipovivienda)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TipoVivienda::default()
            }
        }
    }
}