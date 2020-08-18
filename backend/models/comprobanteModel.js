'use strict';

const util = require('util');
const df = require('dateformat');
var SqlString = require('sqlstring');

var factory = require('./firebird.js');
var conn = factory.getConnection();

var firebird = require('./connection');

const Auxiliar = require('./auxiliarModel.js');
const TipoComprobante = require('./tipoComprobanteModel.js');


//Comprobante object constructor
class Comprobante {

    constructor(item) {
        this.ID_COMPROBANTE = item.ID_COMPROBANTE;
        this.ID_AGENCIA = item.ID_AGENCIA;
        this.TIPO_COMPROBANTE = item.TIPO_COMPROBANTE;
        this.FECHADIA = item.FECHADIA;
        this.DESCRIPCION = item.DESCRIPCION;
        this.TOTAL_DEBITO = item.TOTAL_DEBITO;
        this.TOTAL_CREDITO = item.TOTAL_CREDITO;
        this.ESTADO = item.ESTADO;
        this.IMPRESO = item.IMPRESO;
        this.ANULACION = item.ANULACION;
        this.ID_EMPLEADO = item.ID_EMPLEADO;
        this.AUXS = item.AUXS;
    }
    toArray() {
        return [this.ID_AGENCIA, this.FECHADIA, this.DESCRIPCION, this.TOTAL_DEBITO, this.TOTAL_CREDITO, this.ESTADO, this.IMPRESO, this.ANULACION, this.ID_EMPLEADO, this.TIPO_COMPROBANTE, this.ID_COMPROBANTE];
    }
    static getAll(result) {
        var sql = `SELECT cc.ID_COMPROBANTE, cc.ID_AGENCIA, CAST(TRIM(cc.TIPO_COMPROBANTE) AS INTEGER) AS TIPO_COMPROBANTE, cc.FECHADIA, CAST(cc.DESCRIPCION AS VARCHAR(500)) AS DESCRIPCION, cc.TOTAL_DEBITO, cc.TOTAL_CREDITO, cc.ESTADO, cc.IMPRESO, CAST(cc.ANULACION AS VARCHAR(500)) AS ANULACION, cc.ID_EMPLEADO FROM "con$comprobante" cc ORDER BY cc.TIPO_COMPROBANTE, cc.ID_COMPROBANTE`;
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
    static getAllPage(current_page, page_size, order_by, filter, result) {
        var current = parseInt(current_page);
        var page = parseInt(page_size);
        var query = `SELECT FIRST ? SKIP ? DISTINCT cc.ID_COMPROBANTE, cc.ID_AGENCIA, CAST(TRIM(cc.TIPO_COMPROBANTE) AS INTEGER) AS TIPO_COMPROBANTE, cc.FECHADIA, CAST(cc.DESCRIPCION AS VARCHAR(500)) AS DESCRIPCION, cc.TOTAL_DEBITO, cc.TOTAL_CREDITO, cc.ESTADO, cc.IMPRESO, CAST(cc.ANULACION AS VARCHAR(500)) AS ANULACION, cc.ID_EMPLEADO FROM "con$comprobante" cc
                  LEFT JOIN "con$auxiliar" ca ON ca.TIPO_COMPROBANTE = cc.TIPO_COMPROBANTE AND ca.ID_COMPROBANTE = cc.ID_COMPROBANTE 
                `;
        if (filter) {
            query += "WHERE " + filter + " ";
        }
        query += " ORDER BY " + order_by;
        var sql = SqlString.format(query, [page, page * (current - 1)]);
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
                sql = `SELECT COUNT(*) AS TOTAL FROM (SELECT DISTINCT cc.TIPO_COMPROBANTE, cc.ID_COMPROBANTE FROM "con$comprobante" cc 
                   LEFT JOIN "con$auxiliar" ca ON ca.TIPO_COMPROBANTE = cc.TIPO_COMPROBANTE AND ca.ID_COMPROBANTE = cc.ID_COMPROBANTE `;
                if (filter) {
                    sql += "WHERE " + filter + " ";
                }
                sql += ") o";
                conn.query(sql, (err, res) => {
                    if (err) {
                        console.log("error: " + err);
                        conn.rollback();
                        result(err, null);
                    }
                    if (res) {
                        var atotal = res.fetchSync(1, true);
                        var total = atotal[0].TOTAL;
                        console.log("total: " + total);
                        conn.commit();
                        result(null, { "total": total, "data": rows });
                    }
                });
            }
        });
    }

    static getById(tipo, numero, result) {
        var sql = SqlString.format(`SELECT cc.ID_COMPROBANTE, cc.ID_AGENCIA, CAST(TRIM(cc.TIPO_COMPROBANTE) AS INTEGER) AS TIPO_COMPROBANTE, cc.FECHADIA, CAST(cc.DESCRIPCION AS VARCHAR(500)) AS DESCRIPCION, cc.TOTAL_DEBITO, cc.TOTAL_CREDITO, cc.ESTADO, cc.IMPRESO, CAST(cc.ANULACION AS VARCHAR(500)) AS ANULACION, cc.ID_EMPLEADO FROM "con$comprobante" cc WHERE cc.TIPO_COMPROBANTE = ? AND cc.ID_COMPROBANTE = ?`, [parseInt(tipo, 10), parseInt(numero, 10)]);
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

    static getByIdAll(tipo, numero, result) {
        var query = `SELECT cc.ID_COMPROBANTE, cc.ID_AGENCIA, CAST(TRIM(cc.TIPO_COMPROBANTE) AS INTEGER) AS TIPO_COMPROBANTE, tc.DESCRIPCION as DESCRIPCION_COMPROBANTE, cc.FECHADIA, CAST(cc.DESCRIPCION AS VARCHAR(500)) AS DESCRIPCION, cc.TOTAL_DEBITO, cc.TOTAL_CREDITO, cc.ESTADO, cc.IMPRESO, CAST(cc.ANULACION AS VARCHAR(500)) AS ANULACION, cc.ID_EMPLEADO, p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO AS EMPLEADO FROM "con$comprobante" cc 
            LEFT JOIN "con$tipocomprobante" tc ON tc.ID = cc.TIPO_COMPROBANTE
            LEFT JOIN "gen$empleado" p ON p.ID_EMPLEADO = cc.ID_EMPLEADO
            WHERE cc.TIPO_COMPROBANTE = ? AND cc.ID_COMPROBANTE = ?`;
        firebird.executeQueryWithParams(query, [parseInt(tipo, 10), parseInt(numero, 10)], (err, rows) => {
            if (err)
                result(err, null);
            var comprobante = rows[0];
            Auxiliar.getByIdAll(tipo, numero, (err, rows) => {
                if (err)
                    result(err, null);
                comprobante.AUXS = rows;
                result(null, comprobante);
            });
        });
    }

    save(result) {
        firebird.getConnection((err, db) => {

            if (err) {
                result(err, null);
            }
            // db = DATABASE
            db.transaction(db.ISOLATION_READ_COMMITED, (err, transaction) => {

                if (err) {
                    console.log("err00: " + err);
                    result(err, null);
                }
                // validar si es crear o actualizar
                if (this.ID_COMPROBANTE) { // actualizar
                    Auxiliar.removeAll(transaction, [this.TIPO_COMPROBANTE, this.ID_COMPROBANTE], (err, res) => {
                        if (err) {
                            console.log("err01: " + err);
                            transaction.rollback();
                            result(err, null);
                        } else {
                            console.log("aux remove res: ", res);
                            var queryU = `UPDATE "con$comprobante" SET 
                                        ID_AGENCIA = ?, 
                                        FECHADIA = ?, 
                                        DESCRIPCION = ?, 
                                        TOTAL_DEBITO = ?, 
                                        TOTAL_CREDITO = ?, 
                                        ESTADO = ?, 
                                        IMPRESO = ?, 
                                        ANULACION = ?, 
                                        ID_EMPLEADO = ? 
                                        WHERE 
                                            TIPO_COMPROBANTE = ? AND
                                            ID_COMPROBANTE = ?`;
                            transaction.query(queryU, this.toArray(), async (err, res) => {
                                console.log("actualicé el comprobante con err:", err);
                                if (err) {
                                    console.log("err02: " + err);
                                    result(err, null);
                                } else {
                                    console.log("comprobante actualizado:", res);
                                    let guardarAuxiliar = new Promise(resolve => {
                                        console.log("guardando auxiliares...");
                                        this.AUXS.forEach((a, index) => {
                                            var auxiliar = new Auxiliar(a);
                                            const { error, result } = auxiliar.save(db);
                                            console.log("guardé auxiliar...");
                                            if (error) {
                                                transaction.rollback();
                                                resolve({ err: err, res: null });
                                            }
                                            if (index === this.AUXS.length - 1) resolve({ err: null, res: true });
                                        });
                                    });
                                    guardarAuxiliar.then(({ err, res }) => {
                                        console.log("estoy en el then del guardarAuxiliar");
                                        if (err) {
                                            transaction.rollback();
                                            result(err, null);
                                        } else {
                                            transaction.commit();
                                            result(null, res);
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else { // crear
                    // primero obtener el id_comprobate
                    TipoComprobante.getConsecutivo(this.TIPO_COMPROBANTE, (err, res) => {
                        if (err) {
                            console.log("err csc:", err);
                            result(err, null);
                        } else {
                            this.ID_COMPROBANTE = res;
                            var query = `INSERT INTO "con$comprobante" (ID_AGENCIA, FECHADIA, DESCRIPCION, TOTAL_DEBITO, TOTAL_CREDITO, ESTADO, IMPRESO, ANULACION, ID_EMPLEADO, TIPO_COMPROBANTE, ID_COMPROBANTE) VALUES (?,?,?,?,?,?,?,?,?,?,?)`;
                            transaction.query(query, this.toArray(), async (err, res) => {
                                if (err) {
                                    console.log("err0202: " + err);
                                    result(err, null);
                                } else {
                                    let guardarAuxiliar = new Promise(resolve => {
                                        this.AUXS.forEach((a, index) => {
                                            var auxiliar = new Auxiliar(a);
                                            const { error, result } = auxiliar.save(db)
                                            if (error) {
                                                transaction.rollback();
                                                resolve( { err, res:null });
                                            }
                                            if (index === this.AUXS.length - 1) resolve({ err: null, res: true });
                                        });
                                    });
                                    guardarAuxiliar.then(({ err, res }) => {
                                        if (err) {
                                            transaction.rollback();
                                            result(err, null);
                                        } else {
                                            transaction.commit();
                                            result(null, this.ID_COMPROBANTE);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        });
    }

    static nullify(tipo, numero, texto, result) {
        var query = `UPDATE "con$comprobante" SET 
                                 ESTADO = 'N',
                                 ANULACION = ?
                                WHERE 
                                 TIPO_COMPROBANTE = ? AND
                                 ID_COMPROBANTE = ?
                               `;
        firebird.getConnection((err, db) => {

            if (err) {
                result(err, null);
            }
            // db = DATABASE
            db.transaction(db.ISOLATION_READ_COMMITED, (err, transaction) => {

                if (err) {
                    console.log("err00: " + err);
                    result(err, null);
                } else {

                    transaction.query(query, [texto, parseInt(tipo, 10), parseInt(numero, 10)], (err, rows) => {
                        if (err) {
                            console.log("error anulando: ", err);
                            transaction.rollback();
                            result(err, null);
                        } else {
                            Auxiliar.nullify(db, tipo, numero, (err, rows) => {
                                if (err) {
                                    transaction.rollback();
                                    result(err, null);
                                } else {
                                    transaction.commit();
                                    result(null, rows);
                                }
                            });
                        }
                    });
                }
            });
        });

    }
}

module.exports = Comprobante;