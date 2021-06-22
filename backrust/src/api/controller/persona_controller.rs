use std::result::Result;
use super::models::Persona;

// r01 -> rutas a persona controller

pub struct PersonaController;

impl PersonaController {
    pub fn get(id_identificacion: i64, id_persona: String) -> Result<Persona, String> {
        Ok(Persona::get(id_identificacion, id_persona))
    }
}