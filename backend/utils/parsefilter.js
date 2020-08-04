'use strict';

var parsefilter = function (filter) {
    var resultString = "("
    var logicalOperator = ""
    switch (filter.logicalOperator) {
        case "all" : logicalOperator = "AND";
                     break;
        case "any": logicalOperator = "OR";
                    break;
        default: logicalOperator = "";
                 break;
    }
    var childrens = filter.children;
    if (childrens) {
        childrens.forEach(child => {
            switch (child.type) {
                case "query-builder-group":
                    var query = child.query;
                    resultString = resultString + parsefilter(query) + " "
                    break;
                case 'query-builder-rule':
                    var rule = child.query
                    var value = rule.value
                    switch (rule.operator) {
                        case "igual a": resultString = resultString + rule.rule + " = " + (typeof value === 'string'? "'" + value + "'": value) + " ";
                            break;
                        case "no igual a": resultString = resultString + rule.rule + " <> " + (typeof value === 'string'? "'" + value + "'": value) + " ";
                            break;
                        case "contiene a": resultString = resultString + rule.rule + " like '%" + value + "%'";
                            break;
                        case "comienza con": resultString = resultString + rule.rule + " like '" + value + "%'";
                            break;
                        case "termina con": resultString = resultString + rule.rule + " like '%" + value + "'";
                            break;
                        default: resultString = resultString + rule.rule + " " + rule.operator + " " + (typeof value === 'string'? "'" + value + "'": value) + " ";
                            break;
                    }
            }
            resultString = resultString + " " + logicalOperator + " ";
        });
    }
    resultString = resultString.trim();
    resultString = resultString.slice(0, resultString.length - logicalOperator.length);
    resultString = resultString + ")";
    resultString = resultString.trim();
    resultString = resultString.replace("\"", "'");
    resultString = resultString.trim();
    resultString = resultString.replace("\"", "'");

    return resultString;
}

module.exports = parsefilter;