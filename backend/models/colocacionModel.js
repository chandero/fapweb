'user strict';

const util = require('util');
const df = require('dateformat');

var factory = require('./firebird.js');
var conn = factory.getConnection();

//Colocacion object constructor
var Colocacion = function (item) {
    this.ID_AGENCIA = item.ID_AGENCIA;
    this.ID_COLOCACION = item.ID_COLOCACION;
    this.ID_IDENTIFICACION = item.ID_IDENTIFICACION;
    this.ID_PERSONA = item.ID_PERSONA;
    this.ID_CLASIFICACION = item.ID_CLASIFICACION;
    this.ID_LINEA = item.ID_LINEA;
    this.ID_INVERSION = item.ID_INVERSION;
    this.ID_RESPALDO = item.ID_RESPALDO;
    this.ID_GARANTIA = item.ID_GARANTIA;
    this.ID_CATEGORIA = item.ID_CATEGORIA;
    this.ID_EVALUACION = item.ID_EVALUACION;
    this.FECHA_DESEMBOLSO = item.FECHA_DESEMBOLSO;
    this.VALOR_DESEMBOLSO = item.VALOR_DESEMBOLSO;
    this.PLAZO_COLOCACION = item.PLAZO_COLOCACION;
    this.FECHA_VENCIMIENTO = item.FECHA_VENCIMIENTO;
    this.TIPO_INTERES = item.TIPO_INTERES;
    this.ID_INTERES = item.ID_INTERES;
    this.TASA_INTERES_CORRIENTE = item.TASA_INTERES_CORRIENTE;
    this.TASA_INTERES_MORA = item.TASA_INTERES_MORA;
    this.PUNTOS_INTERES = item.PUNTOS_INTERES;
    this.ID_TIPO_CUOTA = item.ID_TIPO_CUOTA;
    this.AMORTIZA_CAPITAL = item.AMORTIZA_CAPITAL;
    this.AMORTIZA_INTERES = item.AMORTIZA_INTERES;
    this.PERIODO_GRACIA = item.PERIODO_GRACIA;
    this.DIAS_PRORROGADOS = item.DIAS_PRORROGADOS;
    this.VALOR_CUOTA = item.VALOR_CUOTA;
    this.ABONOS_CAPITAL = item.ABONOS_CAPITAL;
    this.FECHA_CAPITAL = item.FECHA_CAPITAL;
    this.FECHA_INTERES = item.FECHA_INTERES;
    this.ID_ESTADO_COLOCACION = item.ID_ESTADO_COLOCACION;
    this.ID_ENTE_APROBADOR = item.ID_ENTE_APROBADOR;
    this.ID_EMPLEADO = item.ID_EMPLEADO;
    this.NOTA_CONTABLE = item.NOTA_CONTABLE;
    this.NUMERO_CUENTA = item.NUMERO_CUENTA;
    this.ES_ANORMAL = item.ES_ANORMAL;
    this.DIAS_PAGO = item.DIAS_PAGO;
    this.RECIPROCIDAD = item.RECIPROCIDAD;
    this.FECHA_SALDADO = item.FECHA_SALDADO;
};

Colocacion.create = function (newE, result) {
    firebird.attach(options, function (err, sql) {
        if (err)
            throw err;
        sql.query(`INSERT INTO \"col$colocacion\"
            (   ID_AGENCIA, 
                ID_COLOCACION, 
                ID_IDENTIFICACION, 
                ID_PERSONA, 
                ID_CLASIFICACION, 
                ID_LINEA, 
                ID_INVERSION, 
                ID_RESPALDO, 
                ID_GARANTIA, 
                ID_CATEGORIA, 
                ID_EVALUACION, 
                FECHA_DESEMBOLSO, 
                VALOR_DESEMBOLSO, 
                PLAZO_COLOCACION, 
                FECHA_VENCIMIENTO, 
                TIPO_INTERES, 
                ID_INTERES, 
                TASA_INTERES_CORRIENTE, 
                TASA_INTERES_MORA, 
                PUNTOS_INTERES, 
                ID_TIPO_CUOTA, 
                AMORTIZA_CAPITAL, 
                AMORTIZA_INTERES, 
                PERIODO_GRACIA, 
                DIAS_PRORROGADOS, 
                VALOR_CUOTA, 
                ABONOS_CAPITAL, 
                FECHA_CAPITAL, 
                FECHA_INTERES, 
                ID_ESTADO_COLOCACION, 
                ID_ENTE_APROBADOR, 
                ID_EMPLEADO, 
                NOTA_CONTABLE, 
                NUMERO_CUENTA, 
                ES_ANORMAL, 
                DIAS_PAGO, 
                RECIPROCIDAD, 
                FECHA_SALDADO)
            VALUES( ?, ?, ?, ?, ?, ?, ?, ?, 
                    ?, ?, ?, ?, ?, ?, ?, ?, 
                    ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?)`, newE, function (err, res) {
            if (err) {
                console.log("error: ", err);
                sql.detach();
                result(err, null);
            }
            else {
                console.log(res);
                sql.detach();
                result(null, res);
            }
        });
    });
};
Colocacion.getById = function (id_colocacion, result) {
    console.log("estoy en getbyid");
    firebird.attach(Firebird.options, function (err, sql) {
        console.log("voy a ejecutar el query con el Id: " + Id);
        sql.query("SELECT * FROM \"col$colocacion\" WHERE ID_COLOCACION = ?", [id_colocacion], function (err, res) {
            if (err) {
                console.log("error: ", err);
                sql.detach();
                result(err, null);
            }
            else {
                console.log("res:" + JSON.stringify(res));
                sql.detach();
                result(null, res);
            }
        });
    });
};
Colocacion.getAll = function (result) {
    firebird.attach(options, function (err, sql) {
        sql.query("SELECT * FROM \"col$colocacion\" ", function (err, res) {
            if (err) {
                console.log("error: ", err);
                sql.detach();
                result(null, err);
            }
            else {
                console.log('elementos : ', res);
                sql.detach();
                result(null, res);
            }
        });
    });
};
Colocacion.updateById = function (id, e, result) {
    firebird.attach(options, function (err, sql) {
        sql.query(`UPDATE \"col$colocacion\"
             SET ID_AGENCIA = ?, ID_IDENTIFICACION=?, ID_PERSONA=?, ID_CLASIFICACION=?, ID_LINEA=?, 
             ID_INVERSION=?, ID_RESPALDO=?, ID_GARANTIA=?, ID_CATEGORIA=?, ID_EVALUACION=?, 
             FECHA_DESEMBOLSO=?, VALOR_DESEMBOLSO=?, PLAZO_COLOCACION=?, FECHA_VENCIMIENTO=?,
             TIPO_INTERES=?, ID_INTERES=?, TASA_INTERES_CORRIENTE=?, TASA_INTERES_MORA=?,
             PUNTOS_INTERES=?, ID_TIPO_CUOTA=?, AMORTIZA_CAPITAL=?, AMORTIZA_INTERES=?, 
             PERIODO_GRACIA=?, DIAS_PRORROGADOS=?, VALOR_CUOTA=?, ABONOS_CAPITAL=?,
             FECHA_CAPITAL=?, FECHA_INTERES=?, ID_ESTADO_COLOCACION=?, ID_ENTE_APROBADOR=?,
             ID_EMPLEADO=?, NOTA_CONTABLE=?, NUMERO_CUENTA=?, ES_ANORMAL=?, DIAS_PAGO=?,
             RECIPROCIDAD=?, FECHA_SALDADO=? WHERE ID_COLOCACION=?`, [e, id], function (err, res) {
            if (err) {
                console.log("error: ", err);
                sql.detach();
                result(null, err);
            }
            else {
                console.log('res : ', res);
                sql.detach();
                result(null, res);
            }
        });
    });
};
Colocacion.remove = function (id, result) {
    firebird.attach(options, function (err, sql) {
        sql.query("DELETE FROM \"col$colocacion\" WHERE ID_COLOCACION = ?", [id], function (err, res) {
            if (err) {
                console.log("error: ", err);
                result(null, err);
            }
            else {
                result(null, res);
            }
        });
    });
};

Colocacion.getPlan = function (id, result) {
    var sql = `SELECT * FROM "col$tablaliquidacion" t WHERE t.ID_COLOCACION = '%s';`;
    sql = util.format(sql, id);
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

Colocacion.getListByDocument = function (id_identificacion, id_persona, result) {
    var sql = `SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, 
    p.NOMBRE || ' ' || p.PRIMER_APELLIDO || ' ' || p.SEGUNDO_APELLIDO AS NOMBRE,
    a.ID_CLASIFICACION, a.ID_LINEA, a.ID_INVERSION, a.ID_RESPALDO, a.ID_GARANTIA, 
    a.ID_CATEGORIA, a.ID_EVALUACION, a.FECHA_DESEMBOLSO, a.VALOR_DESEMBOLSO, 
    a.PLAZO_COLOCACION, a.FECHA_VENCIMIENTO, a.TIPO_INTERES, a.ID_INTERES, 
    a.TASA_INTERES_CORRIENTE, a.TASA_INTERES_MORA, a.PUNTOS_INTERES, a.ID_TIPO_CUOTA, 
    a.AMORTIZA_CAPITAL, a.AMORTIZA_INTERES, a.PERIODO_GRACIA, a.DIAS_PRORROGADOS, 
    a.VALOR_CUOTA, a.ABONOS_CAPITAL, a.FECHA_CAPITAL, a.FECHA_INTERES, a.ID_ESTADO_COLOCACION,
    a.ID_ENTE_APROBADOR, a.ID_EMPLEADO, a.NOTA_CONTABLE, a.NUMERO_CUENTA, a.ES_ANORMAL, 
    a.DIAS_PAGO, a.RECIPROCIDAD, a.FECHA_SALDADO, e.DESCRIPCION_ESTADO_COLOCACION AS ESTADO, \'D\' AS tipo FROM "col$colocacion" a
    LEFT JOIN "gen$persona" p ON 
      p.ID_IDENTIFICACION = a.ID_IDENTIFICACION AND p.ID_PERSONA = a.ID_PERSONA
    LEFT JOIN "col$estado" e ON 
      e.ID_ESTADO_COLOCACION = a.ID_ESTADO_COLOCACION
    WHERE a.ID_IDENTIFICACION = %i AND a.ID_PERSONA = '%s';`;
    sql = util.format(sql, id_identificacion, id_persona);
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

Colocacion.getListById = async function (id, result) {
    var sql = `SELECT ID_IDENTIFICACION, ID_PERSONA FROM "col$colocacion" t WHERE t.ID_COLOCACION = '%s';`;
    sql = util.format(sql, id);
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
            var iden = rs.fetchSync(1, true);
            console.log(iden);
            sql = `SELECT * FROM "col$colocacion" c WHERE c.ID_IDENTIFICACION = %i AND c.ID_PERSONA = '%s'`;
            sql = util.format(sql, iden[0].ID_IDENTIFICACION, iden[0].ID_PERSONA);
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
    });
}


module.exports = Colocacion;