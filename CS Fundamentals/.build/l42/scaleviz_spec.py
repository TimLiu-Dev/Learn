"""Builds the scaleviz spec for L42. Every number below comes from a real run (captures/*.txt);
the ring positions are computed with the same MD5 hash as Sharding.kt (cross-checked against Ring)."""
import bisect, hashlib, json

def lb_mode():
    nodes = [{"id": "c", "label": "client", "x": 60, "y": 120}, {"id": "lb", "label": "LB", "x": 210, "y": 120},
             {"id": "b1", "label": "b1", "x": 430, "y": 40}, {"id": "b2", "label": "b2", "x": 430, "y": 120},
             {"id": "b3", "label": "b3", "x": 430, "y": 200}]
    up = {"b1": "UP", "b2": "UP", "b3": "UP"}
    S = []
    S.append({"note": "Three backends, each its own JVM process. The load balancer's health checker asks each one for <code>/health</code> every 500 ms.", "state": up})
    for i, b in enumerate(["b1", "b2", "b3"]):
        S.append({"note": f"Round-robin: request {i + 1} goes to <b>{b}</b>, the next one to the next backend.", "state": up,
                  "arrows": [{"from": "c", "to": "lb", "label": "GET /name"}, {"from": "lb", "to": b, "label": f"#{i + 1}"}], "cls": {b: "hot"}})
    S.append({"note": "At 691 ms the demo kills b2 like <code>kill -9</code>. The load balancer doesn't know: its last health check said UP.",
              "state": {"b1": "UP", "b2": "LB thinks UP", "b3": "UP"}, "cls": {"b2": "dead"}})
    S.append({"note": "Request 22 is b2's turn. The connection is refused, so the client gets <b>502 Bad Gateway</b>.",
              "state": {"b1": "UP", "b2": "LB thinks UP", "b3": "UP"}, "cls": {"b2": "dead"},
              "arrows": [{"from": "lb", "to": "b2", "label": "refused", "fail": True}, {"from": "lb", "to": "c", "label": "502", "fail": True}]})
    S.append({"note": "Requests 25 and 28 hit b2 again: 3 failures in the 331 ms before the next health check.",
              "state": {"b1": "UP", "b2": "LB thinks UP", "b3": "UP"}, "cls": {"b2": "dead"},
              "arrows": [{"from": "lb", "to": "b2", "label": "#25, #28", "fail": True}]})
    S.append({"note": "At 1,022 ms the health check fails and b2 is marked <b>DOWN</b>. Round-robin now cycles over b1 and b3 only: 57 of 60 requests succeeded.",
              "state": {"b1": "UP", "b2": "DOWN", "b3": "UP"}, "cls": {"b2": "down"},
              "arrows": [{"from": "lb", "to": "b1", "label": "#29"}, {"from": "lb", "to": "b3", "label": "#30"}]})
    S.append({"note": "Second run, with retry on: the first request that fails marks b2 DOWN on the spot (a <em>passive</em> check) and is resent to the next backend. 60 of 60 succeeded. Only safe for GET.",
              "state": {"b1": "UP", "b2": "DOWN", "b3": "UP"}, "cls": {"b2": "down"},
              "arrows": [{"from": "lb", "to": "b2", "label": "refused", "fail": True}, {"from": "lb", "to": "b3", "label": "retry"}, {"from": "lb", "to": "c", "label": "200"}]})
    return {"name": "Load balancer", "type": "boxes", "h": 240, "nodes": nodes, "steps": S}

def repl_mode():
    nodes = [{"id": "w", "label": "writer", "x": 70, "y": 50}, {"id": "L", "label": "leader", "x": 300, "y": 50},
             {"id": "F", "label": "follower", "x": 300, "y": 170}, {"id": "r", "label": "reader", "x": 70, "y": 170}]
    S = [
        {"note": "A write goes to the leader: the row and its change-log entry in one transaction. The leader is now at version 1.",
         "state": {"L": "v1", "F": "v0"}, "arrows": [{"from": "w", "to": "L", "label": "write Dune"}]},
        {"note": "A reader asks the follower straight away. The change hasn't arrived: <b>not found</b>.",
         "state": {"L": "v1", "F": "v0"}, "cls": {"F": "stale"},
         "arrows": [{"from": "r", "to": "F", "label": "book 1?"}, {"from": "F", "to": "r", "label": "not found", "fail": True}]},
        {"note": "300 ms later the replication stream applies change 1 on the follower.",
         "state": {"L": "v1", "F": "v1"}, "arrows": [{"from": "L", "to": "F", "label": "change 1"}]},
        {"note": "Now the follower answers <b>Dune</b>. It was right all along, just late: that's <em>eventual</em> consistency.",
         "state": {"L": "v1", "F": "v1"}, "arrows": [{"from": "r", "to": "F", "label": "book 1?"}, {"from": "F", "to": "r", "label": "Dune"}]},
        {"note": "Read-your-writes: the writer writes Emma (version 2) and remembers the version. The follower is only at 1, so the read goes to the leader.",
         "state": {"L": "v2", "F": "v1"}, "cls": {"F": "stale"},
         "arrows": [{"from": "w", "to": "L", "label": "need v2: read"}, {"from": "L", "to": "w", "label": "Emma"}]},
        {"note": "A burst of 20 writes: the leader jumps to v22 while the follower sits at v2, 20 versions behind, for about 300 ms.",
         "state": {"L": "v22", "F": "v2, behind 20"}, "cls": {"F": "stale"}, "arrows": [{"from": "w", "to": "L", "label": "20 writes"}]},
        {"note": "At +327 ms the follower has caught up: v22. Replication lag is a window, not a bug, and every read from a replica must live with it.",
         "state": {"L": "v22", "F": "v22"}, "arrows": [{"from": "L", "to": "F", "label": "changes 3-22"}]},
    ]
    return {"name": "Replication", "type": "boxes", "h": 220, "nodes": nodes, "steps": S}

def h64(s):
    return int.from_bytes(hashlib.md5(s.encode()).digest()[:8], "big", signed=True)

def angle(h):
    return round((h + 2**63) / 2**64 * 360, 2)

def ring_mode():
    keys = [f"user:{i}" for i in range(16)]
    steps = []
    prev = None
    for nodes in (["n1", "n2", "n3", "n4"], ["n1", "n2", "n3", "n4", "n5"]):
        ring = sorted((h64(f"{n}#{i}"), n) for n in nodes for i in range(3))
        pos = [p for p, _ in ring]
        owner = {k: ring[bisect.bisect_left(pos, h64(k)) % len(ring)][1] for k in keys}
        moved = sum(1 for k in keys if prev and prev[k] != owner[k])
        note = ("Four nodes, 3 virtual nodes each: 12 coloured ticks on the ring. Each of the 16 keys belongs to the first tick clockwise from it."
                if not prev else
                f"Add n5 (3 more ticks). Only the keys just before an n5 tick change owner: {moved} of 16, every one of them to n5. The rest stay put.")
        steps.append({"note": note, "points": [{"a": angle(p), "n": n} for p, n in ring],
                      "keys": [{"a": angle(h64(k)), "n": owner[k], "moved": bool(prev and prev[k] != owner[k])} for k in keys]})
        prev = owner
    steps.append({"note": "With only 3 ticks per node the shares are lopsided (n5 grabbed 7 of 16 here). Sharding.kt uses 100 per node: then the load evens out to 22–27% each, and adding a node moves about a fifth of the keys.",
                  "points": steps[-1]["points"], "keys": steps[-1]["keys"]})
    return {"name": "Hash ring", "type": "ring", "nodes": ["n1", "n2", "n3", "n4", "n5"], "steps": steps}

def queue_mode():
    nodes = [{"id": "p", "label": "producer", "x": 60, "y": 120}, {"id": "q", "label": "queue", "x": 220, "y": 120},
             {"id": "w1", "label": "w1", "x": 430, "y": 40}, {"id": "w2", "label": "w2", "x": 430, "y": 120},
             {"id": "w3", "label": "w3", "x": 430, "y": 200}]
    S = [
        {"note": "Six email jobs are pushed. They wait in the queue.", "state": {"q": "6 ready"}, "arrows": [{"from": "p", "to": "q", "label": "6 jobs"}]},
        {"note": "Three workers take one each. A received message isn't deleted: it's <em>in flight</em> for 300 ms (the visibility timeout).",
         "state": {"q": "3 ready · 3 in flight", "w1": "#1", "w2": "#2", "w3": "#3"},
         "arrows": [{"from": "q", "to": "w1", "label": "#1"}, {"from": "q", "to": "w2", "label": "#2"}, {"from": "q", "to": "w3", "label": "#3"}]},
        {"note": "w2 sends email #2, then crashes before it can ack.", "state": {"q": "#2 in flight", "w2": "sent #2, no ack"}, "cls": {"w2": "dead"}},
        {"note": "w1 and w3 send and ack the rest: #1, #3, #4, #5, #6. Five acks; #2 is still in flight.",
         "state": {"q": "#2 in flight", "w1": "acked 4", "w3": "acked 1", "w2": "crashed"}, "cls": {"w2": "dead"},
         "arrows": [{"from": "w1", "to": "q", "label": "ack"}, {"from": "w3", "to": "q", "label": "ack"}]},
        {"note": "At about 330 ms #2's visibility timeout expires. The sweeper puts it back as attempt 2.",
         "state": {"q": "#2 ready (attempt 2)", "w2": "crashed"}, "cls": {"w2": "dead", "q": "hot"}},
        {"note": "w1 receives #2 and sends it again: <b>7 emails for 6 jobs</b>. At-least-once delivery means duplicates happen.",
         "state": {"q": "empty", "w1": "sent #2 again", "w2": "crashed"}, "cls": {"w2": "dead", "w1": "hot"},
         "arrows": [{"from": "q", "to": "w1", "label": "#2 again"}]},
        {"note": "The idempotent consumer remembers which jobs are done. On redelivery it sees the id, skips the email and just acks: 6 emails for 6 jobs.",
         "state": {"q": "empty", "w1": "skips #2", "w2": "crashed"}, "cls": {"w2": "dead"},
         "arrows": [{"from": "q", "to": "w1", "label": "#2 again"}, {"from": "w1", "to": "q", "label": "ack, skip"}]},
    ]
    return {"name": "Queue", "type": "boxes", "h": 240, "nodes": nodes, "steps": S}

def spec():
    return {"modes": [lb_mode(), repl_mode(), ring_mode(), queue_mode()]}

if __name__ == "__main__":
    print(json.dumps(spec(), indent=1)[:400])
