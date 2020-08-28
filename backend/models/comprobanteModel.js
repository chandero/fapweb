'use strict';

const moment = require('moment');
const util = require('util');
const df = require('dateformat');
var SqlString = require('sqlstring');

var factory = require('./firebird.js');
var conn = factory.getConnection();

var firebird = require('./connection');

const Auxiliar = require('./auxiliarModel.js');
const TipoComprobante = require('./tipoComprobanteModel.js');
const { resolve } = require('path');


//Comprobante object constructor
class Comprobante {

    constructor(item) {
        if (item.ID_COMPROBANTE && item.ID_COMPROBANTE > 0) {
            this.ID_COMPROBANTE = item.ID_COMPROBANTE;
        }
        else {
            this.ID_COMPROBANTE = null;
        }
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
        return [this.ID_AGENCIA, moment(Date.parse(this.FECHADIA)).format('DD.MM.YYYY'), this.DESCRIPCION, this.TOTAL_DEBITO, this.TOTAL_CREDITO, this.ESTADO, this.IMPRESO, this.ANULACION, this.ID_EMPLEADO, this.TIPO_COMPROBANTE, this.ID_COMPROBANTE];
    }
    static getAll(result) {
        var sql = `SELECT cc.ID_COMPROBANTE, cc.ID_AGENCIA, CAST(TRIM(cc.TIPO_COMPROBANTE) AS INTEGER) AS TIPO_COMPROBANTE, cc.FECHADIA, CAST(cc.DESCRIPCION AS VARCHAR(500)) AS DESCRIPCION, cc.TOTAL_DEBITO, cc.TOTAL_CREDITO, TRIM(cc.ESTADO) AS ESTADO, cc.IMPRESO, CAST(cc.ANULACION AS VARCHAR(500)) AS ANULACION, cc.ID_EMPLEADO FROM "con$comprobante" cc ORDER BY cc.TIPO_COMPROBANTE, cc.ID_COMPROBANTE`;
        firebird.executeQueryWithoutParams(sql, result);
    }
    static getAllPage(current_page, page_size, order_by, filter, result) {
        var current = parseInt(current_page);
        var page = parseInt(page_size);
        var query = `SELECT FIRST ? SKIP ? DISTINCT cc.ID_COMPROBANTE, cc.ID_AGENCIA, CAST(TRIM(cc.TIPO_COMPROBANTE) AS INTEGER) AS TIPO_COMPROBANTE, cc.FECHADIA, CAST(cc.DESCRIPCION AS VARCHAR(500)) AS DESCRIPCION, cc.TOTAL_DEBITO, cc.TOTAL_CREDITO, TRIM(cc.ESTADO) AS ESTADO, cc.IMPRESO, CAST(cc.ANULACION AS VARCHAR(500)) AS ANULACION, cc.ID_EMPLEADO FROM "con$comprobante" cc
                  LEFT JOIN "con$auxiliar" ca ON ca.TIPO_COMPROBANTE = cc.TIPO_COMPROBANTE AND ca.ID_COMPROBANTE = cc.ID_COMPROBANTE 
                `;
        if (filter) {
            query += "WHERE " + filter + " ";
        }
        query += " ORDER BY " + order_by;
        firebird.executeQueryWithParams(query,[page, page * (current - 1)], (err, rows) => {
            if (err) {
                result(err, null);
            } else {
                query = `SELECT COUNT(*) AS TOTAL FROM (SELECT DISTINCT cc.TIPO_COMPROBANTE, cc.ID_COMPROBANTE FROM "con$comprobante" cc 
                   LEFT JOIN "con$auxiliar" ca ON ca.TIPO_COMPROBANTE = cc.TIPO_COMPROBANTE AND ca.ID_COMPROBANTE = cc.ID_COMPROBANTE `;
                if (filter) {
                    query += "WHERE " + filter + " ";
                }
                query += ") o";
                firebird.executeQueryWithoutParams(query, (err, res) => {
                    if (err) {
                        result(err, null);
                    }
                    if (res) {
                        var atotal = res;
                        var total = atotal[0].TOTAL;
                        console.log("total: " + total);
                        result(null, { "total": total, "data": rows });
                    }
                });
            }
        });
    }

    static getById(tipo, numero, result) {
        var sql = `SELECT cc.ID_COMPROBANTE, cc.ID_AGENCIA, CAST(TRIM(cc.TIPO_COMPROBANTE) AS INTEGER) AS TIPO_COMPROBANTE, cc.FECHADIA, CAST(cc.DESCRIPCION AS VARCHAR(500)) AS DESCRIPCION, cc.TOTAL_DEBITO, cc.TOTAL_CREDITO, TRIM(cc.ESTADO) AS ESTADO, cc.IMPRESO, CAST(cc.ANULACION AS VARCHAR(500)) AS ANULACION, cc.ID_EMPLEADO FROM "con$comprobante" cc WHERE cc.TIPO_COMPROBANTE = ? AND cc.ID_COMPROBANTE = ?`;
        firebird.executeQueryWithParams(sql,[parseInt(tipo, 10), parseInt(numero, 10)],result);
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
        var query = [];
        query.push('EXECUTE BLOCK');
        query.push('AS');
        query.push('DECLARE id INT = 0;');
        query.push('BEGIN');
        if (this.ID_COMPROBANTE) {
            var sql = SqlString.format(`  UPDATE "con$comprobante" SET 
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
              ID_COMPROBANTE = ?;`, this.toArray());
            query.push(sql);
            sql = SqlString.format(`DELETE FROM "con$auxiliar" WHERE TIPO_COMPROBANTE = ? AND ID_COMPROBANTE = ?;`, [this.TIPO_COMPROBANTE, this.ID_COMPROBANTE]);
            query.push(sql);
            sql = SqlString.format(`DELETE FROM "con$auxiliarext" WHERE TIPO_COMPROBANTE = ? AND ID_COMPROBANTE = ?;`, [this.TIPO_COMPROBANTE, this.ID_COMPROBANTE]);
            query.push(sql);
            let auxPromise = new Promise(resolve => {
                this.AUXS.forEach((a, index) => {
                    var auxiliar = new Auxiliar(a);
                    var sql = auxiliar.getInsert();
                    query.push(sql);
                    if (index === this.AUXS.length - 1) resolve();
                });
            });
            auxPromise.then((sql) => {
                query.push('END');
                firebird.executeQueryWithoutParams(query.join("\n"), (err, res) => {
                    if (err) {
                        result(err, null);
                    } else {
                        result(null, this.ID_COMPROBANTE);
                    }
                });
            })
        } else {
            let cscPromise = new Promise(resolve => {
                TipoComprobante.getConsecutivo(this.TIPO_COMPROBANTE, (err, res) => {
                    if (err) {
                        console.log("err csc:", err);
                        result(err, null);
                        resolve(null);
                    } else {
                        console.log("csc:", res);
                        resolve(res);
                    }
                });
            });
            cscPromise.then((csc) => {
                this.ID_COMPROBANTE = csc;
                var sql = SqlString.format(`INSERT INTO "con$comprobante" 
                        (ID_AGENCIA, FECHADIA, DESCRIPCION, TOTAL_DEBITO, TOTAL_CREDITO, ESTADO, IMPRESO, ANULACION, ID_EMPLEADO, TIPO_COMPROBANTE, ID_COMPROBANTE) 
                     VALUES (?,?,?,?,?,?,?,?,?,?,?);`, this.toArray());
                query.push(sql);
                let auxPromise = new Promise(resolve => {
                    this.AUXS.forEach((a, index) => {
                        a.TIPO_COMPROBANTE = this.TIPO_COMPROBANTE;
                        a.ID_COMPROBANTE = this.ID_COMPROBANTE;
                        var auxiliar = new Auxiliar(a);
                        var sql = auxiliar.getInsert();
                        query.push(sql);
                        if (index === this.AUXS.length - 1) resolve();
                    });
                });
                auxPromise.then((sql) => {
                    query.push('END');
                    firebird.executeQueryWithoutParams(query.join("\n"), (err, res) => {
                        if (err) {
                            result(err, null);
                        } else {
                            result(null, this.ID_COMPROBANTE);
                        }
                    });
                });
            });
        }
    }

    static nullify(tipo, numero, texto, result) {
        var query = [];
        var sql = 'EXECUTE BLOCK';
        query.push(sql);
        sql = 'AS';
        query.push(sql);
        sql = 'BEGIN';
        query.push(sql);
        sql = SqlString.format(`UPDATE "con$comprobante" SET 
                                 ESTADO = 'N',
                                 ANULACION = ?
                                WHERE 
                                 TIPO_COMPROBANTE = ? AND
                                 ID_COMPROBANTE = ?;
                               `, [texto, parseInt(tipo, 10), parseInt(numero, 10)]);
        query.push(sql);
        sql = Auxiliar.getNullify(tipo, numero);
        query.push(sql);
        sql = 'END';
        query.push(sql);
        firebird.executeQueryWithoutParams(query.join("\n"), result);
    }

    static recover(tipo, numero, result) {
        var query = [];
        var sql = 'EXECUTE BLOCK';
        query.push(sql);
        sql = 'AS';
        query.push(sql);
        sql = 'BEGIN';
        query.push(sql);
        sql = SqlString.format(`UPDATE "con$comprobante" SET 
                                 ESTADO = 'O',
                                 ANULACION = ?
                                WHERE 
                                 TIPO_COMPROBANTE = ? AND
                                 ID_COMPROBANTE = ?;
                               `, [null, parseInt(tipo, 10), parseInt(numero, 10)]);
        query.push(sql);
        sql = Auxiliar.getRecover(tipo, numero);
        query.push(sql);
        sql = 'END';
        query.push(sql);
        firebird.executeQueryWithoutParams(query.join("\n"), result);
    }    
}

module.exports = Comprobante;