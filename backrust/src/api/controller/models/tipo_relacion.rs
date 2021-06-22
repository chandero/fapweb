#![allow(unused_variables)]
use super::connection::factory::Factory;
use rsfbclient::{prelude::*, FbError, SimpleConnection, FromRow, Column, ColumnToVal};
use serde::{ Serialize, Deserialize };
use chrono::{NaiveDate, NaiveDateTime};
use std::convert::Into;
use std::result::Result;
use std::clone::Clone;
use std::fmt::{Display, Formatter, Result as FmtResult};

#[derive(Clone, IntoParams)]
struct Param {
    id_tipo_relacion: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct TipoRelacion {
    pub id_tipo_relacion: i64,
    pub descripcion_relacion: String
}

impl Display for TipoRelacion {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {})", self.id_tipo_relacion, self.descripcion_relacion)
    }
}

impl FromRow for TipoRelacion {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_tipo_relacion: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_relacion: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl TipoRelacion {
    pub fn get(id_tipo_relacion: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_tipo_relacion: id_tipo_relacion
            };
            let _tipo_relacion: Option<TipoRelacion> = 
                                tr.query_first(
                                                "SELECT gtr1.* FROM \"gen$tiposrelacion\" gtr1 WHERE gtr1.ID_TIPO_RELACION = :id_tipo_relacion;",
                                                 _param.clone())?;
            match _tipo_relacion {
                Some(tr) => Ok(tr),
                None => Err(FbError::from(format!("Not Found TipoRelacion {}", id_tipo_relacion)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TipoRelacion::default()
            }
        }
    }
}