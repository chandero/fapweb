'use strict';

var SqlString = require('sqlstring');

var Connection = require('./connection.js');

class PersonaExtra {
    constructor(item) {
        this.ID_IDENTIFICACION = item.ID_IDENTIFICACION;
        this.ID_PERSONA = item.ID_PERSONA;
        this.ES_PROVEEDOR = item.ES_PROVEEDOR;
        this.NUMERO_RUT = item.NUMERO_RUT;
    }

    toArray() {
        return [this.ES_PROVEEDOR, this.NUMERO_RUT, this.ID_IDENTIFICACION, this.ID_PERSONA];
    }
}

class PersAdicional {
    constructor(item) {
        this.ID_IDENTIFICACION = item.ID_IDENTIFICACION;
        this.ID_PERSONA = item.ID_PERSONA;
        this.NUMERO_HIJOS = item.NUMERO_HIJOS;
        this.ID_OCUPACION = item.ID_OCUPACION;
        this.ID_TIPOCONTRATO = item.ID_TIPOCONTRATO;
        this.DESCRIPCION_CONTRATO = item.DESCRIPCION_CONTRATO;
        this.ID_SECTOR = item.ID_SECTOR;
        this.DESCRIPCION_SECTOR = item.DESCRIPCION_SECTOR;
        this.VENTA_ANUAL = item.VENTA_ANUAL;
        this.FECHA_ULTIMO_BALANCE = item.FECHA_ULTIMO_BALANCE;
        this.NUMERO_EMPLEADOS = item.NUMERO_EMPLEADOS;
        this.DECLARA_RENTA = item.DECLARA_RENTA;
        this.PERSONAS_A_CARGO = item.PERSONAS_A_CARGO;
        this.ID_ESTRATO = item.ID_ESTRATO;
        this.CABEZAFAMILIA = item.CABEZAFAMILIA;
        this.ID_ESTUDIO = item.ID_ESTUDIO;
        this.ID_TIPOVIVIENDA = this.ID_TIPOVIVIENDA;
    }

    toArray() {
        return [this.NUMERO_HIJOS,
        this.ID_OCUPACION,
        this.ID_TIPOCONTRATO,
        this.DESCRIPCION_CONTRATO,
        this.ID_SECTOR,
        this.DESCRIPCION_SECTOR,
        this.VENTA_ANUAL,
        this.FECHA_ULTIMO_BALANCE,
        this.NUMERO_EMPLEADOS,
        this.DECLARA_RENTA,
        this.PERSONAS_A_CARGO,
        this.ID_ESTRATO,
        this.CABEZAFAMILIA,
        this.ID_ESTUDIO,
        this.ID_TIPOVIVIENDA
        ];
    }
}

class Persona {
    constructor(item) {
        this.ID_IDENTIFICACION = item.ID_IDENTIFICACION;
        this.ID_PERSONA = item.ID_PERSONA;
        this.LUGAR_EXPEDICION = item.LUGAR_EXPEDICION;
        this.FECHA_EXPEDICION = item.FECHA_EXPEDICION;
        this.NOMBRE = item.NOMBRE;
        this.PRIMER_APELLIDO = item.PRIMER_APELLIDO;
        this.SEGUNDO_APELLIDO = item.SEGUNDO_APELLIDO;
        this.ID_TIPO_PERSONA = item.ID_TIPO_PERSONA;
        this.SEXO = item.SEXO;
        this.FECHA_NACIMIENTO = item.FECHA_NACIMIENTO;
        this.LUGAR_NACIMIENTO = item.LUGAR_NACIMIENTO;
        this.PROVINCIA_NACIMIENTO = item.PROVINCIA_NACIMIENTO;
        this.DEPTO_NACIMIENTO = item.DEPTO_NACIMIENTO;
        this.PAIS_NACIMIENTO = item.PAIS_NACIMIENTO;
        this.ID_TIPO_ESTADO_CIVIL = item.ID_TIPO_ESTADO_CIVIL;
        this.ID_CONYUGE = item.ID_CONYUGE;
        this.ID_IDENTIFICACION_CONYUGE = item.ID_IDENTIFICACION_CONYUGE;
        this.NOMBRE_CONYUGE = item.NOMBRE_CONYUGE;
        this.PRIMER_APELLIDO_CONYUGE = item.PRIMER_APELLIDO_CONYUGE;
        this.SEGUNDO_APELLIDO_CONYUGE = item.SEGUNDO_APELLIDO_CONYUGE;
        this.ID_APODERADO = item.ID_APODERADO;
        this.ID_IDENTIFICACION_APODERADO = item.ID_IDENTIFICACION_APODERADO;
        this.NOMBRE_APODERADO = item.NOMBRE_APODERADO;
        this.PRIMER_APELLIDO_APODERADO = item.PRIMER_APELLIDO_APODERADO;
        this.SEGUNDO_APELLIDO_APODERADO = item.SEGUNDO_APELLIDO_APODERADO;
        this.PROFESION = item.PROFESION;
        this.ID_ESTADO = item.ID_ESTADO;
        this.ID_TIPO_RELACION = item.ID_TIPO_RELACION;
        this.ID_CIIU = item.ID_CIIU;
        this.EMPRESA_LABORA = item.EMPRESA_LABORA;
        this.FECHA_INGRESO_EMPRESA = item.FECHA_INGRESO_EMPRESA;
        this.CARGO_ACTUAL = item.CARGO_ACTUAL;
        this.DECLARACION = item.DECLARACION;
        this.INGRESOS_A_PRINCIPAL = item.INGRESOS_A_PRINCIPAL;
        this.INGRESOS_OTROS = item.INGRESOS_OTROS;
        this.INGRESOS_CONYUGE = item.INGRESOS_CONYUGE;
        this.INGRESOS_CONYUGE_OTROS = item.INGRESOS_CONYUGE_OTROS;
        this.DESC_INGR_OTROS = item.DESC_INGR_OTROS;
        this.EGRESOS_ALQUILER = item.EGRESOS_ALQUILER;
        this.EGRESOS_SERVICIOS = item.EGRESOS_SERVICIOS;
        this.EGRESOS_TRANSPORTE = item.EGRESOS_TRANSPORTE;
        this.EGRESOS_ALIMENTACION = item.EGRESOS_ALIMENTACION;
        this.EGRESOS_DEUDAS = item.EGRESOS_DEUDAS;
        this.EGRESOS_OTROS = item.EGRESOS_OTROS;
        this.DESC_EGRE_OTROS = item.DESC_EGRE_OTROS;
        this.EGRESOS_CONYUGE = item.EGRESOS_CONYUGE;
        this.OTROS_EGRESOS_CONYUGE = item.OTROS_EGRESOS_CONYUGE;
        this.TOTAL_ACTIVOS = item.TOTAL_ACTIVOS;
        this.TOTAL_PASIVOS = item.TOTAL_PASIVOS;
        this.EDUCACION = item.EDUCACION;
        this.RETEFUENTE = item.RETEFUENTE;
        this.ACTA = item.ACTA;
        this.FECHA_REGISTRO = item.FECHA_REGISTRO;
        this.FOTO = item.FOTO;
        this.FIRMA = item.FIRMA;
        this.ESCRITURA_CONSTITUCION = item.ESCRITURA_CONSTITUCION;
        this.DURACION_SOCIEDAD = item.DURACION_SOCIEDAD;
        this.CAPITAL_SOCIAL = item.CAPITAL_SOCIAL;
        this.MATRICULA_MERCANTIL = item.MATRICULA_MERCANTIL;
        this.FOTO_HUELLA = item.FOTO_HUELLA;
        this.DATOS_HUELLA = item.DATOS_HUELLA;
        this.EMAIL = item.EMAIL;
        this.ID_EMPLEADO = item.ID_EMPLEADO;
        this.FECHA_ACTUALIZACION = item.FECHA_ACTUALIZACION;
        this.ADICIONAL = item.ADICIONAL;
        this.EXTRA = item.EXTRA;
    }

    toArray() {
        return [
            this.LUGAR_EXPEDICION,
            this.FECHA_EXPEDICION,
            this.NOMBRE,
            this.PRIMER_APELLIDO,
            this.SEGUNDO_APELLIDO,
            this.ID_TIPO_PERSONA,
            this.SEXO,
            this.FECHA_NACIMIENTO,
            this.LUGAR_NACIMIENTO,
            this.PROVINCIA_NACIMIENTO,
            this.DEPTO_NACIMIENTO,
            this.PAIS_NACIMIENTO,
            this.ID_TIPO_ESTADO_CIVIL,
            this.ID_CONYUGE,
            this.ID_IDENTIFICACION_CONYUGE,
            this.NOMBRE_CONYUGE,
            this.PRIMER_APELLIDO_CONYUGE,
            this.SEGUNDO_APELLIDO_CONYUGE,
            this.ID_APODERADO,
            this.ID_IDENTIFICACION_APODERADO,
            this.NOMBRE_APODERADO,
            this.PRIMER_APELLIDO_APODERADO,
            this.SEGUNDO_APELLIDO_APODERADO,
            this.PROFESION,
            this.ID_ESTADO,
            this.ID_TIPO_RELACION,
            this.ID_CIIU,
            this.EMPRESA_LABORA,
            this.FECHA_INGRESO_EMPRESA,
            this.CARGO_ACTUAL,
            this.DECLARACION,
            this.INGRESOS_A_PRINCIPAL,
            this.INGRESOS_OTROS,
            this.INGRESOS_CONYUGE,
            this.INGRESOS_CONYUGE_OTROS,
            this.DESC_INGR_OTROS,
            this.EGRESOS_ALQUILER,
            this.EGRESOS_SERVICIOS,
            this.EGRESOS_TRANSPORTE,
            this.EGRESOS_ALIMENTACION,
            this.EGRESOS_DEUDAS,
            this.EGRESOS_OTROS,
            this.DESC_EGRE_OTROS,
            this.EGRESOS_CONYUGE,
            this.OTROS_EGRESOS_CONYUGE,
            this.TOTAL_ACTIVOS,
            this.TOTAL_PASIVOS,
            this.EDUCACION,
            this.RETEFUENTE,
            this.ACTA,
            this.FECHA_REGISTRO,
            this.FOTO,
            this.FIRMA,
            this.ESCRITURA_CONSTITUCION,
            this.DURACION_SOCIEDAD,
            this.CAPITAL_SOCIAL,
            this.MATRICULA_MERCANTIL,
            this.FOTO_HUELLA,
            this.DATOS_HUELLA,
            this.EMAIL,
            this.ID_EMPLEADO,
            this.FECHA_ACTUALIZACION,
            this.ID_IDENTIFICACION,
            this.ID_PERSONA];
    }

    static obtenerTipoDocumento(result) {
        Connection.executeQueryWithoutParams(`SELECT p.* FROM "gen$tiposidentificacion" p`, result);
    }

    static obtenerPorDocumento(tp, id, result) {
        Connection.executeQueryWithParams(`SELECT p.* FROM "gen$persona" p WHERE p.ID_IDENTIFICACION = ? AND p.ID_PERSONA = ?`, [tp, id], result);
    }

    static obtenerAdicionalPorDocumento(tp, id, result) {
        Connection.executeQueryWithParams(`SELECT p.* FROM "gen$persadicional" p WHERE p.ID_IDENTIFICACION = ? AND p.ID_PERSONA = ?`, [tp, id], function (err, rs) {
            if (err) {
                result(err, null);
            } else {
                result(null, rs);    
            }
        });         
    }

    static obtenerExtraPorDocumento(tp, id, result) {
        Connection.executeQueryWithParams(`SELECT p.* FROM "gen$personaextra" p WHERE p.ID_IDENTIFICACION = ? AND p.ID_PERSONA = ?`, [tp, id], function (err, rs) {
            if (err) {
                result(err, null);
            } else {
                result(null, rs);    
            }
        });         
    }    

    static obtenerNombrePorDocumento(tp, id, result) {
        Connection.executeQueryWithParams(`SELECT p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO AS NOMBRE FROM "gen$persona" p WHERE p.ID_IDENTIFICACION = ? AND p.ID_PERSONA = ?`, [tp, id], function (err, rs) {
                    if (err) {
                        result(err, null);
                    } else {
                        result(null, rs);    
                    }
        });
    }

    static obtenerPorNombre(options, result) {
        options = options || {};
        var name = options.name || "";
        var firstname = options.firstname || "";
        var lastname = options.lastname || "";
        var sep = "";
        var params = [];

        var query = `SELECT 
                        p.*
                        FROM "gen$persona" p WHERE `;

        if (firstname !== "") {
            query += sep + "p.PRIMER_APELLIDO LIKE ?";
            sep = " AND ";
            console.log("firstname: " + firstname);
            params.push(firstname);
        }

        if (lastname !== "") {
            query += sep + "p.SEGUNDO_APELLIDO LIKE '?'";
            sep = " AND ";
            params.push(lastname);
        }

        if (name !== "") {
            query += sep + "p.NOMBRE LIKE '?'";
            params.push(name);
        }

        query += " ORDER BY p.PRIMER_APELLIDO, p.SEGUNDO_APELLIDO, p.NOMBRE";

        Connection.executeQueryWithParams(query, params, result);
    }
}

module.exports = Persona;