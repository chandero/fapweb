'use strict';

const util = require('util');
const df = require('dateformat');
var SqlString = require('sqlstring');

var factory = require('./firebird.js');
var conn = factory.getConnection();

var firebird = require('./connection');
const AuxiliarExt = require('./auxiliarextModel.js');
const { SSL_OP_MICROSOFT_SESS_ID_BUG } = require('constants');

var item = {};

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
            this.FECHA,
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
            this.FECHA,
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
        if (!conn.inTransaction) {
            conn.startNewTransactionSync();
        }
        conn.query(sql, (err, rs) => {
            if (err) {
                console.log("error: " + err);
                conn.rollback();
                result(err, null);
            }
            if (rs) {
                var rows = rs.fetchSync('all', true);
                conn.commit();
                result(null, rows);
            }
        });
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
        var sql = SqlString.format(`
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
                        WHERE a.TIPO_COMPROBANTE = ? and a.ID_COMPROBANTE = ?`, [parseInt(tp, 10), parseInt(id, 10)]);
        if (!conn.inTransaction) {
            conn.startNewTransactionSync();
        }
        conn.query(sql, (err, rs) => {
            if (err) {
                console.log("error: " + err);
                conn.rollback();
                result(err, null);
            }
            if (rs) {
                var rows = rs.fetchSync('all', true);
                conn.commit();
                result(null, rows);
            }
        });
    }
    static getByIdSync(cxion, tp, id) {
        console.log('en Auxiliar get By Id');
        var sql = SqlString.format(`
                        SELECT 
                         a.ID_COMPROBANTE, 
                         a.ID_AGENCIA, 
                         CAST(a.FECHA AS VARCHAR(10)) AS FECHA, 
                         a.CODIGO, 
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
                         a.ID_CLASE_OPERACION
                        FROM "con$auxiliar" a
                        WHERE a.TIPO_COMPROBANTE = ? and a.ID_COMPROBANTE = ?`, [tp, id]);
        if (!cxion.inTransaction) {
            conn.startNewTransactionSync();
        }
        var Auxiliar = cxion.querySync(sql);
        var AuxExt = AuxiliarExt.getByIdSync(cxion, id);
        Auxiliar.EXT = AuxExt;
        return Auxiliar;
    }
    static removeAll(transaction, params, result) {
        var queryA = `DELETE FROM "con$auxiliar" WHERE TIPO_COMPROBANTE = ? AND ID_COMPROBANTE = ?`;
        transaction.query(queryA, params, (err, res) => {
            if (err) {
                result(err, null);
            }
            var queryE = `DELETE FROM "con$auxiliarext" WHERE TIPO_COMPROBANTE = ? AND ID_COMPROBANTE = ?`;
            transaction.query(queryE, params, (err, res) => {
                if (err) {
                    result(err, null);
                }
                result(null, res);
            });
        });
    }
    save(db) {
        console.log("guardando Auxiliar...");
        var queryI = `INSERT INTO "con$auxiliar" (
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
        ID_COMPROBANTE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING ID`;
        var queryU = `INSERT INTO "con$auxiliar" (
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
        ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)`;
        return new Promise(resolve => {
            db.transaction(db.ISOLATION_READ_COMMITED, (err, transaction) => {
                if (this.ID) {
                    const param = this.toArray();
                    transaction.query(queryU, param, (err, res) => {
                        if (err) {
                            console.log("errExt01: " + err);
                            transaction.rollback();
                            resolve({ err: err, res: null });
                        } else {
                            var item = { ID: this.ID, DETALLE: this.EXT.DETALLE, CHEQUE: this.EXT.CHEQUE, ID_COMPROBANTE: this.EXT.ID_COMPROBANTE, TIPO_COMPROBANTE: this.EXT.TIPO_COMPROBANTE, ID_AGENCIA: this.EXT.ID_AGENCIA };
                            var auxext = new AuxiliarExt(item);
                            const { error, rows } = auxext.create(db);
                            if (error) {
                                transaction.rollback();
                                resolve({ err, res });
                            } else {
                                transaction.commit();
                                resolve({ err, rows });
                            }
                        }
                    });
                }
                else {
                    transaction.query(queryI, this.toArrayI(), (err, res) => {
                        if (err) {
                            console.log("error insertando nuevo aux...", err);
                            transaction.rollback();
                            resolve({ err: err, res: null });
                        } else {
                            console.log("id: ", res);
                            var _id = res[0].ID;
                            var item = { ID: _id, DETALLE: this.EXT.DETALLE, CHEQUE: this.EXT.CHEQUE, ID_COMPROBANTE: this.EXT.ID_COMPROBANTE, TIPO_COMPROBANTE: this.EXT.TIPO_COMPROBANTE, ID_AGENCIA: this.EXT.ID_AGENCIA };
                            var auxext = new AuxiliarExt(item);
                            const { error, rows } = auxext.create(db);
                            if (error) {
                                transaction.rollback();
                                resolve({ error, rows });
                            } else {
                                transaction.commit();
                                resolve({ error, rows });
                            }
                        }
                    });
                }
            });
        });
    }

    static nullify(db, tipo, numero, result) {
        var query = `UPDATE "con$auxiliar" SET 
                                 ESTADOAUX = 'N'
                                WHERE 
                                 TIPO_COMPROBANTE = ? AND
                                 ID_COMPROBANTE = ?
                               `;
        db.transaction(db.ISOLATION_READ_COMMITED, (err, transaction) => {

            if (err) {
                console.log("err00: " + err);
                result(err, null);
            } else {
              transaction.query(query, [parseInt(tipo, 10), parseInt(numero, 10)], (err, res) => {
                if (err) {
                    console.log("error anulando aux: ", err);
                    transaction.rollback();
                    result(err, null);
                } else {
                    transaction.commit();
                    result(null, res);
                }
              })
            }
        });
    }
}













module.exports = Auxiliar;