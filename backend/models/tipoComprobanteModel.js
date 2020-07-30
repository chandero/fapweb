'user strict';

const util = require('util');
const df = require('dateformat');
var SqlString = require('sqlstring');

var factory = require('./firebird.js');
const Auxiliar = require('./auxiliarModel.js');
var conn = factory.getConnection();

var item = {};

var TipoComprobante = function (item) {
    this.ID = item.ID;
    this.DESCRIPCION = item.DESCRIPCION;
    this.ABREVIATURA = item.ABREVIATURA;
    this.LLAVECSC = item.LLAVECSC;
}

TipoComprobante.getAll = function (result) {
    var sql = `SELECT t.* FROM "con$tipocomprobante" t ORDER BY ID ASC`;
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

module.exports = TipoComprobante;