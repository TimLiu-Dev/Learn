import java.sql.Connection

data class Book(val id: Long, val title: String, val year: Int)

// An AI suggestion: tidy, confident, and calling a method ResultSet doesn't have.
fun all(db: Connection): List<Book> =
    db.prepareStatement("SELECT id, title, year FROM books ORDER BY id").use { st ->
        st.executeQuery().use { rs -> rs.mapRows { row -> Book(row.getLong(1), row.getString(2), row.getInt(3)) } }
    }
