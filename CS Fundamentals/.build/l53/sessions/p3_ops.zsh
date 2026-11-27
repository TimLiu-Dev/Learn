## rm -f app.log
"$(/usr/libexec/java_home)/bin/jwebserver" -b 127.0.0.1 -p 8080 > /dev/null 2>&1 &
## sleep 2
lsof -i :8080
lsof -nP -iTCP:8080 -sTCP:LISTEN
ps -o pid,command -p $(lsof -t -iTCP:8080 -sTCP:LISTEN)
kill $(lsof -t -iTCP:8080 -sTCP:LISTEN); sleep 1; lsof -i :8080; echo "exit $?"
top -l 2 -n 5 -o cpu -stats pid,command,cpu,mem | tail -6
df -h .
du -sh ~/katas/l53
du -sh * | sort -h | tail -3
(for i in 1 2 3; do echo "request $i" >> app.log; sleep 0.4; done) &
tail -f app.log & sleep 2; kill $!
