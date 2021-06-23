#![allow(unused_variables)]
use crate::api::models::connection::factory::Factory;
use rsfbclient::{prelude::*, FbError, SimpleConnection, FromRow, Column, ColumnToVal};
use serde::{ Serialize, Deserialize };
use std::result::Result;
use std::clone::Clone;
use std::fmt::{Display, Formatter, Result as FmtResult};

#[derive(Clone, IntoParams)]
struct Param {
    id_categoria: i64
}

#[derive(Clone, Debug, Serialize, Deserialize, Default)]
pub struct Categoria {
    pub id_categoria: i64,
    pub descripcion_categoria: String,
}

impl Display for Categoria {
    fn fmt(&self, f: &mut Formatter<'_>) -> FmtResult {
        write!(f, "(id: {}, descripcion: {})", self.id_categoria, self.descripcion_categoria)
    }
}

impl FromRow for Categoria {
    fn try_from(row: Vec<Column>) -> Result<Self, FbError> {
        Ok(Self {
            id_categoria: match row[0].clone().to_val() { Ok(v) => v, Err(_) => 0 },
            descripcion_categoria: match row[1].clone().to_val() { Ok(v) => v, Err(_) => "".to_string() },
        })
    }
}

impl Categoria {
    pub fn get(id_categoria: i64) -> Self {
        let mut _conn: SimpleConnection = Factory::get_fb_connection();
        let res = _conn.with_transaction(|tr| {
            let _param = Param {
                id_categoria: id_categoria
            };
            let _categoria: Option<Categoria> = 
                                tr.query_first(
                                                "SELECT cc1.* FROM \"col$categoria\" cc1 WHERE cc1.id_categoria = :id_categoria;",
                                                 _param.clone())?;
            match _categoria {
                Some(te) => Ok(te),
                None => Err(FbError::from(format!("Not Found Categoria id_categoria: {}", id_categoria)))
            }
        });
        match res {
            Ok(r) => r,
            Err(e) => { 
                        println!("Error: {}", e);
                        Categoria::default()
            }
        }
    }
}