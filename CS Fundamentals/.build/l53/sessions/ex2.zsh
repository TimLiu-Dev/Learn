## rm -rf ex2 && mkdir ex2 && cd ex2 && cp ../Perms.* ../GroupWrite.* .
(umask 027; touch a.txt; mkdir d) && stat -f '%Sp %Lp %N' a.txt d
touch shared.txt && chmod 664 shared.txt
chmod u-w a.txt && echo hi >> a.txt
kotlinc GroupWrite.kt Perms.kt -include-runtime -d gw.jar 2>/dev/null && java -cp gw.jar GroupWriteKt a.txt shared.txt
javac -d out GroupWrite.java Perms.java && java -cp out GroupWrite a.txt shared.txt
## cd .. && rm -rf ex2
