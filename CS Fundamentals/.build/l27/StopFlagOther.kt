// Two more ways to make the worker see the write: an AtomicBoolean, and a lock on both sides.
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

val atomicStop = AtomicBoolean(false)

object Locked {
    private var stop = false
    @Synchronized fun isStopped() = stop
    @Synchronized fun requestStop() { stop = true }
}

fun runWorker(name: String, stopped: () -> Boolean, stop: () -> Unit) {
    val worker = thread(isDaemon = true) {
        var spins = 0L
        while (!stopped()) spins++
        println("$name: worker stopped after $spins spins")
    }
    Thread.sleep(1000)
    stop()
    worker.join(3000)
    if (worker.isAlive) println("$name: still running after 3 s")
}

fun main() {
    runWorker("AtomicBoolean", { atomicStop.get() }, { atomicStop.set(true) })
    runWorker("synchronized ", Locked::isStopped, Locked::requestStop)
}
