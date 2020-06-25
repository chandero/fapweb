'user strict';
var options = require('./connection.js');
var firebird = require('node-firebird');

//Colocacion object constructor
var Tabla = function(item){
	this.id_agencia = item.id_agencia;
	this.id_colocacion = item.id_colocacion;
	this.id_identificacion = item.id_identificacion;
	this.id_persona = item.id_persona;
	this.id_clasificacion = item.id_clasificacion;
	this.id_linea = item.id_linea;
	this.id_inversion = item.id_inversion;
	this.id_respaldo = item.id_respaldo;
	this.id_garantia = item.id_garantia;
	this.id_categoria = item.id_categoria;
	this.id_evaluacion = item.id_evaluacion;
	this.fecha_desembolso = item.fecha_desembolso;
	this.valor_desembolso = item.valor_desembolso;
	this.plazo_colocacion = item.plazo_colocacion;
	this.fecha_vencimiento = item.fecha_vencimiento;
	this.tipo_interes = item.tipo_interes;
	this.id_interes = item.id_interes;
	this.tasa_interes_corriente = item.tasa_interes_corriente;
	this.tasa_interes_mora = item.tasa_interes_mora;
	this.puntos_interes = item.puntos_interes;
	this.id_tipo_cuota = item.id_tipo_cuota;
	this.amortiza_capital = item.amortiza_capital;
	this.amortiza_interes = item.amortiza_interes;
    this.periodo_gracia = item.periodo_gracia;
	this.dias_prorrogados = item.dias_prorrogados;
	this.valor_cuota = item.valor_cuota;
	this.abonos_capital = item.abonos_capital;
	this.fecha_capital = item.fecha_capital;
	this.fecha_interes = item.fecha_interes;
	this.id_estado_colocacion = item.id_estado_colocacion;
	this.id_ente_aprobador = item.id_ente_aprobador;
	this.id_empleado = item.id_empleado;
	this.nota_contable = item.nota_contable;
	this.numero_cuenta = item.numero_cuenta;
    this.es_anormal = item.es_anormal;
	this.dias_pago = item.dias_pago;
	this.reciprocidad = item.reciprocidad;
	this.fecha_saldado = item.fecha_saldado;
};

Tabla.create = function (newE, result) {
    firebird.attach(options, function(err, sql) {
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
                if(err) {
                    console.log("error: ", err);
                    sql.detach();
                    result(err, null);
                }
                else{
                    console.log(res);
                    sql.detach();
                    result(null, res);
                }
            }); 
        });          
};
Tabla.getById = function (id_colocacion, result) {
    console.log("estoy en getbyid");
    firebird.attach(Firebird.options, function(err, sql) {
        console.log("voy a ejecutar el query con el Id: " + Id);
        sql.query("SELECT * FROM \"col$colocacion\" WHERE ID_COLOCACION = ?", [id_colocacion], function (err, res) {             
                if(err) {
                    console.log("error: ", err);
                    sql.detach();
                    result(err, null);
                }
                else{
                    console.log("res:" + JSON.stringify(res));
                    sql.detach();
                    result(null, res);
                }
        });   
    });
};
Tabla.getAll = function (result) {
    firebird.attach(options, function(err, sql) {    
        sql.query("SELECT * FROM \"col$colocacion\" ", function (err, res) {
                if(err) {
                    console.log("error: ", err);
                    sql.detach();
                    result(null, err);
                }
                else{
                  console.log('elementos : ', res);  
                  sql.detach();
                  result(null, res);
                }
        });  
    }); 
};
Tabla.updateById = function(id, e, result){
    firebird.attach(options, function(err, sql) {    
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
            if(err) {
              console.log("error: ", err);
              sql.detach();
              result(null, err);
            }
            else{
              console.log('res : ', res);
              sql.detach(); 
              result(null, res);
            }
        }); 
    });
};
Tabla.remove = function(id, result){
    firebird.attach(options, function(err, sql) {    
        sql.query("DELETE FROM \"col$colocacion\" WHERE ID_COLOCACION = ?", [id], function (err, res) {
            if(err) {
                console.log("error: ", err);
                result(null, err);
            }
            else {
                result(null, res);
            }
        }); 
    });
};

Tabla.getPlan = function(id, result) {
    firebird.attach(options, function(err,sql) {
        sql.query('SELECT * FROM "col$tablaliquidacion" t WHERE t.ID_COLOCACION = ?', id,function (err, res) {
            if(err) {
                console.log("error: ", err);
                sql.detach();
                result(null, err);
            }
            else {
                sql.detach();
                result(null, res);
            }
        });
    });
}

Tabla.getListByDocument = function(id_identificacion, id_persona, result) {
    firebird.attach(options, function(err,sql) {    
        sql.query(`SELECT a.ID_AGENCIA, a.ID_COLOCACION, a.ID_IDENTIFICACION, a.ID_PERSONA, 
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
                    WHERE a.ID_IDENTIFICACION = ? AND a.ID_PERSONA = ?`, [id_identificacion, id_persona], function(err, res) {
            if(err) {
                console.log("error: ", err);
                sql.detach();
                result(null, err);
            }
            else {
                console.log("estoy aqui 2");
                sql.detach();
                result(null, res);
            }
        });
    });
}

Tabla.getListById = async function(id, result) {
    firebird.attach(options, function(err,sql) {
        console.log("estoy aqui 0");
        sql.query('SELECT ID_IDENTIFICACION, ID_PERSONA FROM "col$colocacion" t WHERE t.ID_COLOCACION = ?', [id], function(err, res) {
            if(err) {
                console.log("error: ", err);
                sql.detach();
                result(null, err);
            }
            else {
                console.log("estoy aqui 1: " + JSON.stringify(res));
                sql.query('SELECT * FROM "col$colocacion" c WHERE c.ID_IDENTIFICACION = ? AND c.ID_PERSONA = ?', [res[0].ID_IDENTIFICACION, res[0].ID_PERSONA], function(err, res) {
                    if(err) {
                        console.log("error: ", err);
                        sql.detach();
                        result(null, err);
                    }
                    else {
                        console.log("estoy aqui 2");
                        sql.detach();
                        result(null, res);
                    }
                });
            }
        });
    });
}


module.exports= Tabla;