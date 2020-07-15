'use strict';

var Model = require('../models/facturaModel');

exports.list_all = function(req, res) {
    Model.getAll(function(err, items) {
        if (err)
            res.send(err);
        res.send(items);
    });
};

/*
exports.create = function(req, res) {
  var new_item = new Task(req.body);

  //handles null error 
    if(!new_item){
            res.status(400).send({ error:true, message: 'Por favor provea un item' });
    }
    else{  
        Model.create(new_item, function(err, item) {    
            if (err)
                res.send(err);
            res.json(item);
        });
    }
};
*/

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

/*
exports.update = function(req, res) {
  Model.updateById(req.params.id_colocacion, new Model(req.body), function(err, item) {
    if (err)
      res.send(err);
    res.json(item);
  });
};
*/

/*
exports.delete = function(req, res) {
  Model.remove( req.params.id_colocacion, function(err, item) {
    if (err)
      res.send(err);
    res.json({ message: 'Item eliminado con exito!' });
  });
};
*/
