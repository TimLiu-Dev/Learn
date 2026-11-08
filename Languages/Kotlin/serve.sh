#!/usr/bin/env python3
"""Local course server (http://localhost:8000) with hybrid Kotlin execution engine.
- If local kotlinc / JDK is detected (e.g. IntelliJ IDEA, Android Studio, Brew, SDKMAN),
  runs code locally with zero latency and 100% offline capability.
- Otherwise proxies POST /api/run -> https://api.kotlinlang.org without browser CORS restrictions."""
import glob
import http.server
import json
import os
import re
import shutil
import subprocess
import sys
import tempfile
import urllib.error
import urllib.request

PORT = int(sys.argv[1]) if len(sys.argv) > 1 else 8000
ROOT = os.path.dirname(os.path.abspath(__file__))
ENDPOINTS = [
    "https://api.kotlinlang.org/api/2.1.20/compiler/run",
    "https://api.kotlinlang.org/api/2.0.21/compiler/run",
    "https://api.kotlinlang.org/api/2.4.21/compiler/run",
    "https://api.kotlinlang.org/api/compiler/run",
]


def find_local_toolchain():
    """Locate kotlinc, java runtime, and kotlin stdlib/coroutines jars."""
    candidate_kotlinc = [
        shutil.which("kotlinc"),
        "/Applications/IntelliJ IDEA.app/Contents/plugins/Kotlin/kotlinc/bin/kotlinc",
        "/Applications/Android Studio.app/Contents/plugins/Kotlin/kotlinc/bin/kotlinc",
        os.path.expanduser("~/.sdkman/candidates/kotlin/current/bin/kotlinc"),
        "/opt/homebrew/bin/kotlinc",
        "/usr/local/bin/kotlinc",
    ]
    kotlinc_bin = next((p for p in candidate_kotlinc if p and os.path.isfile(p) and os.access(p, os.X_OK)), None)
    if not kotlinc_bin:
        return None

    # Check for JAVA_HOME
    candidate_java_home = [
        os.environ.get("JAVA_HOME"),
        "/Applications/IntelliJ IDEA.app/Contents/jbr/Contents/Home",
        "/Applications/Android Studio.app/Contents/jbr/Contents/Home",
        "/Library/Java/JavaVirtualMachines",
    ]
    java_home = next((h for h in candidate_java_home if h and os.path.isdir(h)), None)

    java_bin = None
    if java_home:
        cand = os.path.join(java_home, "bin", "java")
        if os.path.isfile(cand) and os.access(cand, os.X_OK):
            java_bin = cand
    if not java_bin:
        java_bin = shutil.which("java")

    if not java_bin:
        return None

    # Find lib directory with standard library & coroutines
    kotlinc_dir = os.path.dirname(os.path.dirname(os.path.abspath(kotlinc_bin)))
    lib_dir = os.path.join(kotlinc_dir, "lib")
    jars = glob.glob(os.path.join(lib_dir, "*.jar")) if os.path.isdir(lib_dir) else []
    classpath = ":".join(jars)

    return {
        "kotlinc": kotlinc_bin,
        "java": java_bin,
        "java_home": java_home,
        "classpath": classpath,
    }


LOCAL_TOOLCHAIN = find_local_toolchain()


def run_code_locally(code):
    tc = LOCAL_TOOLCHAIN
    if not tc:
        return None

    env = os.environ.copy()
    if tc["java_home"]:
        env["JAVA_HOME"] = tc["java_home"]

    with tempfile.TemporaryDirectory() as tmpdir:
        src_file = os.path.join(tmpdir, "File.kt")
        out_dir = os.path.join(tmpdir, "out")
        os.makedirs(out_dir, exist_ok=True)

        with open(src_file, "w", encoding="utf-8") as f:
            f.write(code)

        cp = tc["classpath"]
        comp_cmd = [tc["kotlinc"], "-d", out_dir]
        if cp:
            comp_cmd.extend(["-cp", cp])
        comp_cmd.append(src_file)

        comp = subprocess.run(comp_cmd, env=env, capture_output=True, text=True)
        errors = {"File.kt": []}

        if comp.returncode != 0:
            for line in comp.stderr.splitlines():
                m = re.match(r".*File\.kt:(\d+):\d+:\s*(error|warning):\s*(.*)", line)
                if m:
                    line_no = int(m.group(1)) - 1
                    sev = "ERROR" if m.group(2) == "error" else "WARNING"
                    errors["File.kt"].append({
                        "severity": sev,
                        "message": m.group(3),
                        "interval": {"start": {"line": line_no, "ch": 0}, "end": {"line": line_no, "ch": 0}},
                    })
                elif "error:" in line:
                    errors["File.kt"].append({
                        "severity": "ERROR",
                        "message": line.strip(),
                        "interval": {"start": {"line": 0, "ch": 0}, "end": {"line": 0, "ch": 0}},
                    })
            return {"errors": errors, "text": "", "exception": None}

        # Find compiled class name
        class_files = [f[:-6] for f in os.listdir(out_dir) if f.endswith(".class") and not "$" in f]
        main_class = "FileKt" if "FileKt" in class_files else (class_files[0] if class_files else "FileKt")

        run_cp = f"{out_dir}:{cp}" if cp else out_dir
        run_proc = subprocess.run([tc["java"], "-cp", run_cp, main_class], env=env, capture_output=True, text=True)

        out_stream = run_proc.stdout
        err_stream = run_proc.stderr
        exc = None

        if run_proc.returncode != 0 and err_stream:
            lines = err_stream.splitlines()
            if lines and ("Exception" in lines[0] or "Error" in lines[0]):
                exc_line = lines[0]
                m_exc = re.search(r"Exception in thread \"\w+\" ([\w\.]+)(?:: (.*))?", exc_line)
                if m_exc:
                    full_name = m_exc.group(1)
                    msg = m_exc.group(2) or ""
                    exc = {
                        "fullName": full_name,
                        "message": msg,
                        "stackTrace": [{"fileName": "File.kt", "lineNumber": 1}],
                    }

        text_payload = f"<outStream>{out_stream}</outStream>"
        if err_stream and not exc:
            text_payload += f"<errStream>{err_stream}</errStream>"

        return {"errors": errors, "text": text_payload, "exception": exc}


class CourseHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=ROOT, **kwargs)

    def do_POST(self):
        if self.path != "/api/run":
            self.send_error(404, "Not Found")
            return

        length = int(self.headers.get("Content-Length", "0"))
        payload_bytes = self.rfile.read(length)

        # 1. Try local execution if toolchain is available
        if LOCAL_TOOLCHAIN:
            try:
                body = json.loads(payload_bytes.decode("utf-8"))
                files = body.get("files", [])
                code = files[0].get("text", "") if files else ""
                local_result = run_code_locally(code)
                if local_result is not None:
                    self.send_response(200)
                    self.send_header("Content-Type", "application/json; charset=utf-8")
                    self.send_header("Cache-Control", "no-store")
                    self.end_headers()
                    self.wfile.write(json.dumps(local_result).encode("utf-8"))
                    return
            except Exception:
                pass  # Fall back to remote proxy if local execution fails unexpectedly

        # 2. Remote proxy fallback
        last_err = "Unknown compiler proxy error"
        for url in ENDPOINTS:
            try:
                req = urllib.request.Request(
                    url,
                    data=payload_bytes,
                    headers={"Content-Type": "application/json", "Accept": "application/json"},
                )
                with urllib.request.urlopen(req, timeout=30) as resp:
                    data = resp.read()
                    self.send_response(200)
                    self.send_header("Content-Type", "application/json; charset=utf-8")
                    self.send_header("Cache-Control", "no-store")
                    self.end_headers()
                    self.wfile.write(data)
                    return
            except urllib.error.HTTPError as e:
                last_err = f"HTTP {e.code} from {url}"
                if e.code not in (404, 405, 502, 503):
                    break
            except Exception as e:
                last_err = str(e)

        self.send_response(502)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.end_headers()
        self.wfile.write(json.dumps({"proxyError": last_err}).encode("utf-8"))


if __name__ == "__main__":
    print(f"Serving Idiomatic Kotlin at http://localhost:{PORT}")
    if LOCAL_TOOLCHAIN:
        print(f"⚡ Local toolchain detected: {LOCAL_TOOLCHAIN['kotlinc']} (offline execution ready)")
    else:
        print("🌐 Online proxy mode active: forwarding /api/run -> api.kotlinlang.org")
    print("Press Ctrl+C to stop.")
    with http.server.ThreadingHTTPServer(("127.0.0.1", PORT), CourseHandler) as httpd:
        httpd.serve_forever()
