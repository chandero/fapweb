#![allow(unused_variables)]
use crate::connection::Connection;
use protocol::model::usuario::Usuario;

pub trait CrudUsuario {
    fn validar(&self) -> Result<Usuario, String>;
}

impl CrudUsuario for Usuario {
    /// Create a new session from a given token

    fn validar(&self) -> Result<Self, String> {
        let query = "SELECT * FROM nomina.usuario WHERE usua_email = $1 AND usua_clave = crypt($2, usua_clave);";
        match Connection::get_connection() {
            Ok(mut client) => 
                match client.transaction() {
                    Ok(mut _tr) => match _tr.query_one(query, &[&self.usua_email, &self.usua_clave]) {
                        Ok(row) => {
                            Ok(Self {
                                usua_uid: match row.try_get(0) { Ok(v) => v, Err(_) => 0},
                                usua_documento: match row.try_get(1) { Ok(v) => v , Err(_) => "".to_string()},
                                usua_apellidos: match row.try_get(2) { Ok(v) => v , Err(_) => "".to_string()},
                                usua_nombres: match row.try_get(3) { Ok(v) => v , Err(_) => "".to_string()},
                                usua_email: match row.try_get(4) { Ok(v) => v, Err(_) => "".to_string()},
                                usua_clave: match row.try_get(5) { Ok(v) => v, Err(_) => "".to_string()}
                            })
                        },
                        Err(e) => Err(e.to_string())
                    },
                    Err(e) => Err(e.to_string())
                },
            Err(e) => Err(e.to_string())
        }
    }
}