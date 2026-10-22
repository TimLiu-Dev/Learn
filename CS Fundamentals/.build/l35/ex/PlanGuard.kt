import java.sql.Connection
import java.sql.DriverManager

// Fail fast if a query's plan scans a whole table or index.
// Use it in a test so a dropped index breaks the build, not production.
fun Connection.requireNoScan(sql: String) {
    val plan = createStatement().use { st ->
        st.executeQuery("EXPLAIN QUERY PLAN $sql").use { rs ->
            buildList { while (rs.next()) add(rs.getString("detail")) }
        }
    }
    val scans = plan.filter { it.startsWith("SCAN") }
    check(scans.isEmpty()) { "full scan in plan: $scans" }
    println("ok: $plan")
}

fun main(args: Array<String>) {
    val q = "SELECT title, year FROM books WHERE author_id = 7 AND year BETWEEN 1990 AND 2000"
    DriverManager.getConnection("jdbc:sqlite:${args[0]}").use { c ->
        try {
            c.requireNoScan(q)
        } catch (e: IllegalStateException) {
            println("caught: ${e.message}")
        }
        c.createStatement().use { it.execute("CREATE INDEX idx_books_author_year ON books(author_id, year)") }
        c.requireNoScan(q)
        c.createStatement().use { it.execute("DROP INDEX idx_books_author_year") }
    }
}
