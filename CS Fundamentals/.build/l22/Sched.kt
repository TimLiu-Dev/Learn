import java.lang.management.ManagementFactory
import java.util.concurrent.locks.LockSupport

fun main() {
    // 1. More busy threads than cores. None of them ever sleeps or yields.
    val cores = Runtime.getRuntime().availableProcessors()
    val n = cores * 2
    val seconds = 3
    val mx = ManagementFactory.getThreadMXBean()
    val cpuMs = LongArray(n)
    val deadline = System.nanoTime() + seconds * 1_000_000_000L
    val spinners = List(n) { i ->
        Thread {
            var x = 0L
            while (System.nanoTime() < deadline) x++                // pure spinning
            cpuMs[i] = mx.currentThreadCpuTime / 1_000_000          // CPU time this thread actually got
            if (x == 42L) println("unlikely")                       // keep the loop from being removed
        }
    }
    spinners.forEach { it.start() }
    spinners.forEach { it.join() }
    println("$n spinning threads on $cores cores for $seconds s of wall time")
    println("CPU time per thread: min ${cpuMs.min()} ms, max ${cpuMs.max()} ms, average ${cpuMs.average().toLong()} ms")

    // 2. Hand a turn back and forth between two threads: each handoff blocks one and wakes the other.
    val rounds = 100_000
    var turn = 0                                                    // 0 = main's turn, 1 = partner's
    val lock = Object()
    val partner = Thread {
        repeat(rounds) {
            synchronized(lock) {
                while (turn != 1) (lock as Object).wait()
                turn = 0
                (lock as Object).notify()
            }
        }
    }
    partner.start()
    val t = System.nanoTime()
    repeat(rounds) {
        synchronized(lock) {
            turn = 1
            (lock as Object).notify()
            while (turn != 0) (lock as Object).wait()
        }
    }
    val ns = (System.nanoTime() - t).toDouble() / (2 * rounds)
    partner.join()
    println("one thread-to-thread handoff: %.1f µs".format(ns / 1000))
}
