#![allow(unused_variables)]
use super::connection::factory::Factory;
use rsfbclient::{ SimpleConnection, Queryable, FromRow, Column, ColumnToVal, FbError, SqlType };
use serde::{ Serialize, Deserialize };
use chrono::{NaiveDate, NaiveDateTime};
use std::convert::Into;
use std::result::Result;

use crate::api::controller::models::custom_date_format;

#[derive(Debug, Serialize, Deserialize)]
pub struct Persona {
    pub id_identificacion: i64,
    pub id_persona: String,
    pub lugar_expedicion: String,
    #[serde(with = "custom_date_format")]
    pub fecha_expedicion: NaiveDateTime,
    pub nombre: String,
    pub primer_apellido: String,
    pub segundo_apellido: String,
    pub id_tipo_persona: i64,
    pub sexo: String,
    #[serde(with = "custom_date_format")]
    pub fecha_nacimiento: NaiveDateTime,
    pub lugar_nacimiento: String,
    pub provincia_nacimiento: String,
    pub depto_nacimiento: String,
    pub pais_nacimiento: String,
    pub id_tipo_estado_civil: i64,
    pub id_conyuge: i64,
    pub id_identificacion_conyuge: String,
    pub nombre_conyuge: String,
    pub primer_apellido_conyuge: String,
    pub segundo_apellido_conyuge: String,
    pub id_apoderado: i64,
    pub id_identificacion_apoderado: String,
    pub nombre_apoderado: String,
    pub primer_apellido_apoderado: String,
    pub segundo_apellido_apoderado: String,
    pub profesion: String,
    pub id_estado: i64,
    pub id_tipo_relacion: i64,
    pub id_ciiu: i64,
    pub empresa_labora: String,
    
}

impl FromRow for Persona {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_identificacion: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            id_persona: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            lugar_expedicion: match row[2].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            fecha_expedicion: match row[3].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(0, 0, 0).and_hms(0,0,0) },
            nombre: match row[4].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            primer_apellido: match row[5].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            segundo_apellido: match row[6].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            id_tipo_persona: match row[7].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            sexo: match row[8].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            fecha_nacimiento: match row[9].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(0, 0, 0).and_hms(0,0,0) },
            lugar_nacimiento: match row[10].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            provincia_nacimiento: match row[11].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            depto_nacimiento: match row[12].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            pais_nacimiento: match row[13].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            id_tipo_estado_civil: match row[14].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            id_conyuge: match row[15].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            id_identificacion_conyuge: match row[16].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            nombre_conyuge: match row[17].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            primer_apellido_conyuge: match row[18].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            segundo_apellido_conyuge: match row[19].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            id_apoderado: match row[20].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            id_identificacion_apoderado: match row[21].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            nombre_apoderado: match row[22].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            primer_apellido_apoderado: match row[23].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            segundo_apellido_apoderado: match row[24].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            profesion: match row[25].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            id_estado: match row[26].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            id_tipo_relacion: match row[27].clone().to_val() { Ok(v) => v, Err(_) => 0},
            id_ciiu: match row[28].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            empresa_labora: match row[29].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl Persona {
    pub fn get(id_identificacion: i32, id_persona: String) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _persona: Persona = 
                                            tr.query_first(
                                                "SELECT gp1.* FROM \"gen$persona\" gp1 WHERE gp1.ID_IDENTIFICACION = ? AND gp1.ID_PERSONA = ?;",
                                                 (id_identificacion, id_persona))?.unwrap();
            Ok(_persona)
            /* match _persona {
                Some(r) => 
                    Ok(r),
                None => 
                    Ok(Self {
                    id_identificacion: 0,
                    id_persona: "".to_string(),
                    lugar_expedicion: "".to_string(),
                    fecha_expedicion: NaiveDate::from_ymd(0, 0, 0).and_hms(0,0,0),
                    nombre: "No Existe".to_string(),
                    primer_apellido: "".to_string(),
                    segundo_apellido: "".to_string(),
                    id_tipo_persona: 0,
                    sexo: "".to_string(),
                    fecha_nacimiento: NaiveDate::from_ymd(0,0,0).and_hms(0,0,0),
                    lugar_nacimiento: "".to_string(),
                    provincia_nacimiento: "".to_string(),
                    depto_nacimiento: "".to_string(),
                    pais_nacimiento: "".to_string(),
                })
            }*/
        });
        res.unwrap()
    }
}