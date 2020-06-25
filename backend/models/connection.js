'user strict';

var environment = require('../environments');
var PropertiesReader = require('properties-reader');
var properties = new PropertiesReader(environment);

var options = {};
 
options.host = properties.get('database.firebird.host');
options.port = properties.get('database.firebird.port');
options.database = properties.get('database.firebird.path');
options.user = properties.get('database.firebird.username');
options.password = properties.get('database.firebird.password');
options.lowercase_keys = properties.get('database.firebird.lowercase_keys'); // set to true to lowercase keys
options.role = properties.get('database.firebird.role'); // default
options.pageSize = properties.get('database.firebird.pageSize'); // default when creating database

console.log('options: ' + JSON.stringify(options))

var Firebird = { options};

Firebird.select = function(query, params, result) {
    firebird.attach(options, function(err, sql) {
        if (err)
            throw err;  
        
        if (params) {
            console.log("params:" + params);
            sql.query(query, params, function (err, res) {
                if(err) {
                    console.log("error: ", err);
                    result(err, null);
                    sql.detach();
                }
                else{
                    console.log("result1:" + JSON.stringify(res));
                    result(null, res);
                    sql.detach();
                }
            });
        } else {
            sql.query(query, [], function (err, res) {
                if(err) {
                    console.log("error: ", err);
                    result(err, null);
                    sql.detach();
                }
                else{
                    console.log("result2:" + JSON.stringify(res));
                    result(null, res);
                    sql.detach();
                }
            });            
        }
    });
}

Firebird.select2 = async function(query, params) {
    var result = { items: null, err: null};
    firebird.attach(options, function(err, sql) {
        if (err)
            throw err;  
        
        if (params) {
            console.log("params:" + params);
            sql.query(query, params, function (err, res) {
                if(err) {
                    console.log("error: ", err);
                    result.items = null;
                    result.err = err;
                    sql.detach();
                    return result;
                }
                else{
                    console.log("result1:" + JSON.stringify(res));
                    result.items = res;
                    result.err = null;
                    sql.detach();
                    return result;
                }
            });
        } else {
            sql.query(query, [], function (err, res) {
                if(err) {
                    console.log("error: ", err);
                    result.items = null;
                    result.err = err;
                    sql.detach();
                    return result;
                }
                else {
                    console.log("result2:" + JSON.stringify(res));
                    result.items = res;
                    result.err = null;
                    sql.detach();
                    return result;
                }
            });            
        }
    });
}

module.exports = options;