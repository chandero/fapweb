var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/ipa', function(req, res, next) {
  res.render('index', { title: 'sifweb' });
});

module.exports = router;
