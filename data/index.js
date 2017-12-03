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

fs.readFile("./actors.csv", function (err, fileData) {
  parse(fileData, {columns: true, trim: true}, function(err, rows) {
    //console.log(rows);
    rows.forEach(function (actor) {
      console.log(actor.NAME);
      console.log(actor.DOB);
      console.log(actor.ACTORID);
      console.log(actor.CURRENTPRICE);
      let query = "INSERT INTO Actor_Stock VALUES ('"+actor.NAME+"', STR_TO_DATE('"+moment(actor.DOB).format('DD/MM/YYYY')+"', '%d/%m/%Y'), '"+
        actor.ACTORID+"', "+actor.CURRENTPRICE+", 'whatever')";
        console.log(query);
      con.query(query, function (error, results, fields) {
        if (error) throw error;
        console.log("inserted "+actor.NAME);
      });
    });
  })
})
