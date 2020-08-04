var express = require('express');
var router = express.Router();
var model = require('../controllers/personaController');

router.route('/otd')
    .get(model.obtenerTipoDocumento);

router.route('/opd/:tp/:id')
    .get(model.obtenerPorDocumento);

router.route('/oapd/:tp/:id')
    .get(model.obtenerAdicionalPorDocumento);

router.route('/oepd/:tp/:id')
    .get(model.obtenerExtraPorDocumento);

router.route('/onpd/:tp/:id')
    .get(model.obtenerNombrePorDocumento);

router.route('/opn')
    .post(model.obtenerPorNombre);

module.exports = router;