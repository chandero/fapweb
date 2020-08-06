'use strict';

var SqlString = require('sqlstring');

var Connection = require('./connection.js');

class TipoOperacion {
    constructor(item) {
        this.ID_CLASE_OPERACION = item.ID_CLASE_OPERACION;
        this.DESCRIPCION_CLASE_OPERACION = item.DESCRIPCION_CLASE_OPERACION;
        this.ES_VISIBLE = item.ES_VISIBLE;
    }

    static getAgencia(result) {
        Connection.executeQueryWithoutParams(`SELECT p.* FROM "gen$claseoperacion" p`, result);
    }
}

module.exports = TipoOperacion;