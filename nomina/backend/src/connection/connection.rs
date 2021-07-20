use postgres::{ Client, NoTls, error::Error };
use protocol::{config::Config, CONFIG_FILENAME};

pub struct Connection;

impl Connection {
    pub fn get_connection() -> Result<Client, Error> {
        let config = Config::from_file(CONFIG_FILENAME).unwrap();
        let params = format!("host={} port={} dbname={} user={} password={}", config.postgres.host, config.postgres.port, config.postgres.database, config.postgres.username, config.postgres.password);
        let mut client = Client::connect(&params, NoTls);
        client
    }
}

