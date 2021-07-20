use protocol::model::usuario::Usuario;
use crate::crud::usuario::CrudUsuario;

pub struct UsuarioController;

impl UsuarioController {
    pub fn get_by_credentials(username:String, password: String) -> Result<Usuario, String> {
        let usuario = Usuario {
            usua_uid: 0,
            usua_documento: String::default(),
            usua_apellidos: String::default(),
            usua_nombres: String::default(),
            usua_email: username,
            usua_clave: password
        };
        usuario.validar()
    }
}