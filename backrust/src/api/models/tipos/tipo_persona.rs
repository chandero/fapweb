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
    id_tipo_persona: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct TipoPersona {
    pub id_tipo_persona: i64,
    pub descripcion_persona: String,
    pub sexo: i64
}

impl Display for TipoPersona {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {}, pide sexo: {})", self.id_tipo_persona, self.descripcion_persona, self.sexo)
    }
}

impl FromRow for TipoPersona {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_tipo_persona: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_persona: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            sexo: match row[2].clone().to_val() { Ok(v) => v, Err(_) => 0 },
        })
    }
}

impl TipoPersona {
    pub fn get(id_tipo_persona: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_tipo_persona: id_tipo_persona
            };
            let _tipo_persona: Option<TipoPersona> = 
                                tr.query_first(
                                                "SELECT gti1.* FROM \"gen$tipospersona\" gti1 WHERE gti1.ID_TIPO_PERSONA = :id_tipo_persona;",
                                                 _param.clone())?;
            match _tipo_persona {
                Some(tp) => Ok(tp),
                None => Err(FbError::from(format!("Not Found TipoPersona {}", id_tipo_persona)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TipoPersona::default()
            }
        }
    }
}