#!/usr/bin/env python3
"""Compile+run every <code class="kt-run"> block in the given HTML files.
Uses local kotlinc toolchain (via serve.sh) if available, otherwise JetBrains Kotlin 2.4.21 API.
Usage: python3 -I tools/check_kt.py lessons/0001-*.html [...]"""
import html
import importlib.machinery
import importlib.util
import json
import os
import re
import sys
import urllib.request

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
API = "https://api.kotlinlang.org/api/2.4.21/compiler/run"

serve_mod = None
serve_path = os.path.join(ROOT, "serve.sh")
if os.path.isfile(serve_path):
    loader = importlib.machinery.SourceFileLoader("serve_mod", serve_path)
    spec = importlib.util.spec_from_file_location("serve_mod", serve_path, loader=loader)
    serve_mod = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(serve_mod)


def strip_markers(code):
    return "\n".join(
        ln for ln in code.splitlines() if ln.strip() not in ("//sampleStart", "//sampleEnd")
    ).strip()


def run_snippet(code):
    if serve_mod and serve_mod.LOCAL_TOOLCHAIN:
        res = serve_mod.run_code_locally(code)
        if res is not None:
            return res
    body = json.dumps({
        "args": "",
        "files": [{"name": "File.kt", "text": code, "publicId": ""}],
        "confType": "java",
    }).encode()
    req = urllib.request.Request(API, data=body, headers={"Content-Type": "application/json"})
    return json.load(urllib.request.urlopen(req, timeout=60))


for path in sys.argv[1:]:
    src = open(path, encoding="utf-8").read()
    blocks = re.findall(r'<code class="kt-run"[^>]*>(.*?)</code>', src, re.S)
    print(f"=== {path}: {len(blocks)} runnable blocks")
    for i, b in enumerate(blocks, 1):
        raw = html.unescape(re.sub(r'<textarea class="hidden-dependency">.*?</textarea>', "", b, flags=re.S))
        code = strip_markers(raw)
        r = run_snippet(code)
        errs = [
            f'{e["severity"]}: {e["message"]} (line {e["interval"]["start"]["line"] + 1})'
            for e in r.get("errors", {}).get("File.kt", [])
            if e.get("severity") != "INFO"
        ]
        out = re.sub(r"</?outStream>|</?errStream>", "", r.get("text") or "").strip()
        exc = r.get("exception")
        first = code.splitlines()[0][:60] if code else ""
        print(f"--- block {i} [{first}]")
        for e in errs:
            print("   ", e)
        if exc:
            print("    EXCEPTION:", exc.get("message") or exc.get("fullName"))
        if out:
            print("    OUT:", out.replace("\n", " | ")[:300])
