'user strict';

const util = require('util');
const df = require('dateformat');
var SqlString = require('sqlstring');

var factory = require('./firebird.js');
var conn = factory.getConnection();

var item = {};

var Auxiliar = function (item) {
    this.ID = item.ID;
    this.ID_COMPROBANTE = item.ID_COMPROBANTE;
    this.ID_AGENCIA = item.ID_AGENCIA;
    this.FECHA = item.FECHA;
    this.CODIGO = item.CODIGO;
    this.DEBITO = item.DEBITO;
    this.CREDITO = item.CREDITO;
    this.ID_CUENTA = item.ID_CUENTA;
    this.ID_COLOCACION = item.ID_COLOCACION;
    this.ID_IDENTIFICACION = item.ID_IDENTIFICACION;
    this.ID_PERSONA = item.ID_PERSONA;
    this.MONTO_RETENCION = item.MONTO_RETENCION;
    this.TASA_RETENCION = item.TASA_RETENCION;
    this.ESTADOAUX = item.ESTADOAUX;
    this.TIPO_COMPROBANTE = item.TIPO_COMPROBANTE;
    this.ID_CLASE_OPERACION = item.ID_CLASE_OPERACION;
    this.EXT = item.EXT
}

Auxiliar.toArray = function () {
    return [
        this.ID_AGENCIA,
        this.FECHA,
        this.CODIGO,
        this.DEBITO,
        this.CREDITO,
        this.ID_CUENTA,
        this.ID_COLOCACION,
        this.ID_IDENTIFICACION,
        this.ID_PERSONA,
        this.MONTO_RETENCION,
        this.TASA_RETENCION,
        this.ESTADOAUX,
        this.ID_CLASE_OPERACION,
        this.TIPO_COMPROBANTE,        
        this.ID_COMPROBANTE,
        this.ID
    ];
}

Auxiliar.getAll = function (result) {
    var sql =`SELECT * FROM "con$auxiliar" ORDER BY TIPO_COMPROBANTE, ID_COMPROBANTE`;
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

Auxiliar.getById = function (tp, id, result) {
    console.log('en Auxiliar get By Id');
    var sql = SqlString.format(`
                        SELECT 
                         a.ID_COMPROBANTE, 
                         a.ID_AGENCIA, 
                         CAST(a.FECHA AS VARCHAR(10)) AS FECHA, 
                         a.CODIGO, 
                         a.DEBITO, 
                         a.CREDITO, 
                         a.ID_CUENTA, 
                         a.ID_COLOCACION, 
                         a.ID_IDENTIFICACION, 
                         a.ID_PERSONA, 
                         a.MONTO_RETENCION, 
                         a.TASA_RETENCION, 
                         a.ESTADOAUX, 
                         a.TIPO_COMPROBANTE, 
                         a.ID, 
                         a.ID_CLASE_OPERACION,
                         e.ID, 
                         e.DETALLE,
                         e.CHEQUE,
                         e.ID_COMPROBANTE,
                         e.TIPO_COMPROBANTE,
                         e.ID_AGENCIA
                        FROM "con$auxiliar" a
                        LEFT JOIN "con$auxiliarext" e ON e.ID = a.ID
                        WHERE a.TIPO_COMPROBANTE = ? and a.ID_COMPROBANTE = ?`, [parseInt(tp,10), parseInt(id, 10)]);
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

Auxiliar.getByIdSync = function (cxion, tp, id) {
    console.log('en Auxiliar get By Id');
    var sql = SqlString.format(`
                        SELECT 
                         a.ID_COMPROBANTE, 
                         a.ID_AGENCIA, 
                         CAST(a.FECHA AS VARCHAR(10)) AS FECHA, 
                         a.CODIGO, 
                         a.DEBITO, 
                         a.CREDITO, 
                         a.ID_CUENTA, 
                         a.ID_COLOCACION, 
                         a.ID_IDENTIFICACION, 
                         a.ID_PERSONA, 
                         a.MONTO_RETENCION, 
                         a.TASA_RETENCION, 
                         a.ESTADOAUX, 
                         a.TIPO_COMPROBANTE, 
                         a.ID, 
                         a.ID_CLASE_OPERACION
                        FROM "con$auxiliar" a
                        WHERE a.TIPO_COMPROBANTE = ? and a.ID_COMPROBANTE = ?`, [tp, id]);
    if (!cxion.inTransaction) {
        conn.startNewTransactionSync();
    }
    var Auxiliar = cxion.querySync(sql);
    var AuxExt = AuxiliarExt.getByIdSync(cxion, id);
    Auxiliar.EXT = AuxExt;
    return Auxiliar;
};

Auxiliar.create = function (result) {
    var sql = SqlString.format(`INSERT INTO "con$auxiliar" (
        ID_AGENCIA,
        FECHA,
        CODIGO,
        DEBITO,
        CREDITO,
        ID_CUENTA,
        ID_COLOCACION,
        ID_IDENTIFICACION,
        ID_PERSONA,
        MONTO_RETENCION,
        TASA_RETENCION,
        ESTADOAUX,
        ID_CLASE_OPERACION,
        TIPO_COMPROBANTE,        
        ID_COMPROBANTE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)`, this.toArray());
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
            var AuxiliarExt = new Auxiliar(this.EXT);
            AuxiliarExt.create();
            result(null, rows)
        }
    });    
}

Auxiliar.createSync = function (conn) {
    var sql = SqlString.format(`INSERT INTO "con$auxiliar" (
        ID_AGENCIA,
        FECHA,
        CODIGO,
        DEBITO,
        CREDITO,
        ID_CUENTA,
        ID_COLOCACION,
        ID_IDENTIFICACION,
        ID_PERSONA,
        MONTO_RETENCION,
        TASA_RETENCION,
        ESTADOAUX,
        ID_CLASE_OPERACION,
        TIPO_COMPROBANTE,        
        ID_COMPROBANTE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING ID`, this.toArray());
    try{
        var id = conn.querySync(sql);
        this.EXT.ID = id;
        var AuxiliarExt = new Auxiliar(this.EXT);
        var result = AuxiliarExt.create();
        return result;
    } catch (error) {
        return false;
    }
}

Auxiliar.update = function (result) {
    var sql = SqlString.format(`UPDATE "con$auxiliar" SET 
                        ID_AGENCIA = ?,
                        FECHA = ?,
                        CODIGO = ?,
                        DEBITO = ?,
                        CREDITO = ?,
                        ID_CUENTA = ?,
                        ID_COLOCACION = ?,
                        ID_IDENTIFICACION = ?,
                        ID_PERSONA = ?,
                        MONTO_RETENCION = ?,
                        TASA_RETENCION = ?,
                        ESTADOAUX = ?,
                        ID_CLASE_OPERACION= ?,
                        TIPO_COMPROBANTE = ?,
                        ID_COMPROBANTE = ?
                        WHERE 
                        ID = ?`, this.toArray());
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
            var AuxiliarExt = new Auxiliar(this.EXT);
            AuxiliarExt.update();            
            conn.commit();
            result(null, rows)
        }
    });     
}

Auxiliar.remove = function (result) {
    var sql = SqlString.format(`DELETE FROM "con$auxiliar" 
                                WHERE 
                                ID = ?`, this.ID);
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

Auxiliar.nullify = function (tipo, numero, texto, result) {
    var sql = SqlString.format(`UPDATE "con$comprobante" SET 
                                 ESTADO = 'N',
                                 ANULACION = ?,
                                WHERE 
                                 TIPO_COMPROBANTE = ?,
                                 ID_COMPROBANTE = ?
                               `, [texto, tipo, numero]);
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

module.exports = Auxiliar;