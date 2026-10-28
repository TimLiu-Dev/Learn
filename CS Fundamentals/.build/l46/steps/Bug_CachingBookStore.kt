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
    private val loading = ConcurrentHashMap<Long, CompletableFuture<Book?>>()

    // Request coalescing (L41): 50 concurrent misses for one id become one store call.
    override fun find(id: Long): Book? {
        byId.get(id)?.let { return it }
        val mine = CompletableFuture<Book?>()
        loading.putIfAbsent(id, mine)?.let { return it.join() }    // someone else is loading it
        try {
            return store.find(id).also { book -> if (book != null) byId.put(id, book); mine.complete(book) }
        } catch (e: Exception) {
            mine.completeExceptionally(e)
            throw e
        } finally {
            loading.remove(id, mine)
        }
    }

    override fun list(limit: Int, offset: Int, author: String?): List<Book> =
        pages.getOrPut("$limit/$offset/$author") { store.list(limit, offset, author) }

    override fun count(author: String?): Int = counts.getOrPut("$author") { store.count(author) }

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
