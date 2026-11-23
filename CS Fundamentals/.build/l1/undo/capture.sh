#!/bin/bash
# v7: real git captures for L1 Part 4 "Undo" and "Back it up". Runs in a throwaway HOME.
# Usage: bash capture.sh SCRATCH_DIR > transcript.txt
set -e
S="$1"; rm -rf "$S"; mkdir -p "$S/home/katas/l01"
export HOME="$S/home" GIT_CONFIG_GLOBAL="$S/home/.gitconfig" GIT_CONFIG_NOSYSTEM=1
git config --global user.name "Your Name"; git config --global user.email you@example.com
git config --global init.defaultBranch main
cd "$S/home/katas"
printf 'fun main() {\n    println("Hello, Kotlin!")\n}\n' > l01/Hello.kt
printf 'public class Hello {\n    public static void main(String[] args) {\n        System.out.println("Hello, Java!");\n    }\n}\n' > l01/Hello.java
printf '*.class\n*.jar\nout/\nbuild/\n.gradle/\n.kotlin/\n.idea/\n' > .gitignore
git init -q; git add .; git commit -q -m "L1: placement check solutions"
r(){ echo "\$ $*"; eval "$@"; }
echo "=== A restore"
sed -i '' 's/Hello, Kotlin!/Helo, Kotln!!/' l01/Hello.kt
r git status; r git restore l01/Hello.kt; r git status
echo "=== B restore --staged"
sed -i '' 's/Hello, Kotlin!/Hello again, Kotlin!/' l01/Hello.kt; printf 'my bank PIN: 1234\n' > notes.txt
r git add .; r git status; r git restore --staged notes.txt; r git status
echo "=== C amend"
r git commit -m '"Chnage greeting"'; r git log --oneline
sed -i '' 's/Hello, Java!/Hello again, Java!/' l01/Hello.java
r git add l01/Hello.java; r git commit --amend -m '"Change greeting in both languages"'; r git log --oneline
echo "=== D remote (a local bare repo stands in for GitHub)"
git init -q --bare ../katas-backup.git
r git remote add origin ../katas-backup.git; r git remote -v
r git branch -M main; r git push -u origin main 2>&1
rm notes.txt
r git status
echo "=== E amend after push"
r git commit --amend -q -m '"Change greetings"'; r git status; r git push 2>&1 || true
