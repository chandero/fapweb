'use strict';

var Comprobante = require('../models/comprobanteModel');
var Auxiliar = require('../models/auxiliarModel');
var TipoComprobante = require('../models/tipoComprobanteModel');

exports.getTypes = function(req, res) {
    TipoComprobante.getAll(function(err, items) {
        if (err)
            res.status(400).send(err);
        res.json(items);
    });
};

exports.readComp = function (req, res) {
    Comprobante.getById(req.params.tp, req.params.id, function (err, items) {
        if (err)
          res.status(400).send(err)
        res.json(items)
    })
}

exports.readAux = function (req, res) {
    Auxiliar.getById(req.params.tp, req.params.id, function (err, items) {
        if (err)
          res.status(400).send(err)
        res.json(items)
    })
}

exports.list_all = function (req, res) {
    Comprobante.getAll(function (err, items) {
        if (err)
            res.status(400).send(err);
        res.json(items);
    });
}

exports.list_all_page = function (req, res) {
    Comprobante.getAllPage(req.params.current_page, req.params.page_size, function (err, items) {
        if (err)
            res.status(400).send(err);
        res.json(items);        
    });
}

exports.create = function (req, res) {
    var new_item = new Comprobante(req.body);

    //handles null error 
    if (!new_item) {
        res.status(400).send({ error: true, message: 'Por favor provea un item' });
    }
    else {
        Comprobante.create(function (err, item) {
            if (err)
                res.status(400).send(err);
            res.json(items);
        });
    }
};

