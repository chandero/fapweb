#![allow(unused_variables)]
use crate::api::models::connection::factory::Factory;
use rsfbclient::{prelude::*, FbError, SimpleConnection, FromRow, Column, ColumnToVal};
use serde::{ Serialize, Deserialize };
use std::result::Result;
use std::clone::Clone;
use std::fmt::{Display, Formatter, Result as FmtResult};

#[derive(Clone, IntoParams)]
struct Param {
    id_ente_aprobador: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct EnteAprobador {
    pub id_ente_aprobador: i64,
    pub descripcion_ente_aprobador: String,
    pub monto_maximo: f64
}

impl Display for EnteAprobador {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {}, monto: {})", self.id_ente_aprobador, self.descripcion_ente_aprobador, self.monto_maximo)
    }
}

impl FromRow for EnteAprobador {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_ente_aprobador: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_ente_aprobador: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            monto_maximo: match row[2].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
        })
    }
}

impl EnteAprobador {
    pub fn get(id_ente_aprobador: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_ente_aprobador: id_ente_aprobador
            };
            let _ente_aprobador: Option<EnteAprobador> = 
                                tr.query_first(
                                                "SELECT cea1.* FROM \"col$enteaprobador\" cea1 WHERE cea1.id_ente_aprobador = :id_ente_aprobador;",
                                                 _param.clone())?;
            match _ente_aprobador {
                Some(te) => Ok(te),
                None => Err(FbError::from(format!("Not Found EnteAprobador id_ente_aprobador: {}", id_ente_aprobador)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        EnteAprobador::default()
            }
        }
    }
}