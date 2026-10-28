package cache

import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals

/** A fake slow catalog (L38): counts reads, takes 50 ms each. */
class SlowFakeCatalog : BookCatalog {
    val books = ConcurrentHashMap(mapOf(1 to Book(1, "Dune"), 2 to Book(2, "Emma")))
    val reads = AtomicInteger()
    override fun find(id: Int): Book? { reads.incrementAndGet(); Thread.sleep(50); return books[id] }
    override fun save(book: Book) { books[book.id] = book }
}

class CachedCatalogTest {
    private val db = SlowFakeCatalog()
    private val catalog = CachedCatalog(db, LruCache(10, Duration.ofMinutes(1)))

    @Test fun `second read is served from the cache`() {
        catalog.find(1); catalog.find(1)
        assertEquals(1, db.reads.get())
    }

    @Test fun `concurrent misses for one key hit the database once`() {
        val gate = CountDownLatch(1)
        Executors.newVirtualThreadPerTaskExecutor().use { pool ->
            repeat(50) { pool.submit { gate.await(); catalog.find(2) } }
            gate.countDown()
        }
        assertEquals(1, db.reads.get())
    }

    @Test fun `save invalidates so the next read sees the new value`() {
        catalog.find(1)
        catalog.save(Book(1, "Dune Messiah"))
        assertEquals("Dune Messiah", catalog.find(1)?.title)
        assertEquals(2, db.reads.get())
    }

    @Test fun `missing books are not cached`() {
        repeat(3) { catalog.find(99) }
        assertEquals(3, db.reads.get())
    }
}
