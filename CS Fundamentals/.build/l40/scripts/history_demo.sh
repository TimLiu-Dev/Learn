#!/bin/bash
# Part 2: rebase vs merge, conflicts, amend, fixup/autosquash, squash/reword, reflog, stash, cherry-pick.
set -e
L40W="$1"; source "$L40W/scripts/lib.sh"
newrepo "$L40W/repos/history"
cat > Greeting.kt <<'K'
fun greet(name: String) = "Hello, $name"

fun main() {
    println(greet("Ada"))
}
K
git add . && tick && git commit -q -m "Add greeting"
echo "# Greetings" > README.md && git add . && tick && git commit -q -m "Add README"
git switch -q -c feature
cat > Farewell.kt <<'K'
fun farewell(name: String) = "Bye, $name"
K
git add . && tick && git commit -q -m "Add farewell"
printf 'fun main2() {\n    println(farewell("Ada"))\n}\n' >> Farewell.kt && tick && git commit -q -am "Print farewell"
git switch -q main
printf '\nRun with `kotlinc Greeting.kt`.\n' >> README.md && tick && git commit -q -am "Explain how to run"
git branch -q feature-merge feature           # a copy, to compare merge with rebase
echo "=== BEFORE"
show git log --oneline --graph --all
echo "=== MERGE"
show git switch -q feature-merge
show git merge -q --no-edit main
show git log --oneline --graph feature-merge
echo "=== REBASE"
show git switch -q feature
show git rebase main
show git log --oneline --graph --all
echo "=== FFONLY"
show git switch -q main
show git merge --ff-only feature
show git log --oneline -4
echo "=== NOFF"
git reset -q --hard HEAD~2               # put main back where it was, to show --no-ff on the same branch
show git merge --no-ff --no-edit feature
show git log --oneline --graph -6
git reset -q --hard HEAD~1
git merge -q --ff-only feature
git branch -q -D feature-merge
echo "=== CONFLICT"
git switch -q -c shout
sed -i '' 's/Hello, \$name/HELLO, \$name!/' Greeting.kt && tick && git commit -q -am "Shout the greeting"
git switch -q main
sed -i '' 's/Hello, \$name/Hello there, \$name/' Greeting.kt && tick && git commit -q -am "Warmer greeting"
git switch -q shout
show git rebase main || true
show git status
show cat Greeting.kt
show git rebase --abort
show git log --oneline -1
show git rebase main >/dev/null 2>&1 || true
printf 'fun greet(name: String) = "HELLO THERE, $name!"\n\nfun main() {\n    println(greet("Ada"))\n}\n' > Greeting.kt
show git add Greeting.kt
show env GIT_EDITOR=true git rebase --continue
show git log --oneline --graph -3
git switch -q main && git merge -q --ff-only shout && git branch -q -d shout
