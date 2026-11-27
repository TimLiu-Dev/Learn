#!/bin/bash
# v7: the "AI diff of a test file" transcript for Part 1's "Working with AI as a senior".
# Usage: bash scripts/ai_demo.sh WORKDIR AIDIR > captures/ai_out.txt   (AIDIR = .build/l40/ai)
set -euo pipefail
W="$1"; AI="$(cd "$2" && pwd)"
source "$(dirname "$0")/lib.sh"
mkdir -p "$W"; cd "$W"
newrepo fees
mkdir -p src/main/kotlin/fees src/test/kotlin/fees
cp "$AI/before/build.gradle.kts" .
cp "$AI/before/LateFee.kt" src/main/kotlin/fees/
cp "$AI/before/LateFeeTest.kt" src/test/kotlin/fees/
git add -A; tick; git commit -q -m "feat: late fees"
cp "$AI/after/build.gradle.kts" .
cp "$AI/after/LateFee.kt" src/main/kotlin/fees/
cp "$AI/after/LateFeeTest.kt" src/test/kotlin/fees/
echo "=== STAT"
show git diff --stat
echo "=== DIFF"
show git diff
