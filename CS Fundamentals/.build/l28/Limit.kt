import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger

val dbPermits = Semaphore(10)                  // the "database" accepts 10 connections
val inFlight = AtomicInteger()
val maxSeen = AtomicInteger()

fun query(id: Int): Int {
    dbPermits.acquire()                        // virtual thread parks here if all 10 are taken
    try {
        val now = inFlight.incrementAndGet()
        maxSeen.accumulateAndGet(now) { a, b -> maxOf(a, b) }
        Thread.sleep(20)                       // the query itself
        return id
    } finally {
        inFlight.decrementAndGet()
        dbPermits.release()
    }
}

fun main() {
    val start = System.nanoTime()
    Executors.newVirtualThreadPerTaskExecutor().use { ex ->
        repeat(1_000) { id -> ex.submit { query(id) } }   // 1,000 virtual threads...
    }
    val ms = (System.nanoTime() - start) / 1_000_000
    println("1000 queries, at most ${maxSeen.get()} at once, $ms ms")   // ...but only 10 in the DB
}
