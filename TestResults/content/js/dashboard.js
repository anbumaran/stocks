/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 88.55555555555556, "KoPercent": 11.444444444444445};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.8855555555555555, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.96, 500, 1500, "08Logout"], "isController": false}, {"data": [0.82, 500, 1500, "01Login"], "isController": false}, {"data": [0.88, 500, 1500, "05GetCartInfo"], "isController": false}, {"data": [0.88, 500, 1500, "06RemoveProductFromCart"], "isController": false}, {"data": [0.84, 500, 1500, "07GetEmptyCartInfo"], "isController": false}, {"data": [0.97, 500, 1500, "00AsappShopping-UI"], "isController": false}, {"data": [0.88, 500, 1500, "03GetSpecificProducts"], "isController": false}, {"data": [0.92, 500, 1500, "04AddProductToCart"], "isController": false}, {"data": [0.82, 500, 1500, "02GetProducts"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 900, 103, 11.444444444444445, 6.126666666666671, 1, 61, 5.0, 10.0, 12.0, 18.0, 7.892175346598035, 3.8109461594701717, 1.3743312203495357], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["08Logout", 100, 4, 4.0, 7.770000000000001, 6, 21, 7.0, 8.900000000000006, 9.0, 20.929999999999964, 0.8789895136551021, 0.15794342823490115, 0.18412426824122988], "isController": false}, {"data": ["01Login", 100, 18, 18.0, 9.120000000000005, 6, 31, 8.0, 12.0, 14.0, 30.909999999999954, 0.8783178456619881, 0.15696500562123422, 0.2062846112126056], "isController": false}, {"data": ["05GetCartInfo", 100, 12, 12.0, 4.099999999999998, 3, 22, 4.0, 5.0, 5.949999999999989, 21.889999999999944, 0.8789740614754459, 0.24159769467078027, 0.11802630220007207], "isController": false}, {"data": ["06RemoveProductFromCart", 100, 12, 12.0, 11.029999999999998, 8, 32, 10.5, 13.900000000000006, 15.0, 31.85999999999993, 0.8789354334030622, 0.1819327680313604, 0.20782016321830998], "isController": false}, {"data": ["07GetEmptyCartInfo", 100, 16, 16.0, 3.960000000000001, 3, 11, 4.0, 5.0, 6.0, 10.989999999999995, 0.87900496637806, 0.14421175229640049, 0.11803045202830396], "isController": false}, {"data": ["00AsappShopping-UI", 100, 3, 3.0, 2.94, 1, 61, 2.0, 4.0, 4.0, 60.459999999999724, 0.877369995700887, 1.764164864402467, 0.0993895698254911], "isController": false}, {"data": ["03GetSpecificProducts", 100, 12, 12.0, 4.029999999999999, 3, 16, 4.0, 5.0, 6.949999999999989, 15.93999999999997, 0.8788736355486808, 0.3019497399632631, 0.12884424821149215], "isController": false}, {"data": ["04AddProductToCart", 100, 8, 8.0, 8.040000000000008, 6, 21, 7.0, 9.0, 13.899999999999977, 20.95999999999998, 0.8788736355486808, 0.1887861766360233, 0.20093935112759487], "isController": false}, {"data": ["02GetProducts", 100, 18, 18.0, 4.150000000000001, 3, 10, 4.0, 5.0, 6.949999999999989, 10.0, 0.8785261844729282, 0.6786271600762561, 0.11367648383072558], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["The operation lasted too long: It took 12 milliseconds, but should not have lasted longer than 11 milliseconds.", 9, 8.737864077669903, 1.0], "isController": false}, {"data": ["The operation lasted too long: It took 31 milliseconds, but should not have lasted longer than 11 milliseconds.", 1, 0.970873786407767, 0.1111111111111111], "isController": false}, {"data": ["The operation lasted too long: It took 21 milliseconds, but should not have lasted longer than 9 milliseconds.", 2, 1.941747572815534, 0.2222222222222222], "isController": false}, {"data": ["The operation lasted too long: It took 13 milliseconds, but should not have lasted longer than 11 milliseconds.", 1, 0.970873786407767, 0.1111111111111111], "isController": false}, {"data": ["The operation lasted too long: It took 22 milliseconds, but should not have lasted longer than 11 milliseconds.", 1, 0.970873786407767, 0.1111111111111111], "isController": false}, {"data": ["The operation lasted too long: It took 17 milliseconds, but should not have lasted longer than 9 milliseconds.", 1, 0.970873786407767, 0.1111111111111111], "isController": false}, {"data": ["The operation lasted too long: It took 16 milliseconds, but should not have lasted longer than 9 milliseconds.", 1, 0.970873786407767, 0.1111111111111111], "isController": false}, {"data": ["The operation lasted too long: It took 14 milliseconds, but should not have lasted longer than 12 milliseconds.", 4, 3.883495145631068, 0.4444444444444444], "isController": false}, {"data": ["The operation lasted too long: It took 14 milliseconds, but should not have lasted longer than 9 milliseconds.", 3, 2.912621359223301, 0.3333333333333333], "isController": false}, {"data": ["The operation lasted too long: It took 32 milliseconds, but should not have lasted longer than 12 milliseconds.", 1, 0.970873786407767, 0.1111111111111111], "isController": false}, {"data": ["The operation lasted too long: It took 13 milliseconds, but should not have lasted longer than 9 milliseconds.", 1, 0.970873786407767, 0.1111111111111111], "isController": false}, {"data": ["The operation lasted too long: It took 10 milliseconds, but should not have lasted longer than 9 milliseconds.", 1, 0.970873786407767, 0.1111111111111111], "isController": false}, {"data": ["The operation lasted too long: It took 12 milliseconds, but should not have lasted longer than 9 milliseconds.", 3, 2.912621359223301, 0.3333333333333333], "isController": false}, {"data": ["The operation lasted too long: It took 18 milliseconds, but should not have lasted longer than 12 milliseconds.", 1, 0.970873786407767, 0.1111111111111111], "isController": false}, {"data": ["The operation lasted too long: It took 14 milliseconds, but should not have lasted longer than 11 milliseconds.", 4, 3.883495145631068, 0.4444444444444444], "isController": false}, {"data": ["The operation lasted too long: It took 6 milliseconds, but should not have lasted longer than 4 milliseconds.", 5, 4.854368932038835, 0.5555555555555556], "isController": false}, {"data": ["The operation lasted too long: It took 9 milliseconds, but should not have lasted longer than 4 milliseconds.", 1, 0.970873786407767, 0.1111111111111111], "isController": false}, {"data": ["The operation lasted too long: It took 10 milliseconds, but should not have lasted longer than 4 milliseconds.", 1, 0.970873786407767, 0.1111111111111111], "isController": false}, {"data": ["The operation lasted too long: It took 7 milliseconds, but should not have lasted longer than 4 milliseconds.", 1, 0.970873786407767, 0.1111111111111111], "isController": false}, {"data": ["The operation lasted too long: It took 11 milliseconds, but should not have lasted longer than 4 milliseconds.", 1, 0.970873786407767, 0.1111111111111111], "isController": false}, {"data": ["The operation lasted too long: It took 15 milliseconds, but should not have lasted longer than 12 milliseconds.", 2, 1.941747572815534, 0.2222222222222222], "isController": false}, {"data": ["Assertion failed", 42, 40.77669902912621, 4.666666666666667], "isController": false}, {"data": ["The operation lasted too long: It took 61 milliseconds, but should not have lasted longer than 4 milliseconds.", 1, 0.970873786407767, 0.1111111111111111], "isController": false}, {"data": ["The operation lasted too long: It took 16 milliseconds, but should not have lasted longer than 12 milliseconds.", 2, 1.941747572815534, 0.2222222222222222], "isController": false}, {"data": ["The operation lasted too long: It took 5 milliseconds, but should not have lasted longer than 4 milliseconds.", 9, 8.737864077669903, 1.0], "isController": false}, {"data": ["The operation lasted too long: It took 13 milliseconds, but should not have lasted longer than 12 milliseconds.", 2, 1.941747572815534, 0.2222222222222222], "isController": false}, {"data": ["The operation lasted too long: It took 18 milliseconds, but should not have lasted longer than 11 milliseconds.", 2, 1.941747572815534, 0.2222222222222222], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 900, 103, "Assertion failed", 42, "The operation lasted too long: It took 12 milliseconds, but should not have lasted longer than 11 milliseconds.", 9, "The operation lasted too long: It took 5 milliseconds, but should not have lasted longer than 4 milliseconds.", 9, "The operation lasted too long: It took 6 milliseconds, but should not have lasted longer than 4 milliseconds.", 5, "The operation lasted too long: It took 14 milliseconds, but should not have lasted longer than 12 milliseconds.", 4], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["08Logout", 100, 4, "The operation lasted too long: It took 21 milliseconds, but should not have lasted longer than 9 milliseconds.", 1, "The operation lasted too long: It took 14 milliseconds, but should not have lasted longer than 9 milliseconds.", 1, "The operation lasted too long: It took 13 milliseconds, but should not have lasted longer than 9 milliseconds.", 1, "The operation lasted too long: It took 12 milliseconds, but should not have lasted longer than 9 milliseconds.", 1, "", ""], "isController": false}, {"data": ["01Login", 100, 18, "The operation lasted too long: It took 12 milliseconds, but should not have lasted longer than 11 milliseconds.", 9, "The operation lasted too long: It took 14 milliseconds, but should not have lasted longer than 11 milliseconds.", 4, "The operation lasted too long: It took 18 milliseconds, but should not have lasted longer than 11 milliseconds.", 2, "The operation lasted too long: It took 13 milliseconds, but should not have lasted longer than 11 milliseconds.", 1, "The operation lasted too long: It took 22 milliseconds, but should not have lasted longer than 11 milliseconds.", 1], "isController": false}, {"data": ["05GetCartInfo", 100, 12, "Assertion failed", 12, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["06RemoveProductFromCart", 100, 12, "The operation lasted too long: It took 14 milliseconds, but should not have lasted longer than 12 milliseconds.", 4, "The operation lasted too long: It took 16 milliseconds, but should not have lasted longer than 12 milliseconds.", 2, "The operation lasted too long: It took 13 milliseconds, but should not have lasted longer than 12 milliseconds.", 2, "The operation lasted too long: It took 15 milliseconds, but should not have lasted longer than 12 milliseconds.", 2, "The operation lasted too long: It took 18 milliseconds, but should not have lasted longer than 12 milliseconds.", 1], "isController": false}, {"data": ["07GetEmptyCartInfo", 100, 16, "The operation lasted too long: It took 5 milliseconds, but should not have lasted longer than 4 milliseconds.", 8, "The operation lasted too long: It took 6 milliseconds, but should not have lasted longer than 4 milliseconds.", 5, "The operation lasted too long: It took 9 milliseconds, but should not have lasted longer than 4 milliseconds.", 1, "The operation lasted too long: It took 10 milliseconds, but should not have lasted longer than 4 milliseconds.", 1, "The operation lasted too long: It took 11 milliseconds, but should not have lasted longer than 4 milliseconds.", 1], "isController": false}, {"data": ["00AsappShopping-UI", 100, 3, "The operation lasted too long: It took 61 milliseconds, but should not have lasted longer than 4 milliseconds.", 1, "The operation lasted too long: It took 5 milliseconds, but should not have lasted longer than 4 milliseconds.", 1, "The operation lasted too long: It took 7 milliseconds, but should not have lasted longer than 4 milliseconds.", 1, "", "", "", ""], "isController": false}, {"data": ["03GetSpecificProducts", 100, 12, "Assertion failed", 12, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["04AddProductToCart", 100, 8, "The operation lasted too long: It took 14 milliseconds, but should not have lasted longer than 9 milliseconds.", 2, "The operation lasted too long: It took 12 milliseconds, but should not have lasted longer than 9 milliseconds.", 2, "The operation lasted too long: It took 17 milliseconds, but should not have lasted longer than 9 milliseconds.", 1, "The operation lasted too long: It took 16 milliseconds, but should not have lasted longer than 9 milliseconds.", 1, "The operation lasted too long: It took 21 milliseconds, but should not have lasted longer than 9 milliseconds.", 1], "isController": false}, {"data": ["02GetProducts", 100, 18, "Assertion failed", 18, "", "", "", "", "", "", "", ""], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
