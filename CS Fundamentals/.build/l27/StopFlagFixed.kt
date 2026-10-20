// The same program with one annotation added: @Volatile.
import kotlin.concurrent.thread

object Flags {
    @Volatile var stop = false             // @Volatile: every read sees the latest write
}

fun main() {
    val worker = thread(isDaemon = true) { // a stuck daemon can't keep the JVM alive
        var spins = 0L
        while (!Flags.stop) {
            spins++                        // busy loop, no synchronization inside
        }
        println("worker saw stop after $spins spins")
    }

    Thread.sleep(1000)                     // let the loop run hot enough to be JIT-compiled
    Flags.stop = true
    println("main set stop = true")

    worker.join(3000)                      // wait at most 3 s
    if (worker.isAlive) {
        println("still running after 3 s: the worker never saw the write")
    }
}
