#!/usr/bin/env python3
import http.server
import socketserver
import os

# Change to the directory containing your APK
os.chdir('app/build/outputs/apk/debug')

PORT = 8000

Handler = http.server.SimpleHTTPServer

with socketserver.TCPServer(("", PORT), Handler) as httpd:
    print(f"Serving APK at http://localhost:{PORT}")
    print("Access from mobile browser using your computer's IP address")
    httpd.serve_forever()