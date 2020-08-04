'use strict';

var Firebird = require('node-firebird/lib/index.js');
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

class Connection {

    static getConnection = function (callback) {
        var conn = new Firebird.Database(options.host, options.port, options.database, options.user, options.password, callback(conn));
    };

    static executeQueryWithParams = function (query, params, response) {
        Firebird.attach(options, function (err, db) {

            if (err) {
                response(err, null);
            }

            // db = DATABASE
            db.transaction(Firebird.ISOLATION_READ_COMMITED, function (err, transaction) {
                transaction.query(query, params, function (err, rs) {

                    if (err) {
                        transaction.rollback();
                        response(err, null);
                    }
                    transaction.commit(function (err) {
                        if (err) {
                            transaction.rollback();
                            response(err, null);
                        }
                        else {
                            db.detach();
                        }
                    });
                    response(null, rs);
                });
            });
        });
    }

    static executeQueryWithoutParams = function (query, response) {
        Firebird.attach(options, function (err, db) {

            if (err) {
                response(err, null);
            }

            // db = DATABASE
            db.transaction(Firebird.ISOLATION_READ_COMMITED, function (err, transaction) {
                if (err)
                   response(err, null);

                transaction.query(query, function (err, rs) {
                    if (err) {
                        transaction.rollback();
                        response(err, null);
                    }
                    transaction.commit(function (err) {
                        if (err) {
                            transaction.rollback();
                            response(err, null);
                        }
                        else {
                            db.detach();
                            response(null, rs);
                        }
                    });
                });
            });
        });
    }    
}


module.exports = Connection;