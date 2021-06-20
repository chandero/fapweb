#![allow(unused_variables)]
use super::connection::factory::Factory;
use rsfbclient::{ SimpleConnection, Queryable, FromRow };
use serde::{ Serialize, Deserialize };

#[derive(Debug, Serialize, Deserialize)]
pub struct Persona {
    id_identificacion: i32,
    id_persona: String,
    nombre: String,
    primer_apellido: String,
    segundo_apellido: String,
}

impl Persona {
    pub fn get(id_identificacion: i32, id_persona: String) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _rows: Option<(i32, String, String, String, String)> = 
                                            tr.query_first(
                                                "SELECT gp1.ID_IDENTIFICACION, gp1.ID_PERSONA, gp1.NOMBRE, gp1.PRIMER_APELLIDO, gp1.SEGUNDO_APELLIDO FROM \"gen$persona\" gp1 WHERE gp1.ID_IDENTIFICACION = ? AND gp1.ID_PERSONA = ?;",
                                                 (id_identificacion, id_persona))?;
            match _rows {
                Some(r) => 
                    Ok(Self {
                        id_identificacion: r.0,
                        id_persona: r.1,
                        nombre: r.2,
                        primer_apellido: r.3,
                        segundo_apellido: r.4,
                    }),
                None => 
                    Ok(Self {
                    id_identificacion: 0,
                    id_persona: "".to_string(),
                    nombre: "No Existe".to_string(),
                    primer_apellido: "".to_string(),
                    segundo_apellido: "".to_string(),
                })
            }
        });
        res.unwrap()
    }
}