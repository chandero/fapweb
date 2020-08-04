'use strict';

const util = require('util');
const df = require('dateformat');
var SqlString = require('sqlstring');

var factory = require('./firebird.js');
var conn = factory.getConnection();

var item = {};

//Comprobante object constructor
var Contabilidad = function (item) {
    this.comprobante = item.comprobante;
}

Contabilidad.getAll = function (result) {
    var sql = `SELECT c.* FROM "con$comprobante" c ORDER BY c.FECHADIA DESC, c.TIPO_COMPROBANTE, c.ID_COMPROBANTE`;
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

Contabilidad.getAllPage = function (current_page, page_size, result) {
    var sql = SqlString.format(`SELECT FIRST ? SKIP ? c.* FROM "con$comprobante" c ORDER BY c.FECHADIA DESC, c.TIPO_COMPROBANTE, c.ID_COMPROBANTE`, [page_size, page_size * (current_page - 1)]);
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

module.exports = Contabilidad;