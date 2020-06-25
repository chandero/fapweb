var express = require('express');
var router = express.Router();
var model = require('../controllers/colocacionController');

router.route('/all')
    .get(model.list_all);

router.route('/add')
    .post(model.create);
   
router.route('/get/:id_colocacion')
    .get(model.read);

router.route('/plan/:id_colocacion')
    .get(model.get_plan);
    
router.route('/listbyid/:id_colocacion')
    .get(model.get_list_by_id_colocacion);

router.route('/listbydocument/:id_identificacion/:id_persona')
    .get(model.get_list_by_document);

module.exports = router;

