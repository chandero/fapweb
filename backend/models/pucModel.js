'use strict';
var options = require('./connection.js');

var SqlString = require('sqlstring');
var factory = require('./firebird.js');
var conn = factory.getConnection();

//Puc object constructor
var Puc = function(item){
    this.CODIGO = item.CODIGO;
    this.ID_AGENCIA = item.ID_AGENCIA;
    this.CLAVE = item.CLAVE;
    this.NOMBRE = item.NOMBRE;
    this.TIPO = item.TIPO;
    this.CODIGO_MAYOR = item.CODIGO_MAYOR;
    this.MOVIMIENTO = item.MOVIMIENTO;
    this.ESBANCO = item.ESBANCO;
    this.INFORME = item.INFORME;
    this.NIVEL = item.NIVEL;
    this.NATURALEZA = item.NATURALEZA;
    this.CENTROCOSTO = item.CENTROCOSTO;
    this.SALDOINICIAL = item.SALDOINICIAL;
    this.ESCAPTACION = item.ESCAPTACION;
    this.ESCOLOCACION = item.ESCOLOCACION;
}

Puc.getAll = function (result) {
    var sql = `SELECT * FROM "con$puc" ORDER BY CODIGO ASC`;
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

Puc.getById = function (id, result) {
    var sql = SqlString.format(`SELECT * FROM "con$puc" WHERE CODIGO = ?`, [id]);
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

module.exports = Puc;