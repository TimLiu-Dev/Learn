import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class LockCounter {
    private val lock = ReentrantLock()
    private var count = 0
    fun increment() = lock.withLock { count++ }        // lock, run, unlock in finally
    fun get() = lock.withLock { count }
}

fun main() {
    val c = LockCounter()
    List(2) { thread { repeat(1_000_000) { c.increment() } } }.forEach { it.join() }
    println("ReentrantLock counter = ${c.get()}")

    val lock = ReentrantLock()
    val holder = thread {
        lock.withLock { println("holder: got the lock, working for 300 ms"); Thread.sleep(300) }
    }
    Thread.sleep(50)
    if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
        try { println("main: got it") } finally { lock.unlock() }
    } else {
        println("main: gave up after 100 ms instead of waiting forever")
    }
    holder.join()
}
