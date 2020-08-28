'use strict';

const util = require('util');
const df = require('dateformat');
var SqlString = require('sqlstring');

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

    toArrayI() {
        return [this.DETALLE, this.CHEQUE, this.ID_AGENCIA, this.TIPO_COMPROBANTE, this.ID_COMPROBANTE];
    }

    getInsert() {
        var sql;
        var queryU = `INSERT INTO "con$auxiliarext" (
            DETALLE,
            CHEQUE,
            ID_AGENCIA,
            TIPO_COMPROBANTE,
            ID_COMPROBANTE,
            ID
        ) VALUES (?,?,?,?,?,?);`;
        var queryI = `INSERT INTO "con$auxiliarext" (
            DETALLE,
            CHEQUE,
            ID_AGENCIA,
            TIPO_COMPROBANTE,
            ID_COMPROBANTE,
            ID
        ) VALUES (?,?,?,?,?,:id);`;        
        if (this.ID) {
            sql = SqlString.format(queryU, this.toArray());
        } else {
            sql = SqlString.format(queryI, this.toArrayI());
        }
        return sql;
    }

}

module.exports = AuxiliarExt;