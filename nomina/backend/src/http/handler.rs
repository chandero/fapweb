use actix_web::{
    dev::HttpResponseBuilder,
    web,
    error,
    error::{ErrorInternalServerError, ErrorUnauthorized},
    http::header,
    http::StatusCode,
    web::Json,
    Error, HttpResponse,
};

use crate::controller::session_controller::SessionController;
use crate::token::{ Token, TokenError };
use protocol::{ 
    request::request::{
        LoginCredentials, LoginSession, Logout },
    response::response::{Login}
};
use crate::controller::usuario_controller::UsuarioController;
use log::debug;

use derive_more::{Display, Error};

#[derive(Debug, Display, Error)]
enum UserError {
    #[display(fmt = "Validation error on field: {}", field)]
    ValidationError { field: String },
}

#[cfg(feature = "backend")]
impl error::ResponseError for UserError {
    fn error_response(&self) -> HttpResponse {
        HttpResponseBuilder::new(self.status_code())
            .set_header(header::CONTENT_TYPE, "text/html; charset=utf-8")
            .body(self.to_string())
    }
    fn status_code(&self) -> StatusCode {
        match *self {
            UserError::ValidationError { .. } => StatusCode::BAD_REQUEST,
        }
    }
}

pub struct Handler;

impl Handler {
    pub async fn index() -> Result<HttpResponse, Error> {
        Ok(HttpResponse::Ok()
            .content_type("text/html; charset=utf-8")
            .header("X-FAP-NOMINA", "true")
            .body("En Escucha"))
    }

    pub async fn login_credentials(json: Json<LoginCredentials>) -> Result<HttpResponse, Error> {
        println!("En login credentials");
        let r = json.into_inner();
        debug!("Usuario {} está tratando de logearse", r.username);
        // Verify username and password
        if r.username.is_empty() || r.password.is_empty() {
            return Err(ErrorUnauthorized("Usuario o Clave Incorrecto"));
        }

        match UsuarioController::get_by_credentials(r.username, r.password) {
            Ok(usuario) => {
                // Create a new token
                match Token::create(&usuario.usua_email) {
                    Ok(token) => {
                        // Update the session in the database
                        let result = SessionController::create_session(token);
                        Ok(HttpResponse::Ok()
                            .json(Login(result.map_err(ErrorInternalServerError)?)))
                    }
                    Err(e) => {
                        println!("Error validando usuario: {}", e);
                        Err(e.into())
                    }
                }
            }
            Err(e) => {
                println!("Error validando usuario: {}", e);
                Err(TokenError::Create.into())
            }
        }
    }

    pub async fn login_session(payload: Json<LoginSession>) -> Result<HttpResponse, Error> {
        let session = payload.into_inner().0;
        // Create a new token for the already given one
        debug!("Session token {} wants to be renewed", session.sesi_token);
        let new_token = Token::verify(&session.sesi_token)?;
        // Update the session in the database
        let result = SessionController::update_session(session, new_token);
        Ok(HttpResponse::Ok().json(Login(result.map_err(ErrorInternalServerError)?)))
    }

    pub async fn logout(payload: Json<Logout>) -> Result<HttpResponse, Error> {
        let session = payload.into_inner().0;
        // Remove the session from the database
        println!(
            "Session token {} wants to be logged out",
            session.sesi_token
        );
        let result = SessionController::delete_session(session);
        Ok(HttpResponse::Ok().json(Logout(result.map_err(ErrorInternalServerError)?)))
    }
}
