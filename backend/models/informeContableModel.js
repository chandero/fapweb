'use strict';

var SqlString = require('sqlstring');

var Connection = require('./connection.js');

class InformeContable {
    constructor(item) {
        this.ID = item.ID;
        this.DESCRIPCION = item.DESCRIPCION;
        this.PIDOID = item.PIDOID;
        this.PIDOMONTO = item.PIDOMONTO;
        this.PIDOTASA = item.PIDOTASA;
        this.PIDOTIPOID = item.PIDOTIPOID;
    }

    static getAll(result) {
        Connection.executeQueryWithoutParams(`SELECT p.* FROM "con$informes" p`, result);
    }
}

module.exports = InformeContable;