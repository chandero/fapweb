use protocol::model::session::Session;
use crate::crud::session::CrudSession;

// r01 -> rutas a persona controller

pub struct SessionController;

impl SessionController {
    pub fn create_session(token: String) -> Result<Session, String> {
        let session = Session { sesi_uid: 0, sesi_token: token };
        session.create_session()
    }

    pub fn update_session(session: Session, new_token: String) -> Result<Session, String> {
        session.update_session(new_token)
    }

    pub fn delete_session(session: Session) -> Result<Session, String> {
        session.delete_session()
    }
}