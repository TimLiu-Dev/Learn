#!/usr/bin/env python3
"""
PulseFlow / iOS Learning Workspace Local Swift 6 Compilation Server
Serves static files and provides a POST /api/run endpoint to compile and run Swift code.
Usage: python3 serve.py [port]
"""

import http.server
import json
import os
import subprocess
import sys
import tempfile
import uuid

PORT = int(sys.argv[1]) if len(sys.argv) > 1 else 8000
WORKSPACE_DIR = os.path.dirname(os.path.abspath(__file__))

class SwiftLearningHTTPHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=WORKSPACE_DIR, **kwargs)

    def end_headers(self):
        # Enable CORS for local cross-origin file:// or local server requests
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        super().end_headers()

    def do_OPTIONS(self):
        self.send_response(200)
        self.end_headers()

    def do_POST(self):
        if self.path == '/api/run':
            self.handle_run_swift()
        else:
            self.send_error(404, "Endpoint not found")

    def handle_run_swift(self):
        try:
            content_length = int(self.headers.get('Content-Length', 0))
            post_body = self.rfile.read(content_length)
            data = json.loads(post_body.decode('utf-8'))
            code = data.get('code', '')

            if not code.strip():
                self.send_json_response({
                    'success': False,
                    'output': 'Error: Empty code payload received.',
                    'exitCode': 1
                })
                return

            # Write code to a secure temporary file
            temp_id = uuid.uuid4().hex
            temp_swift_path = f"/tmp/swift_run_{temp_id}.swift"
            with open(temp_swift_path, 'w', encoding='utf-8') as f:
                f.write(code)

            # Execute Swift compiler with temporary clang module cache
            cmd = [
                'swift',
                '-module-cache-path', '/tmp/clang-module-cache',
                temp_swift_path
            ]

            try:
                result = subprocess.run(
                    cmd,
                    capture_output=True,
                    text=True,
                    timeout=12
                )
                output = (result.stdout or '') + (result.stderr or '')
                # Clean up temporary path reference from output
                output = output.replace(temp_swift_path, 'practice.swift')

                self.send_json_response({
                    'success': result.returncode == 0,
                    'output': output if output.strip() else 'Program executed successfully with no output.',
                    'exitCode': result.returncode
                })

            except subprocess.TimeoutExpired:
                self.send_json_response({
                    'success': False,
                    'output': '⏱️ Execution timed out (12-second limit exceeded).\nPossible cause: infinite loop, deadlock, or awaiting unfulfilled Task.',
                    'exitCode': -1
                })
            finally:
                if os.path.exists(temp_swift_path):
                    try:
                        os.remove(temp_swift_path)
                    except OSError:
                        pass

        except Exception as e:
            self.send_json_response({
                'success': False,
                'output': f'Server Execution Error: {str(e)}',
                'exitCode': 500
            })

    def send_json_response(self, data):
        response_bytes = json.dumps(data).encode('utf-8')
        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.send_header('Content-Length', str(len(response_bytes)))
        self.end_headers()
        self.wfile.write(response_bytes)

if __name__ == '__main__':
    print(f"🚀 Starting Swift Learning Server on http://127.0.0.1:{PORT}...")
    print(f"📂 Serving workspace directory: {WORKSPACE_DIR}")
    print(f"⚡ Live Swift 6 compilation endpoint: POST http://127.0.0.1:{PORT}/api/run")
    print("Press Ctrl+C to stop.")
    server = http.server.ThreadingHTTPServer(('127.0.0.1', PORT), SwiftLearningHTTPHandler)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\n🛑 Server stopped.")
