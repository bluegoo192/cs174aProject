const fs = require('fs');
const parse = require('csv-parse')
const mysql = require('mysql');
const moment = require('moment');
const files = ["actors", "admins", "customers", "marketAccounts", "stocks"];
const taxIdMap = {};
let buyId = -1; // must start at less than 0

/*
INSERT INTO Buy_Stock VALUES (0, 100, 'SKB', '001', 'alfred:SKB', '2000-01-01', 20, 100, 40, 1);
INSERT INTO Buy_Stock VALUES
  (0, 100, 'SKB', '001', 'alfred:SKB', '2000-01-01', 20, 100, 40, 1),
  (1, 500, 'SMD', '002', 'billy:SMD', '2000-01-01', 20, 500, 71, 1),
  (2, 100, 'STC', '002', 'billy:STC', '2000-01-01', 20, 100, 32.5, 1),
  (3, 100, 'SKB', '006', 'brush:SKB', '2000-01-01', 20, 100, 40, 1),
  (4, 250, 'STC', '003', 'cindy:STC', '2000-01-01', 20, 250, 32.5, 1),
  (5, 100, 'SKB', '004', 'david:SKB', '2000-01-01', 20, 100, 40, 1),
  (6, 500, 'SMD', '004', 'david:SMD', '2000-01-01', 20, 500, 71, 1),
  (7, 50, 'STC', '004', 'david:STC', '2000-01-01', 20, 50, 32.5, 1),
  (8, 100, 'SKB', '011', 'frank:SKB', '2000-01-01', 20, 100, 40, 1),
  (9, 100, 'SMD', '011', 'frank:SMD', '2000-01-01', 20, 100, 71, 1),
  (10, 200, 'STC', '011', 'frank:STC', '2000-01-01', 20, 200, 32.5, 1),
  (11, 300, 'SMD', '007', 'ivan:SMD', '2000-01-01', 20, 300, 71, 1),
  (12, 500, 'SKB', '008', 'joe:SKB', '2000-01-01', 20, 500, 40, 1),
  (13, 200, 'SMD', '008', 'joe:SMD', '2000-01-01', 20, 200, 71, 1),
  (14, 100, 'STC', '008', 'joe:STC', '2000-01-01', 20, 100, 32.5, 1),
  (15, 1000, 'SKB', '009', 'magic:SKB', '2000-01-01', 20, 1000, 40, 1),
  (16, 100, 'SKB', '010', 'olive:SKB', '2000-01-01', 20, 100, 40, 1),
  (17, 100, 'SMD', '010', 'olive:SMD', '2000-01-01', 20, 100, 71, 1),
  (18, 100, 'STC', '010', 'olive:STC', '2000-01-01', 20, 100, 32.5, 1),
  (19, 1000, 'SMD', '005', 'sailor:SMD', '2000-01-01', 20, 1000, 71, 1);
*/

var con = mysql.createConnection({
  host: "cs174a.engr.ucsb.edu",
  user: "silverstein",
  password: "954",
  database: 'silversteinDB'
});

con.connect();

// Actors
fs.readFile("./actors.csv", function (err, fileData) {
  parse(fileData, {columns: true, trim: true}, function(err, rows) {
    //console.log(rows);
    rows.forEach(function (actor) {
      let query = "INSERT INTO Actor_Stock VALUES ('"+actor.NAME+"', STR_TO_DATE('"+moment(actor.DOB).format('DD/MM/YYYY')+"', '%d/%m/%Y'), '"+
        actor.ACTORID+"', "+actor.CURRENTPRICE+", 'whatever')";
      con.query(query, function (error, results, fields) {
        //if (error) throw error;
        console.log("inserted "+actor.NAME);
      });
    });
  })
})

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
        console.log("inserted stock "+stock.TAXID);
      });
    });
  })
})

var createBuyRecord = function() {
  console.log("Buy ID is "+buyId);
  buyId = buyId + 1;
  console.log("new buy id is "+buyId);
}

var buy = function() {
  if (buyId < 0) {
    let getBuyIdQuery = "SELECT * FROM Settings WHERE setting_id = 1";
    con.query(getBuyIdQuery, function (error, results, fields) {
      buyId = results[0].curr_buy_id;
      createBuyRecord();
    });
  } else {
    createBuyRecord();
  }
}

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
        //if (error) throw error;
        console.log("inserted MA "+ma.TAXID);
      });
    });
  })
})
