## kotlinc Hook.kt -include-runtime -d hook.jar 2>/dev/null
kill -l
java -jar hook.jar 1; echo "exit $?"
java -jar hook.jar 30 > hook.out &
sleep 1; pgrep -fl hook.jar
ps -o pid,ppid,stat,command -p $!
kill -TERM $!; wait $!; echo "exit $?"
cat hook.out
(sleep 1; pkill -INT -f 'hook.jar 30') & java -jar hook.jar 30; echo "exit $?"
java -jar hook.jar 30 > hook.out &
sleep 1; kill -KILL $!; wait $!; echo "exit $?"; cat hook.out
java Hook.java 30 > hook.out &
sleep 2; kill $!; wait $!; echo "exit $?"; cat hook.out
java -jar hook.jar 3 > hook.out &
sleep 1; kill -INT $!; wait $!; echo "exit $?"; cat hook.out
