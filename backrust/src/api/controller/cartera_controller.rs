use std::result::Result;
use crate::api::models::cartera::Colocacion;
use crate::api::models::cartera::Extracto;
use crate::api::models::cartera::Solicitud;

// r01 -> rutas a persona controller

pub struct CarteraController;

impl CarteraController {

    pub fn get_solicitud(id_solicitud: String) -> Result<Solicitud, String> {
        Ok(Solicitud::get(id_solicitud))
    }

    pub fn get_solicitud_by_persona(id_identificacion:i64, id_persona: String) -> Result<Vec<Solicitud>, String> {
        Ok(Solicitud::get_by_persona(id_identificacion, id_persona))
    }  

    pub fn get_colocacion(id_solicitud: String) -> Result<Colocacion, String> {
        Ok(Colocacion::get(id_solicitud))
    }

    pub fn get_colocacion_by_persona(id_identificacion:i64, id_persona: String) -> Result<Vec<Colocacion>, String> {
        Ok(Colocacion::get_by_persona(id_identificacion, id_persona))
    }    

    pub fn get_extracto(id_colocacion: String) -> Result<Vec<Extracto>, String> {
        Ok(Extracto::get(id_colocacion))
    }
}