import kotlin.concurrent.thread

fun main() {
    thread(isDaemon = true, name = "heartbeat") {
        while (true) {                   // never ends on its own
            println("  heartbeat")
            Thread.sleep(100)
        }
    }
    Thread.sleep(350)
    println("main ends; only a daemon thread is left, so the JVM exits")
}
