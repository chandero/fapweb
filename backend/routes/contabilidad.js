var express = require('express');
var router = express.Router();
var model = require('../controllers/contabilidadController');

router.route('/save')
    .post(model.save);

router.route('/nullify')
    .post(model.nullify);

router.route('/recover')
    .post(model.recover);

router.route('/gtpc')
    .get(model.getTypes);

router.route('/gccc')
    .get(model.getCentro);

router.route('/gcto')
    .get(model.getTipoOperacion);

router.route('/gcic')
    .get(model.getInformeContable);

router.route('/gpuc')
    .get(model.getPucAll);

router.route('/vcod/:id')
    .get(model.getIsValid2Mov);

router.route('/gpbi/:id')
    .get(model.getPucById);
    
router.route('/all')
    .get(model.list_all);

router.route('/allpage')
    .post(model.list_all_page);

router.route('/gcom/:tp/:id')
    .get(model.readComp)

router.route('/gaux/:tp/:id')
    .get(model.readAux);

router.route('/gnota/:tp/:id')
    .get(model.pdfNota);

module.exports = router;    