// What runs when a process is asked to stop (SIGTERM, SIGINT) or forced to (SIGKILL)?
fun main(args: Array<String>) {
    val seconds = args.firstOrNull()?.toInt() ?: 30
    Runtime.getRuntime().addShutdownHook(Thread {
        println("hook: saving state before exit")
    })
    println("pid ${ProcessHandle.current().pid()}: working for $seconds s")
    try {
        repeat(seconds * 5) { Thread.sleep(200) }
        println("main: finished normally")
    } finally {
        println("main: finally block")
    }
}
