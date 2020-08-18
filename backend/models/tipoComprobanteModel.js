'use strict';

const util = require('util');
const df = require('dateformat');
var SqlString = require('sqlstring');

var factory = require('./firebird.js');
const Auxiliar = require('./auxiliarModel.js');
var conn = factory.getConnection();

var firebird = require('./connection');

var item = {};

var TipoComprobante = function (item) {
    this.ID = item.ID;
    this.DESCRIPCION = item.DESCRIPCION;
    this.ABREVIATURA = item.ABREVIATURA;
    this.LLAVECSC = item.LLAVECSC;
}

TipoComprobante.getAll = function (result) {
    var sql = `SELECT t.ID, t.DESCRIPCION, t.ABREVIATURA, t.LLAVECSC FROM "con$tipocomprobante" t ORDER BY ID ASC`;
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

TipoComprobante.getConsecutivo = (id, result) => {
    var query = `SELECT t.LLAVECSC FROM "con$tipocomprobante" t WHERE t.ID = ?`;
    firebird.executeQueryWithParams(query, [id], (err, res) => {
        if (err)
          result(err, null);
        var _csc = res[0].LLAVECSC;
        _csc += 1;
        // actualizar a siguiente consecutivo
        var query = `UPDATE "con$tipocomprobante" SET LLAVECSC = ? WHERE ID = ?`;
        firebird.executeQueryWithParams(query, [_csc, id], (err, res) => {
            if (err)
              result(err, null);
            result(null, _csc);
        });
    });
}



module.exports = TipoComprobante;