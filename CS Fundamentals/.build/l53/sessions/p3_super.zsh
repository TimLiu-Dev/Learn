## kotlinc Hook.kt -include-runtime -d hook.jar 2>/dev/null
kotlinc Supervisor.kt -include-runtime -d supervisor.jar 2>/dev/null && java -jar supervisor.jar
java Supervisor.java
