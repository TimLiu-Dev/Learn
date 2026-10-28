package shelf.cache

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import shelf.domain.Book
import shelf.store.BookStore

/**
 * Cache-aside in front of any BookStore (a decorator, L39): same interface, so nothing else changes.
 * Reads try the cache first; writes go to the store, then invalidate (L41).
 */
class CachingBookStore(
    private val store: BookStore,
    private val byId: LruCache<Long, Book>,
    private val pages: LruCache<String, List<Book>>,      // list() results, keyed by their arguments
    private val counts: LruCache<String, Int>,
) : BookStore {
    private val loading = ConcurrentHashMap<Any, CompletableFuture<Any?>>()

    override fun find(id: Long): Book? = load(id, byId) { store.find(id) }
    override fun list(limit: Int, offset: Int, author: String?): List<Book> =
        load("$limit/$offset/$author", pages) { store.list(limit, offset, author) }!!
    override fun count(author: String?): Int = load("count/$author", counts) { store.count(author) }!!

    /**
     * Request coalescing (L41): concurrent misses for one key become one store call.
     * The slow load runs OUTSIDE the cache's lock: a lock held during I/O makes every
     * other caller wait for it (Exercise 2 found exactly that bug in my first version).
     */
    @Suppress("UNCHECKED_CAST")
    private fun <K : Any, V : Any> load(key: K, cache: LruCache<K, V>, fetch: () -> V?): V? {
        cache.get(key)?.let { return it }
        val mine = CompletableFuture<Any?>()
        loading.putIfAbsent(key, mine)?.let { return it.join() as V? }   // someone else is loading it
        try {
            return fetch().also { v -> if (v != null) cache.put(key, v); mine.complete(v) }
        } catch (e: Exception) {
            mine.completeExceptionally(e)
            throw e
        } finally {
            loading.remove(key, mine)
        }
    }

    // Any write can change any page or count, so those are dropped wholesale; single books by id.
    override fun add(title: String, author: String, year: Int?): Book =
        store.add(title, author, year).also { dropLists() }

    override fun update(id: Long, title: String, author: String, year: Int?): Book? =
        store.update(id, title, author, year).also { byId.remove(id); dropLists() }

    override fun delete(id: Long): Boolean = store.delete(id).also { byId.remove(id); dropLists() }

    private fun dropLists() {
        pages.clear()
        counts.clear()
    }

    fun hitRate(): Double = byId.stats().hitRate
}
