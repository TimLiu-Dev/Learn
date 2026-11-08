#!/usr/bin/env bash
# runner.sh — Manage the local Java 25 networking lab runner daemon
# Usage:
#   ./scripts/runner.sh start
#   ./scripts/runner.sh stop
#   ./scripts/runner.sh status
#   ./scripts/runner.sh test

set -euo pipefail

PORT=8765
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORKSPACE_DIR="$(dirname "$SCRIPT_DIR")"
JAVA_BIN="/opt/homebrew/opt/openjdk@25/bin/java"

if [ ! -x "$JAVA_BIN" ]; then
  JAVA_BIN="$(command -v java || true)"
fi

PID_FILE="/tmp/net-lab-runner.pid"

is_running() {
  if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p "$PID" > /dev/null 2>&1; then
      return 0
    fi
  fi
  # Fallback check on port
  if lsof -i :"$PORT" -sTCP:LISTEN > /dev/null 2>&1; then
    return 0
  fi
  return 1
}

start() {
  if is_running; then
    echo "LabRunner is already running on http://127.0.0.1:$PORT"
    return 0
  fi
  echo "Starting LabRunner on http://127.0.0.1:$PORT using $JAVA_BIN..."
  nohup "$JAVA_BIN" -cp "$WORKSPACE_DIR" scripts.LabRunner "$PORT" > /tmp/net-lab-runner.log 2>&1 &
  RUNNER_PID=$!
  echo "$RUNNER_PID" > "$PID_FILE"
  sleep 0.4
  if is_running; then
    echo "✅ LabRunner started successfully (PID: $RUNNER_PID)."
  else
    echo "❌ Failed to start LabRunner. Log output:"
    cat /tmp/net-lab-runner.log
    return 1
  fi
}

stop() {
  if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p "$PID" > /dev/null 2>&1; then
      kill "$PID" || true
      rm -f "$PID_FILE"
      echo "LabRunner (PID $PID) stopped."
      return 0
    fi
    rm -f "$PID_FILE"
  fi
  # If running without PID file
  EXISTING_PID=$(lsof -ti :"$PORT" -sTCP:LISTEN 2>/dev/null || true)
  if [ -n "$EXISTING_PID" ]; then
    kill "$EXISTING_PID" || true
    echo "LabRunner (PID $EXISTING_PID) stopped."
  else
    echo "LabRunner is not running."
  fi
}

status() {
  if is_running; then
    echo "🟢 LabRunner is running on http://127.0.0.1:$PORT"
    curl -s "http://127.0.0.1:$PORT/health" || echo ""
    echo ""
  else
    echo "⚪ LabRunner is NOT running."
  fi
}

test_runner() {
  start
  echo "Testing LabRunner health endpoint..."
  curl -s "http://127.0.0.1:$PORT/health"
  echo ""
  echo "Testing code execution endpoint..."
  PAYLOAD='{"source":"public class Lab { public static void main(String[] args) { System.out.println(\"PASS: Test 1\"); System.out.println(\"SUMMARY:1/1\"); } }"}'
  RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" -d "$PAYLOAD" "http://127.0.0.1:$PORT/run")
  echo "Response: $RESPONSE"
}

case "${1:-status}" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  status)
    status
    ;;
  restart)
    stop
    start
    ;;
  test)
    test_runner
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status|test}"
    exit 1
    ;;
esac
