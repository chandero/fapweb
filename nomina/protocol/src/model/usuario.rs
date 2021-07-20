use serde::{ Serialize, Deserialize };
/// Schema para Tabla Usuario
#[allow(missing_docs)]
#[derive(Debug, Serialize, Deserialize, Default)]
pub struct Usuario {
    pub usua_uid: i64,
    pub usua_documento: String,
    pub usua_apellidos: String,
    pub usua_nombres: String,
    pub usua_email: String,
    pub usua_clave: String
}

/*
CREATE TABLE nomina.usuario (
	usua_uid int8 NOT NULL GENERATED ALWAYS AS IDENTITY,
	usua_documento varchar(100) NULL,
	usua_apellidos varchar(200) NULL,
	usua_nombres varchar(200) NOT NULL,
	usua_email varchar(200) NOT NULL,
	usua_clave varchar(200) NOT NULL
);

*/