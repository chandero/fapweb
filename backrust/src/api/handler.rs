use super::controller::PersonaController;
use actix_web::{web, Error, HttpResponse, Responder};

pub struct Handler;

impl Handler {
    pub async fn index() -> impl Responder {
        format!("FaP está escuchando")
    }

    pub async fn get_persona_by_id(
        params: web::Path<(i32,String)>,
    ) -> Result<HttpResponse, Error> {
        let (id_identificacion, id_persona) = params.into_inner();
        Ok(web::block(move || {
            PersonaController::get(id_identificacion, id_persona)
        })
        .await
        .map(|persona| HttpResponse::Ok().json(persona))
        .map_err(|_| HttpResponse::InternalServerError())?)
    }
}
