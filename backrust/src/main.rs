#![feature(proc_macro_hygiene, decl_macro)]

#[macro_use] extern crate rocket;

use rocket_contrib::json::Json;

mod modules;

pub use crate::modules::factura;

#[get("/")]
fn index() -> &'static str {
    "Hola, mundo!"
}

#[get("/fact/<id>")]
fn factura(id: i64) -> Json<factura::Factura> {
    let fact = factura::from(id);
    match fact {
        Ok(v) => return Json(v),
        Err(e) => return Json(),
    }
}

fn main() {
   rocket::ignite().mount("/", routes![index]).launch();
}
