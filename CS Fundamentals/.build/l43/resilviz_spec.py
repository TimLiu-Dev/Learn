"""Builds the resilviz widget spec from the real captured outputs in out/ (L43)."""
import json, re, pathlib
OUT = pathlib.Path(__file__).parent / "out"

def lines(name):
    return OUT.joinpath(name).read_text().splitlines()[1:]          # drop the "===== file" header

def breaker_tab():
    steps, pending = [], []
    notes = {
        0: "CLOSED: calls go straight through to pricing.",
        300: "Pricing starts failing. Each failure adds 1 to a counter of <em>consecutive</em> failures.",
        500: "The third failure in a row reaches the threshold (3 here): the breaker trips to <b>OPEN</b>.",
        600: "OPEN: the breaker answers immediately with an error and <b>does not call pricing at all</b>. That is the point: a struggling service gets room to recover, and your caller gets a fast answer instead of a slow failure.",
        1000: "500 ms after opening, the breaker goes HALF_OPEN and lets <b>one trial call</b> through. Pricing is still down, so it opens again for another 500 ms.",
        1500: "The next trial call succeeds, so the breaker closes and normal traffic resumes.",
        2400: "Of 25 requests, pricing was really called 17 times. The 8 fast failures cost almost nothing.",
    }
    for ln in lines("Breaker.kt.txt"):
        m = re.match(r"\s+state (\w+) -> (\w+)", ln)
        if m: pending.append(f"{m.group(1)} → {m.group(2)}"); continue
        m = re.match(r"t=\s*(\d+)\s+(.*?)\s+(CLOSED|OPEN|HALF_OPEN)$", ln)
        if not m: continue
        t, text, state = int(m.group(1)), m.group(2).strip(), m.group(3)
        outcome = "ok" if text.startswith("ok") else "fast" if text.startswith("fast") else "error"
        note = notes.get(t, "")
        if pending: note = (note + " " if note else "") + "(state " + ", then ".join(pending) + ")"
        pending = []
        steps.append({"t": t, "outcome": outcome, "text": re.sub(r"\s+", " ", text), "state": state, "note": note})
    return {"type": "breaker", "name": "Circuit breaker", "steps": steps}

def retries_tab():
    series, cur = [], None
    for ln in lines("Backoff.kt.txt"):
        if ln.startswith("=="):
            cur = {"label": ln.strip("= ").replace(" (attempts per 200 ms)", ""), "counts": [], "summary": ""}; series.append(cur); continue
        m = re.match(r"\s+total attempts: (\d+), last client served at (\d+) ms", ln)
        if m: cur["summary"] = f"{m.group(1)} attempts in total; the last client was served at {int(m.group(2))/1000:.1f} s"; continue
        m = re.match(r"\s+(\d+) ms\s+(\d+)", ln)
        if m: cur["counts"].append(int(m.group(2)))
    steps = [
        {"upTo": 0, "note": "t=0: all 100 clients fail at once, and each one retries after its first backoff (50 ms)."},
        {"upTo": 4, "note": "Without jitter (top), every client sleeps exactly the same amount, so the retries arrive in <b>synchronized waves</b> with silence in between. With full jitter (bottom), each client sleeps a random time between 0 and its backoff, so the same retries are spread out."},
        {"upTo": 9, "note": "The server recovers at 1 s and can serve 10 requests per 100 ms. The un-jittered wave at ~1.5 s brings 100 clients at once: 10 succeed and 90 fail again, together."},
        {"upTo": 19, "note": "Each synchronized wave is served 10 at a time while the backoff keeps doubling (up to 3.2 s), so without jitter the last client waits until 28.8 s. With jitter, everyone was served by 3.9 s, with fewer attempts in total."},
    ]
    return {"type": "retries", "name": "Retries: jitter", "bucketMs": 200, "series": series, "steps": steps}

def bucket_tab():
    steps, notes = [], {
        0: "The bucket starts full: 5 tokens. A burst of 7 arrives at once: 5 pass, 2 get 429 Too Many Requests.",
        500: "Half a second at 5 tokens/s refilled 2.5 tokens. Two whole tokens: 2 requests pass, half a token is left over.",
        1000: "Another 0.5 s: 0.5 + 2.5 = 3 tokens. 3 of the 4 requests pass.",
        1200: "Only 200 ms later: 1 token. Short gaps refill little.",
        2000: "800 ms of refill gives 4 tokens. Over time the bucket allows 5 requests per second on average, with bursts of up to 5.",
    }
    for ln in lines("RateLimit.kt.txt")[1:]:
        m = re.match(r"\s*(\d+) ms\s+(\d+)\s+(\d+) ok, (\d+) rejected \(([\d.]+)→([\d.]+) tokens\)", ln)
        if m:
            t = int(m.group(1))
            steps.append({"t": t, "asked": int(m.group(2)), "ok": int(m.group(3)), "before": float(m.group(5)), "after": float(m.group(6)), "note": notes.get(t, "")})
    return {"type": "bucket", "name": "Token bucket", "capacity": 5, "steps": steps}

def trace_tab():
    spans, total = [], 0
    for ln in lines("TraceDemo.kt.txt"):
        m = re.match(r"trace \w+\s+\((\d+) ms\)", ln)
        if m: total = int(m.group(1)); continue
        m = re.match(r"(\s*)(\w+)\s+[ █]+\s+(\d+) ms \(self (\d+), starts \+(\d+)\)", ln)
        if m: spans.append({"label": m.group(2), "depth": len(m.group(1)) // 2, "start": int(m.group(5)), "dur": int(m.group(3)), "self": int(m.group(4))})
    names = [s["label"] for s in spans]
    steps = [
        {"focus": -1, "note": f"One request, one trace id, three spans. The whole request took {total} ms."},
        {"focus": names.index("gateway"), "note": "gateway: the root span. Almost all of its time is spent waiting for books; its own work (self time) is tiny."},
        {"focus": names.index("books"), "note": "books: a 10 ms fake database call, plus the first connection to pricing, then waiting for pricing."},
        {"focus": names.index("pricing"), "note": "pricing: nearly all of its time is self time. <b>This is the slow hop</b>: the place to look first (here an artificial 120 ms sleep)."},
    ]
    return {"type": "trace", "name": "Trace waterfall", "total": total, "spans": spans, "steps": steps}

def spec():
    return json.dumps({"tabs": [breaker_tab(), retries_tab(), bucket_tab(), trace_tab()]}, ensure_ascii=False)

if __name__ == "__main__":
    print(spec())
