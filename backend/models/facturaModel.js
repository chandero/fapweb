'user strict';

const util = require('util');
const df = require('dateformat');

var factory = require('./firebird.js');
var conn = factory.getConnection();

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

var FacturaItem = function (item) {
    this.FAIT_ID = item.FAIT_ID;
    this.FACT_NUMERO = item.FACT_NUMERO;
    this.FAIT_DETALLE = item.FAIT_DETALLE;
    this.FAIT_CANTIDAD = item.FAIT_CANTIDAD;
    this.FAIT_VALORUNITARIO = item.FAIT_VALORUNITARIO;
    this.FAIT_TASAIVA = item.FAIT_TASAIVA;
    this.FAIT_VALORIVA = item.FAIT_VALORIVA;
    this.FAIT_TOTAL = item.FAIT_TOTAL;
}

Factura.toArray = function (item) {
    return [this.FACT_NUMERO, this.FACT_FECHA, this.FACT_DESCRIPCION, this.TIPO_COMPROBANTE, this.ID_COMPROBANTE, this.FECHA_COMPROBANTE, this.ID_IDENTIFICACION, this.ID_PERSONA, this.ID_EMPLEADO, this.FACT_ESTADO];
}

Factura.getAll = function (result) {
    firebird.attach(options, function (err, sql) {
        sql.query("SELECT * FROM FACTURA ORDER BY CODIGO ASC", function (err, res) {
            if (err) {
                console.log("error: ", err);
                sql.detach();
                result(null, err);
            }
            else {
                console.log('puc : ', res);
                sql.detach();
                result(null, res);
            }
        });
    });
};

Factura.getById = function (id, result) {
    firebird.attach(options, function (err, sql) {
        sql.query("SELECT * FROM FACTURA WHERE FACT_NUMERO = ?", [id], function (err, res) {
            if (err) {
                sql.detach();
                result(null, err);
            }
            else {
                sql.detach();
                result(null, res);
            }
        });
    });
};

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
    sql = util.format(sql, startDate, endDate)
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
    firebird.attach(options, function (err, sql) {
        sql.query("SELECT * FROM FACTURA_ITEM WHERE FACT_NUMERO = ? ORDER BY CODIGO ASC", [fact_numero], function (err, res) {
            if (err) {
                console.log("error: ", err);
                sql.detach();
                result(null, err);
            }
            else {
                console.log('puc : ', res);
                sql.detach();
                result(null, res);
            }
        });
    });
}

module.exports = Factura;