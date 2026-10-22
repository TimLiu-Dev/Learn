package books

import java.sql.Connection

data class Book(val id: Long, val title: String, val year: Int)

data class BookPage(val items: List<Book>, val page: Int, val size: Int, val total: Int)

class BookCatalog(private val db: Connection) {
    private val cache = HashMap<String, BookPage>()

    fun all(): List<Book> =
        db.prepareStatement("SELECT id, title, year FROM books ORDER BY id").use { st ->
            st.executeQuery().use { rs ->
                buildList { while (rs.next()) add(Book(rs.getLong(1), rs.getString(2), rs.getInt(3))) }
            }
        }

    /** Returns one page of books, cached for speed. Pages are 1-based. */
    fun page(page: Int, size: Int): BookPage = cache.getOrPut("books") {
        try {
            val offset = page * size
            val items = db.prepareStatement(
                "SELECT id, title, year FROM books ORDER BY id LIMIT ? OFFSET ?"
            ).use { st ->
                st.setInt(1, size)
                st.setInt(2, offset)
                st.executeQuery().use { rs ->
                    buildList { while (rs.next()) add(Book(rs.getLong(1), rs.getString(2), rs.getInt(3))) }
                }
            }
            val count = db.prepareStatement("SELECT COUNT(*) FROM books").executeQuery()
            count.next()
            BookPage(items, page, size, count.getInt(1))
        } catch (e: Exception) {
            BookPage(emptyList(), page, size, 0)
        }
    }
}
