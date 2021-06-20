use super::controller::PersonaController;
use actix_web::{web, Error, HttpResponse, Responder};

pub struct Handler;

impl Handler {
    pub async fn index() -> impl Responder {
        format!("FaP está escuchando")
    }

    pub async fn get_persona_by_id(
        /*id_identificacion: web::Path<i32>,*/
        id_persona: web::Path<String>,
    ) -> Result<HttpResponse, Error> {
        Ok(web::block(move || {
            PersonaController::get(/*id_identificacion.into_inner()*/ 3, id_persona.into_inner())
        })
        .await
        .map(|persona| HttpResponse::Ok().json(persona))
        .map_err(|_| HttpResponse::InternalServerError())?)
    }
}
