const fs = require('fs');
const parse = require('csv-parse')
const mysql = require('mysql');
const moment = require('moment');
const files = ["actors", "admins", "customers", "marketAccounts", "stocks"];
const taxIdMap = {};

var con = mysql.createConnection({
  host: "cs174a.engr.ucsb.edu",
  user: "silverstein",
  password: "954",
  database: 'silversteinDB'
});

con.connect();

// Actors
// fs.readFile("./actors.csv", function (err, fileData) {
//   parse(fileData, {columns: true, trim: true}, function(err, rows) {
//     //console.log(rows);
//     rows.forEach(function (actor) {
//       let query = "INSERT INTO Actor_Stock VALUES ('"+actor.NAME+"', STR_TO_DATE('"+moment(actor.DOB).format('DD/MM/YYYY')+"', '%d/%m/%Y'), '"+
//         actor.ACTORID+"', "+actor.CURRENTPRICE+", 'whatever')";
//       con.query(query, function (error, results, fields) {
//         if (error) throw error;
//         console.log("inserted "+actor.NAME);
//       });
//     });
//   })
// })

// Customers
fs.readFile("./customers.csv", function (err, fileData) {
  parse(fileData, {columns: true, trim: true}, function(err, rows) {
    //console.log(rows);
    rows.forEach(function (customer) {
      taxIdMap[customer.TAXID] = customer.username;
      let query = "INSERT INTO Customers VALUES ('"+customer.username+"', '"+customer.STATE+"', '"+
        customer.email+"', "+customer.TAXID+", '"+
        customer.Phone.replace("(","").replace(")","")+"', '"+
        customer.password+"', '"+customer.Name+"')";
      con.query(query, function (error, results, fields) {
        // if (error) throw error;
        console.log("inserted "+customer.Name);
      });
    });
  })
})

// Stocks
fs.readFile("./stocks.csv", function (err, fileData) {
  console.log(taxIdMap);
  parse(fileData, {columns: true, trim: true}, function(err, rows) {
    rows.forEach(function (stock) {
      let query = "INSERT INTO stock_account VALUES ('"+taxIdMap[stock.TAXID]+":"+stock.ACTORID+"', "+
        stock.numShares+", '"+taxIdMap[stock.TAXID]+"', '"+
        stock.ACTORID+"')";
        console.log(query);
      con.query(query, function (error, results, fields) {
        //if (error) throw error;
        //console.log("inserted "+stock.TAXID);
      });
    });
  })
})

// Market Accounts
fs.readFile("./marketAccounts.csv", function (err, fileData) {
  console.log(taxIdMap);
  parse(fileData, {columns: true, trim: true}, function(err, rows) {
    rows.forEach(function (ma) {
      let query = "INSERT INTO Market_Account VALUES ('"+ma.MARKETACCOUNTID+"', "+
        ma.BALANCE+", '"+taxIdMap[ma.TAXID]+"', "+ma.BALANCE+", STR_TO_DATE('"+moment().format('DD/MM/YYYY')+"', '%d/%m/%Y')"
        +", STR_TO_DATE('"+moment().format('DD/MM/YYYY')+"', '%d/%m/%Y'),0)";
        console.log(query);
      con.query(query, function (error, results, fields) {
        if (error) throw error;
        console.log("inserted "+ma.TAXID);
      });
    });
  })
})
