import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

// The classic cache-aside race, forced into one exact interleaving with latches (L26):
// reader misses, reads the old value, the writer updates + invalidates, then the
// reader puts the OLD value into the cache. Then a fix: versioned values.
data class Row(val title: String, val version: Long)

fun main() {
    println("plain cache-aside:  cache = ${run(versioned = false)}   database = Emma (2nd ed.)")
    println("versioned cache:    cache = ${run(versioned = true)}   database = Emma (2nd ed.)")
}

// keep whichever row is newer: an old read can never overwrite a newer one
val newer = { old: Row, nu: Row -> if (nu.version > old.version) nu else old }

fun run(versioned: Boolean): String {
    val db = ConcurrentHashMap(mapOf(2 to Row("Emma", 1)))
    val cache = ConcurrentHashMap<Int, Row>()
    val readerHasOldValue = CountDownLatch(1)
    val writerDone = CountDownLatch(1)

    val reader = Thread.ofVirtual().start {
        val fromDb = db.getValue(2)                     // cache miss, so read the database: v1
        readerHasOldValue.countDown()
        writerDone.await()                              // ...the reader gets descheduled here...
        if (versioned) cache.merge(2, fromDb, newer)
        else cache[2] = fromDb                          // writes v1 back into the cache: stale!
    }
    val writer = Thread.ofVirtual().start {
        readerHasOldValue.await()
        val updated = Row("Emma (2nd ed.)", 2)
        db[2] = updated                                 // update the database...
        if (versioned) cache.merge(2, updated, newer)
        else cache.remove(2)                            // ...then invalidate the cache
        writerDone.countDown()
    }
    reader.join(); writer.join()
    return cache.getValue(2).title
}
