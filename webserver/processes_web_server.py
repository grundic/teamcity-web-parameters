#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Script for demonstrating possibilities of web-populated select Teamcity plugin.

The scripts runs simple web server and returns list of processes in format,
suitable for the plugin. Two methods are supported: GET and POST.
GET method requires basic authorisation, POST doesn't.
"""
from __future__ import print_function

import BaseHTTPServer
import base64
import json
import time
from SimpleHTTPServer import SimpleHTTPRequestHandler

import psutil

HOST_NAME = '0.0.0.0'
PORT_NUMBER = 8222

key = base64.b64encode('username:password')


class ProcessListHandler(SimpleHTTPRequestHandler):
    def do_HEAD(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

    def do_AUTHHEAD(self):
        self.send_response(401)
        self.send_header('WWW-Authenticate', 'Basic realm=\"Test\"')
        self.send_header('Content-type', 'text/html')
        self.end_headers()

    def do_GET(self):
        if self.headers.getheader('Authorization') is None:
            self.do_AUTHHEAD()
            self.wfile.write('no auth header received')
            pass
        elif self.headers.getheader('Authorization') == 'Basic ' + key:
            self._list_processes()
        else:
            self.do_AUTHHEAD()
            self.wfile.write(self.headers.getheader('Authorization'))
            self.wfile.write('not authenticated')
            pass

    def do_POST(self):
        self._list_processes()

    def _list_processes(self):
        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()

        options = []
        for proc in psutil.process_iter():
            try:
                options.append(dict(key=proc.name(), value=proc.pid))
            except psutil.NoSuchProcess:
                pass

        self.wfile.write(json.dumps(dict(options=sorted(options, key=lambda p: p['value'], reverse=True))))

if __name__ == '__main__':
    server_class = BaseHTTPServer.HTTPServer
    httpd = server_class((HOST_NAME, PORT_NUMBER), ProcessListHandler)
    print(time.asctime(), "Server Starts - %s:%s" % (HOST_NAME, PORT_NUMBER))
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    httpd.server_close()
    print(time.asctime(), "Server Stops - %s:%s" % (HOST_NAME, PORT_NUMBER))
