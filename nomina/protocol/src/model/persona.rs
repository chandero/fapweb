use serde::{ Serialize, Deserialize };
/// Schema para Tabla Persona
#[allow(missing_docs)]
#[derive(Debug, Serialize, Deserialize, Default)]/// 
pub struct Persona {
    pers_id: i64,
    pers_documento: String,
    pers_apellidos: String,
    pers_nombres: String,
    pers_direccion: String,
    pers_telefono: String,
    muni_id: i64, // Código del Múnicipio
    ciiu_id: String,
    pers_labora_direccion: String,
    pers_labora_telefono: String,
    pers_labora_ciiu_id: String
}