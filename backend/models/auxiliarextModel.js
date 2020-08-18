'use strict';

const util = require('util');
const df = require('dateformat');
var SqlString = require('sqlstring');

var factory = require('./firebird.js');
var conn = factory.getConnection();

var firebird = require('./connection');
const Auxiliar = require('./auxiliarModel.js');

var item = {};

class AuxiliarExt {
    constructor(item) {
        this.ID = item.ID;
        this.DETALLE = item.DETALLE;
        this.CHEQUE = item.CHEQUE;
        this.ID_COMPROBANTE = item.ID_COMPROBANTE;
        this.TIPO_COMPROBANTE = item.TIPO_COMPROBANTE;
        this.ID_AGENCIA = item.ID_AGENCIA;
    }
    toArray() {
        return [this.DETALLE, this.CHEQUE, this.ID_AGENCIA, this.TIPO_COMPROBANTE, this.ID_COMPROBANTE, this.ID];
    }

    create(db) {
        console.log("guardando Auxiliar Ext...");
        var queryI = `INSERT INTO "con$auxiliarext" (
        DETALLE,
        CHEQUE,
        ID_AGENCIA,
        TIPO_COMPROBANTE,
        ID_COMPROBANTE,
        ID
    ) VALUES (?,?,?,?,?,?);`;
        return new Promise(resolve => {
            db.transaction(db.ISOLATION_READ_COMMITED, (err, transaction) => {
                transaction.query(queryI, this.toArray(), (err, res) => {
                    if (err) {
                        transaction.rollback();
                        resolve({ err, res: null });
                    } else {
                        transaction.commit();
                        resolve({ err: null, res });
                    }
                });
            });
        });
    }

    static createSync(conn) {
        var sql = SqlString.format(`INSERT INTO "con$auxiliarext" (
        DETALLE,
        CHEQUE,
        ID_AGENCIA,
        TIPO_COMPROBANTE,
        ID_COMPROBANTE,
        ID
) VALUES (?,?,?,?,?,?)`, this.toArray());
        try {
            var res = conn.querySync(sql);
            return true;
        }
        catch (error) {
            return false;
        }
    }
    static getByIdSync(cxion, id) {
        console.log('en AuxiliarExt get By Id');
        var sql = SqlString.format(`
                        SELECT 
                         a.ID, 
                         a.DETALLE,
                         a.CHEQUE,
                         a.ID_COMPROBANTE,
                         a.TIPO_COMPROBANTE,
                         a.ID_AGENCIA
                        FROM "con$auxiliarext" a
                        WHERE a.ID = ?`, [id]);
        if (!cxion.inTransaction) {
            cxion.startNewTransactionSync();
        }
        var AuxiliarExt = cxion.querySync(sql);
        return AuxiliarExt;
    }
}







module.exports = AuxiliarExt;