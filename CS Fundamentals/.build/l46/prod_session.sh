#!/bin/bash
# Run Shelf v2 like production: an installed distribution, configured by environment variables.
# usage: prod_session.sh <project dir>   (prints a curl session; the server's own log goes to $LOG)
set -u
cd "$1"
./gradlew -q installDist
DB=$(mktemp -d)/shelf.db; LOG=$(mktemp)
SHELF_PORT=18046 SHELF_DB=$DB SHELF_RATE_BURST=5 SHELF_RATE_PER_S=1 SHELF_DRAIN_S=5 \
  app/build/install/app/bin/app > "$LOG" 2>&1 &
PID=$!
for i in $(seq 50); do curl -s -o /dev/null http://127.0.0.1:18046/health && break; sleep 0.1; done
H='X-Client-Id: ada'
echo '$ curl -s localhost:18046/health'; curl -s http://127.0.0.1:18046/health; echo
echo '$ curl -s -H "X-Client-Id: ada" -d '"'"'{"title":"Dune","author":"Frank Herbert","year":1965}'"'"' localhost:18046/books'
curl -s -H "$H" -d '{"title":"Dune","author":"Frank Herbert","year":1965}' http://127.0.0.1:18046/books; echo
echo '$ curl -si -H "X-Client-Id: ada" -H "traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01" localhost:18046/books/1'
curl -si -H "$H" -H 'traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01' http://127.0.0.1:18046/books/1 | grep -v '^Date'; echo
echo '$ for i in 1 2 3 4 5; do curl -s -o /dev/null -w "%{http_code} " -H "X-Client-Id: ada" localhost:18046/books/1; done'
for i in 1 2 3 4 5; do curl -s -o /dev/null -w "%{http_code} " -H "$H" http://127.0.0.1:18046/books/1; done; echo
echo '$ curl -si -H "X-Client-Id: ada" localhost:18046/books/1'
curl -si -H "$H" http://127.0.0.1:18046/books/1 | grep -v '^Date'; echo
echo '$ curl -s localhost:18046/metrics'; curl -s http://127.0.0.1:18046/metrics
echo "\$ kill $PID   # SIGTERM, like Ctrl-C or a container stop"
kill "$PID"; wait "$PID" 2>/dev/null
echo "--- server log ($LOG) ---"
cat "$LOG"
lsof -iTCP:18046 -sTCP:LISTEN -n -P >/dev/null && echo "STILL LISTENING" || echo "(port 18046 free)"
rm -rf "$(dirname "$DB")" "$LOG"
