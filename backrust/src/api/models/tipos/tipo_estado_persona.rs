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
    id_estado: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct TipoEstadoPersona {
    pub id_estado: i64,
    pub descripcion_estado: String
}

impl Display for TipoEstadoPersona {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {})", self.id_estado, self.descripcion_estado)
    }
}

impl FromRow for TipoEstadoPersona {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_estado: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_estado: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl TipoEstadoPersona {
    pub fn get(id_estado: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_estado: id_estado
            };
            let _tipo_estado_persona: Option<TipoEstadoPersona> = 
                                tr.query_first(
                                                "SELECT gtec1.* FROM \"gen$tiposestadopersona\" gtec1 WHERE gtec1.ID_ESTADO = :id_estado;",
                                                 _param.clone())?;
            match _tipo_estado_persona {
                Some(tep) => Ok(tep),
                None => Err(FbError::from("Not Found TipoEstadoPersona"))
            }                                                 
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TipoEstadoPersona::default()
            }
        }
    }

}