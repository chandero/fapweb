var express = require('express');
var router = express.Router();
var model = require('../controllers/facturaController');

router.route('/all')
    .get(model.list_all);

router.route('/get/:id')
    .get(model.read);

router.route('/gbdr/:start/:end')
    .get(model.readDateRange);

router.route('/gbnr/:start/:end')
    .get(model.readNumberRange);

router.route('/gitm/:id')
    .get(model.readItems);

router.route('/send/:id')
    .get(model.sendToProvider)

module.exports = router;