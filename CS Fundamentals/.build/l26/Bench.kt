import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.LongAdder
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

interface Counter { fun increment(); fun get(): Long }

class Unsafe : Counter {
    private var n = 0L
    override fun increment() { n++ }
    override fun get() = n
}
class Synced : Counter {
    private var n = 0L
    @Synchronized override fun increment() { n++ }
    @Synchronized override fun get() = n
}
class Locked : Counter {
    private val lock = ReentrantLock(); private var n = 0L
    override fun increment() = lock.withLock { n++; Unit }
    override fun get() = lock.withLock { n }
}
class Atomic : Counter {
    private val n = AtomicInteger()
    override fun increment() { n.incrementAndGet() }
    override fun get() = n.get().toLong()
}
class Adder : Counter {
    private val n = LongAdder()
    override fun increment() = n.increment()
    override fun get() = n.sum()
}

fun measure(make: () -> Counter, threads: Int, perThread: Int): Pair<Long, Long> {
    val c = make()
    val start = System.nanoTime()
    List(threads) { thread { repeat(perThread) { c.increment() } } }.forEach { it.join() }
    return c.get() to (System.nanoTime() - start) / 1_000_000
}

fun main() {
    val kinds = listOf("unsafe" to ::Unsafe, "synchronized" to ::Synced, "ReentrantLock" to ::Locked,
                       "AtomicInteger" to ::Atomic, "LongAdder" to ::Adder)
    val perThread = 1_000_000
    for ((_, make) in kinds) measure(make, 4, perThread)          // warm-up round, not printed
    println("cores: ${Runtime.getRuntime().availableProcessors()}, $perThread increments per thread")
    for (threads in listOf(2, 4, 8)) {
        println("--- $threads threads, expected ${threads.toLong() * perThread}")
        for ((name, make) in kinds) {
            val (total, ms) = measure(make, threads, perThread)
            val ok = if (total == threads.toLong() * perThread) "correct" else "WRONG"
            println("%-14s %10d  %-7s %5d ms".format(name, total, ok, ms))
        }
    }
}
