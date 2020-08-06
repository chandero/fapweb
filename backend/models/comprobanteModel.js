'use strict';

const util = require('util');
const df = require('dateformat');
var SqlString = require('sqlstring');

var factory = require('./firebird.js');
var conn = factory.getConnection();

const Auxiliar = require('./auxiliarModel.js');

var item = {};

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
    static toArray() {
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
    static create(result) {
        var sql = SqlString.format(`INSERT INTO "con$comprobante" (ID_AGENCIA, FECHADIA, DESCRIPCION, TOTAL_DEBITO, TOTAL_CREDITO, ESTADO, IMPRESO, ANULACION, ID_EMPLEADO, TIPO_COMPROBANTE, ID_COMPROBANTE) VALUES (?,?,?,?,?,?,?,?,?,?,?)`, this.toArray());
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
                try {
                    this.AUXS.map(auxiliar => {
                        var Auxiliar = new Auxiliar(auxiliar);
                        Auxiliar.createSync(conn);
                    });
                }
                catch (err) {
                    conn.rollback;
                    result(err, null);
                }
                conn.commit();
                result(null, rows);
            }
        });
    }
    static update(result) {
        var sql = SqlString.format(`UPDATE "con$comprobante" SET 
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
                                 TIPO_COMPROBANTE = ?,
                                 ID_COMPROBANTE = ?
                               `, this.toArray());
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
                this.AUXS.forEach(auxiliar => {
                    var Auxiliar = new Auxiliar(auxiliar);
                    Auxiliar.updateSync(conn);
                });
                conn.commit();
                result(null, rows);
            }
        });
    }
    static remove(result) {
        var sql = SqlString.format(`UPDATE "con$comprobante" SET 
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
                                 TIPO_COMPROBANTE = ?,
                                 ID_COMPROBANTE = ?
                               `, this.toArray());
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
    static nullify(tipo, numero, texto, result) {
        var sql = SqlString.format(`UPDATE "con$comprobante" SET 
                                 ESTADO = 'N',
                                 ANULACION = ?,
                                WHERE 
                                 TIPO_COMPROBANTE = ?,
                                 ID_COMPROBANTE = ?
                               `, [texto, tipo, numero]);
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
}

module.exports = Comprobante;