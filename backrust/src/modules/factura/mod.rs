#![allow(unused_variables, unused_mut)]
extern crate rsfbclient;

use chrono::naive::{NaiveDateTime};
use chrono::offset::Utc;
use chrono::DateTime;

use serde::{Deserialize, Serialize};
use std::string::String;

use rsfbclient::{prelude::*, FbError};

#[derive(Serialize, Deserialize)]
pub struct Factura {
    fact_numero: i64,
    #[serde(with = "json_time")]
    fact_fecha: NaiveDateTime,
    fact_descripcion: String,
    tipo_comprobante: i32,
    id_comprobante: i32,
    #[serde(with = "json_time")]
    fecha_comprobante: NaiveDateTime,
    id_identificacion: i32,
    id_persona: String,
    id_empleado: String,
    fact_estado: i32,
}

pub fn time_to_json(t: NaiveDateTime) -> String {
    DateTime::<Utc>::from_utc(t, Utc).to_rfc3339()
}

mod json_time {
    use super::*;
    use serde::{de::Error, Deserialize, Deserializer, Serialize, Serializer};

    pub fn serialize<S: Serializer>(
        time: &NaiveDateTime,
        serializer: S,
    ) -> Result<S::Ok, S::Error> {
        time_to_json(time.clone()).serialize(serializer)
    }

    pub fn deserialize<'de, D: Deserializer<'de>>(
        deserializer: D,
    ) -> Result<NaiveDateTime, D::Error> {
        let time: String = Deserialize::deserialize(deserializer)?;
        Ok(DateTime::parse_from_rfc3339(&time)
            .map_err(D::Error::custom)?
            .naive_utc())
    }
}

// Items in modules default to private visibility.
pub fn from(id: i64) -> Result<Factura, FbError> {
    let string_conf =
        std::env::var("DATABASE_URL").map_err(|e| FbError::from("DATABASE_URL env var is empty"));

    #[cfg(feature = "pure_rust")]
    let mut conn = ConnectionBuilder::linked()
        .from_string(&string_conf)
        .connect()?;

    let rows: Vec<Factura> =
        conn.query("select f1.* from FACTURA f1 WHERE f1.FACT_NUMERO = ?", &id);
    /*
    None => Factura {
        fact_numero: 0,
        fact_fecha: NaiveDate::from_ymd(1970,1,1).and_hms(0,0,0),
        fact_descripcion: "".to_string(),
        tipo_comprobante: 0,
        id_comprobante: 0,
        fecha_comprobante: NaiveDate::from_ymd(1970,1,1).and_hms(0,0,0),
        id_identificacion: 0,
        id_persona: "".to_string(),
        id_empleado: "".to_string(),
        fact_estado: 0
    },
    Some(fact) => Factura {
        fact_numero: fact.try_from("FACT_NUMERO"),
        fact_fecha: fact.try_from("FACT_FECHA"),
        fact_descripcion: fact.try_from("FACT_DESCRIPCION"),
        tipo_comprobante: fact.try_from("TIPO_COMPROBANTE"),
        id_comprobante: fact.try_from("ID_COMPROBANTE"),
        fecha_comprobante: fact.try_from("FECHA_COMPROBANTE"),
        id_identificacion: fact.try_from("ID_IDENTIFICACION"),
        id_persona: fact.try_from("ID_PERSONA"),
        id_empleado: fact.try_from("ID_EMPLEADO"),
        fact_estado: fact.try_from("FACT_ESTADO")
    },
    */
    Ok(rows[0])
}
