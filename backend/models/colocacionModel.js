'user strict';
var Firebird = require('./connection.js');

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
    Firebird.firebird.attach(Firebird.options, function(err, sql) {
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
                    result(err, null);
                }
                else{
                    console.log(res);
                    result(null, res);
                }
            }); 
        });          
};
Tabla.getById = function (id_colocacion, result) {
    console.log("estoy en getbyid");
    Firebird.firebird.attach(Firebird.options, function(err, sql) {
        console.log("voy a ejecutar el query con el Id: " + Id);
        sql.query("SELECT * FROM \"col$colocacion\" WHERE ID_COLOCACION = ?", [id_colocacion], function (err, res) {             
                if(err) {
                    console.log("error: ", err);
                    result(err, null);
                }
                else{
                    console.log("res:" + JSON.stringify(res));
                    result(null, res);
                }
                Firebird.firebird.detach();
        });   
    });
};
Tabla.getAll = function (result) {
    Firebird.attach(options, function(err, sql) {    
        sql.query("SELECT * FROM \"col$colocacion\" ", function (err, res) {

                if(err) {
                    console.log("error: ", err);
                    result(null, err);
                }
                else{
                  console.log('elementos : ', res);  

                 result(null, res);
                }
        });  
    }); 
};
Tabla.updateById = function(id, e, result){
    Firebird.attach(options, function(err, sql) {    
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
                result(null, err);
            }
            else{   
             result(null, res);
                }
        }); 
    });
};
Tabla.remove = function(id, result){
    Firebird.attach(options, function(err, sql) {    
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

module.exports= Tabla;