#!/usr/bin/env python3
"""Compile+run every <code class="kt-run"> block in the given HTML files via the Kotlin Playground API.
Usage: check_kt.py lesson.html [...]   Prints output or errors per block."""
import html, json, re, sys, urllib.request

API = "https://api.kotlinlang.org/api/2.4.21/compiler/run"
for path in sys.argv[1:]:
    src = open(path, encoding="utf-8").read()
    blocks = re.findall(r'<code class="kt-run"[^>]*>(.*?)</code>', src, re.S)
    print(f"=== {path}: {len(blocks)} runnable blocks")
    for i, b in enumerate(blocks, 1):
        code = html.unescape(re.sub(r'<textarea class="hidden-dependency">.*?</textarea>', '', b, flags=re.S))
        body = json.dumps({"args": "", "files": [{"name": "File.kt", "text": code, "publicId": ""}], "confType": "java"}).encode()
        req = urllib.request.Request(API, data=body, headers={"Content-Type": "application/json"})
        r = json.load(urllib.request.urlopen(req, timeout=60))
        errs = [f'{e["severity"]}: {e["message"]} (line {e["interval"]["start"]["line"]+1})' for e in r["errors"].get("File.kt", []) if e["severity"] != "INFO"]
        out = re.sub(r"</?outStream>|</?errStream>", "", r.get("text") or "").strip()
        exc = r.get("exception")
        first = code.strip().splitlines()[0][:60] if code.strip() else ""
        print(f"--- block {i} [{first}]")
        for e in errs: print("   ", e)
        if exc: print("    EXCEPTION:", exc.get("message") or exc.get("fullName"))
        if out: print("    OUT:", out.replace("\n", " | ")[:300])
