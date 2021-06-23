use super::controller::PersonaController;
use super::controller::SolicitudController;
use super::controller::ColocacionController;

use actix_web::{web, Error, HttpResponse, Responder};

pub struct Handler;

impl Handler {
    pub async fn index() -> impl Responder {
        format!("FaP está escuchando")
    }

    pub async fn get_persona_by_id(
        params: web::Path<(i64,String)>,
    ) -> Result<HttpResponse, Error> {
        let (id_identificacion, id_persona) = params.into_inner();
        Ok(web::block(move || {
            PersonaController::get(id_identificacion, id_persona)
        })
        .await
        .map(|persona| HttpResponse::Ok().json(persona))
        .map_err(|_| HttpResponse::InternalServerError())?)
    }

    pub async fn get_solicitud_by_id(
        params: web::Path<String>,
    ) -> Result<HttpResponse, Error> {
        let id_solicitud = params.into_inner();
        Ok(web::block(move || {
            SolicitudController::get(id_solicitud)
        })
        .await
        .map(|solicitud| HttpResponse::Ok().json(solicitud))
        .map_err(|_| HttpResponse::InternalServerError())?)
    }

    pub async fn get_solicitud_by_persona(
        params: web::Path<(i64,String)>,
    ) -> Result<HttpResponse, Error> {
        let (id_identificacion, id_persona) = params.into_inner();
        Ok(web::block(move || {
            SolicitudController::get_by_persona(id_identificacion, id_persona)
        })
        .await
        .map(|res| HttpResponse::Ok().json(res))
        .map_err(|_| HttpResponse::InternalServerError())?)
    }    

    pub async fn get_colocacion_by_id(
        params: web::Path<String>,
    ) -> Result<HttpResponse, Error> {
        let id_solicitud = params.into_inner();
        Ok(web::block(move || {
            ColocacionController::get(id_solicitud)
        })
        .await
        .map(|solicitud| HttpResponse::Ok().json(solicitud))
        .map_err(|_| HttpResponse::InternalServerError())?)
    }

    pub async fn get_colocacion_by_persona(
        params: web::Path<(i64,String)>,
    ) -> Result<HttpResponse, Error> {
        let (id_identificacion, id_persona) = params.into_inner();
        Ok(web::block(move || {
            ColocacionController::get_by_persona(id_identificacion, id_persona)
        })
        .await
        .map(|res| HttpResponse::Ok().json(res))
        .map_err(|_| HttpResponse::InternalServerError())?)
    }      
}
