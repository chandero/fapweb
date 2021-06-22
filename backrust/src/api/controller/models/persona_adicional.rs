#![allow(unused_variables)]
use super::connection::factory::Factory;
use rsfbclient::{ SimpleConnection, Queryable, FromRow, Column, ColumnToVal, FbError, SqlType };
use serde::{ Serialize, Deserialize };
use chrono::{NaiveDate, NaiveDateTime};
use std::convert::Into;
use std::result::Result;

use crate::api::controller::models::custom_date_format;

use super::tipo_ocupacion::TipoOcupacion;
use super::tipo_contrato::TipoContrato;
use super::tipo_sector::TipoSector;
use super::tipo_estrato::TipoEstrato;
use super::tipo_nivel_estudio::TipoNivelEstudio;
use super::tipo_vivienda::TipoVivienda;

#[derive(Debug, Serialize, Deserialize)]
pub struct PersonaAdicional {
    pub numero_hijos: i64,
    pub id_ocupacion: TipoOcupacion,
    pub id_tipocontrato: TipoContrato,
    pub descripcion_contrato: String,
    pub id_sector: TipoSector,
    pub descripcion_sector: String,
    pub venta_anual: f64,
    #[serde(with = "custom_date_format")]
    pub fecha_ultimo_balance: NaiveDateTime,
    pub numero_empleados: i64,
    pub declara_renta: bool,
    pub personas_a_cargo: i64,
    pub id_estrato: TipoEstrato,
    pub cabeza_familia: bool,
    pub id_estudio: TipoNivelEstudio,
    pub id_tipo_vivienda: TipoVivienda
}

impl Default for PersonaAdicional {
    fn default() -> PersonaAdicional {
        Self {
                numero_hijos: 0,
                id_ocupacion: TipoOcupacion::default(),
                id_tipocontrato: TipoContrato::default(),
                descripcion_contrato: "".to_string(),
                id_sector: TipoSector::default(),
                descripcion_sector: "".to_string(),
                venta_anual: 0.0,
                fecha_ultimo_balance: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
                numero_empleados: 0,
                declara_renta: false,
                personas_a_cargo: 0,
                id_estrato: TipoEstrato::default(),
                cabeza_familia: false,
                id_estudio: TipoNivelEstudio::default(),
                id_tipo_vivienda: TipoVivienda::default()
            }            
    }
}

impl FromRow for PersonaAdicional {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            numero_hijos: match row[2].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            id_ocupacion: match row[3].clone().to_val() { Ok(v) => TipoOcupacion::get(v), Err(_) => TipoOcupacion::default() },
            id_tipocontrato: match row[4].clone().to_val() { Ok(v) => TipoContrato::get(v), Err(_) => TipoContrato::default() },
            descripcion_contrato: match row[5].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            id_sector: match row[6].clone().to_val() { Ok(v) => TipoSector::get(v), Err(_) => TipoSector::default() },
            descripcion_sector: match row[7].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            venta_anual: match row[8].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            fecha_ultimo_balance: match row[9].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(0, 0, 0).and_hms(0,0,0) },
            numero_empleados: match row[10].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            declara_renta: match row[11].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            personas_a_cargo: match row[12].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            id_estrato: match row[13].clone().to_val() { Ok(v) => TipoEstrato::get(v), Err(_) => TipoEstrato::default() },
            cabeza_familia: match row[14].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            id_estudio: match row[15].clone().to_val() { Ok(v) => TipoNivelEstudio::get(v), Err(_) => TipoNivelEstudio::default() },
            id_tipo_vivienda: match row[16].clone().to_val() { Ok(v) => TipoVivienda::get(v), Err(_) => TipoVivienda::default() },
        })
    }
}

impl PersonaAdicional {
    pub fn get(id_identificacion: i64, id_persona: String) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _persona_adicional: Option<PersonaAdicional> = 
                                            tr.query_first(
                                                "SELECT gpa1.* FROM \"gen$persadicional\" gpa1 WHERE gpa1.ID_IDENTIFICACION = ? AND gpa1.ID_PERSONA = ?;",
                                                 (id_identificacion, &id_persona))?;
            match _persona_adicional {
                Some(p) => Ok(p),
                None => Err(FbError::from(format!("Not Found PersonaAdicional id_identificacion: {}, id_persona:{}", id_identificacion, id_persona)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        PersonaAdicional::default()
            }
        }
    }
}