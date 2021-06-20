#![allow(unused_variables, unused_mut)]

use actix_web::{web, App, HttpServer};
use api::handler::Handler;

mod api;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    std::env::set_var("RUST_LOG", "actix_web=debug");
    HttpServer::new(|| {
        App::new().service(
            // prefixes all resources and routes attached to it...
            web::scope("/api")
                // ...so this handles requests for `GET /api`
                .route("/",web::get().to(Handler::index))
                .route("/r01/{id_persona}", web::get().to(Handler::get_persona_by_id))
        )
    })
    .bind("127.0.0.1:8080")?
    .run()
    .await
}