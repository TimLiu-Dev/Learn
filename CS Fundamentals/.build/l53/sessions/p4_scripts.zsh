## rm -f "my notes.txt"
bash -c 'false | true; echo "exit $?"'
bash -c 'set -o pipefail; false | true; echo "exit $?"'
bash -c 'echo "hello, $NAME"; echo "still running"'
bash -c 'set -u; echo "hello, $NAME"; echo "still running"'; echo "exit $?"
bash -c 'cd /nonexistent; echo "still running in $PWD"'
bash -c 'set -e; cd /nonexistent; echo "still running in $PWD"'; echo "exit $?"
./quoting.sh
./args.sh "my notes.txt" two
bash --version | head -2
bash -c 'declare -A count; count[x]=1; echo "${count[x]}"'
zsh -c 'typeset -A count; count[x]=1; echo "${count[x]}"'
cat same.sh
./same.sh TopPaths
./same.sh SshPub demo/id_demo.pub
./same.sh; echo "exit $?"
./same.sh Nope; echo "exit $?"
