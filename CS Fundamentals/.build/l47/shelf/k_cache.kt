// Excerpt of L41 Catalog.kt, lines 53–83 (tested there; not a standalone file)
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
