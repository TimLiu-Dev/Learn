#!/bin/bash
# Part 2b: amend, fixup + autosquash, scripted squash/reword, reflog recovery, stash, cherry-pick.
set -e
L40W="$1"; source "$L40W/scripts/lib.sh"
newrepo "$L40W/repos/clean"
printf 'fun price(cents: Int) = cents\n' > Price.kt
git add . && tick && git commit -q -m "Add price"
git switch -q -c discount
echo "=== AMEND"
printf 'fun discount(cents: Int, percent: Int) = cents * percent / 100\n' > Discount.kt
git add . && tick
show 'git commit -q -m "Add discont"'
show git log --oneline -1
tick
show 'git commit -q --amend -m "Add discount"'
show git log --oneline -1
echo "=== FIXUP"
printf 'fun total(items: List<Int>) = items.sum()\n' > Total.kt && git add . && tick && git commit -q -m "Add total"
printf '// Prices are in cents.\n' > README.md && git add . && tick && git commit -q -m "Document units"
# review finds a bug in "Add discount": negative percent is accepted
printf 'fun discount(cents: Int, percent: Int): Int {\n    require(percent in 0..100) { "percent must be 0..100" }\n    return cents * percent / 100\n}\n' > Discount.kt
show git log --oneline main..
show git add Discount.kt
DISC=$(git log --format=%h --grep='^Add discount$' -1)
tick
show git commit --fixup $DISC
show git log --oneline main..
show env GIT_SEQUENCE_EDITOR=cat git rebase -i --autosquash main
show git log --oneline main..
show git show --stat --format=%s HEAD~2
echo "=== SQUASH"
show git log --oneline main..
show "GIT_SEQUENCE_EDITOR=\"sed -i '' -e '3s/^pick/squash/'\" GIT_EDITOR=true git rebase -q -i main"
show git log --oneline main..
show 'git log -1 --format=%B'
echo "=== REWORD"
show "GIT_SEQUENCE_EDITOR=\"sed -i '' -e '2s/^pick/reword/'\" GIT_EDITOR=\"sed -i '' -e '1s/.*/Add total and document cents/'\" git rebase -q -i main"
show git log --oneline main..
echo "=== REFLOG"
show git reset --hard HEAD~1
show git log --oneline main..
show git reflog -4
show 'git reset --hard HEAD@{1}'
show git log --oneline main..
echo "=== STASH"
printf 'fun tax(cents: Int) = cents / 10\n' > Tax.kt
printf 'fun price(cents: Int) = cents  // TODO rounding\n' > Price.kt
show git status --short
show 'git stash push --include-untracked -m "half-done tax"'
show git status --short
show git stash list
show git stash pop
show git status --short
git stash -q --include-untracked && git stash drop -q
echo "=== CHERRY"
git switch -q main
printf 'fun price(cents: Int) = maxOf(cents, 0)\n' > Price.kt && tick && git commit -q -am "Never return a negative price"
FIX=$(git rev-parse --short HEAD)
git switch -q discount
tick
show git cherry-pick $FIX
show git log --oneline -2
