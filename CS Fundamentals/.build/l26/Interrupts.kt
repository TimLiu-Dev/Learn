import kotlin.concurrent.thread

fun main() {
    val sleeper = thread(name = "sleeper") {
        try {
            Thread.sleep(10_000)
            println("woke up normally")
        } catch (e: InterruptedException) {
            println("sleeper: interrupted (${e.message}), cleaning up and stopping")
        }
    }
    Thread.sleep(100)
    sleeper.interrupt()                  // a polite request, not a kill
    sleeper.join()

    val crasher = thread(name = "crasher") { error("boom") }
    crasher.join()
    println("main is still alive; crasher.isAlive = ${crasher.isAlive}")

    val handled = Thread { throw IllegalStateException("bang") }
    handled.setUncaughtExceptionHandler { t, e -> println("handler: ${t.name} died with ${e.message}") }
    handled.start()
    handled.join()
}
