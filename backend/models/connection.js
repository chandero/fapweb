'user strict';

var environment = require('../environments');
var PropertiesReader = require('properties-reader');
var properties = new PropertiesReader(environment);

var firebird = require('node-firebird');

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

var Firebird = { firebird, options}
module.exports = Firebird;