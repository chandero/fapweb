use serde::{ Serialize, Deserialize };
/// Schema para Tabla Parametro
#[allow(missing_docs)]
#[derive(Debug, Serialize, Deserialize, Default)]/// 
pub struct Parametro {
    para_id: i64,
    para_codigo: i64,
    para_descripcion: String,
    para_numerico: bool,
    para_valor_cadena: String,
    para_valor_numero: f64,
    para_aplica_desde: i64,
    para_aplica_hasta: i64
}

/*
    Definición de parámetros generales de nómina
    para_codigo                                         valor         aplica
    1 Salario mínimo                                $908,526.00      2021
    2 Auxilio Transporte                            $106,454.00      2021
    3 Salario mínimo integral                       $11,810,838.00   2021
    Parafiscales
    4 Sena                                          0.0200           2021
    5 ICBF                                          0.0300           2021
    6 Caja Compensación                             0.0400           2021
    Prestacionales
    7 Cesantias                                     0.0833           2021
    8 Prima Servicio                                0.0833           2021
    9 Vacaciones                                    0.0417           2021
   10 Interés Cesantía                              0.0100           2021
    Seguridad Social
   11 SS Empresa                                    0.0850           2021
   12 SS Empleado                                   0.0400           2021
    Pensión
   13 FP Empresa                                    0.0120           2021
    Hora Extra
   20 Trabajo Extra Diurno                          0.2500           2021
   21 Trabajo Nocturno                              0.3500           2021
   22 Trabajo Extra Nocturno                        0.7500           2021 
   23 Trabajo Dominical Y Festivo                   0.7500           2021
   24 Trabajo Extra Diurno Dominical y Festivo      1.0000           2021
   25 Trabajo Nocturno En Dominical y Festivo       1.1000           2021
   26 Trabjao Extra Nocturno en Dominical y Festivo 1.5000           2021

   Horario Diurno
   6am a 9pm
   Horario Nocturno
   9pm a 6 am

   
*/