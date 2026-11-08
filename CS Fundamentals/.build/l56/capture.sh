#!/bin/bash
# Re-runs every L56 program and saves its output to out/ (fill.py pastes them). Run from .build/l56.
# Needs KOTLINC (see .build/rules/v5-rules.md). Gradle outputs (green_*, red_*, run_*) are captured by hand:
# see the final exercise in the lesson for how each red run was made.
set -u
K="${KOTLINC:-kotlinc}"
SQ=../libs/sqlite-jdbc-3.53.4.0.jar
NA=--enable-native-access=ALL-UNNAMED
d="$(mktemp -d)"
kt() {  # kt <main> <files...> : compile Kotlin files together and run the main class
  local main="$1"; shift
  "$K" -cp "$SQ" "$@" -include-runtime -d "$d/$main.jar" 2>&1 | grep -v '^$'
  java $NA -cp "$d/$main.jar:$SQ" "${main}Kt" ${ARGS:-} > "out/$main.kt.txt" 2>&1
}
jv() { java $NA -cp "$SQ" "$1.java" ${ARGS:-} > "out/$1.java.txt" 2>&1; }
mkdir -p out
kt LogDemo LogKV.kt LogDemo.kt;            jv LogDemo
kt AutoCompact LogKV.kt AutoCompact.kt;    jv AutoCompact
kt TornDemo LogKV.kt TornDemo.kt;          jv TornDemo
kt TornFix LogKV.kt TornFix.kt;            jv TornFix
ARGS=parent kt KillDemo LogKV.kt TornFix.kt KillDemo.kt; ARGS=parent jv KillDemo
kt FsyncCost LogKV.kt FsyncCost.kt;        jv FsyncCost
kt SyncKinds SyncKinds.kt;                 jv SyncKinds
kt BloomDemo Bloom.kt BloomDemo.kt;        jv BloomDemo
kt BloomSize Bloom.kt BloomSize.kt;        jv BloomSize
kt LsmDemo LogKV.kt Bloom.kt Lsm.kt LsmDemo.kt; jv LsmDemo
kt WalAmp LogKV.kt WalAmp.kt;              jv WalAmp
kt Friends Friends.kt;                     jv Friends
/usr/bin/sqlite3 :memory: < models.sql > out/models.txt 2>&1
/usr/bin/sqlite3 :memory: < prereqs.sql > out/prereqs.txt 2>&1
javac -d "$d/viz" LogKV.java Bloom.java Lsm.java viz/VizTrace.java && java -cp "$d/viz" VizTrace > viz/logviz.json
rm -rf "$d"
echo "outputs saved"
