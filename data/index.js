const fs = require('fs')
var parse = require('csv-parse')
fs.readFile("./actors.csv", function (err, fileData) {
  parse(fileData, {columns: true, trim: true}, function(err, rows) {
    // Your CSV data is in an array of arrys passed to this callback as rows.
    console.log(rows);
  })
})
