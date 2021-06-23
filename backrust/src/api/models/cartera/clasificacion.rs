#![allow(unused_variables)]
use crate::api::models::connection::factory::Factory;
use rsfbclient::{prelude::*, FbError, SimpleConnection, FromRow, Column, ColumnToVal};
use serde::{ Serialize, Deserialize };
use std::result::Result;
use std::clone::Clone;
use std::fmt::{Display, Formatter, Result as FmtResult};

#[derive(Clone, IntoParams)]
struct Param {
    id_clasificacion: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct Clasificacion {
    pub id_clasificacion: i64,
    pub descripcion_clasificacion: String,
}

impl Display for Clasificacion {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {})", self.id_clasificacion, self.descripcion_clasificacion)
    }
}

impl FromRow for Clasificacion {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_clasificacion: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_clasificacion: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl Clasificacion {
    pub fn get(id_clasificacion: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_clasificacion: id_clasificacion
            };
            let _clasificacion: Option<Clasificacion> = 
                                tr.query_first(
                                                "SELECT cc1.* FROM \"col$clasificacion\" cc1 WHERE cc1.id_clasificacion = :id_clasificacion;",
                                                 _param.clone())?;
            match _clasificacion {
                Some(te) => Ok(te),
                None => Err(FbError::from(format!("Not Found Clasificacion id_clasificacion: {}", id_clasificacion)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        Clasificacion::default()
            }
        }
    }
}