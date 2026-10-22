package books

import java.sql.Connection

data class Book(val id: Long, val title: String, val year: Int)

class BookCatalog(private val db: Connection) {
    fun all(): List<Book> =
        db.prepareStatement("SELECT id, title, year FROM books ORDER BY id").use { st ->
            st.executeQuery().use { rs ->
                buildList { while (rs.next()) add(Book(rs.getLong(1), rs.getString(2), rs.getInt(3))) }
            }
        }
}
