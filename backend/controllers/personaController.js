'use strict';

var Persona = require('../models/personaModel');

exports.obtenerTipoDocumento = function(req, res) {
    Persona.obtenerTipoDocumento(function(err, items) {
        if (err)
            res.status(400).send(err);
        res.json(items);
    });
};

exports.obtenerPorDocumento = function(req, res) {
    Persona.obtenerPorDocumento(req.params.tp, req.params.id, function(err, items) {
        if (err)
            res.status(400).send(err);
        res.json(items);
    });
};

exports.obtenerAdicionalPorDocumento = function(req, res) {
    Persona.obtenerAdicionalPorDocumento(req.params.tp, req.params.id, function(err, items) {
        if (err)
          res.status(400).send(err);
        res.json(items);
    })
}

exports.obtenerExtraPorDocumento = function(req, res) {
    Persona.obtenerExtraPorDocumento(req.params.tp, req.params.id, function(err, items) {
        if (err)
          res.status(400).send(err);
        res.json(items);
    })
}

exports.obtenerNombrePorDocumento = function(req, res) {
    Persona.obtenerNombrePorDocumento(req.params.tp, req.params.id, function(err, items) {
        if (err)
            res.status(400).send(err);
        res.json(items);
    });
};

exports.obtenerPorNombre = function(req, res) {
    var options = { name: req.body.name, firstname: req.body.firstname, lastname: req.body.lastname };
    Persona.obtenerPorNombre(options, function(err, items) {
        if (err)
            res.status(400).send(err);
        res.json(items);
    });
};