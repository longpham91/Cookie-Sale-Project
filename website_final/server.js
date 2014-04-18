#!/bin/env node
//  OpenShift sample Node application
var express = require('express');
var fs      = require('fs');
var socket = require('socket.io');

var mysql      = require('mysql');
var connection = mysql.createConnection({
  host     : process.env.OPENSHIFT_MYSQL_DB_HOST,
  user     : 'adminwURHPyk',
  password : 'zszaGmnRIhQm',
  database : 'nodejs',
  _socket: '/var/run/mysqld/mysqld.sock'
});

/**
 *  Define the sample application.
 */
var SampleApp = function() {

    //  Scope.
    var self = this;


    /*  ================================================================  */
    /*  Helper functions.                                                 */
    /*  ================================================================  */

    /**
     *  Set up server IP address and port # using env variables/defaults.
     */
    self.setupVariables = function() {
        //  Set the environment variables we need.
        self.ipaddress = process.env.OPENSHIFT_NODEJS_IP;
        self.port      = process.env.OPENSHIFT_NODEJS_PORT || 8080;

        if (typeof self.ipaddress === "undefined") {
            //  Log errors on OpenShift but continue w/ 127.0.0.1 - this
            //  allows us to run/test the app locally.
            console.warn('No OPENSHIFT_NODEJS_IP var, using 127.0.0.1');
            self.ipaddress = "127.0.0.1";
        };
    };


    /**
     *  Populate the cache.
     */
    self.populateCache = function() {
        if (typeof self.zcache === "undefined") {
            self.zcache = { 'index.html': '' };
        }

        //  Local cache for static content.
        self.zcache['index.html'] = fs.readFileSync('./index.html');
    };


    /**
     *  Retrieve entry (content) from cache.
     *  @param {string} key  Key identifying content to retrieve from cache.
     */
    self.cache_get = function(key) { return self.zcache[key]; };


    /**
     *  terminator === the termination handler
     *  Terminate server on receipt of the specified signal.
     *  @param {string} sig  Signal to terminate on.
     */
    self.terminator = function(sig){
        if (typeof sig === "string") {
           console.log('%s: Received %s - terminating sample app ...',
                       Date(Date.now()), sig);
           process.exit(1);
        }
        console.log('%s: Node server stopped.', Date(Date.now()) );
    };


    /**
     *  Setup termination handlers (for exit and a list of signals).
     */
    self.setupTerminationHandlers = function(){
        //  Process on exit and signals.
        process.on('exit', function() { self.terminator(); });

        // Removed 'SIGPIPE' from the list - bugz 852598.
        ['SIGHUP', 'SIGINT', 'SIGQUIT', 'SIGILL', 'SIGTRAP', 'SIGABRT',
         'SIGBUS', 'SIGFPE', 'SIGUSR1', 'SIGSEGV', 'SIGUSR2', 'SIGTERM'
        ].forEach(function(element, index, array) {
            process.on(element, function() { self.terminator(element); });
        });
    };


    /*  ================================================================  */
    /*  App server functions (main app logic here).                       */
    /*  ================================================================  */

    /**
     *  Create the routing table entries + handlers for the application.
     */
    self.createRoutes = function() {
        self.routes = { };
        
        self.routes['/'] = function(req, res) {
            res.setHeader('Content-Type', 'text/html');
            res.send(self.cache_get('index.html') );
        };
        
        self.routes['/index.html'] = function(req, res) {
            res.setHeader('Content-Type', 'text/html');
            res.send(self.cache_get('index.html') );
        };
        
        //self.routes['/orders.html'] = function(req, res) {
        //    res.setHeader('Content-Type', 'text/html');
        //    res.send(self.cache_get('tables.html') );
        //};
    };


    /**
     *  Initialize the server (express) and create the routes and register
     *  the handlers.
     */
    self.initializeServer = function() {
        self.createRoutes();
        self.app = express();
        
        self.app.use(express.static(__dirname));
        //  Add handlers for the app (from the routes).
        for (var r in self.routes) {
            self.app.get(r, self.routes[r]);
        }
    };


    /**
     *  Initializes the sample application.
     */
    self.initialize = function() {
        self.setupVariables();
        self.populateCache();
        self.setupTerminationHandlers();

        // Create the express server and routes.
        self.initializeServer();
    };

    /**
     *  Start the server (starts up the sample application).
     */
    self.start = function() {
        //  Start the app on the specific interface (and port).
        var server = self.app.listen(self.port, self.ipaddress, function() {
            console.log('%s: Node server started on %s:%d ...',
                        Date(Date.now() ), self.ipaddress, self.port);
        });
        var io = socket.listen(server);
        io.sockets.on('connection', function (socket) {
            var strQuery = "SELECT cust_id AS `#`, SPLIT_STR(CreatedAt, ' ', 1) AS Date, SPLIT_STR(CreatedAt, ' ', 2) As Time, amount as Amount FROM c_order";
            connection.query(strQuery, function(err, rows){
                if(err)	{
                    throw err;
                } else {
                    socket.emit('table1', rows);
                }
            });
                      
            var strQuery2 = "SELECT type AS label, amount AS value FROM c_order ord JOIN cookie co ON ord.cookie_id = co.cookie_id";
            connection.query(strQuery2, function(err, rows){
                if(err)	{
                    throw err;
                } else {
                    socket.emit('piechart', rows);
                }
            });
                      
            var strQuery3 = "SELECT sc.scout_name AS Scout, cu.cust_name AS Customer, co.type AS CookieType, ord.amount AS Amount, CASE WHEN cu.paid = 1 THEN 'Y' ELSE 'N' END AS Paid, CASE WHEN cu.picked_up = 1 THEN 'Y' ELSE 'N' END AS PickedUp FROM c_order ord JOIN scout sc ON sc.scout_id = ord.scout_id JOIN customer cu ON cu.cust_id = ord.cust_id JOIN cookie co ON co.cookie_id = ord.cookie_id";
                
            connection.query(strQuery3, function(err, rows){
                if(err)	{
                    throw err;
                } else {
                    socket.emit('table2', rows);
                }
            });
                      
            var strQuery4 = "SELECT scout_name AS Name, scout_address AS Address, CASE WHEN email IS NULL THEN 'None' ELSE email END AS Email, CASE WHEN phone IS NULL THEN 'None' ELSE phone END AS PhoneNumber, age AS Age FROM scout ORDER BY 1 DESC";
                
            connection.query(strQuery4, function(err, rows){
                if(err)	{
                    throw err;
                } else {
                    socket.emit('table3', rows);
                }
            });
            
            socket.emit('cookie-start', {value: 'saving cookie'});
            socket.on('cookie', function(data) {
                var stringQuery = "INSERT INTO cookie (type, price) VALUES (" + data.value + ")";
                connection.query(stringQuery, function(err, rows){
                    if(err)	{
                        throw err;
                        socket.emit('cookie-result', {value: err});
                    } else {
                        socket.emit('cookie-result', {value: 'Success'});
                    }
                });
            });
                      
            socket.emit('scout-start', {value: 'saving scout'});
            socket.on('scout', function(data) {
                var stringQuery = "INSERT INTO scout (scout_name, password, scout_address, email, phone, age) VALUES (" + data.value + ")";
                connection.query(stringQuery, function(err, rows){
                    if(err)	{
                        throw err;
                        socket.emit('scout-result', {value: err});
                    } else {
                        socket.emit('scout-result', {value: 'Success'});
                    }
                });
            });
            
            socket.emit('order-start', {value: 'saving order'});
            socket.on('order', function(data) {
                var stringQuery = "INSERT IGNORE INTO customer (cust_name, address, phone, email, total_boxes, total_price, paid, picked_up) VALUES (" + data.value0 + ")";
                connection.query(stringQuery, function(err, rows){
                    if(err)	{
                        throw err;
                        socket.emit('order-result', {value: err});
                    } else {
                                 
                        var dataArray = data.value1;
                        
                        for (var i = 0; i < dataArray.length; i++) {
                            var dataItem = dataArray[i];
                            connection.query("SELECT cust_id FROM customer WHERE cust_name = '" + dataItem[0] + "'", function(err, rows) {
                                 if (err) {
                                    throw err;
                                    socket.emit('order-result', {value: err});
                                 } else {
                                    var cust_id = rows[0].cust_id;
                                    connection.query("SELECT scout_id FROM scout WHERE scout_name = '" + dataItem[1] + "'", function(err, rows) {
                                        if (err) {
                                            throw err;
                                            socket.emit('order-result', {value: err});
                                        } else {
                                            var scout_id = rows[0].scout_id;
                                            
                                            connection.query("SELECT cookie_id FROM cookie WHERE type = '" + dataItem[2] + "'", function(err, rows) {
                                                if (err) {
                                                    throw err;
                                                    socket.emit('order-result', {value: err});
                                                } else {
                                                    var cookie_id = rows[0].cookie_id;
                                                    var stringQuery2 = "INSERT INTO c_order (cust_id, scout_id, cookie_id, amount) VALUES (" + cust_id + "," + scout_id + "," + cookie_id + "," + dataItem[3] + ")";
                                                    //socket.emit('order-result', {value: cookie_id});
                                                    connection.query(stringQuery2, function(err, rows) {
                                                        if (err) {
                                                            throw err;
                                                            socket.emit('order-result', {value: err});
                                                        } else {
                                                            socket.emit('order-result', {value: 'Success'});
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                 }
                            });
                        }
                    }
                });
            });
        });
    };

};   /*  Sample Application.  */


/**
 *  main():  Main code.
 */
var zapp = new SampleApp();
zapp.initialize();
zapp.start();
//zapp.post();