//! Response specific implementations
use crate::model::session::Session;
use serde::{Deserialize, Serialize};

#[derive(Debug, Deserialize, Serialize)]
/// The login response
pub struct Login(pub Session);

#[derive(Debug, Deserialize, Serialize)]
/// The logout response
pub struct Logout;

#[derive(Debug, Deserialize, Serialize)]
/// The logout response
pub struct PaginationResponse {
    /// Total Records
    pub total_rows: u32,
}