'use strict';

var Model = require('../models/colocacionModel');

exports.list_all = function(req, res) {
    Model.getAll(function(err, items) {

    console.log('controller')
    if (err)
      res.send(err);
      console.log('res', items);
    res.send(items);
  });
};



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


exports.read = function(req, res) {
  Model.getById(req.params.id_colocacion, function(err, item) {
    if (err)
      res.send(err);
    res.json(item);
  });
};


exports.update = function(req, res) {
  Model.updateById(req.params.id_colocacion, new Model(req.body), function(err, item) {
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

exports.get_plan = function(req, res) {
  Model.getPlan(req.params.id_colocacion, function(err, items) {
    if (err)
      res.send(err);
    res.json(items);    
  });
}

exports.get_list_by_id_colocacion = function(req, res) {
  Model.getListById(req.params.id_colocacion, function(err, items) {
    if (err)
      res.send(err);
    res.json(items);
  });
}

exports.get_list_by_document = function(req, res) {
  Model.getListByDocument(req.params.id_identificacion, req.params.id_persona, function(err, items) {
    if (err)
      res.send(err);
    res.json(items);
  });
}