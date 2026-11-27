#!/bin/bash
# Reproduces the L25 Part 1 compiler-plugin captures. Run from .build/l25:  bash plugins/capture.sh
set -euo pipefail
K=../tools/kotlinc; L=../libs; T=$(mktemp -d)
CP=$L/kotlinx-serialization-core-jvm-1.12.0.jar:$L/kotlinx-serialization-json-jvm-1.12.0.jar
$K/bin/kotlinc plugins/Plugins.kt -cp $CP \
  -Xplugin=$K/lib/kotlinx-serialization-compiler-plugin.jar \
  -Xplugin=$K/lib/allopen-compiler-plugin.jar -P plugin:org.jetbrains.kotlin.allopen:annotation=Service -d $T/with
java -cp $T/with:$CP:$K/lib/kotlin-stdlib.jar PluginsKt
(cd $T/with && javap Catalog.class Helper.class 'Book$Companion.class')
$K/bin/kotlinc plugins/Plugins.kt -cp $CP -d $T/without || true   # no plugins: expect 'unresolved reference'
rm -rf "$T"
