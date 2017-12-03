const fs = require('fs');
const parse = require('csv-parse')
const mysql = require('mysql');
const moment = require('moment');
const files = ["actors", "admins", "customers", "marketAccounts", "stocks"];

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
// fs.readFile("./customers.csv", function (err, fileData) {
//   parse(fileData, {columns: true, trim: true}, function(err, rows) {
//     //console.log(rows);
//     rows.forEach(function (customer) {
//       let query = "INSERT INTO Customers VALUES ('"+customer.username+"', '"+customer.STATE+"', '"+
//         customer.email+"', "+customer.TAXID+", '"+
//         customer.Phone.replace("(","").replace(")","")+"', '"+
//         customer.password+"', '"+customer.Name+"')";
//         console.log(query);
//       con.query(query, function (error, results, fields) {
//         if (error) throw error;
//         console.log("inserted "+customer.Name);
//       });
//     });
//   })
// })
