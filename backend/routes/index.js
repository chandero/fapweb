var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/ipa', function(req, res, next) {
  res.render('index', { title: 'fapweb' });
});

module.exports = router;
