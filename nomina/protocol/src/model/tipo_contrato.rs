use serde::{ Serialize, Deserialize };

/// Schema para Tabla TipoContrato
#[allow(missing_docs)]
#[derive(Debug, Serialize, Deserialize, Default)]
pub struct TipoContrato {
    tico_id: i64,
    tico_descripcion: String,
    tico_normativa: String,
}

/*
 * Tipos de Contrato
 * 
 * 1. Contrato a término fijo (Art. 46 del Código Sustantivo de Trabajo y Art. 28 de la Ley 789 de 2002)
 * 2. Contrato a Término Indefinido (Art. 47 del Código Sustantivo de Trabajo)
 * 3. Contrato de Obra o labor (Art. 45 del Código Sustantivo de Trabajo)
 * 4. Contrato de aprendizaje (Art. 30 de la Ley 789 de 2002)
 * 5. Contrato temporal, ocasional o accidental (Art. 6 del Código Sustantivo de Trabajo)
 * 6. Contrato civil por prestación de servicios
 */