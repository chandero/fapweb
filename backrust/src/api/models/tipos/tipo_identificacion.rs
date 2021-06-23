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
    id_identificacion: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct TipoIdentificacion {
    pub id_identificacion: i64,
    pub descripcion_identificacion: String,
    pub mascara_identificacion: String,
    pub inicial_identificacion: String
}

impl Display for TipoIdentificacion {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {}, mascara: {}, inicial: {})", self.id_identificacion, self.descripcion_identificacion, self.mascara_identificacion, self.inicial_identificacion)
    }
}

impl FromRow for TipoIdentificacion {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_identificacion: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_identificacion: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            mascara_identificacion: match row[2].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            inicial_identificacion: match row[3].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl TipoIdentificacion {
    pub fn get(id_identificacion: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_identificacion: id_identificacion
            };
            let _tipo_identificacion: Option<TipoIdentificacion> = 
                                tr.query_first(
                                                "SELECT gti1.* FROM \"gen$tiposidentificacion\" gti1 WHERE gti1.ID_IDENTIFICACION = :id_identificacion;",
                                                 _param.clone())?;
            match _tipo_identificacion {
                Some(ti) => Ok(ti),
                None => Err(FbError::from(format!("Not Found TipoIdentificacion {}", id_identificacion)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TipoIdentificacion::default()
            }
        }
    }
}