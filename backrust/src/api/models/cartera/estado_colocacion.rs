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
    id_estado_colocacion: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct EstadoColocacion {
    pub id_estado_colocacion: i64,
    pub descripcion_estado_colocacion: String,
    pub es_prejuridico: bool,
    pub es_juridico: bool,
    pub es_castigado: bool,
    pub es_no_visado: bool,
    pub es_anulado: bool,
    pub es_cancelado: bool,
    pub es_vigente: bool,
    pub es_saldado: bool,
    pub color: i64,
    pub es_fallecido: bool,
    pub es_incapacitado: bool,
    pub orden: i64
}

impl Display for EstadoColocacion {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {}, orden: {})", self.id_estado_colocacion, self.descripcion_estado_colocacion, self.orden)
    }
}

impl FromRow for EstadoColocacion {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_estado_colocacion: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_estado_colocacion: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            es_prejuridico: match row[2].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            es_juridico: match row[3].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            es_castigado: match row[4].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            es_no_visado: match row[5].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            es_anulado: match row[6].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            es_cancelado: match row[7].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            es_vigente: match row[8].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            es_saldado: match row[9].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            color: match row[10].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            es_fallecido: match row[11].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            es_incapacitado: match row[12].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            orden: match row[13].clone().to_val() { Ok(v) => v, Err(_) => 0 },
        })
    }
}

impl EstadoColocacion {
    pub fn get(id_estado_colocacion: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_estado_colocacion: id_estado_colocacion
            };
            let _tipo_estado: Option<EstadoColocacion> =
                                tr.query_first(
                                                "SELECT ces1.* FROM \"col$estado\" ces1 WHERE ces1.id_estado_colocacion = :id_estado_colocacion;",
                                                 _param.clone())?;
            match _tipo_estado {
                Some(tc) => Ok(tc),
                None => Err(FbError::from(format!("Not Found EstadoColocacion {}", id_estado_colocacion)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        EstadoColocacion::default()
            }
        }
    }
}