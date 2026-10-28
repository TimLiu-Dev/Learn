// Spring Framework 7.0 JdbcClient, following "Unified JDBC Query/Update Operations". Not run here.
@Repository
class JdbcBookStore {
    private final JdbcClient jdbc;

    JdbcBookStore(JdbcClient jdbc) { this.jdbc = jdbc; }

    Optional<Book> find(long id) {
        return jdbc.sql("""
                SELECT b.id, b.title, a.name AS author, b.year
                FROM books b JOIN authors a ON a.id = b.author_id WHERE b.id = ?""")
            .param(id)
            .query(Book.class)                  // maps columns to the record by name
            .optional();
    }
}
