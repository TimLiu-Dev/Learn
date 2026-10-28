import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.ConcurrentHashMap

// Cache-aside, read-through, write-through, negative caching and TTL staleness,
// all in front of the same slow SQLite table.

// The "database": a SQLite file, 5 ms extra per query so the cache matters
class BookDb(file: Path) : AutoCloseable {
    private val c: Connection = DriverManager.getConnection("jdbc:sqlite:$file")
    var queries = 0
    init {
        c.createStatement().use { s ->
            s.execute("CREATE TABLE IF NOT EXISTS books(id INTEGER PRIMARY KEY, title TEXT NOT NULL)")
            s.execute("INSERT OR REPLACE INTO books VALUES (1,'Dune'),(2,'Emma'),(3,'Ulysses'),(4,'Beloved')")
        }
    }
    @Synchronized fun find(id: Int): String? {
        queries++
        Thread.sleep(5)
        return c.prepareStatement("SELECT title FROM books WHERE id = ?").use { q ->
            q.setInt(1, id)
            q.executeQuery().use { rs -> if (rs.next()) rs.getString(1) else null }
        }
    }
    @Synchronized fun save(id: Int, title: String) {
        c.prepareStatement("INSERT OR REPLACE INTO books VALUES (?, ?)").use { u ->
            u.setInt(1, id); u.setString(2, title); u.executeUpdate()
        }
    }
    override fun close() = c.close()
}

// A tiny TTL cache. `value == null` inside an Entry means "known missing".
class Entry(val value: String?, val expiresAt: Long)
class TtlCache(ttlMillis: Long) {
    private val map = ConcurrentHashMap<Int, Entry>()
    private val ttlNanos = ttlMillis * 1_000_000
    var hits = 0; var misses = 0
    fun get(id: Int): Entry? {
        val e = map[id]
        if (e != null && e.expiresAt > System.nanoTime()) { hits++; return e }
        misses++; return null
    }
    fun put(id: Int, v: String?) { map[id] = Entry(v, System.nanoTime() + ttlNanos) }
    fun invalidate(id: Int) { map.remove(id) }
}

// Read-through: the cache itself knows how to load. Callers never see the DB.
class ReadThrough(val cache: TtlCache, private val loader: (Int) -> String?) {
    fun get(id: Int): String? {
        val e = cache.get(id)
        if (e != null) return e.value        // a hit, even when the cached answer is "missing"
        return loader(id).also { cache.put(id, it) }
    }
}

val READS = intArrayOf(1, 2, 1, 1, 3, 2, 1, 4, 1, 2)

fun main() {
    val file = Files.createTempFile("l41-patterns", ".db")
    BookDb(file).use { db ->
        // 0. No cache: every read goes to the database
        var t = System.nanoTime()
        for (id in READS) db.find(id)
        println("%-15s reads %d, db reads %d, %d ms".format("no cache", READS.size, db.queries, (System.nanoTime() - t) / 1_000_000))

        // 1. Cache-aside: the application checks the cache, then the DB, then fills the cache
        db.queries = 0
        val cache = TtlCache(60_000)
        t = System.nanoTime()
        for (id in READS) {
            if (cache.get(id) == null) cache.put(id, db.find(id))
        }
        report("cache-aside", t, cache, db)
        // ...and on a write: update the DB, then invalidate (not update) the cached copy
        db.save(2, "Emma (2nd ed.)")
        cache.invalidate(2)
        val after = cache.get(2)?.value ?: db.find(2)
        println("  after write + invalidate, id 2 = $after")

        // 2. Read-through: same counts, but the loading logic moved into the cache
        db.queries = 0
        val rt = ReadThrough(TtlCache(60_000), db::find)
        t = System.nanoTime()
        for (id in READS) rt.get(id)
        report("read-through", t, rt.cache, db)

        // 3. Write-through: every write goes to the DB and the cache together
        db.queries = 0
        val wt = TtlCache(60_000)
        db.save(5, "Middlemarch"); wt.put(5, "Middlemarch")
        println("write-through   read after write: ${wt.get(5)?.value}, db reads ${db.queries}")

        // 4. Negative caching: remember "not found" too
        db.queries = 0
        repeat(5) { db.find(99) }
        println("no negative cache: 5 lookups of id 99 -> ${db.queries} db reads")
        db.queries = 0
        val neg = ReadThrough(TtlCache(60_000), db::find)
        repeat(5) { neg.get(99) }
        println("negative cache:    5 lookups of id 99 -> ${db.queries} db read")
        db.save(99, "Persuasion")                           // the book arrives...
        println("  after insert, cache still says: ${neg.get(99) ?: "not found"}")

        // 5. TTL staleness: someone updates the DB behind the cache's back
        val ttl = ReadThrough(TtlCache(300), db::find)
        ttl.get(3)
        val changed = System.nanoTime()
        db.save(3, "Ulysses (annotated)")
        var seen: String?
        do { Thread.sleep(50); seen = ttl.get(3) } while (seen == "Ulysses")
        println("TTL 300 ms: stale for ${(System.nanoTime() - changed) / 1_000_000} ms, then saw '$seen'")
    }
    Files.delete(file)
}

fun report(name: String, start: Long, c: TtlCache, db: BookDb) =
    println("%-15s reads %d, hits %d, misses %d, db reads %d, %d ms"
        .format(name, READS.size, c.hits, c.misses, db.queries, (System.nanoTime() - start) / 1_000_000))
