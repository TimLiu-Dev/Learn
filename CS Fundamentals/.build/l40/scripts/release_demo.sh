#!/bin/bash
# Part "rel": trunk with a release toggle, annotated tags, a hotfix from a tag, pushing tags, changelog input.
# Run: bash release_demo.sh L40W > ../captures/release_out.txt 2>&1   (L40W only needs scripts/lib.sh)
set -e
L40W="$1"; source "$L40W/scripts/lib.sh"
export HOME="$L40W/home"; mkdir -p "$HOME"     # scratch HOME: the real ~/.gitconfig is never read or written
rm -rf "$L40W/repos/origin.git"; git init -q --bare -b main "$L40W/repos/origin.git"
newrepo "$L40W/repos/release"
cat > Greeter.kt <<'K'
fun greet(name: String) = "Hello, $name"
K
git add . && tick && git commit -q -m "feat: greet by name"
cat > Greeter.kt <<'K'
fun greet(name: String) = "Hello, ${name.trim()}"
K
tick && git commit -q -am "fix: trim spaces around the name"
printf '# Greeter\n' > README.md && git add . && tick && git commit -q -m "docs: add README"
echo "=== TAG"
tick
show git tag -a v1.0.0 -m '"Release 1.0.0"'
show git show --no-patch v1.0.0
show git tag mark
show git cat-file -t v1.0.0
show git cat-file -t mark
show git tag -d mark
echo "=== TRUNK"
cat > Flags.kt <<'K'
// Release toggle: on only where FLAG_FAREWELL=on (your machine, CI), off for users.
val farewellOn = System.getenv("FLAG_FAREWELL") == "on"
K
cat > Greeter.kt <<'K'
fun greet(name: String) = "Hello, ${name.trim()}"

fun farewell(name: String) = "Bye, ${name.trim()}"   // unfinished, hidden behind farewellOn
K
git add . && tick && git commit -q -m "feat: add farewell behind the farewell flag"
printf '# Greeter\n\nRun with `kotlinc Greeter.kt`.\n' > README.md && tick && git commit -q -am "docs: explain how to run"
show git log --oneline --decorate
echo "=== HOTFIX"
show git switch -c hotfix-1.0.1 v1.0.0
cat > Greeter.kt <<'K'
fun greet(name: String) = "Hello, ${name.trim().ifEmpty { "friend" }}"
K
tick
show 'git commit -q -am "fix: greet a blank name as friend"'
tick
show git tag -a v1.0.1 -m '"Release 1.0.1"'
show git switch main
tick
show git merge --no-edit hotfix-1.0.1 || true
show git diff --name-only --diff-filter=U
show cat Greeter.kt
cat > Greeter.kt <<'K'
fun greet(name: String) = "Hello, ${name.trim().ifEmpty { "friend" }}"

fun farewell(name: String) = "Bye, ${name.trim()}"   // unfinished, hidden behind farewellOn
K
show cat Greeter.kt
show git add Greeter.kt
tick
show env GIT_EDITOR=true git commit -q
show git branch -d hotfix-1.0.1
show git log --oneline --graph --decorate
show git describe
echo "=== PUSH"
git remote add origin ../origin.git      # a local bare repo stands in for GitHub
show git push -q origin main
show git push --tags
show git ls-remote --tags origin
echo "=== LOG"
show git log --oneline --no-merges v1.0.1..main
