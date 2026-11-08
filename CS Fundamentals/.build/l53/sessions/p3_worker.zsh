## kotlinc Worker.kt -include-runtime -d worker.jar 2>/dev/null
java -jar worker.jar > worker.out &
sleep 1.1; kill $!; wait $!; echo "exit $?"
cat worker.out
java Worker.java > worker.out &
sleep 1.5; kill $!; wait $!; echo "exit $?"
cat worker.out
