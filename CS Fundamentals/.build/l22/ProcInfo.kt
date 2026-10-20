fun main() {
    val me = ProcessHandle.current()
    println("my pid:     ${me.pid()}")
    println("parent pid: ${me.parent().map { it.pid() }.orElse(-1)}")
    println("command:    ${me.info().command().orElse("?")}")

    // One process, many threads: the JVM starts its own helpers.
    val names = Thread.getAllStackTraces().keys.map { it.name }.sorted()
    println("${names.size} threads: $names")

    // Start a child process and read what it prints.
    val child = ProcessBuilder("echo", "hello from a child").start()
    val text = child.inputStream.bufferedReader().use { it.readText() }.trim()
    println("child ${child.pid()} said '$text', exit ${child.waitFor()}")

    val failing = ProcessBuilder("sh", "-c", "exit 3").start()
    println("sh -c 'exit 3' gave exit ${failing.waitFor()}")
}
