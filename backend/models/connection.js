'user strict';

var fb = require('node-firebird/lib/index.js');
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

var Factory = {};

Factory.getConnection = function(callback) {
    var conn = new fb.Database(options.host, options.port, options.database, options.user, options.password, callback(conn));
};

module.exports = Factory;