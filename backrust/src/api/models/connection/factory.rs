use std::str::FromStr;
use rsfbclient::SimpleConnection;
use rsfbclient::{prelude::*, FbError, Charset, Dialect};
use std::collections::HashMap;

pub struct Factory;

impl Factory {
    pub fn get_fb_connection() -> SimpleConnection {
        let parsed = dotproperties::parse_from_file("config.properties").unwrap();
        let mapped: HashMap<_, _> = parsed.into_iter().collect();
        let db_host = mapped.get("DB_HOST").expect("DATABASE HOST must be set");
        let db_name = mapped
            .get("DB_NAME")
            .expect("DATABASE URL AND NAME must be set");
        let db_user = mapped.get("DB_USER").expect("DATABASE USER must be set");
        let db_pass = mapped.get("DB_PASS").expect("DATABASE PASS must be set");
        let db_charset = mapped.get("DB_CHARSET").expect("DATABASE CHARSET must be set");

        let mut conn_native = rsfbclient::builder_native()
            .with_dyn_link()
            .with_remote()
            .host(db_host)
            .db_name(db_name)
            .user(db_user)
            .pass(db_pass)
            .charset(Charset::from_str(&db_charset[..]).unwrap())
            .dialect(Dialect::D3)
            .connect()
            .unwrap();

        SimpleConnection::from(conn_native)
    }
}
