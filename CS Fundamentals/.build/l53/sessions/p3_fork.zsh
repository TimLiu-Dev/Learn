## kotlinc Hook.kt -include-runtime -d hook.jar 2>/dev/null; rm -f fd3.txt
echo "shell pid $$"
bash -c 'echo "child pid $$, parent $PPID"'
bash -c 'echo "bash, pid $$"; exec ps -o pid,ppid,command -p $$'
exec 3> fd3.txt
bash -c 'echo "child $$ wrote this to fd 3" >&3'
exec 3>&-
cat fd3.txt
(sleep 1 & exec sleep 3) &
sleep 2; ps -A -o pid,ppid,stat,command | awk -v p=$! 'NR == 1 || $1 == p || $2 == p'
## wait
bash -c 'sleep 2 & echo "bash started sleep $!, now bash exits"'
sleep 0.5; ps -o pid,ppid,command -p $(pgrep -n -f 'sleep 2')
ps -o pid,command -p 1
java Spawn.java
cat entry_bad.sh
sh entry_bad.sh > bad.out &
sleep 1; ps -A -o pid,ppid,command | grep -E '[e]ntry_|[h]ook[.]jar'
kill -TERM $!; wait $!; echo "exit $?"
sleep 1; pgrep -fl 'hook.jar 30'; cat bad.out
## pkill -KILL -f 'hook.jar 30'; sleep 0.5
cat entry_good.sh
sh entry_good.sh > good.out &
sleep 1; ps -A -o pid,ppid,command | grep -E '[e]ntry_|[h]ook[.]jar'
kill -TERM $!; wait $!; echo "exit $?"; cat good.out
