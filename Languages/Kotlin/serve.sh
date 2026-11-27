#!/usr/bin/env python3
"""Local course server (http://localhost:8000) with hybrid Kotlin 2.4 execution engine.
- If local kotlinc / JDK is detected (e.g. IntelliJ IDEA, Android Studio, Brew, SDKMAN),
  runs code locally with timeouts, accurate File.kt line numbers, and 100% offline capability.
- Otherwise proxies POST /api/run -> https://api.kotlinlang.org (2.4.21+) without browser CORS restrictions."""
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
COMPILE_TIMEOUT_SEC = 25
RUN_TIMEOUT_SEC = 10
ENDPOINTS = [
    "https://api.kotlinlang.org/api/2.4.21/compiler/run",
    "https://api.kotlinlang.org/api/compiler/run",
    "https://api.kotlinlang.org/api/2.1.20/compiler/run",
    "https://api.kotlinlang.org/api/2.0.21/compiler/run",
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

    candidate_java_home = [
        os.environ.get("JAVA_HOME"),
        "/Applications/IntelliJ IDEA.app/Contents/jbr/Contents/Home",
        "/Applications/Android Studio.app/Contents/jbr/Contents/Home",
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


def parse_jvm_exception(err_stream):
    """Extract exception fullName, message, and accurate File.kt lineNumber from JVM stderr."""
    lines = err_stream.splitlines()
    if not lines:
        return None

    full_name = None
    msg = ""
    for line in lines:
        m_thread = re.search(r'Exception in thread "[^"]+"\s+([\w\.\$]+)(?::\s*(.*))?', line)
        if m_thread:
            full_name = m_thread.group(1)
            msg = m_thread.group(2) or ""
            break
        m_plain = re.match(r'^([\w\.\$]+(?:Exception|Error))(?::\s*(.*))?$', line.strip())
        if m_plain:
            full_name = m_plain.group(1)
            msg = m_plain.group(2) or ""
            break

    if not full_name:
        return None

    m_frame = re.search(r'\(File\.kt:(\d+)\)', err_stream)
    line_num = int(m_frame.group(1)) if m_frame else None
    trace = [{"fileName": "File.kt", "lineNumber": line_num}] if line_num else []
    return {
        "fullName": full_name,
        "message": msg,
        "stackTrace": trace,
    }


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

        try:
            comp = subprocess.run(comp_cmd, env=env, capture_output=True, text=True, timeout=COMPILE_TIMEOUT_SEC)
        except subprocess.TimeoutExpired:
            return {
                "errors": {"File.kt": [{
                    "severity": "ERROR",
                    "message": f"Compilation timed out after {COMPILE_TIMEOUT_SEC}s.",
                    "interval": {"start": {"line": 0, "ch": 0}, "end": {"line": 0, "ch": 0}},
                }]},
                "text": "",
                "exception": None,
            }

        errors = {"File.kt": []}

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
            elif comp.returncode != 0 and "error:" in line:
                errors["File.kt"].append({
                    "severity": "ERROR",
                    "message": line.strip(),
                    "interval": {"start": {"line": 0, "ch": 0}, "end": {"line": 0, "ch": 0}},
                })

        if comp.returncode != 0:
            return {"errors": errors, "text": "", "exception": None}

        class_files = [f[:-6] for f in os.listdir(out_dir) if f.endswith(".class") and "$" not in f]
        main_class = "FileKt" if "FileKt" in class_files else (class_files[0] if class_files else "FileKt")

        run_cp = f"{out_dir}:{cp}" if cp else out_dir
        try:
            run_proc = subprocess.run(
                [tc["java"], "-cp", run_cp, main_class],
                env=env,
                capture_output=True,
                text=True,
                timeout=RUN_TIMEOUT_SEC,
            )
        except subprocess.TimeoutExpired as te:
            partial_out = te.stdout if isinstance(te.stdout, str) else (te.stdout.decode("utf-8", "replace") if te.stdout else "")
            return {
                "errors": errors,
                "text": f"<outStream>{partial_out}</outStream>",
                "exception": {
                    "fullName": "java.util.concurrent.TimeoutException",
                    "message": f"Program timed out after {RUN_TIMEOUT_SEC}s — check your loop termination condition for an infinite loop.",
                    "stackTrace": [],
                },
            }

        out_stream = run_proc.stdout
        err_stream = run_proc.stderr
        exc = parse_jvm_exception(err_stream) if (run_proc.returncode != 0 and err_stream) else None

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
                pass

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
        print(f"⚡ Local toolchain detected: {LOCAL_TOOLCHAIN['kotlinc']} (offline Kotlin 2.4 execution ready)")
    else:
        print("🌐 Online proxy mode active: forwarding /api/run -> api.kotlinlang.org (Kotlin 2.4.21+)")
    print("Press Ctrl+C to stop.")
    with http.server.ThreadingHTTPServer(("127.0.0.1", PORT), CourseHandler) as httpd:
        httpd.serve_forever()
