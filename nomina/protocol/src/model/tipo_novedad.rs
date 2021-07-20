use serde::{ Serialize, Deserialize };

/// Schema para Tabla TipoNovedad
#[allow(missing_docs)]
#[derive(Debug, Serialize, Deserialize, Default)]
pub struct TipoNovedad {
    tino_id: i64,
    tino_descripcion: String,
}

/*
    1. Aportes a Salud
    2. Aportes a Pensión
    3. Fondo de Solidaridad Pensional
    4. Cuotas Sindicales
    5. Aportes a Cooperativas
    6. Embargos Judiciales
    7. Cuotas de Créditos a Entidades Financieras
    8. Deudas del Empleado con la Empresa
    9. Retención en la Fuente
*/