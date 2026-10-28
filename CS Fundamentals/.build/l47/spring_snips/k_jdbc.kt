// Spring Framework 7.0 JdbcClient, following "Unified JDBC Query/Update Operations". Not run here.
@Repository
class JdbcBookStore(private val jdbc: JdbcClient) {
    fun find(id: Long): Book? = jdbc.sql("""
            SELECT b.id, b.title, a.name AS author, b.year
            FROM books b JOIN authors a ON a.id = b.author_id WHERE b.id = ?""")
        .param(id)
        .query(Book::class.java)            // maps columns to the data class by name
        .optional().orElse(null)
}
