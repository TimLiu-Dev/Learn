package shelf.load

import java.nio.file.Files
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import shelf.store.Db
import shelf.store.SqliteBookStore
import shelf.store.query

// Part 1 measurements: SQLite write contention, and connection-per-call vs a pool.
fun main() {
    println("Busy timeout: 200 writers add a book at the same instant")
    for (timeout in listOf(0, 3000)) {
        val file = Files.createTempFile("shelf-busy", ".db")
        Db(file, busyTimeoutMs = timeout).use { db ->
            db.createSchema()
            val pragma = db.read { c -> c.query("PRAGMA busy_timeout") { it.getInt(1) }.single() }
            val busy = AtomicInteger()
            val ok = AtomicInteger()
            together(200) { i ->
                try { SqliteBookStore(db).add("Book $i", "Author ${i % 10}", null); ok.incrementAndGet() }
                catch (e: Exception) { if ("SQLITE_BUSY" in e.toString()) busy.incrementAndGet() else throw e }
            }
            println("  busy_timeout=$pragma ms: $ok added, $busy failed with SQLITE_BUSY")
        }
        Files.deleteIfExists(file)
    }

    println("Connections: 64 virtual threads x 500 book lookups each")
    val file = Files.createTempFile("shelf-pool", ".db")
    Db(file).use { db -> db.createSchema(); val s = SqliteBookStore(db); repeat(200) { s.add("Book $it", "A", null) } }
    for (poolSize in listOf(0, 4, 8, 16, 0, 4, 8, 16)) {          // twice: the first pass warms up the JIT
        Db(file, poolSize = poolSize).use { db ->
            val store = SqliteBookStore(db)
            val t0 = System.nanoTime()
            together(64) { i -> repeat(500) { store.find((i * 500L + it) % 200 + 1) } }
            val ms = (System.nanoTime() - t0) / 1_000_000
            val label = if (poolSize == 0) "new connection per call" else "pool of $poolSize"
            println("  %-24s %5d ms  (%,d lookups/s)".format(label, ms, 32_000L * 1000 / ms))
        }
    }
    Files.deleteIfExists(file)
}

private fun together(n: Int, work: (Int) -> Unit) {
    val start = CountDownLatch(1)
    Executors.newVirtualThreadPerTaskExecutor().use { pool ->
        val futures = (0 until n).map { i -> pool.submit { start.await(); work(i) } }
        start.countDown()
        futures.forEach { it.get() }
    }
}
