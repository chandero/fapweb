#![allow(unused_variables)]
use crate::connection::Connection;
use protocol::model::session::Session;

pub trait CrudSession {
    fn create_session(&self) -> Result<Session, String>;
    fn update_session(&self, new_token: String) -> Result<Session, String>;
    fn delete_session(&self) -> Result<Session, String>;
}

impl CrudSession for Session {
    /// Create a new session from a given token

    fn create_session(&self) -> Result<Self, String> {
        let query = "INSERT INTO nomina.session (sesi_token) VALUES ($1);";
        match Connection::get_connection() {
            Ok(mut client) => 
                match client.transaction() {
                    Ok(mut _tr) => match _tr.execute(query, &[&self.sesi_token]) {
                        Ok(row_inserted) => {
                            _tr.commit().unwrap();
                            Ok(Self {
                                sesi_uid: row_inserted,
                                sesi_token: self.sesi_token.to_owned()
                            })
                        },
                        Err(e) => {
                               _tr.rollback().unwrap();
                               Err(e.to_string())
                        }
                    },
                    Err(e) => Err(e.to_string())
                },
            Err(e) => Err(e.to_string())
        }
    }
    
    fn update_session(&self, new_token: String) -> Result<Self, String> {
        let query = "UPDATE nomina.session SET sesi_token = $1 WHERE sesi_token = $2";
        match Connection::get_connection() {
            Ok(mut client) => 
                match client.transaction() {
                    Ok(mut _tr) => 
                        match _tr.execute(query, &[&new_token, &self.sesi_token]) {
                            Ok(rows_updated) => {
                                _tr.commit().unwrap();
                                if rows_updated > 0 {
                                    Ok(Self {
                                        sesi_uid: self.sesi_uid,
                                        sesi_token: new_token
                                    })
                                } else {
                                    Err("No se encontró sesión".to_string())
                                }
                            },
                            Err(e) => {
                                _tr.rollback().unwrap();
                                Err(e.to_string())
                            }
                        },
                    Err(e) => Err(e.to_string())
                },
            Err(e) => Err(e.to_string())
        }
    }

    fn delete_session(&self) -> Result<Self, String> {
        let query = "DELETE FROM nomina.session WHERE sesi_token = ?1";
        match Connection::get_connection() {
            Ok(mut client) => 
                match client.transaction() {
                    Ok(mut _tr) => 
                        match _tr.execute(query, &[&self.sesi_token]) {
                            Ok(rows_deleted) => {
                                _tr.commit().unwrap();
                                if rows_deleted > 0 {
                                    Ok(Self {
                                        sesi_uid: 0,
                                        sesi_token: "".to_string()
                                    })
                                } else {
                                    Err("No se encontró sesión".to_string())
                                }
                            },
                            Err(e) => {
                                _tr.rollback().unwrap();   
                                Err(e.to_string())
                            }
                        },
                    Err(e) => Err(e.to_string())
                },
            Err(e) => Err(e.to_string())
        }
    }
}