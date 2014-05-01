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
            
            socket.emit('login-start', {value: 'logging in'});
            socket.on('login', function(data) {
                socket.emit('login-result', {value: 'Success'});
            });

            var strQuery = "SELECT cust_id AS `#`, SPLIT_STR(CreatedAt, ' ', 1) AS Date, SPLIT_STR(CreatedAt, ' ', 2) As Time, amount as Amount FROM c_order";
            connection.query(strQuery, function(err, rows){
                if(err)	{
                    throw err;
                } else {
                    socket.emit('table1', rows);
                }
            });
                      
            var strQuery2 = "SELECT co.type AS label, co.amount AS value FROM c_order ord JOIN cookie_type co ON ord.order_id = co.order_id";
            connection.query(strQuery2, function(err, rows){
                if(err)	{
                    throw err;
                } else {
                    socket.emit('piechart', rows);
                }
            });
                      
            var strQuery3 = "SELECT ord.order_id, sc.scout_name, cu.cust_name, co.type, ord.amount, CASE WHEN cu.paid = 1 THEN 'Y' ELSE 'N' END AS paid, CASE WHEN cu.picked_up = 1 THEN 'Y' ELSE 'N' END AS picked_up FROM c_order ord JOIN scout sc ON sc.scout_id = ord.scout_id JOIN customer cu ON cu.cust_id = ord.cust_id JOIN cookie_type co ON co.order_id = ord.order_id";
                
            connection.query(strQuery3, function(err, rows){
                if(err)	{
                    throw err;
                } else {
                    socket.emit('table2', rows);
                }
            });
                      
            var strQuery4 = "SELECT scout_id AS `#`, scout_name AS Name, scout_address AS Address, CASE WHEN email IS NULL THEN 'None' ELSE email END AS Email, CASE WHEN phone IS NULL THEN 'None' ELSE phone END AS PhoneNumber, age AS Age FROM scout ORDER BY 1 DESC";
                
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
            
            socket.emit('scoutload-start', {value: 'loading scout'});
            socket.on('scoutload', function(data) {
                var stringQuery = "SELECT * FROM scout where scout_id = '" + data.value + "'";
                connection.query(stringQuery, function(err, rows){
                    if(err)	{
                        throw err;
                        socket.emit('scoutload-result', {value: err});
                    } else {
                        socket.emit('scoutload-result', {value: rows[0]});
                    }
                });
            });
            
            socket.on('scoutdelete', function(data) {
                connection.query("DELETE FROM scout WHERE scout_id = " + data.value, function(err,rows) {
                    if (err){
                        throw err;
                        socket.emit('scoutdelete-result', { value: err });
                    } else {
                        socket.emit('scoutdelete-result', { value: 'Success' });
                    }
                });
            });
                      
            socket.on('scout', function(data) {
                if (data.value1=="add") {
                    var stringQuery = "INSERT INTO scout (scout_name, password, scout_address, email, phone, age) VALUES (" + data.value2 + ")";
                    connection.query(stringQuery, function(err, rows){
                        if(err)	{
                            //throw err;
                            socket.emit('scout-result', {value: err});
                        } else {
                            socket.emit('scout-result', {value: 'Success'});
                        }
                    });
                } else {
                    var value = data.value2;
                    var stringQuery = "UPDATE scout SET scout_name = '" + value.scout_name + "', password = '" + value.password + "', scout_address = '" + value.scout_address + "', email = " + value.email + ", phone = " + value.phone + ", age = '" + value.age + "' WHERE scout_id = '" + value.scout_id + "'";
                    connection.query(stringQuery, function(err, rows){
                        if(err)	{
                            //throw err;
                            socket.emit('scout-result', {value: err});
                        } else {
                            socket.emit('scout-result', {value: 'Success'});
                        }
                    });

                }
            });
                      
            socket.emit('orderload-start', {value: 'loading scout'});
            socket.on('orderload', function(data) {
                var stringQuery = "SELECT cu.*, sc.scout_name, co.type, co.amount FROM c_order ord JOIN customer cu ON cu.cust_id = ord.cust_id JOIN scout sc ON sc.scout_id = ord.scout_id JOIN cookie_type co ON co.order_id = ord.order_id WHERE ord.order_id = '" + data.value + "'";
                connection.query(stringQuery, function(err, rows){
                    if(err)	{
                        throw err;
                        socket.emit('orderload-result', {value: err});
                    } else {
                        socket.emit('orderload-result', {value: rows});
                    }
                });
            });
                      
            socket.on('orderdelete', function(data) {
                connection.query("DELETE FROM c_order WHERE order_id = " + data.value, function(err, rows){
                    if(err)	{
                        throw err;
                        socket.emit('orderdelete-result', {value: err});
                    } else {
                        connection.query("DELETE FROM cookie_type WHERE order_id = " + data.value, function(err, rows){
                            if (err) {
                                throw err;
                                socket.emit('orderdelete-result', {value: err});
                            } else {
                                socket.emit('orderdelete-result', { value: 'Success'});
                            }
                        });
                    }
                });
            });
               
            socket.on('order', function(data) {
                var cust_val = data.value1;
                var stringQuery = "";
                if (data.value0=="edit") {
                    stringQuery = "UPDATE customer SET address = '" + cust_val[1] + "', phone = " + cust_val[2] + ", email = " + cust_val[3] + ", total_boxes = " + cust_val[4] + ", total_price = " + cust_val[5] + ", paid = " + cust_val[6] + ", picked_up = " + cust_val[7] + " WHERE cust_name = '" + cust_val[0] + "'";
                } else {
                    stringQuery = "INSERT IGNORE INTO customer (cust_name, address, phone, email, total_boxes, total_price, paid, picked_up) VALUES ('" + cust_val[0] + "','" + cust_val[1] + "'," + cust_val[2] + "," + cust_val[3] + "," + cust_val[4] + "," + cust_val[5] + "," + cust_val[6] + "," + cust_val[7] + ")";
                }
                connection.query(stringQuery, function(err, rows){
                    if(err)	{
                        throw err;
                        socket.emit('order-result', {value: err});
                    } else {
                        connection.query("SELECT cust_id FROM customer WHERE cust_name = '" + data.value2 + "'", function(err, rows) {
                             if (err) {
                                throw err;
                                socket.emit('order-result', {value: err});
                             } else {
                                var cust_id = rows[0].cust_id;
                                connection.query("SELECT scout_id FROM scout WHERE scout_name = '" + data.value3 + "'", function(err, rows) {
                                    if (err) {
                                        throw err;
                                        socket.emit('order-result', {value: err});
                                    } else {
                                        var scout_id = rows[0].scout_id;
                                                 
                                        var stringQuery2;
                                        if (data.value0=="edit") {
                                            stringQuery2 = "UPDATE c_order SET amount = " + data.value4 + " WHERE cust_id = " + cust_id + " AND scout_id = " + scout_id;
                                        } else {
                                            stringQuery2 = "INSERT INTO c_order (cust_id, scout_id, amount) VALUES (" + cust_id + "," + scout_id + "," + data.value4 + ")";
                                        }
                                        connection.query(stringQuery2, function(err, rows) {
                                            if (err) {
                                                throw err;
                                                socket.emit('order-result', {value: err});
                                            } else {
                                                connection.query("SELECT order_id FROM c_order WHERE cust_id = " + cust_id + " AND scout_id = " + scout_id, function (err, rows) {
                                                    if (err) {
                                                        throw err;
                                                        socket.emit('order-result', {
                                                            value: err
                                                        });
                                                    } else {
                                                        
                                                        var order_id = rows[0].order_id;
//                                                        connection.query("DELETE FROM cookie_type WHERE order_id = " + order_id, function(err, rows) {
//                                                            if (err) {
//                                                                throw err;
//                                                                socket.emit('order-result', {value: err});
//                                                            } else {
                                                                for (var i = 0; i < data.value5.length; i++) {
                                                                    var dataItem = data.value5[i];
                                                                         
                                                                    connection.query("INSERT INTO cookie_type (order_id, type, amount) VALUES (" + order_id + ",'" + dataItem[0] + "'," + dataItem[1] + ")", function (err, rows) {
                                                                        if (err) {
                                                                            connection.query("UPDATE cookie_type SET amount = '" + dataItem[1] + "' WHERE order_id = '" + order_id + "' AND type = '" + dataItem[0] + "'", function(err, rows) {
                                                                                if (err) {
                                                                                    throw err;
                                                                                    socket.emit('order-result', {
                                                                                        value: err
                                                                                    });
                                                                                } else {
                                                                                    socket.emit('order-result', {
                                                                                        value: "Success"
                                                                                    });
                                                                                }
                                                                            });
                                                                        } else {
                                                                            socket.emit('order-result', {
                                                                                value: "Success"
                                                                            });
                                                                        }
                                                                    });
                                                                }
//                                                            }
//                                                        });
                                                    }
                                                });
                                             }
                                        });
                                    }
                                });
                            }
                        });
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