## rm -rf fakebin; kotlinc Env.kt -include-runtime -d env.jar 2>/dev/null
## export PATH=/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin
echo $HOME
GREETING=hi
echo $GREETING
java Env.java
export GREETING=hi
java Env.java
unset GREETING
GREETING=once java Env.java
echo "after: [$GREETING]"
java -jar env.jar
echo $PATH | tr ':' '\n'
which java jq
which nosuchtool; echo "exit $?"
mkdir fakebin && printf '#!/bin/sh\necho "gotcha: this is not java"\n' > fakebin/java && chmod +x fakebin/java
PATH="$PWD/fakebin:$PATH" java -version
(PATH="$PWD/fakebin:$PATH"; which java; java -version)
java -version 2>&1 | head -1
