use serde::{ Serialize, Deserialize };
/// A session representation
#[allow(missing_docs)]
#[derive(Debug, Serialize, Deserialize, Default)]
pub struct Session {
    pub sesi_uid: u64,
    pub sesi_token: String
}
impl Session {
    /// Create a new session from a given token
    pub fn new<T>(token: T) -> Self
    where
        String: From<T>,
    {
        Self {
            sesi_uid: 0,
            sesi_token: token.into(),
        }
    }
}

/*

-- DROP TABLE nomina."session";

CREATE TABLE nomina."session" (
	sesi_uid int8 NOT NULL GENERATED ALWAYS AS IDENTITY,
	sesi_token varchar(250) NOT NULL
);

*/