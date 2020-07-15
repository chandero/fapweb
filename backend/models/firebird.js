'user strict';


var fb = require("firebird");
const environment = require('../environments');
const PropertiesReader = require('properties-reader');


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

var Factory = {};

Factory.getConnection = function() {
    var conn = fb.createConnection();
    conn.connectSync(options.database,options.user,options.password, '')
    if(conn.connected) {
        console.log("Connected to database");
    }
    return conn;
}

module.exports = Factory;
/* con.connectSync(options.database,options.user,options.password,'');
con.querySync("insert into test (id,name) values (5, 'new one')");
var res = con.querySync("select * from test");
con.commitSync();
var rows = res.fetchSync("all",true);
console.log(sys.inspect(rows));
*/