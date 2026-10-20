// 4 threads, 10 accounts, 100,000 random transfers. Run with v1, v2 or v3.
import java.lang.management.ManagementFactory
import java.util.Random
import java.util.concurrent.atomic.LongAdder
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

const val ACCOUNTS = 10
const val THREADS = 4
const val TRANSFERS = 100_000

class Account(val id: Int) {
    var balance = 1_000L
}

val accounts = List(ACCOUNTS) { Account(it) }
val done = LongAdder()
val bankLock = ReentrantLock()
@Volatile var running = true                            // tells the monitor thread to stop

fun move(from: Account, to: Account, amount: Long) {
    if (from.balance >= amount) {                       // no overdrafts
        from.balance -= amount
        to.balance += amount
    }
}

// v1: lock "from" then "to": two threads can take the same pair in opposite order
fun transferV1(from: Account, to: Account, amount: Long) =
    synchronized(from) { synchronized(to) { move(from, to, amount) } }

// v2: always lock the lower id first
fun transferV2(from: Account, to: Account, amount: Long) {
    val (first, second) = if (from.id < to.id) from to to else to to from
    synchronized(first) { synchronized(second) { move(from, to, amount) } }
}

// v3: one lock for the whole bank
fun transferV3(from: Account, to: Account, amount: Long) = bankLock.withLock { move(from, to, amount) }

fun main(args: Array<String>) {
    val version = args.firstOrNull() ?: "v1"

    thread(isDaemon = true) {                           // the monitor
        while (running) {
            Thread.sleep(500)
            if (running) println("  monitor: ${done.sum()} transfers done")
        }
    }

    val start = System.nanoTime()
    val workers = List(THREADS) { t ->
        val rnd = Random(42L + t)                       // fixed seed per thread
        thread(isDaemon = true, name = "worker-$t") {
            repeat(TRANSFERS / THREADS) {
                val from = accounts[rnd.nextInt(ACCOUNTS)]
                val to = accounts[rnd.nextInt(ACCOUNTS)]
                if (from !== to) {
                    val amount = 1L + rnd.nextInt(100)
                    when (version) {
                        "v1" -> transferV1(from, to, amount)
                        "v2" -> transferV2(from, to, amount)
                        else -> transferV3(from, to, amount)
                    }
                }
                done.increment()
            }
        }
    }

    val deadline = System.currentTimeMillis() + 2_000   // watchdog: 2 s for everything
    workers.forEach { it.join(maxOf(1, deadline - System.currentTimeMillis())) }
    running = false

    val mx = ManagementFactory.getThreadMXBean()
    val stuck = mx.findDeadlockedThreads()
    if (stuck != null) {
        println("$version: DEADLOCK after ${done.sum()} transfers")
        for (info in mx.getThreadInfo(stuck)) {
            println("  ${info.threadName} waits for ${info.lockName} held by ${info.lockOwnerName}")
        }
        return
    }
    val ms = (System.nanoTime() - start) / 1_000_000
    println("$version: ${done.sum()} transfers in $ms ms, total ${accounts.sumOf { it.balance }}" +
            " (expected ${1_000L * ACCOUNTS})")
}
