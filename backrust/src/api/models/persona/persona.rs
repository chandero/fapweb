#![allow(unused_variables)]
use crate::api::models::connection::factory::Factory;
use rsfbclient::{ SimpleConnection, Queryable, FromRow, Column, ColumnToVal, FbError, SqlType };
use serde::{ Serialize, Deserialize };
use chrono::{NaiveDate, NaiveDateTime};
use std::convert::Into;
use std::result::Result;

use crate::api::models::custom_date_format;
use crate::api::models::custom_vec_format;

use crate::api::models::tipos::tipo_identificacion::TipoIdentificacion;
use crate::api::models::tipos::tipo_persona::TipoPersona;
use crate::api::models::tipos::tipo_estado_civil::TipoEstadoCivil;
use crate::api::models::tipos::tipo_estado_persona::TipoEstadoPersona;
use crate::api::models::tipos::tipo_relacion::TipoRelacion;
use crate::api::models::tipos::tipo_ciiu::TipoCiiu;
use super::persona_adicional::PersonaAdicional;
use super::persona_extra::PersonaExtra;

#[derive(Debug, Serialize, Deserialize)]
pub struct Persona {
    pub id_identificacion: TipoIdentificacion,
    pub id_persona: String,
    pub lugar_expedicion: String,
    #[serde(with = "custom_date_format")]
    pub fecha_expedicion: NaiveDateTime,
    pub nombre: String,
    pub primer_apellido: String,
    pub segundo_apellido: String,
    pub id_tipo_persona: TipoPersona,
    pub sexo: String,
    #[serde(with = "custom_date_format")]
    pub fecha_nacimiento: NaiveDateTime,
    pub lugar_nacimiento: String,
    pub provincia_nacimiento: String,
    pub depto_nacimiento: String,
    pub pais_nacimiento: String,
    pub id_tipo_estado_civil: TipoEstadoCivil,
    pub id_conyuge: String,
    pub id_identificacion_conyuge: i64,
    pub nombre_conyuge: String,
    pub primer_apellido_conyuge: String,
    pub segundo_apellido_conyuge: String,
    pub id_apoderado: String,
    pub id_identificacion_apoderado: i64,
    pub nombre_apoderado: String,
    pub primer_apellido_apoderado: String,
    pub segundo_apellido_apoderado: String,
    pub profesion: String,
    pub id_estado: TipoEstadoPersona,
    pub id_tipo_relacion: TipoRelacion,
    pub id_ciiu: TipoCiiu,
    pub empresa_labora: String,
    #[serde(with = "custom_date_format")]
    pub fecha_ingreso_empresa: NaiveDateTime,
    pub cargo_actual: String,
    pub declaracion: String,
    pub ingresos_actividad_principal: f64,
    pub ingresos_otros: f64,
    pub ingresos_conyuge: f64,
    pub ingresos_conyuge_otros: f64,
    pub desc_ingr_otros: String,
    pub egresos_alquiler: f64,
    pub egresos_servicios: f64,
    pub egresos_transporte: f64,
    pub egresos_alimentacion: f64,
    pub egresos_deudas: f64,
    pub egresos_otros: f64,
    pub desc_egre_otros: String,
    pub egresos_conyuge: f64,
    pub otros_egresos_conyuge: f64,
    pub total_activos: f64,
    pub total_pasivos: f64,
    pub educacion: i64,
    pub retefuente: i64,
    pub acta: String,
    #[serde(with = "custom_date_format")]
    pub fecha_registro: NaiveDateTime,
    #[serde(with = "custom_vec_format")]
    pub foto: Vec<u8>,
    #[serde(with = "custom_vec_format")]
    pub firma: Vec<u8>,
    pub escritura_constitucion: String,
    pub duracion_sociedad: i64,
    pub capital_social: f64,
    pub matricula_mercantil: String,
    #[serde(with = "custom_vec_format")]
    pub foto_huella: Vec<u8>,
    #[serde(with = "custom_vec_format")]
    pub datos_huella: Vec<u8>,
    pub email: String,
    pub id_empleado: String,
    #[serde(with = "custom_date_format")]
    pub fecha_actualizacion: NaiveDateTime,
    pub adicional: PersonaAdicional,
    pub extra: PersonaExtra
}

impl FromRow for Persona {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_identificacion: match row[0].clone().to_val() { Ok(v) => TipoIdentificacion::get(v), Err(_) => TipoIdentificacion::default() },
            id_persona: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            lugar_expedicion: match row[2].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            fecha_expedicion: match row[3].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            nombre: match row[4].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            primer_apellido: match row[5].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            segundo_apellido: match row[6].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            id_tipo_persona: match row[7].clone().to_val() { Ok(v) => TipoPersona::get(v), Err(_) => TipoPersona::default() },
            sexo: match row[8].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            fecha_nacimiento: match row[9].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 0).and_hms(0,0,0) },
            lugar_nacimiento: match row[10].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            provincia_nacimiento: match row[11].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            depto_nacimiento: match row[12].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            pais_nacimiento: match row[13].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            id_tipo_estado_civil: match row[14].clone().to_val() { Ok(v) => TipoEstadoCivil::get(v), Err(_) => TipoEstadoCivil::default() },
            id_conyuge: match row[15].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            id_identificacion_conyuge: match row[16].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            nombre_conyuge: match row[17].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            primer_apellido_conyuge: match row[18].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            segundo_apellido_conyuge: match row[19].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            id_apoderado: match row[20].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            id_identificacion_apoderado: match row[21].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            nombre_apoderado: match row[22].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            primer_apellido_apoderado: match row[23].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            segundo_apellido_apoderado: match row[24].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            profesion: match row[25].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            id_estado: match row[26].clone().to_val() { Ok(v) => TipoEstadoPersona::get(v), Err(_) => TipoEstadoPersona::default() },
            id_tipo_relacion: match row[27].clone().to_val() { Ok(v) => TipoRelacion::get(v), Err(_) => TipoRelacion::default() },
            id_ciiu: match row[28].clone().to_val() { Ok(v) => TipoCiiu::get(v), Err(_) => TipoCiiu::default() },
            empresa_labora: match row[29].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            fecha_ingreso_empresa: match row[30].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            cargo_actual: match row[31].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            declaracion: match row[32].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            ingresos_actividad_principal: match row[33].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            ingresos_otros: match row[34].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            ingresos_conyuge: match row[35].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            ingresos_conyuge_otros: match row[36].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            desc_ingr_otros: match row[37].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            egresos_alquiler: match row[38].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            egresos_servicios: match row[39].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            egresos_transporte: match row[40].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            egresos_alimentacion: match row[41].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            egresos_deudas: match row[42].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            egresos_otros: match row[43].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            desc_egre_otros: match row[44].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            egresos_conyuge: match row[45].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            otros_egresos_conyuge: match row[46].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            total_activos: match row[47].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            total_pasivos: match row[48].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            educacion: match row[49].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            retefuente: match row[50].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            acta: match row[51].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            fecha_registro: match row[52].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            foto: match row[53].clone().to_val() { Ok(v) => v, Err(_) => Vec::new() },
            firma: match row[54].clone().to_val() { Ok(v) => v, Err(_) => Vec::new() },
            escritura_constitucion: match row[55].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            duracion_sociedad: match row[56].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            capital_social: match row[57].clone().to_val() { Ok(v) => v, Err(_) => 0.0 },
            matricula_mercantil: match row[58].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            foto_huella: match row[59].clone().to_val() { Ok(v) => v, Err(_) => Vec::new() },
            datos_huella: match row[60].clone().to_val() { Ok(v) => v, Err(_) => Vec::new() },
            email: match row[61].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            id_empleado: match row[62].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
            fecha_actualizacion: match row[63].clone().to_val() { Ok(v) => v, Err(_) => NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0) },
            adicional: PersonaAdicional::get(row[0].clone().to_val().unwrap(), row[1].clone().to_val().unwrap()),
            extra: PersonaExtra::get(row[0].clone().to_val().unwrap(), row[1].clone().to_val().unwrap())
        })
    }
}

impl Default for Persona {
    fn default() -> Persona {
        Persona {
            id_identificacion: TipoIdentificacion::default(),
            id_persona: "".to_string(),
            lugar_expedicion:  "".to_string(),
            fecha_expedicion:  NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            nombre: "No Registrado".to_string(),
            primer_apellido:  "".to_string(),
            segundo_apellido: "".to_string(),
            id_tipo_persona: TipoPersona::default(),
            sexo: "".to_string(),
            fecha_nacimiento: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            lugar_nacimiento: "".to_string(),
            provincia_nacimiento: "".to_string(),
            depto_nacimiento: "".to_string(),
            pais_nacimiento: "".to_string(),
            id_tipo_estado_civil: TipoEstadoCivil::default(),
            id_conyuge: "".to_string(),
            id_identificacion_conyuge:  0,
            nombre_conyuge: "".to_string(),
            primer_apellido_conyuge: "".to_string(),
            segundo_apellido_conyuge: "".to_string(),
            id_apoderado: "".to_string(),
            id_identificacion_apoderado: 0,
            nombre_apoderado: "".to_string(),
            primer_apellido_apoderado: "".to_string(),
            segundo_apellido_apoderado: "".to_string(),
            profesion: "".to_string(),
            id_estado: TipoEstadoPersona::default(),
            id_tipo_relacion: TipoRelacion::default(),
            id_ciiu: TipoCiiu::default(),
            empresa_labora: "".to_string(),
            fecha_ingreso_empresa: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            cargo_actual: "".to_string(),
            declaracion: "".to_string(),
            ingresos_actividad_principal: 0.0,
            ingresos_otros: 0.0,
            ingresos_conyuge: 0.0,
            ingresos_conyuge_otros: 0.0,
            desc_ingr_otros: "".to_string(),
            egresos_alquiler: 0.0, 
            egresos_servicios: 0.0,
            egresos_transporte: 0.0,
            egresos_alimentacion: 0.0,
            egresos_deudas: 0.0,
            egresos_otros: 0.0,
            desc_egre_otros: "".to_string(),
            egresos_conyuge: 0.0,
            otros_egresos_conyuge: 0.0,
            total_activos: 0.0,
            total_pasivos: 0.0,
            educacion: 0,
            retefuente: 0,
            acta: "".to_string(),
            fecha_registro: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            foto: Vec::new(),
            firma: Vec::new(),
            escritura_constitucion: "".to_string(),
            duracion_sociedad: 0,
            capital_social: 0.0,
            matricula_mercantil: "".to_string(),
            foto_huella: Vec::new(),
            datos_huella: Vec::new(),
            email: "".to_string(),
            id_empleado: "".to_string(),
            fecha_actualizacion: NaiveDate::from_ymd(1970, 1, 1).and_hms(0,0,0),
            adicional: PersonaAdicional::default(),
            extra: PersonaExtra::default()
        }
    }
}

impl Persona {
    pub fn get(id_identificacion: i64, id_persona: String) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _persona: Option<Persona> = 
                                            tr.query_first(
                                                "SELECT gp1.* FROM \"gen$persona\" gp1 WHERE gp1.ID_IDENTIFICACION = ? AND gp1.ID_PERSONA = ?;",
                                                 (id_identificacion, id_persona))?;
            match _persona {
                Some(p) => Ok(p),
                None => Ok(Persona::default())
            }
        });
        res.unwrap()
    }
}