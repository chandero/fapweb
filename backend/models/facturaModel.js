'user strict';

const util = require('util');
const df = require('dateformat');
var SqlString = require('sqlstring');

var factory = require('./firebird.js');
var conn = factory.getConnection();

var item = {};

//Factura object constructor
var Factura = function (item) {
    this.FACT_NUMERO = item.FACT_NUMERO;
    this.FACT_FECHA = item.FACT_FECHA;
    this.FACT_DESCRIPCION = item.FACT_DESCRIPCION;
    this.TIPO_COMPROBANTE = item.TIPO_COMPROBANTE;
    this.ID_COMPROBANTE = item.ID_COMPROBANTE;
    this.FECHA_COMPROBANTE = item.FECHA_COMPROBANTE;
    this.ID_IDENTIFICACION = item.ID_IDENTIFICACION;
    this.ID_PERSONA = item.ID_PERSONA;
    this.ID_EMPLEADO = item.ID_EMPLEADO;
    this.FACT_ESTADO = item.FACT_ESTADO;
    this.FACTURA_ITEMS = item.FACTURA_ITEMS;
}

var FacturaItem = function (i) {
    item.FAIT_ID = i.FAIT_ID;
    item.FACT_NUMERO = i.FACT_NUMERO;
    item.FAIT_DETALLE = i.FAIT_DETALLE;
    item.FAIT_CANTIDAD = i.FAIT_CANTIDAD;
    item.FAIT_VALORUNITARIO = i.FAIT_VALORUNITARIO;
    item.FAIT_TASAIVA = i.FAIT_TASAIVA;
    item.FAIT_VALORIVA = i.FAIT_VALORIVA;
    item.FAIT_TOTAL = i.FAIT_TOTAL;
}

Factura.toArray = function (item) {
    return [this.FACT_NUMERO, this.FACT_FECHA, this.FACT_DESCRIPCION, this.TIPO_COMPROBANTE, this.ID_COMPROBANTE, this.FECHA_COMPROBANTE, this.ID_IDENTIFICACION, this.ID_PERSONA, this.ID_EMPLEADO, this.FACT_ESTADO];
}

Factura.create = function (factura, result) {
    var sql = `INSERT INTO FACTURA (FACT_NUMERO, 
                                    FACT_FECHA, 
                                    FACT_DESCRIPCION, 
                                    TIPO_COMPROBANTE,
                                    ID_COMPROBANTE,
                                    FECHA_COMPROBANTE,
                                    ID_IDENTIFICACION,
                                    ID_PERSONA,
                                    ID_EMPLEADO,
                                    FACT_ESTADO,
                                    FACT_ESTADO_DESCRIPCION) VALUES (
                                        ?,
                                        ?,
                                        ?,
                                        ?,
                                        ?,
                                        ?,
                                        ?,
                                        ?,
                                        ?,
                                        ?,
                                        ?
                                    );`;
    sql = SqlString.format(sql, factura.toArray())
    if(!conn.inTransaction){
        conn.startNewTransactionSync();
    }
    conn.query(sql, (err, rs) => {
        if (err) {
            console.log("error: "+ err);
            conn.rollback();
            result(err, null);
        }
        if (rs) {
          var rows = rs.fetchSync('all', true);
          conn.commit();
          result(null, rows)
        }
    });
}

Factura.update = function (factura, result) {
    var sql = `UPDATE FACTURA SET   FACT_FECHA = '%s',
                                    FACT_DESCRIPCION = '%s',
                                    TIPO_COMPROBANTE = %i,
                                    ID_COMPROBANTE = %i,
                                    FECHA_COMPROBANTE = '%s',
                                    ID_IDENTIFICACION = %i,
                                    ID_PERSONA = '%s',
                                    ID_EMPLEADO = '%s',
                                    FACT_ESTADO = %i,
                                    FACT_ESTADO_DESCRIPCION = '%s'
                                    WHERE FACT_NUMERO = %i
                                    );`;
    sql = util.format(sql, factura.FACT_FECHA, factura.FACT_DESCRIPCION, factura.TIPO_COMPROBANTE, 
        factura.ID_COMPROBANTE, factura.FECHA_COMPROBANTE, factura.ID_IDENTIFICACION, 
        factura.ID_PERSONA, factura.ID_EMPLEADO, factura.FACT_ESTADO, factura.FACT_ESTADO_DESCRIPCION, factura.FACT_NUMERO);
    if(!conn.inTransaction){
        conn.startNewTransactionSync();
    }
    conn.query(sql, (err, rs) => {
        if (err) {
            console.log("error: "+ err);
            conn.rollback();
            result(err, null);
        }
        if (rs) {
          var rows = rs.fetchSync('all', true);
          conn.commit();
          result(null, rows)
        }
    });
}

Factura.remove = function (fact_numero, result) {
    var sql = `UPDATE FACTURA SET FACT_ESTADO = 9 WHERE FACT_NUMERO = %i`;
    sql = util.format(sql, fact_numero);
    if(!conn.inTransaction){
        conn.startNewTransactionSync();
    }
    conn.query(sql, (err, rs) => {
        if (err) {
            console.log("error: "+ err);
            conn.rollback();
            result(err, null);
        }
        if (rs) {
          var rows = rs.fetchSync('all', true);
          conn.commit();
          result(null, rows)
        }
    });    
}

Factura.getAll = function (fact_numero, result) {
    var sql = `SELECT * FROM FACTURA ORDER BY CODIGO ASC;`;
    if(!conn.inTransaction){
        conn.startNewTransactionSync();
    }
    conn.query(sql, (err, rs) => {
        if (err) {
            console.log("error: "+ err);
            conn.rollback();
            result(err, null);
        }
        if (rs) {
          var rows = rs.fetchSync('all', true);
          conn.commit();
          result(null, rows)
        }
    });    
}

Factura.getById = function (fact_numero, result) {
    var sql = `SELECT * FROM FACTURA WHERE FACT_NUMERO = %i;`;
    sql = util.format(sql, fact_numero);
    if(!conn.inTransaction){
        conn.startNewTransactionSync();
    }
    conn.query(sql, (err, rs) => {
        if (err) {
            console.log("error: "+ err);
            conn.rollback();
            result(err, null);
        }
        if (rs) {
          var rows = rs.fetchSync('all', true);
          conn.commit();
          result(null, rows)
        }
    });    
}

Factura.getNumberRange = function (start, end, result) {
    const startNumber = parseInt(start, 10);
    const endNumber = parseInt(end, 10);
    var sql = `SELECT f.FACT_NUMERO, f.FACT_FECHA, f.FACT_DESCRIPCION, f.ID_COMPROBANTE, p.NOMBRE, p.PRIMER_APELLIDO, p.SEGUNDO_APELLIDO 
    FROM "FACTURA" f 
    LEFT JOIN "gen$persona" p ON p.ID_IDENTIFICACION = f.ID_IDENTIFICACION AND p.ID_PERSONA = f.ID_PERSONA
    WHERE f.FACT_NUMERO BETWEEN %i AND %i ORDER BY f.FACT_NUMERO ASC;`;
    sql = util.format(sql, startNumber, endNumber);
    if(!conn.inTransaction){
        conn.startNewTransactionSync();
    }
    conn.query(sql, (err, rs) => {
        if (err) {
            console.log("error: "+ err);
            conn.rollback();
            result(err, null);
        }
        if (rs) {
          var rows = rs.fetchSync('all', true);
          conn.commit();
          result(null, rows)
        }
    });    
}

Factura.getDateRange = function (start, end, result) {
    const startDate = df(new Date(parseInt(start)), "dd.mm.yyyy HH:MM:ss");
    const endDate = df(new Date(parseInt(end)), "dd.mm.yyyy HH:MM:ss");
    var sql = `SELECT f.FACT_NUMERO, f.FACT_FECHA, f.FACT_DESCRIPCION, f.ID_COMPROBANTE, p.NOMBRE, p.PRIMER_APELLIDO, p.SEGUNDO_APELLIDO 
    FROM FACTURA f 
    LEFT JOIN "gen$persona" p ON p.ID_IDENTIFICACION = f.ID_IDENTIFICACION AND p.ID_PERSONA = f.ID_PERSONA
    WHERE f.FACT_FECHA BETWEEN '%s' AND '%s' ORDER BY f.FACT_NUMERO ASC;`;
    sql = util.format(sql, startDate, endDate);
    console.log("query: " + sql);
    if(!conn.inTransaction){
        conn.startNewTransactionSync();
    }
    conn.query(sql, (err, rs) => {
        if (err) {
            console.log("error: "+ err);
            conn.rollback();
            result(err, null);
        }
        if (rs) {
          var rows = rs.fetchSync('all', true);
          conn.commit();
          result(null, rows)
        }
    });    
}

Factura.getItems = function (fact_numero, result) {
    var sql = `SELECT * FROM FACTURA_ITEM WHERE FACT_NUMERO = %i ORDER BY CODIGO ASC;`;
    sql = util.format(sql, fact_numero);
    if(!conn.inTransaction){
        conn.startNewTransactionSync();
    }
    conn.query(sql, (err, rs) => {
        if (err) {
            console.log("error: "+ err);
            conn.rollback();
            result(err, null);
        }
        if (rs) {
          var rows = rs.fetchSync('all', true);
          conn.commit();
          result(null, rows)
        }
    });    
}

Factura.updateStatus = function(fact_numero, response, result) {
    console.log("actualizando estado");
    var sql = `UPDATE FACTURA SET FACT_ESTADO = %i, FACT_ESTADO_DESCRIPCION = '%s' WHERE FACT_NUMERO = %i`;
    var fact_estado = 0;
    if (response) {
        if (response.SetDocumentResult) {
            if (response.SetDocumentResult.DetalleRespuesta === 'Ok') {
                fact_estado = 1;
            } else {
                fact_estado = 2;
            }
        } else {
            fact_estado = 3;
        }
    }

    const fact_estado_descripcion = String(response);
    sql = util.format(sql, fact_estado, fact_estado_descripcion.replace(/'/gi, "''"), fact_numero);
    console.log("udate query: " + sql);
    if(!conn.inTransaction){
        conn.startNewTransactionSync();
    }
    conn.query(sql, (err, rs) => {
        if (err) {
            console.log("error: "+ err);
            conn.rollback();
            result(err, null);
        }
        if (rs) {
          var rows = rs.fetchSync('all', true);
          conn.commit();
          result(null, rows)
        }
    });
}

module.exports = Factura;