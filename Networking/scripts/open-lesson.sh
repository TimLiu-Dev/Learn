#!/usr/bin/env bash
# Helper script to open any networking lesson or course home in the default browser on macOS.
# Usage:
#   ./scripts/open-lesson.sh        -> opens index.html (Course Home)
#   ./scripts/open-lesson.sh 1      -> opens Session 1
#   ./scripts/open-lesson.sh 15     -> opens Session 15

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORKSPACE_DIR="$(dirname "$SCRIPT_DIR")"

# Auto-start local Java 25 LabRunner daemon if not running
if [ -x "$SCRIPT_DIR/runner.sh" ]; then
  "$SCRIPT_DIR/runner.sh" start > /dev/null 2>&1 || true
fi

if [ $# -eq 0 ]; then
  echo "Opening Course Home & Master Plan..."
  open "$WORKSPACE_DIR/index.html"
  exit 0
fi

# Format number with 4 leading zeros
NUM=$(printf "%04d" "$1")
LESSON_FILE=$(find "$WORKSPACE_DIR/lessons" -name "${NUM}-*.html" | head -n 1)

if [ -n "$LESSON_FILE" ] && [ -f "$LESSON_FILE" ]; then
  echo "Opening Session $1: $(basename "$LESSON_FILE")..."
  open "$LESSON_FILE"
else
  echo "Error: Lesson session $1 (${NUM}) not found in lessons/." >&2
  exit 1
fi
