data class Book(val isbn: String, val title: String)

interface BookCatalog {
    fun find(isbn: String): Book?
    fun all(): List<Book>
    fun size(): Int
}

class SlowCatalog : BookCatalog {                 // pretend each lookup is a network call
    private val books = listOf(Book("9780441172719", "Dune"), Book("9780141439587", "Emma")).associateBy { it.isbn }
    override fun find(isbn: String): Book? { Thread.sleep(50); return books[isbn] }
    override fun all() = books.values.toList()
    override fun size() = books.size
}

// `by inner` generates all() and size() forwarding: we write only what changes (L15)
class CachingCatalog(private val inner: BookCatalog) : BookCatalog by inner {
    private val cache = HashMap<String, Book?>()
    var misses = 0
        private set
    override fun find(isbn: String): Book? {
        if (isbn in cache) return cache[isbn]       // not getOrPut: it re-runs for null values
        misses++
        return inner.find(isbn).also { cache[isbn] = it }
    }
}

class LoggingCatalog(private val inner: BookCatalog) : BookCatalog by inner {
    override fun find(isbn: String): Book? { println("  find($isbn)"); return inner.find(isbn) }
}

fun timeFiveLookups(c: BookCatalog): Long {
    val start = System.nanoTime()
    for (isbn in listOf("9780441172719", "9780141439587", "9780441172719", "9780441172719", "9780000000000")) c.find(isbn)
    return (System.nanoTime() - start) / 1_000_000
}

fun main() {
    println("plain:  ${timeFiveLookups(SlowCatalog())} ms")
    val cached = CachingCatalog(SlowCatalog())
    val stack: BookCatalog = LoggingCatalog(cached)
    println("cached: ${timeFiveLookups(stack)} ms, misses=${cached.misses}")
    println("size still forwards: ${stack.size()}")
}
