#!/bin/env node
//  OpenShift sample Node application
var express = require('express');
var fs      = require('fs');
var extensions = {
    ".html" : "text/html",
    ".css" : "text/css",
    ".js" : "application/javascript",
    ".png" : "image/png",
    ".gif" : "image/gif",
    ".jpg" : "image/jpeg"
};

var mysql      = require('mysql');
var connection = mysql.createConnection({
  host     : process.env.OPENSHIFT_MYSQL_DB_HOST,
  user     : 'adminwURHPyk',
  password : 'zszaGmnRIhQm',
  database : 'nodejs',
  _socket: '/var/run/mysqld/mysqld.sock'
});

connection.connect();

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

        self.routes['/asciimo'] = function(req, res) {
            var link = "http://i.imgur.com/kmbjB.png";
            res.send("<html><body><img src='" + link + "'></body></html>");
        };

        self.routes['/'] = function(req, res) {
            res.setHeader('Content-Type', 'text/html');
            res.send(self.cache_get('index.html') );
        };
        
        self.routes['/orders.html'] = function(req, res) {
            var link = "<tr class='odd gradeX'><td>Emily</td><td>Rudy</td><td>DoSiDos</td><td class='center'>4</td><td class='center'>Y</td><td class='center'>N</td></tr><tr class='even gradeC'><td>LaFonda</td><td>Luke Scum</td><td>Thin Mints</td><td class='center'>5</td><td class='center'>Y</td><td class='center'>N</td></tr><tr class='odd gradeA'><td>Laquisha</td><td>Albert Li</td><td>Tagalongs</td><td class='center'>5</td><td class='center'>Y</td><td class='center'>N</td></tr><tr class='even gradeA'><td>Isabella</td><td>Jack</td><td>Samoas</td><td class='center'>6</td><td class='center'>Y </td><td class='center'>N </td></tr>";
            res.send("<html><head><meta charset='utf-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'><title>Orders | Girl Scout Cookie Sale</title><link href='css/bootstrap.min.css' rel='stylesheet'><link href='font-awesome/css/font-awesome.css' rel='stylesheet'><link href='css/sb-admin.css' rel='stylesheet'><style>.dataTables_filter, .dataTables_paginate{text-align:right;}</style></head><body><div id='wrapper'><nav class='navbar navbar-default navbar-static-top' role='navigation' style='margin-bottom: 0'><div class='navbar-header'><button type='button' class='navbar-toggle' data-toggle='collapse' data-target='.sidebar-collapse'><span class='sr-only'>Toggle navigation</span><span class='icon-bar'></span><span class='icon-bar'></span><span class='icon-bar'></span></button><a class='navbar-brand' href='index.html'>Girl Scout Cookie Sale</a></div></nav><nav class='navbar-default navbar-static-side' role='navigation'><div class='sidebar-collapse'><ul class='nav' id='side-menu'><li class='sidebar-search'><div class='input-group custom-search-form'><input type='text' class='form-control' placeholder='Search...'><span class='input-group-btn'><button class='btn btn-default' type='button'><i class='fa fa-search'></i></button></span></div></li><li><a href='index.html'><i class='fa fa-dashboard fa-fw'></i> Dashboard</a></li><li class='active'><a href='tables.html'><i class='fa fa-table fa-fw'></i> Orders</a></li><li class='active'><a href='order.html'><i class='fa fa-edit fa-fw'></i> New Order</a></li><li><a href='scout.html'><i class='fa fa-edit fa-fw'></i> New Scout</a></li><li><a href='cookie.html'><i class='fa fa-edit fa-fw'></i> New Cookie</a></li><li class='active'><a href='login.html'><i class='fa fa-user fa-fw'></i> Log In</a></li></ul></div></nav><div id='page-wrapper' style='min-height: 700px;'><div class='row'><div class='col-lg-12'><h1 class='page-header'>Tables</h1></div></div><div class='row'><div class='col-lg-12'><div class='panel panel-default'><div class='panel-heading'>Cookie Orders</div><div class='panel-body'><div class='table-responsive'><table class='table table-hover' id='dataTables-example'><thead><tr><th>Scout</th><th>Customer</th><th>Types of Cookie</th><th>Boxes</th><th>Paid</th><th>Picked up</th></tr></thead><tbody>" + link + "</tbody></table></div></div></div></div></div></div></div><script src='js/jquery-1.10.2.js'></script><script src='js/bootstrap.min.js'></script><script src='js/plugins/metisMenu/jquery.metisMenu.js'></script><script src='js/plugins/dataTables/jquery.dataTables.js'></script><script src='js/plugins/dataTables/dataTables.bootstrap.js'></script><script src='js/sb-admin.js'></script><script>$(document).ready(function() {$('#dataTables-example').dataTable();});</script></body></html>");
        };
    };


    /**
     *  Initialize the server (express) and create the routes and register
     *  the handlers.
     */
    self.initializeServer = function() {
        self.createRoutes();
        self.app = express.createServer(function(request, response) {
            //Ready!
        });

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
        self.app.use(express.static(__dirname));
        self.app.listen(self.port, self.ipaddress, function() {
            console.log('%s: Node server started on %s:%d ...',
                        Date(Date.now() ), self.ipaddress, self.port);
        });
    };

};   /*  Sample Application.  */



/**
 *  main():  Main code.
 */
var zapp = new SampleApp();
zapp.initialize();
zapp.start();
connection.end();


