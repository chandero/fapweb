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
pub struct TipoEstadoSolicitud {
    pub id_estado: i64,
    pub descripcion_estado: String,
    pub actualiza_informacion: bool,
    pub desembolso: bool,
    pub vigencia: i64,
    pub estado_proximo: i64,
    pub orden: i64
}

impl Display for TipoEstadoSolicitud {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {}, actualiza: {}, desembolso: {}, vigencia: {}, proximo: {}, orden: {})", self.id_estado, self.descripcion_estado, self.actualiza_informacion, self.desembolso, self.vigencia, self.estado_proximo, self.orden)
    }
}

impl FromRow for TipoEstadoSolicitud {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_estado: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_estado: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            actualiza_informacion: match row[2].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            desembolso: match row[3].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            vigencia: match row[4].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            estado_proximo: match row[5].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            orden: match row[6].clone().to_val() { Ok(v) => v, Err(_) => 0 },
        })
    }
}

impl TipoEstadoSolicitud {
    pub fn get(id_estado: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_estado: id_estado
            };
            let _tipo_estado: Option<TipoEstadoSolicitud> =
                                tr.query_first(
                                                "SELECT ces1.* FROM \"col$estadosolicitud\" ces1 WHERE ces1.id_estado = :id_estado;",
                                                 _param.clone())?;
            match _tipo_estado {
                Some(tc) => Ok(tc),
                None => Err(FbError::from(format!("Not Found TipoEstadoSolicitud {}", id_estado)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        TipoEstadoSolicitud::default()
            }
        }
    }
}