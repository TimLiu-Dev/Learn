#!/bin/bash
# Re-runs every L50 program and saves its output to outs/<File>.<kt|java>.txt (fill.py pastes them).
# Run from .build/l50. Needs KOTLINC (see .build/rules/v5-rules.md).
set -u
K="${KOTLINC:-kotlinc}"
mkdir -p outs
for f in TwoPointers PairTiming Windows WindowTiming WindowMax MinWindowSum Subsets NoCopy Queens FirstPerms Parens \
         Trie CountingTrie Stress IntervalStress; do
  d="$(mktemp -d)"
  "$K" "$f.kt" -include-runtime -d "$d/$f.jar" 2>&1 | grep -v '^$'
  java -jar "$d/$f.jar" > "outs/$f.kt.txt" 2>&1
  java "$f.java" > "outs/$f.java.txt" 2>&1
  rm -rf "$d"
done
d="$(mktemp -d)"
"$K" Trie.kt TrieMemory.kt -include-runtime -d "$d/tm.jar" 2>&1 | grep -v '^$'
java -cp "$d/tm.jar" TrieMemoryKt > outs/TrieMemory.kt.txt 2>&1
java TrieMemory.java > outs/TrieMemory.java.txt 2>&1
rm -rf "$d"
echo "outputs saved"
