#!/bin/bash
# Part 1: reading diffs. Builds a throwaway repo and prints a transcript.
set -e
L40W="$1"; source "$L40W/scripts/lib.sh"
newrepo "$L40W/repos/diffdemo"
mkdir kt java
cp "$L40W/diff_base/BookCatalog.kt" "$L40W/diff_base/BookCatalogTest.kt" kt/
cp "$L40W/diff_base/BookCatalog.java" "$L40W/diff_base/BookCatalogTest.java" java/
git add . && tick && git commit -q -m "Add BookCatalog with all()"
git switch -q -c ai-pagination
cp "$L40W/gr_kotlin/app/src/main/kotlin/books/BookCatalog.kt" "$L40W/gr_kotlin/app/src/test/kotlin/books/BookCatalogTest.kt" kt/
cp "$L40W/gr_java/app/src/main/java/books/BookCatalog.java" "$L40W/gr_java/app/src/test/java/books/BookCatalogTest.java" java/
git add . && tick && git commit -q -m "Add pagination and caching to GET /books"
echo "=== STAT"
show git diff --stat main...ai-pagination
echo "=== KT"
show git diff main...ai-pagination -- kt/BookCatalog.kt
echo "=== KTTEST"
show git diff main...ai-pagination -- kt/BookCatalogTest.kt
echo "=== JAVA"
show git diff main...ai-pagination -- java/BookCatalog.java
echo "=== JAVATEST"
show git diff main...ai-pagination -- java/BookCatalogTest.java
echo "=== WORKING"
# a reviewer-style edit: reword the doc comment, and re-indent one line only
sed -i '' 's/Pages are 1-based./Pages start at 1./' kt/BookCatalog.kt
sed -i '' 's/^            val offset = page \* size$/        val offset = page * size/' kt/BookCatalog.kt
show git status --short
show git diff
show git diff -w
show git diff --word-diff
show git add kt/BookCatalog.kt
show git diff
show git diff --staged --stat
