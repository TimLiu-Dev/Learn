package cache

import java.nio.file.Path
import java.sql.DriverManager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

data class Book(val id: Int, val title: String)

interface BookCatalog {
    fun find(id: Int): Book?
    fun save(book: Book)
}

/** The slow source of truth: SQLite, plus an injected delay per read. */
class SqliteCatalog(private val file: Path, private val delayMillis: Long) : BookCatalog {
    val queries = AtomicLong()

    // One connection per call keeps the example simple; a real app uses a pool (L36).
    private fun <T> withDb(block: (java.sql.Connection) -> T): T =
        DriverManager.getConnection("jdbc:sqlite:$file").use(block)

    fun seed(count: Int) = withDb { c ->
        c.createStatement().use { it.execute("CREATE TABLE IF NOT EXISTS books(id INTEGER PRIMARY KEY, title TEXT NOT NULL)") }
        c.autoCommit = false
        c.prepareStatement("INSERT OR REPLACE INTO books VALUES (?, ?)").use { ins ->
            for (id in 1..count) { ins.setInt(1, id); ins.setString(2, "Book $id"); ins.addBatch() }
            ins.executeBatch()
        }
        c.commit()
    }

    override fun find(id: Int): Book? {
        queries.incrementAndGet()
        Thread.sleep(delayMillis)
        return withDb { c ->
            c.prepareStatement("SELECT title FROM books WHERE id = ?").use { q ->
                q.setInt(1, id)
                q.executeQuery().use { rs -> if (rs.next()) Book(id, rs.getString(1)) else null }
            }
        }
    }

    override fun save(book: Book) = withDb { c ->
        c.prepareStatement("INSERT OR REPLACE INTO books VALUES (?, ?)").use { u ->
            u.setInt(1, book.id); u.setString(2, book.title); u.executeUpdate()
        }
        Unit
    }
}

/** Cache-aside in front of any catalog, with request coalescing for concurrent misses. */
class CachedCatalog(private val db: BookCatalog, private val cache: LruCache<Int, Book>) : BookCatalog {
    private val inFlight = ConcurrentHashMap<Int, CompletableFuture<Book?>>()
    val coalesced = AtomicLong()

    override fun find(id: Int): Book? {
        cache.get(id)?.let { return it }
        val mine = CompletableFuture<Book?>()
        val running = inFlight.putIfAbsent(id, mine)
        if (running != null) {                           // someone is already loading this id
            coalesced.incrementAndGet()
            return running.join()
        }
        try {
            val book = db.find(id)
            if (book != null) cache.put(id, book)
            mine.complete(book)
            return book
        } catch (e: Exception) {
            mine.completeExceptionally(e)
            throw e
        } finally {
            inFlight.remove(id, mine)
        }
    }

    override fun save(book: Book) {
        db.save(book)                                    // write the source of truth first...
        cache.remove(book.id)                            // ...then invalidate the cached copy
    }

    fun stats() = cache.stats()
}
