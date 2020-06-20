var express = require('express');
var router = express.Router();
var model = require('../controllers/colocacionController');

router.route('/all')
    .get(model.list_all);

router.route('/add')
    .post(model.create);
   
router.route('/get/:id_colocacion')
    .get(model.read);

module.exports = router;

