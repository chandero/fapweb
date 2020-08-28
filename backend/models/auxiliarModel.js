'use strict';

const moment = require('moment');
const util = require('util');
const df = require('dateformat');
var SqlString = require('sqlstring');

var firebird = require('./connection');
const AuxiliarExt = require('./auxiliarextModel.js');


class Auxiliar {
    constructor(item) {
        this.ID = item.ID;
        this.ID_COMPROBANTE = item.ID_COMPROBANTE;
        this.ID_AGENCIA = item.ID_AGENCIA;
        this.FECHA = item.FECHA;
        this.CODIGO = item.CODIGO;
        this.DEBITO = item.DEBITO;
        this.CREDITO = item.CREDITO;
        this.ID_CUENTA = item.ID_CUENTA;
        this.ID_COLOCACION = item.ID_COLOCACION;
        this.ID_IDENTIFICACION = item.ID_IDENTIFICACION;
        this.ID_PERSONA = item.ID_PERSONA;
        this.MONTO_RETENCION = item.MONTO_RETENCION;
        this.TASA_RETENCION = item.TASA_RETENCION;
        this.ESTADOAUX = item.ESTADOAUX;
        this.TIPO_COMPROBANTE = item.TIPO_COMPROBANTE;
        this.ID_CLASE_OPERACION = item.ID_CLASE_OPERACION;
        this.EXT = {
            ID: item.ID,
            DETALLE: item.DETALLE,
            CHEQUE: item.CHEQUE,
            ID_COMPROBANTE: item.ID_COMPROBANTE,
            TIPO_COMPROBANTE: item.TIPO_COMPROBANTE,
            ID_AGENCIA: item.ID_AGENCIA
        }
    }
    toArray() {
        return [
            this.ID_AGENCIA,
            moment(Date.parse(this.FECHA)).format('DD.MM.YYYY'),
            this.CODIGO,
            this.DEBITO,
            this.CREDITO,
            this.ID_CUENTA,
            this.ID_COLOCACION,
            this.ID_IDENTIFICACION,
            this.ID_PERSONA,
            this.MONTO_RETENCION,
            this.TASA_RETENCION,
            this.ESTADOAUX,
            this.ID_CLASE_OPERACION,
            this.TIPO_COMPROBANTE,
            this.ID_COMPROBANTE,
            this.ID
        ];
    }
    toArrayI() {
        return [
            this.ID_AGENCIA,
            moment(Date.parse(this.FECHA)).format('DD.MM.YYYY'),
            this.CODIGO,
            this.DEBITO,
            this.CREDITO,
            this.ID_CUENTA,
            this.ID_COLOCACION,
            this.ID_IDENTIFICACION,
            this.ID_PERSONA,
            this.MONTO_RETENCION,
            this.TASA_RETENCION,
            this.ESTADOAUX,
            this.ID_CLASE_OPERACION,
            this.TIPO_COMPROBANTE,
            this.ID_COMPROBANTE
        ];
    }
    static getAll(result) {
        var sql = `SELECT * FROM "con$auxiliar" ORDER BY TIPO_COMPROBANTE, ID_COMPROBANTE`;
        firebird.executeQueryWithoutParams(sql, result);
    }
    static getByIdAll(tp, id, result) {
        var query = `
                    SELECT 
                        a.ID_COMPROBANTE, 
                        a.ID_AGENCIA, 
                        CAST(a.FECHA AS VARCHAR(10)) AS FECHA, 
                        a.CODIGO, 
                        p.NOMBRE AS CUENTA,
                        a.DEBITO, 
                        a.CREDITO, 
                        a.ID_CUENTA, 
                        a.ID_COLOCACION, 
                        a.ID_IDENTIFICACION, 
                        a.ID_PERSONA, 
                        a.MONTO_RETENCION, 
                        a.TASA_RETENCION, 
                        a.ESTADOAUX, 
                        a.TIPO_COMPROBANTE, 
                        a.ID, 
                        a.ID_CLASE_OPERACION,
                        e.ID, 
                        e.DETALLE,
                        e.CHEQUE,
                        e.ID_COMPROBANTE,
                        CAST(e.TIPO_COMPROBANTE AS INTEGER) AS TIPO_COMPROBANTE,
                        e.ID_AGENCIA,
                        (TRIM(r.NOMBRE) || ' ' || TRIM(r.PRIMER_APELLIDO) || ' ' || TRIM(r.SEGUNDO_APELLIDO)) AS PERSONA
                    FROM "con$auxiliar" a
                    LEFT JOIN "con$auxiliarext" e ON e.ID = a.ID
                    LEFT JOIN "con$puc" p ON p.CODIGO = a.CODIGO
                    LEFT JOIN "gen$persona" r ON r.ID_IDENTIFICACION = a.ID_IDENTIFICACION and r.ID_PERSONA = a.ID_PERSONA
                    WHERE a.TIPO_COMPROBANTE = ? and a.ID_COMPROBANTE = ?`;
        firebird.executeQueryWithParams(query, [parseInt(tp, 10), parseInt(id, 10)], result);
    }

    static getById(tp, id, result) {
        firebird.executeQueryWithParams(`
                        SELECT 
                         a.ID_COMPROBANTE, 
                         a.ID_AGENCIA, 
                         CAST(a.FECHA AS VARCHAR(10)) AS FECHA, 
                         a.CODIGO, 
                         p.NOMBRE AS CUENTA,
                         a.DEBITO, 
                         a.CREDITO, 
                         a.ID_CUENTA, 
                         a.ID_COLOCACION, 
                         a.ID_IDENTIFICACION, 
                         a.ID_PERSONA, 
                         a.MONTO_RETENCION, 
                         a.TASA_RETENCION, 
                         a.ESTADOAUX, 
                         a.TIPO_COMPROBANTE, 
                         a.ID, 
                         a.ID_CLASE_OPERACION,
                         e.ID, 
                         e.DETALLE,
                         e.CHEQUE,
                         e.ID_COMPROBANTE,
                         CAST(e.TIPO_COMPROBANTE AS INTEGER) AS TIPO_COMPROBANTE,
                         e.ID_AGENCIA,
                         (TRIM(r.NOMBRE) || ' ' || TRIM(r.PRIMER_APELLIDO) || ' ' || TRIM(r.SEGUNDO_APELLIDO)) AS PERSONA
                        FROM "con$auxiliar" a
                        LEFT JOIN "con$auxiliarext" e ON e.ID = a.ID
                        LEFT JOIN "con$puc" p ON p.CODIGO = a.CODIGO
                        LEFT JOIN "gen$persona" r ON r.ID_IDENTIFICACION = a.ID_IDENTIFICACION and r.ID_PERSONA = a.ID_PERSONA
                        WHERE a.TIPO_COMPROBANTE = ? and a.ID_COMPROBANTE = ?`, [parseInt(tp, 10), parseInt(id, 10)], result);

    }
    
    getInsert() {
        var sql = [];
        var queryI = `  INSERT INTO "con$auxiliar" (
            ID_AGENCIA,
            FECHA,
            CODIGO,
            DEBITO,
            CREDITO,
            ID_CUENTA,
            ID_COLOCACION,
            ID_IDENTIFICACION,
            ID_PERSONA,
            MONTO_RETENCION,
            TASA_RETENCION,
            ESTADOAUX,
            ID_CLASE_OPERACION,
            TIPO_COMPROBANTE,        
            ID_COMPROBANTE) 
        VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) 
        RETURNING ID INTO :id;`;

        var queryU = `  INSERT INTO "con$auxiliar" (
            ID_AGENCIA,
            FECHA,
            CODIGO,
            DEBITO,
            CREDITO,
            ID_CUENTA,
            ID_COLOCACION,
            ID_IDENTIFICACION,
            ID_PERSONA,
            MONTO_RETENCION,
            TASA_RETENCION,
            ESTADOAUX,
            ID_CLASE_OPERACION,
            TIPO_COMPROBANTE,        
            ID_COMPROBANTE,
            ID) 
        VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);`;    
        
        if (this.ID) {
            var query = SqlString.format(queryU, this.toArray());
            sql.push(query);
            var item = { ID: this.ID, DETALLE: this.EXT.DETALLE, CHEQUE: this.EXT.CHEQUE, ID_COMPROBANTE: this.EXT.ID_COMPROBANTE, TIPO_COMPROBANTE: this.EXT.TIPO_COMPROBANTE, ID_AGENCIA: this.EXT.ID_AGENCIA };
            var auxext = new AuxiliarExt(item);
            var queryExt = auxext.getInsert();
            sql.push(queryExt);
        } else {
            var query = SqlString.format(queryI, this.toArrayI());
            sql.push(query);
            var item = { ID: null, DETALLE: this.EXT.DETALLE, CHEQUE: this.EXT.CHEQUE, ID_COMPROBANTE: this.EXT.ID_COMPROBANTE, TIPO_COMPROBANTE: this.EXT.TIPO_COMPROBANTE, ID_AGENCIA: this.EXT.ID_AGENCIA };
            var auxext = new AuxiliarExt(item);
            var queryExt = auxext.getInsert();
            sql.push(queryExt);            
        }
        return sql.join("\n");
    }

    static getNullify(tipo, numero) {
        var sql = [];
        var queryI = `UPDATE "con$auxiliar" SET ESTADOAUX = 'N' WHERE TIPO_COMPROBANTE = ? AND ID_COMPROBANTE = ?;`;
        var query = SqlString.format(queryI, [tipo, numero]);
        sql.push(query);
        return sql.join("\n");
    }

    static getRecover(tipo, numero) {
        var sql = [];
        var queryI = `UPDATE "con$auxiliar" SET ESTADOAUX = 'O' WHERE TIPO_COMPROBANTE = ? AND ID_COMPROBANTE = ?;`;
        var query = SqlString.format(queryI, [tipo, numero]);
        sql.push(query);
        return sql.join("\n");
    }    
}

module.exports = Auxiliar;