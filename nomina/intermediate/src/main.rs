use hyper::server::conn::AddrStream;
use std::convert::Infallible;
use hyper::{Body, Request, Response, Server};
use hyper::service::{make_service_fn, service_fn};


mod proxy;

#[tokio::main]
async fn main() {

    // This is our socket address...
    let addr = ([127, 0, 0, 1], 11001).into();

    let make_svc = make_service_fn(|socket: &AddrStream| {
        let remote_addr = socket.remote_addr();
        async move {
            Ok::<_, Infallible>(service_fn(move |req: Request<Body>| async move {
                Ok::<_, Infallible>(
                    if req.uri().path().starts_with("/api") {
                        // will forward requests to port 11001
                        let res = proxy::call(remote_addr.ip(), "http://127.0.0.1:11001", req).await;
                        match res {
                            Ok(r) => r,
                            Err(_) => {
                                let body_str = format!("{:?}", "Redirect Error");
                                Response::new(Body::from(body_str))
                            }
                        }
                    } else {
                        let body_str = format!("{:?}", req);
                        Response::new(Body::from(body_str))
                    }
            )}))
        }
    });
    
    println!("Intermediate Listening on Port {}", 11001);
    // Then bind and serve...
    let server = Server::bind(&addr)
        .serve(make_svc);
    
    // Finally, spawn `server` onto an Executor...
    if let Err(e) = server.await {
        eprintln!("server error: {}", e);
    }
}