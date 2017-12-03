const fs = require('fs');
const parse = require('csv-parse')
const mysql = require('mysql');
const files = ["actors", "admins", "customers", "marketAccounts", "stocks"];
const objects = {};

var con = mysql.createConnection({
  host: "cs174a.engr.ucsb.edu",
  user: "silverstein",
  password: "954",
  database: "silverteinDB"
});

con.connect(function(err) {
  if (err) throw err;
  // Enter actors
});

files.forEach(function(name) {
  fs.readFile("./"+name+".csv", function (err, fileData) {
    parse(fileData, {columns: true, trim: true}, function(err, rows) {
      // Your CSV data is in an array of arrys passed to this callback as rows.
      if (err) throw err;
      objects[name] = rows;
    })
  })
});
