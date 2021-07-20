//! Request messages
use crate::model::session::Session;
use serde::{Deserialize, Serialize};

#[derive(Debug, PartialEq, Deserialize, Serialize)]
/// The credentials based login request
pub struct LoginCredentials {
    /// The username to login
    pub username: String,

    /// The password to login
    pub password: String,
}

#[derive(Debug, Deserialize, Serialize)]
/// The session based login request
pub struct LoginSession(pub Session);

#[derive(Debug, Deserialize, Serialize)]
/// The logout request
pub struct Logout(pub Session);

#[derive(Debug, Deserialize, Serialize)]
/// the calls request
pub struct PaginationRequest {
    /// Current Page
    pub current_page: u32,
    /// Page Size
    pub page_size: u32,
}

#[derive(Debug, Deserialize, Serialize)]
/// the calls request
pub struct CallRequest {
    /// Pagination info
    pub pagination: PaginationRequest,
    /// Start Date
    pub fi: i64,
    /// End Date
    pub ff: i64
}