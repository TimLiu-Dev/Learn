import java.sql.DriverManager

data class Book(val id: Int, val title: String, val year: Int?)

fun main() {
    DriverManager.getConnection("jdbc:sqlite:library.db").use { conn ->
        val sql = "SELECT id, title, year FROM books WHERE author_id = ? ORDER BY id"
        conn.prepareStatement(sql).use { st ->
            st.setInt(1, 7)                                  // fills the ? (never concatenate: L33)
            st.executeQuery().use { rs ->
                while (rs.next()) {
                    val raw = rs.getInt("year")              // SQL NULL comes back as 0
                    val year = raw.takeUnless { rs.wasNull() }
                    println("${Book(rs.getInt("id"), rs.getString("title"), year)}  getInt=$raw")
                }
            }
        }
    }
}
