#![allow(unused_variables, unused_mut)]

use std::env::set_var;
use actix_web::{
    web, App, HttpServer, HttpResponse
};
use actix_cors::Cors;
use tracing_log::LogTracer;
use tracing::subscriber::set_global_default;
use tracing_actix_web::TracingLogger;
use tracing_bunyan_formatter::{BunyanFormattingLayer, JsonStorageLayer};
use tracing_subscriber::{layer::SubscriberExt, EnvFilter, Registry};
use log;
use crate::http::handler::Handler;
use protocol::{config::Config, CONFIG_FILENAME, API_URL_LOGIN_CREDENTIALS, API_URL_LOGIN_SESSION, API_URL_LOGOUT };

mod token;
mod http;
mod connection;
mod controller;
mod crud;

const PKG_NAME: &str = env!("CARGO_PKG_NAME");


fn index() -> HttpResponse {
    HttpResponse::Ok().body("En Escucha")
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    // Parse the configuration
    let config = Config::from_file(CONFIG_FILENAME).unwrap();

    // Set the logging verbosity
    set_var(
        "RUST_LOG",
        format!(
            "actix_web={}, nomina={}, backend={}",
            config.log.actix_web, config.log.nomina, config.log.nomina
        ),
    );

    // Initialize the logger
    // env_logger::init();    
    LogTracer::init().expect("Failed to set logger");
    let addr = format!("{}:{}",config.server.ip, config.server.port);

    let server = HttpServer::new(|| {
        App::new()
            .wrap(
                Cors::permissive()
                .allowed_methods(vec!["GET", "POST", "OPTIONS"])
                .max_age(3600),
            )
            .route("/", web::get().to(index))
            .service( // prefixes all resources and routes attached to it...
                web::scope("/api")
                // ...so this handles requests for `GET /api`
                .route("/",web::get().to(Handler::index))
                .route(API_URL_LOGIN_CREDENTIALS, web::post().to(Handler::login_credentials))
                .route(API_URL_LOGIN_SESSION, web::post().to(Handler::login_session))
                .route(API_URL_LOGOUT, web::post().to(Handler::logout))
            )
    })
    .bind(addr)?;
    println!("nomina backend listening on {}:{}", config.server.ip, config.server.port);
    server.run()
    .await
}