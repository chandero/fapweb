#![allow(unused_variables)]
use super::connection::factory::Factory;
use rsfbclient::{ SimpleConnection, Queryable, FromRow, Column, ColumnToVal, FbError, SqlType };
use serde::{ Serialize, Deserialize };
use chrono::{NaiveDate, NaiveDateTime};
use std::convert::Into;
use std::result::Result;

#[derive(Debug, Serialize, Deserialize, Default)]
pub struct PersonaExtra {
    pub es_proveedor: bool,
    pub numero_rut: String
}


impl FromRow for PersonaExtra {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            es_proveedor: match row[2].clone().to_val() { Ok(v) => match v { 1 => true, _ => false }, Err(_) => false },
            numero_rut: match row[3].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl PersonaExtra {
    pub fn get(id_identificacion: i64, id_persona: String) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _persona_extra: Option<PersonaExtra> = 
                                            tr.query_first(
                                                "SELECT gpe1.* FROM \"gen$personaextra\" gpe1 WHERE gpe1.ID_IDENTIFICACION = ? AND gpe1.ID_PERSONA = ?;",
                                                 (id_identificacion, &id_persona))?;
            match _persona_extra {
                Some(p) => Ok(p),
                None => Err(FbError::from(format!("Not Found PersonaExtra id_identificacion: {}, id_persona:{}", id_identificacion, id_persona)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        PersonaExtra::default()
            }
        }
    }
}