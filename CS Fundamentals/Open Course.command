#!/bin/bash
# Double-click to study: serves this folder at http://127.0.0.1:8000 and opens the roadmap.
# Lessons run Kotlin in the page through the online Kotlin Playground, which refuses pages opened
# straight from disk (file://), so the course has to be served over http. Close this window to stop.
cd "$(dirname "$0")" || exit 1
JWS="$(/usr/libexec/java_home 2>/dev/null)/bin/jwebserver"
[ -x "$JWS" ] || { echo "jwebserver not found: install a JDK (18 or newer) first."; read -r; exit 1; }
# Keep the same port when possible: saved progress (localStorage) belongs to one address.
for PORT in 8000 8001 8002 8003 8004 8005; do
  lsof -iTCP:"$PORT" -sTCP:LISTEN >/dev/null 2>&1 || break
done
[ "$PORT" = 8000 ] || echo "Port 8000 is busy, using $PORT. Progress saved at :8000 won't show at :$PORT."
(sleep 1; open "http://127.0.0.1:$PORT/reference/roadmap.html") &
exec "$JWS" -b 127.0.0.1 -p "$PORT" -d "$PWD"
