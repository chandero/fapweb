use std::result::Result;
use crate::api::models::cartera::Solicitud;

// r01 -> rutas a persona controller

pub struct SolicitudController;

impl SolicitudController {
    pub fn get(id_solicitud: String) -> Result<Solicitud, String> {
        Ok(Solicitud::get(id_solicitud))
    }

    pub fn get_by_persona(id_identificacion:i64, id_persona: String) -> Result<Vec<Solicitud>, String> {
        Ok(Solicitud::get_by_persona(id_identificacion, id_persona))
    }    
}