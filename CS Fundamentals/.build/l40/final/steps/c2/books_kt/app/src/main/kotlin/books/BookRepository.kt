package books

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/** Where books live. BookApi talks to this interface, never to a map (L38: depend on abstractions). */
interface BookRepository {
    fun add(title: String, year: Int): Book        // assigns the next id
    fun find(id: Long): Book?
    fun all(): List<Book>                          // sorted by id
    fun replace(book: Book): Boolean               // false if no book has that id
    fun delete(id: Long): Boolean                  // false if no book has that id
}

class InMemoryBookRepository : BookRepository {
    private val books = ConcurrentHashMap<Long, Book>()          // L27: safe for concurrent handlers
    private val nextId = AtomicLong(1)

    override fun add(title: String, year: Int): Book =
        Book(nextId.getAndIncrement(), title, year).also { books[it.id] = it }

    override fun find(id: Long): Book? = books[id]
    override fun all(): List<Book> = books.values.sortedBy { it.id }
    override fun replace(book: Book): Boolean = books.replace(book.id, book) != null
    override fun delete(id: Long): Boolean = books.remove(id) != null
}
