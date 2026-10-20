import kotlin.concurrent.thread

fun main() {
    val task = Runnable { println("task runs on: ${Thread.currentThread().name}") }

    Thread(task, "worker").run()     // just a method call: runs on main
    val t = Thread(task, "worker")
    t.start()                        // the JVM starts a new thread, which calls run()
    t.join()                         // main waits here until worker finishes

    val k = thread(name = "kotlin-worker") {     // created AND started
        println("block runs on: ${Thread.currentThread().name}")
    }
    k.join()
    println("main done on: ${Thread.currentThread().name}")
}
