'use strict';

var SqlString = require('sqlstring');

var Connection = require('./connection.js');

class Agencia {
    constructor(item) {
        this.ID_AGENCIA = item.ID_AGENCIA;
        this.DESCRIPCION_AGENCIA = item.DESCRIPCION_AGENCIA;
        this.DIRECCION_AGENCIA = item.DIRECCION_AGENCIA;
        this.TELEFONO_AGENCIA = item.TELEFONO_AGENCIA;
        this.CODIGO_CONTABLE = item.CODIGO_CONTABLE;
        this.CODIGO_MUNICIPIO = item.CODIGO_MUNICIPIO;
        this.HOST = item.HOST;
        this.PUERTO = item.PUERTO;
        this.ACTIVA = item.ACTIVA;
        this.PORT = item.PORT;
        this.CIUDAD = item.CIUDAD;
    }

    static getAgencia(result) {
        Connection.executeQueryWithoutParams(`SELECT p.* FROM "gen$agencia" p ORDER BY p.ID_AGENCIA ASC`, result);
    }
}

module.exports = Agencia;