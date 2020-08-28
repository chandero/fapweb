"use strict";

var Report = require('fluentreports').Report;
var fs = require('fs');

var environment = require('../../environments');
var PropertiesReader = require('properties-reader');
var properties = new PropertiesReader(environment);

var zeroFill = require('zero-fill');
var moment = require('moment');
const Comprobante = require('../../models/comprobanteModel');
const { rotate } = require('fluentreports/lib/fluentReports.pdfkit');

class Reporte {

    result = null;

    constructor() {}

    static printreport = function (options, result) {
        'use strict';
        options = options || {};

        const empresa = properties.get('empresa.nombre.sigla');
        const nit = properties.get("empresa.nit.numero");

        var Current_Date = new Date().toDateString();

        var header = function (rpt, data) {

            // Confidential text, we need this first because everything else prints on top of it
            // Imprime una marca de agua
            console.log("data:", data);
            if (data.ESTADO.trim() === 'N') {
                console.log("colocando anulado");
                rpt.print('ANULADO', { x: 40, y: 610, rotate: 310, opacity: 0.7, textColor: '#eeeeee', width: 1000, fontSize: 127 });
            }
            // Company Info - Top Left
            rpt.setCurrentY(14);

            if (options.image && fs.existsSync(options.image)) {
                rpt.image(options.image, { width: 200 });
            }
            rpt.print(empresa, { x: 50, y: 20, fontSize: 14 });
            rpt.print("Nit:" + nit, {x: 50, y: 36, fontSize: 10 });
            rpt.print(data.DESCRIPCION_COMPROBANTE + " No. " + zeroFill(6, data.ID_COMPROBANTE), {x: 240, y: 20, fontSize: 14 });
            rpt.print("Fecha Registro: " + moment(data.FECHADIA).format('YYYY-MM-DD'), { x: 240, y: 36, fontSize: 10 });
            rpt.print("Fecha Generación: " + moment(new Date()).format('YYYY-MM-DD'), { x: 400, y: 36, fontSize: 10 });
            var y = rpt.getCurrentY();
            rpt.line( 35, y + 5, 580, y + 5);
            rpt.line( 35, y + 5, 35, y + 45);
            rpt.line( 35, y + 45, 580, y + 45);
            rpt.line( 580, y + 5, 580, y + 45);
            rpt.print("Descripción: " + data.DESCRIPCION, { x:50, y: 58, fontSize: 11 });
            rpt.newLine();
            rpt.band([
                { data: 'CODIGO', width: 100, align: 2 },
                { data: 'CUENTA', width: 200, align: 2 },
                { data: 'IDENTIFICACION', width: 85, align: 2 },
                { data: 'DEBITO', width: 80, align: 2 },
                { data: 'CREDITO', width: 80, align: 2 },
            ], { font: "Arimo", fontSize: 8, fontBold: true });            
        }
        
        var detail = function (rpt, data) {

            rpt.setCurrentY(rpt.getCurrentY() + 10);
            data.AUXS.forEach(a => {
                rpt.band([
                    { data: a.CODIGO, width: 100 },
                    { data: a.CUENTA, width: 200 },
                    { data: a.ID_PERSONA, width: 85, align: 3 },
                    { data: new Intl.NumberFormat("en-US", { minimumFractionDigits: 2 }).format(a.DEBITO), width: 80, align: 3 },
                    { data: new Intl.NumberFormat("en-US", { minimumFractionDigits: 2 }).format(a.CREDITO), width: 80, align: 3 },
                ], { font: "Arimo", fontSize: 8, fontBold: false });
                if (a.ID_PERSONA) {
                    rpt.print(a.PERSONA, { x: 120, fontSize: 8, fontBold: false });
                }
            });
        }
        var footer = function (rpt, data) {
            rpt.band([
                { data: '', width: 100, align: 2 },
                { data: '', width: 200, align: 2 },
                { data: 'SUMAS IGUALES', width: 85, align: 2 },
                { data: new Intl.NumberFormat("en-US", { minimumFractionDigits: 2 }).format(data.TOTAL_DEBITO), width: 80, align: 3 },
                { data: new Intl.NumberFormat("en-US", { minimumFractionDigits: 2 }).format(data.TOTAL_CREDITO), width: 80, align: 3 },
            ], { font: "Arimo", fontSize: 8, fontBold: true });

            rpt.newLine();
            var y = rpt.getCurrentY();
            rpt.line( 40, y - 5, 580, y - 5);
            rpt.line( 40, y - 5, 40, y + 62);
            rpt.line( 40, y + 62, 580, y + 62);
            rpt.line( 580, y - 5, 580, y + 62);
            rpt.line( 298, y - 5, 298, y + 62);
            rpt.print("Elaboró", { x:50, y: y, fontSize: 10 });
            rpt.print("Aceptado", { x:300, y: y, fontSize: 10 });
            rpt.print(data.EMPLEADO, { x: 55, y: y + 50, fontSize: 10});
        };


        // If you change the callback to FALSE the report will be cancelled!
        var recordCount = function (count, callback) {
            console.log("We have", count, "records!");
            callback(null, true);
        };

        // You don't have to pass in a report name; it will default to "report.pdf"
        // const reportName = __dirname + "/demo03.pdf";
        const reportName = "buffer";
        const testing = { images: 1, blocks: ["210,330,240,60"] };


        var rpt = new Report(reportName, { font: "Arimo" });
        rpt.registerFont("Arimo", { normal: '../Fonts/Arimo-Regular.ttf', bold: '../Fonts/Arimo-Bold.ttf', italic: '../Fonts/Arimo-Italic.ttf', bolditalic: '../Fonts/Arimo-BoldItalic.ttf', boldItalic: '../Fonts/Arimo-BoldItalic.ttf' });

        rpt
            .recordCount(recordCount)
            .margins({left:20, top:100, bottom:50, right: 50})
            //.autoPrint(true)
            .header(header)
            .detail(detail)
            .footer(footer)
            .data(options.data)
            .outputType(Report.renderType.buffer);

        // Debug output is always nice (Optional, to help you see the structure)
        if (typeof process.env.TESTING === "undefined") { rpt.printStructure(); }


        // This does the MAGIC...  :-)
        console.time("Rendered");
        rpt.render(function (err, name) {
            console.timeEnd("Rendered");
            if (name === false) {
                console.log("Report has been cancelled!");
                result(err, null);
            } else {
                result(null, name);
                // displayReport(err, name, testing);
            }
        });

    }

    
}
/* printreport({
    image: imgLoc,
    name: "James Smith",
    company: "ACME Industries",
    address: "1234 Nowhere St",
    city: "Here",
    state: "Texas",
    postal: "0000",
    data: [{
        phone: "800-555-1212",
        faxTo: "800-555-1211",
        from: "Me",
        attention: "You",
        number_of_pages: 5,
        comments: "Here is the proposal you wanted, it should match what we discussed on the phone.  If this is acceptable; please let me know."
    }]
});
*/

module.exports = Reporte;