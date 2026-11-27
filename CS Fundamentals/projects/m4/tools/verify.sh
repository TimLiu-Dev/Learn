#!/usr/bin/env bash
# Builds both reference solutions and every seeded bug in a temp folder, runs the checker
# against each, and prints the results. Usage: tools/verify.sh [work-dir]
set -euo pipefail
M="$(cd "$(dirname "$0")/.." && pwd)"
WORK="${1:-$(mktemp -d)}"
echo "work dir: $WORK"

build() {   # build <source-dir> <dest-dir> [overlay-dir]
    rm -rf "$2"; mkdir -p "$2"
    rsync -a --exclude build --exclude .gradle --exclude .kotlin "$1/" "$2/"
    if [ -n "${3:-}" ]; then cp -R "$3/app" "$2/"; fi
    (cd "$2" && ./gradlew -q installDist)
}

for sol in solution_java solution_kotlin; do
    echo "== $sol"
    build "$M/$sol" "$WORK/$sol"
    java "$M/check/Check.java" "$WORK/$sol/app/build/install/runner/bin/runner" | tail -1
done

for bug in "$M"/bugs/*/; do
    name="$(basename "$bug")"
    sol="$(sed -n 's/^Applies to: \([a-z_]*\)\/.*/\1/p' "$bug/BUG.txt")"
    echo "== bug $name (on $sol)"
    build "$M/$sol" "$WORK/bug_$name" "$bug"
    java "$M/check/Check.java" "$WORK/bug_$name/app/build/install/runner/bin/runner" | grep -A4 '^FAIL' | head -12 || true
done
