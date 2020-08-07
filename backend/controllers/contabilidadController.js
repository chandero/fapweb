"use strict";

var parsefilter = require("../utils/parsefilter");

var Puc = require("../models/pucModel");
var Comprobante = require("../models/comprobanteModel");
var Auxiliar = require("../models/auxiliarModel");
var TipoComprobante = require("../models/tipoComprobanteModel");
var Agencia = require("../models/agenciaModel");
var TipoOperacion = require("../models/tipoOperacionModel");
var InformeContable = require("../models/informeContableModel");

exports.getTypes = function (req, res) {
  TipoComprobante.getAll(function (err, items) {
    if (err) res.status(400).send(err);
    res.json(items);
  });
};

exports.getPucAll = function (req, res) {
  Puc.getAll(function (err, items) {
    if (err) res.status(400).send(err);
    res.json(items);
  });
};

exports.getPucById = function (req, res) {
  Puc.getById(req.params.id, function (err, items) {
    if (err) res.status(400).send(err);
    res.json(items);
  });
};

exports.getCentro = function (req, res) {
  Agencia.getAgencia(function (err, items) {
    if (err) res.status(400).send(err);
    res.json(items);
  });
};

exports.getTipoOperacion = function (req, res) {
  TipoOperacion.getAgencia(function (err, items) {
    if (err) res.status(400).send(err);
    res.json(items);
  });
};

exports.getInformeContable = function (req, res) {
  InformeContable.getAll(function (err, items) {
    if (err) res.status(400).send(err);
    console.log("informes: " + JSON.stringify(items));
    res.json(items);
  });
};

exports.getIsValid2Mov = function (req, res) {
  Puc.getById(req.params.id, function (err, items) {
    if (err) {
      res.status(400).send(err);
    } else {
      if (items.length > 0) {
        const p = items[0];
        var esMovimiento;
        if (p.MOVIMIENTO === 0) {
          esMovimiento = false;
        } else {
          esMovimiento = true;
        }
        res.json({ cuenta: p, movimiento: esMovimiento });
      } else {
        res.status(400).send('No existe');
      }
    }
  });
};

exports.readComp = function (req, res) {
  Comprobante.getById(req.params.tp, req.params.id, function (err, items) {
    if (err) res.status(400).send(err);
    res.json(items);
  });
};

exports.readAux = function (req, res) {
  Auxiliar.getById(req.params.tp, req.params.id, function (err, items) {
    if (err) res.status(400).send(err);
    res.json(items);
  });
};

exports.list_all = function (req, res) {
  Comprobante.getAll(function (err, items) {
    if (err) res.status(400).send(err);
    res.json(items);
  });
};

exports.list_all_page = function (req, res) {
  const current_page = req.body.current_page;
  const page_size = req.body.page_size;
  const order_by = req.body.order_by;
  var filter = parsefilter(req.body.filter);
  if (filter === "()") {
    filter = null;
  }
  console.log("filtro: " + filter);
  Comprobante.getAllPage(current_page, page_size, order_by, filter, function (
    err,
    items
  ) {
    if (err) res.status(400).send(err);
    res.json(items);
  });
};

exports.create = function (req, res) {
  var new_item = new Comprobante(req.body);

  //handles null error
  if (!new_item) {
    res.status(400).send({ error: true, message: "Por favor provea un item" });
  } else {
    Comprobante.create(function (err, item) {
      if (err) res.status(400).send(err);
      res.json(items);
    });
  }
};
