use std::result::Result;
use super::models::Persona;

pub struct PersonaController;

impl PersonaController {
    pub fn get(id_identificacion: i32, id_persona: String) -> Result<Persona, String> {
        Ok(Persona::get(id_identificacion, id_persona))
    }
}