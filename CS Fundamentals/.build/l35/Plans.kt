import java.sql.Connection
import java.sql.DriverManager

// Print query plans from code: SQLite's EXPLAIN QUERY PLAN and H2's EXPLAIN.
// YEAR is a reserved word in standard SQL (and in H2), so it is quoted: "year".
fun sqlitePlan(conn: Connection, sql: String): List<String> =
    conn.createStatement().use { st ->
        st.executeQuery("EXPLAIN QUERY PLAN $sql").use { rs ->
            buildList { while (rs.next()) add(rs.getString("detail")) }
        }
    }

fun h2Plan(conn: Connection, sql: String): String =
    conn.createStatement().use { st ->
        st.executeQuery("EXPLAIN $sql").use { rs -> rs.next(); rs.getString(1) }
    }

fun main(args: Array<String>) {
    val q = "SELECT title FROM books WHERE author_id = 7 AND \"year\" > 2000"
    DriverManager.getConnection("jdbc:sqlite:${args[0]}").use { c ->
        println("SQLite, no index:   " + sqlitePlan(c, q))
        c.createStatement().use { it.execute("CREATE INDEX idx_books_author_year ON books(author_id, year)") }
        println("SQLite, with index: " + sqlitePlan(c, q))
        c.createStatement().use { it.execute("DROP INDEX idx_books_author_year") }
    }
    DriverManager.getConnection("jdbc:h2:mem:plans").use { c ->
        c.createStatement().use { st ->
            st.execute("CREATE TABLE books(id INT PRIMARY KEY, title VARCHAR(100), author_id INT, \"year\" INT)")
            st.execute("INSERT INTO books SELECT x, 'Book ' || x, MOD(x * 7919, 2000) + 1, 1950 + MOD(x * 31, 76) FROM SYSTEM_RANGE(1, 50000)")
        }
        println("H2, no index:\n" + h2Plan(c, q))
        c.createStatement().use { it.execute("CREATE INDEX idx_books_author_year ON books(author_id, \"year\")") }
        println("H2, with index:\n" + h2Plan(c, q))
    }
}
