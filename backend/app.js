var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var helmet = require('helmet');
var session = require('cookie-session');
var compression = require('compression');

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');
var colocacionRouter = require('./routes/colocacion');
var facturaRouter = require('./routes/factura');
var contabilidadRouter = require('./routes/contabilidad');

var app = express();


var session = require('cookie-session');

var expiryDate = new Date( Date.now() + 24 * 60 * 60 * 1000 ); // 1 hour
app.use(session({
  name: 'fapsession',
  keys: ['key1', 'key2'],
  cookie: { secure: true,
            httpOnly: true,
            domain: 'fap.fundacionapoyo.com',
            path: 'ipa/sess',
            expires: expiryDate
          }
  })
);


app.use(helmet());
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(compression()); //Compress all routes
app.use(express.static(path.join(__dirname, 'public')));

app.use('/ipa', indexRouter);
app.use('/ipa/users', usersRouter);
app.use('/ipa/colocacion', colocacionRouter);
app.use('/ipa/factura/', facturaRouter);
app.use('/ipa/contable/', contabilidadRouter);

module.exports = app;
