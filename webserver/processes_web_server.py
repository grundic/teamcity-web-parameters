#!/usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import print_function

import BaseHTTPServer
import json
import time

import psutil

HOST_NAME = '0.0.0.0'
PORT_NUMBER = 8222


class ProcessListHandler(BaseHTTPServer.BaseHTTPRequestHandler):
    def do_GET(s):
        s.send_response(200)
        s.send_header("Content-type", "application/json")
        s.end_headers()

        options = []
        for proc in psutil.process_iter():
            try:
                options.append(dict(key=proc.name(), value=proc.pid))
            except psutil.NoSuchProcess:
                pass

        s.wfile.write(json.dumps(dict(options=sorted(options, key=lambda p: p['value'], reverse=True))))


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
