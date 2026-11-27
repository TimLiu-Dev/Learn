#!/usr/bin/env bash
# Proves the checker catches every seeded bug: for each bugs/<name>/, copy the
# solution it applies to into a temp folder, overlay the bug's files, build,
# and run the checker. Every bug must produce at least one FAIL.
#   bash tools/check_bugs.sh            (from projects/m1)
set -euo pipefail
M1="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
status=0
for bug in "$M1"/bugs/*/; do
  name="$(basename "$bug")"
  solution="$(grep -o 'solution_[a-z]*' "$bug/README.md" | head -1)"
  work="$TMP/$name"
  mkdir -p "$work"
  (cd "$M1/$solution" && tar --exclude build --exclude .gradle --exclude .kotlin -cf - .) | (cd "$work" && tar -xf -)
  (cd "$bug" && find src -type f | while read -r f; do cp "$f" "$work/$f"; done)
  (cd "$work" && ./gradlew -q installDist --console=plain >/dev/null)
  out="$(java "$M1/check/Check.java" "$work/build/install/budget/bin/budget" || true)"
  fails="$(grep -c '^FAIL' <<<"$out" || true)"
  echo "== $name ($solution): $fails FAIL"
  grep -A2 '^FAIL' <<<"$out" | grep -E '^FAIL|problem:' | head -4 || true
  grep -m1 -A0 'hint:' <<<"$out" || true
  [ "$fails" -gt 0 ] || status=1
done
exit $status
