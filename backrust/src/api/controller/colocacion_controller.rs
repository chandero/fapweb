use std::result::Result;
use crate::api::models::cartera::Colocacion;

// r01 -> rutas a persona controller

pub struct ColocacionController;

impl ColocacionController {
    pub fn get(id_solicitud: String) -> Result<Colocacion, String> {
        Ok(Colocacion::get(id_solicitud))
    }

    pub fn get_by_persona(id_identificacion:i64, id_persona: String) -> Result<Vec<Colocacion>, String> {
        Ok(Colocacion::get_by_persona(id_identificacion, id_persona))
    }    
}