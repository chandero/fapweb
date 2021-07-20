use actix_web::{HttpResponse, ResponseError};
use jsonwebtoken::{decode, encode, DecodingKey, EncodingKey, Algorithm, Header, Validation};
use serde::{Deserialize, Serialize};
use std::time::{SystemTime, UNIX_EPOCH};
use thiserror::Error;
use uuid::Uuid;

const SECRET: &[u8] = b"FeVvVWQWzedP8zpLFzgP";

#[derive(Debug, Error)]
/// Token handling related errors
pub enum TokenError {
    #[error("unable to create session token")]
    /// Session token creation failed
    Create,

    #[error("unable to verify session token")]
    /// Session token verification failed
    Verify,
}

impl ResponseError for TokenError {
    fn error_response(&self) -> HttpResponse {
        match self {
            TokenError::Create => HttpResponse::InternalServerError().into(),
            TokenError::Verify => HttpResponse::Unauthorized().into(),
        }
    }
}

#[derive(Deserialize, Serialize)]
/// A web token
pub struct Token {
    /// The subject of the token
    sub: String,

    /// The exipration date of the token
    exp: usize,

    /// The issued at field
    iat: usize,

    /// The token id
    jti: String,
}

impl Token {
    /// Create a new default token for a given username
    pub fn create(username: &str) -> Result<String, TokenError> {
        const DEFAULT_TOKEN_VALIDITY: usize = 3600;
        let now = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .map_err(|_| TokenError::Create)?;
        let header = Header::new(Algorithm::HS512);
        let claims = Token {
            sub: username.to_owned(),
            exp: now.as_secs() as usize + DEFAULT_TOKEN_VALIDITY,
            iat: now.as_secs() as usize,
            jti: Uuid::new_v4().to_string(),
        };
        encode(
            &header,
            &claims,
            &EncodingKey::from_secret(SECRET),
        )
        .map_err(|_| TokenError::Create)
    }

    /// Verify the validity of a token and get a new one
    pub fn verify(token: &str) -> Result<String, TokenError> {
        println!("token a decodificar: {}", token);
        let data = decode::<Token>(
            token,
            &DecodingKey::from_secret(SECRET),
            &Validation::new(Algorithm::HS512),
        )
        .map_err(|e| { println!("error en decode: {}", e); TokenError::Verify })?;
        Self::create(&data.claims.sub)
    }
}