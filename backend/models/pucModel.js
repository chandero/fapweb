'user strict';
var options = require('./connection.js');
var firebird = require('node-firebird');

//Puc object constructor
var Puc = function(item){
    this.CODIGO = item.CODIGO;
    this.ID_AGENCIA = item.ID_AGENCIA;
    this.CLAVE = item.CLAVE;
    this.NOMBRE = item.NOMBRE;
    this.TIPO = item.TIPO;
    this.CODIGO_MAYOR = item.CODIGO_MAYOR;
    this.MOVIMIENTO = item.MOVIMIENTO;
    this.ESBANCO = item.ESBANCO;
    this.INFORME = item.INFORME;
    this.NIVEL = item.NIVEL;
    this.NATURALEZA = item.NATURALEZA;
    this.CENTROCOSTO = item.CENTROCOSTO;
    this.SALDOINICIAL = item.SALDOINICIAL;
    this.ESCAPTACION = item.ESCAPTACION;
    this.ESCOLOCACION = item.ESCOLOCACION;
}

Puc.getAll = function (result) {
    firebird.attach(options, function(err, sql) {    
        sql.query("SELECT * FROM \"con$puc\" ORDER BY CODIGO ASC", function (err, res) {
                if(err) {
                    console.log("error: ", err);
                    sql.detach();
                    result(null, err);
                }
                else{
                  console.log('puc : ', res);  
                  sql.detach();
                  result(null, res);
                }
        });  
    }); 
};