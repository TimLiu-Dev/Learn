#!/bin/bash
# Final exercise reference run: a pull request simulated locally, start to finish.
set -e
L40W="$1"; source "$L40W/scripts/lib.sh"
F="$L40W/final"
apply() { cp -R "$F/steps/$1/." .; }
newrepo "$L40W/repos/final"
cp -R "$F/start/." .
git add -A && tick && git commit -q -m "Books API with tests (L32)"
echo "=== START"
show git log --oneline
show "./test-all.sh || true"
echo "=== BRANCH"
show git switch -c refactor-books-api
echo "=== C1"; apply c1; show "./test-all.sh || true"; tick; show 'git commit -q -am "Extract duplicated book validation"'
echo "=== C2"; apply c2; show "./test-all.sh || true"; show git add -A; tick; show 'git commit -q -m "Move book storage behind BookRepository"'
echo "=== C3"; apply c3; show "./test-all.sh || true"; tick; show 'git commit -q -am "Inject BookRepository through the constructor"'
echo "=== C4"; apply c4; show "./test-all.sh || true"; show git add -A; tick; show 'git commit -q -m "Test validation without starting a server"'
echo "=== C5"; apply c5; show git add -A; tick; show 'git commit -q -m "Run the build on every push"'
show git log --oneline main..
echo "=== REVIEW"
show git diff --stat main...HEAD
show "git diff main...HEAD | grep -n DEBUG"
echo "=== FIXUP"
apply fixup
show "./test-all.sh || true"
C2=$(git log --format=%h --grep='^Move book storage' -1)
tick
show git commit -q -a --fixup $C2
show git log --oneline main..
show env GIT_SEQUENCE_EDITOR=cat git rebase -i --autosquash main
show git log --oneline main..
show "git log -p main.. | grep -c DEBUG || true"
echo "=== TEAMMATE"
git switch -q main
apply main_update; tick; git commit -q -am "Accept books printed from 1440"
git switch -q refactor-books-api
show git log --oneline --graph --all
echo "=== REBASE"
show git rebase main || true
show git status --short
show "sed -n '/private fun create/,/^    }/p' books_kt/app/src/main/kotlin/books/BookApi.kt"
echo "=== NAIVE"
cp "$F/steps/c1/books_kt/app/src/main/kotlin/books/BookApi.kt" books_kt/app/src/main/kotlin/books/BookApi.kt
cp "$F/steps/c1/books_java/app/src/main/java/books/BookApi.java" books_java/app/src/main/java/books/BookApi.java
show "grep -c '<<<<<<<' books_kt/app/src/main/kotlin/books/BookApi.kt books_java/app/src/main/java/books/BookApi.java || true"
show "./test-all.sh || true"
echo "=== RESOLVE"
apply resolve
show git diff -- books_kt/app/src/main/kotlin/books/Model.kt books_java/app/src/main/java/books/Model.java
show "./test-all.sh || true"
show git add -A
show env GIT_EDITOR=true git rebase --continue
show git log --oneline --graph --all
show "./test-all.sh || true"
echo "=== MERGE"
show git switch main
show git merge --ff-only refactor-books-api
show git log --oneline --graph
show git branch -d refactor-books-api
echo "=== CI"
rm -rf "$L40W/repos/final_clone"
git clone -q "$L40W/repos/final" "$L40W/repos/final_clone"
cd "$L40W/repos/final_clone"
show "(cd books_kt && ./gradlew build --console=plain | tail -3)"
show "(cd books_java && ./gradlew build --console=plain | tail -3)"
