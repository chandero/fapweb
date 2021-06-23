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
    id_tipo_estado_civil: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct TipoEstadoCivil {
    pub id_tipo_estado_civil: i64,
    pub descripcion_estado_civil: String,
    pub conyuge_estado_civil: i64
}

impl Display for TipoEstadoCivil {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {}, pide conyuge: {})", self.id_tipo_estado_civil, self.descripcion_estado_civil, self.conyuge_estado_civil)
    }
}

impl FromRow for TipoEstadoCivil {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_tipo_estado_civil: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_estado_civil: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            conyuge_estado_civil: match row[2].clone().to_val() { Ok(v) => v, Err(_) => 0 },
        })
    }
}

impl TipoEstadoCivil {
    pub fn get(id_tipo_estado_civil: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_tipo_estado_civil: id_tipo_estado_civil
            };
            let _tipo_estado_civil: Option<TipoEstadoCivil> = 
                                tr.query_first(
                                                "SELECT gtec1.* FROM \"gen$tiposestadocivil\" gtec1 WHERE gtec1.ID_TIPO_ESTADO_CIVIL = :id_tipo_estado_civil;",
                                                 _param.clone())?;
            match _tipo_estado_civil {
                Some(tec) => Ok(tec),
                None => Err(FbError::from("Not Found TipoEstadoCivil"))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TipoEstadoCivil::default()
            }
        }
    }
}