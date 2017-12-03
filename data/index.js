const fs = require('fs');
const parse = require('csv-parse')
const mysql = require('mysql');
const files = ["actors", "admins", "customers", "marketAccounts", "stocks"];

var con = mysql.createConnection({
  host: "cs174a.engr.ucsb.edu",
  user: "mschmit",
  password: "798",
  database: 'mschmitDB'
});

con.connect();

fs.readFile("./actors.csv", function (err, fileData) {
  parse(fileData, {columns: true, trim: true}, function(err, rows) {
    console.log(rows[0]);
    for (const actor in rows) {
      let query = 'INSERT INTO Actor_Stock VALUES ('+actor.NAME+', '+actor.DOB+', '+
        actor.ACTORID+', '+actor.CURRENTPRICE+', "whatever")';
      con.query(query, function (error, results, fields) {
        if (error) throw error;
      });
    }
  })
})
