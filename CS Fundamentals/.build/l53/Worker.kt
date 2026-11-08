import java.util.concurrent.atomic.AtomicBoolean

// Graceful shutdown: on SIGTERM, finish the current item, then stop
val running = AtomicBoolean(true)            // the hook runs on another thread (L26–L27)

fun main() {
    val mainThread = Thread.currentThread()
    Runtime.getRuntime().addShutdownHook(Thread {
        println("hook: stop requested")
        running.set(false)
        mainThread.join()                    // wait for main to finish its item
        println("hook: main is done, exiting")
    })
    var item = 0
    while (running.get() && item < 100) {
        item++
        println("item $item: start")
        Thread.sleep(300)                    // pretend to work
        println("item $item: done")
    }
    println("main: stopped cleanly after $item items")
}
