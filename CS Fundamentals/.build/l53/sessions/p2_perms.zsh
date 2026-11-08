## rm -rf notes.txt private only-me.txt secret.txt
umask
touch notes.txt && mkdir private
ls -ld notes.txt private
stat -f '%Sp %Lp %N' notes.txt private
chmod 600 notes.txt && stat -f '%Sp %Lp %N' notes.txt
chmod g+r,o+r notes.txt && stat -f '%Sp %Lp %N' notes.txt
(umask 077; touch only-me.txt) && stat -f '%Sp %Lp %N' only-me.txt
echo "top secret" > private/inside.txt
chmod 600 private
ls private
cat private/inside.txt
cd private
chmod 700 private && cat private/inside.txt
java Perms.java
stat -f '%Sp %Lp %N' secret.txt
kotlinc Perms.kt -include-runtime -d perms.jar 2>/dev/null && java -jar perms.jar
