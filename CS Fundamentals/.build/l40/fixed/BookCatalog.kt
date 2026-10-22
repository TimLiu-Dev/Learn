package books

import java.sql.Connection
import java.util.concurrent.ConcurrentHashMap

data class Book(val id: Long, val title: String, val year: Int)

data class BookPage(val items: List<Book>, val page: Int, val size: Int, val total: Int)

class BookCatalog(private val db: Connection) {
    private val cache = ConcurrentHashMap<Pair<Int, Int>, BookPage>()      // fix 5: thread-safe

    fun all(): List<Book> =
        db.prepareStatement("SELECT id, title, year FROM books ORDER BY id").use { st ->
            st.executeQuery().use { rs ->
                buildList { while (rs.next()) add(Book(rs.getLong(1), rs.getString(2), rs.getInt(3))) }
            }
        }

    /** Returns one page of books, cached for speed. Pages are 1-based. */
    fun page(page: Int, size: Int): BookPage {
        require(page >= 1 && size >= 1) { "page and size must be at least 1" }
        return cache.computeIfAbsent(page to size) { load(page, size) }    // fix 2: key has both params
    }

    // fix 3: no catch. A failed query throws, and computeIfAbsent stores nothing.
    private fun load(page: Int, size: Int): BookPage {
        val offset = (page - 1) * size                                     // fix 1: pages are 1-based
        val items = db.prepareStatement(
            "SELECT id, title, year FROM books ORDER BY id LIMIT ? OFFSET ?"
        ).use { st ->
            st.setInt(1, size)
            st.setInt(2, offset)
            st.executeQuery().use { rs ->
                buildList { while (rs.next()) add(Book(rs.getLong(1), rs.getString(2), rs.getInt(3))) }
            }
        }
        val total = db.prepareStatement("SELECT COUNT(*) FROM books").use { st ->   // fix 4: closed
            st.executeQuery().use { rs -> rs.next(); rs.getInt(1) }
        }
        return BookPage(items, page, size, total)
    }
}
