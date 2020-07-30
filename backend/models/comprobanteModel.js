'user strict';

const util = require('util');
const df = require('dateformat');
var SqlString = require('sqlstring');

var factory = require('./firebird.js');
const Auxiliar = require('./auxiliarModel.js');
const { rejects } = require('assert');
const { resolve } = require('path');
var conn = factory.getConnection();

var item = {};

//Comprobante object constructor
var Comprobante = function (item) {
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

Comprobante.toArray = function () {
    return [this.ID_AGENCIA, this.FECHADIA, this.DESCRIPCION, this.TOTAL_DEBITO, this.TOTAL_CREDITO, this.ESTADO, this.IMPRESO, this.ANULACION, this.ID_EMPLEADO, this.TIPO_COMPROBANTE, this.ID_COMPROBANTE];
}

Comprobante.getAll = function (result) {
    var sql = `SELECT * FROM "con$comprobante" ORDER BY TIPO_COMPROBANTE, ID_COMPROBANTE`;
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
};

Comprobante.getAllPage = function (current_page, page_size, result) {
    var current = parseInt(current_page);
    var page = parseInt(page_size);
    var sql = SqlString.format(`SELECT FIRST ? SKIP ? c.ID_COMPROBANTE, c.ID_AGENCIA, c.TIPO_COMPROBANTE, CAST(c.FECHADIA AS VARCHAR(10)) AS FECHADIA, CAST(DESCRIPCION AS VARCHAR(500)) AS DESCRIPCION, TOTAL_DEBITO, TOTAL_CREDITO, ESTADO, IMPRESO, CAST(ANULACION AS VARCHAR(500)) AS ANULACION, ID_EMPLEADO FROM "con$comprobante" c ORDER BY c.FECHADIA DESC, c.TIPO_COMPROBANTE, c.ID_COMPROBANTE`, [page, page * (current - 1)]);
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
            sql = `SELECT COUNT(*) AS TOTAL FROM "con$comprobante" c`;
            conn.query(sql, (err, res) => {
                if (err) {
                    console.log("error: " + err);
                    conn.rollback();
                    result(err, null);
                }
                if (res) {
                    var atotal = res.fetchSync('all', true);
                    var total = atotal[0].TOTAL;
                    conn.commit();
                    result(null, { "total": total, "data": rows });
                }
            });
        }
    });
}

Comprobante.getById = function (tipo, numero, result) {
    var sql = SqlString.format(`SELECT * FROM "con$comprobante" WHERE TIPO_COMPROBANTE = ? AND ID_COMPROBANTE = ?`, [parseInt(tipo, 10), parseInt(numero, 10)]);
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
};

Comprobante.create = function (result) {
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
                    Auxiliar.createSync(conn)
                });
            } catch (err) {
                conn.rollback
                result(err, null)
            }
            conn.commit();
            result(null, rows)
        }
    });
}

Comprobante.update = function (result) {
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
            result(null, rows)
        }
    });
}

Comprobante.remove = function (result) {
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
            result(null, rows)
        }
    });
}

Comprobante.nullify = function (tipo, numero, texto, result) {
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
            result(null, rows)
        }
    });
}

module.exports = Comprobante;