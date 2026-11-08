"""Call the Kotlin Playground server (the one assets/playground.js uses), with an on-disk cache.

run(code)          -> {"ok": compiled, "out": stdout, "err": stderr, "exc": "Type: message" | None, "errors": [...]}
test(code, tests)  -> {"ok": compiled, "errors": [...], "tests": [(name, "OK"|"FAIL"|"ERROR", message)]}
Requests go one at a time with a short pause, to be polite to a free public service.
"""
import hashlib
import json
import os
import re
import time
import urllib.request
from pathlib import Path

KOTLIN = "2.4.21"   # keep in step with assets/playground.js
SERVER = "https://api.kotlinlang.org/api"
CACHE = Path(__file__).resolve().parent.parent / "cache" / "pgapi.json"
_cache = json.loads(CACHE.read_text()) if CACHE.exists() else {}
_last = [0.0]


def _post(kind, files, conf):
    key = hashlib.sha1(json.dumps([KOTLIN, kind, files, conf]).encode()).hexdigest()
    if key in _cache:
        return _cache[key]
    wait = 0.7 - (time.time() - _last[0])
    if wait > 0:
        time.sleep(wait)
    body = json.dumps({"args": "", "confType": conf,
                       "files": [{"name": n, "text": t, "publicId": ""} for n, t in files]}).encode()
    req = urllib.request.Request(f"{SERVER}/{KOTLIN}/compiler/{kind}", body,
                                 {"Content-Type": "application/json; charset=utf-8"})
    for attempt in range(4):
        try:
            data = json.load(urllib.request.urlopen(req, timeout=90))
            break
        except Exception:
            if attempt == 3:
                raise
            time.sleep(5 * (attempt + 1))
    _last[0] = time.time()
    _cache[key] = data
    # merge with what other runs wrote meanwhile, then replace the file atomically
    disk = json.loads(CACHE.read_text()) if CACHE.exists() else {}
    disk.update(_cache)
    _cache.update(disk)
    tmp = CACHE.with_suffix(f".{os.getpid()}.tmp")
    tmp.write_text(json.dumps(disk))
    os.replace(tmp, CACHE)
    return data


def _errors(data):
    return [f'{n}:{e.get("interval", {}).get("start", {}).get("line", "?")}: {e["message"]}'
            for n, es in (data.get("errors") or {}).items() for e in es if e.get("severity") == "ERROR"]


def _streams(text):
    text = text or ""
    grab = lambda tag: "".join(re.findall(rf"<{tag}>(.*?)</{tag}>", text, re.S))
    unesc = lambda s: s.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&")
    return unesc(grab("outStream")), unesc(grab("errStream"))


def run(code):
    data = _post("run", [("File.kt", code)], "java")
    out, err = _streams(data.get("text"))
    exc = data.get("exception")
    return {"ok": not _errors(data), "errors": _errors(data), "out": out, "err": err,
            "exc": f'{exc.get("fullName")}: {exc.get("message")}' if exc else None}


def test(code, tests):
    data = _post("test", [("File.kt", code), ("hiddenDependency0.kt", tests)], "junit")
    rows = []
    for cls in (data.get("testResults") or {}).values():
        for t in cls:
            fail = t.get("comparisonFailure") or t.get("exception") or {}
            rows.append((t["methodName"], t["status"], fail.get("message") or ""))
    return {"ok": not _errors(data), "errors": _errors(data), "tests": rows}
