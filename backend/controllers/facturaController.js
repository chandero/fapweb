'use strict';

const axios = require('axios');
var Model = require('../models/facturaModel');

const environment = require('../environments');
const PropertiesReader = require('properties-reader');

var properties = new PropertiesReader(environment);

exports.list_all = function(req, res) {
    Model.getAll(function(err, items) {
        if (err)
            res.send(err);
        res.send(items);
    });
};

exports.create = function(req, res) {
  var nueva_factura = new Factura(req.body);

  //handles null error 
    if(!nueva_factura){
            res.status(400).send({ error:true, message: 'Por favor provea una nueva factura' });
    }
    else{  
        Model.create(nueva_factura, function(err, item) {    
            if (err)
                res.send(err);
            res.json(item);
        });
    }
};


exports.read = function(req, res) {
  Model.getById(req.params.id, function(err, item) {
    if (err)
      res.send(err);
    res.json(item);
  });
};

exports.readNumberRange = function(req, res) {
    Model.getNumberRange(req.params.start, req.params.end, function(err, item) {
      if (err)
        res.send(err);
      res.json(item);
    });
  };

exports.readDateRange = function(req, res) {
    Model.getDateRange(req.params.start, req.params.end, function(err, item) {
      if (err)
        res.send(err);
      res.json(item);
    });
};  

exports.readItems = function(req, res) {
    Model.getItems(req.params.id, function(err, item) {
      if (err)
        res.send(err);
      res.json(item);
    });
};

exports.update = function(req, res) {
  Model.update(new Model(req.body), function(err, item) {
    if (err)
      res.send(err);
    res.json(item);
  });
};

exports.delete = function(req, res) {
  Model.remove( req.params.id_colocacion, function(err, item) {
    if (err)
      res.send(err);
    res.json({ message: 'Item eliminado con exito!' });
  });
};

exports.sendToProvider = function(req, res) {
  const urlProvider = properties.get('facturacion.provider.url');
  const httpQuery = urlProvider + '/' + req.params.id;
  axios.get(httpQuery)
  .then(response => {
    Model.updateStatus(req.params.id, response, function(err, res){
      if (err)
        res.send(err);
      res.json(response);
    })
  })
  .catch(error => {
    Model.updateStatus(req.params.id, error, function(err, result){
      if (err)
        res.send(err);
      res.json(error);
    })    
    res.json(error);
  });
}
