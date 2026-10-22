import java.sql.Connection
import java.sql.DriverManager

// Five slow queries: print each plan and its average time.
// Usage: java -cp makeitfast.jar:sqlite-jdbc.jar MakeItFastKt big.db
data class Query(val name: String, val sql: String, val params: List<Any>)

val QUERIES = listOf(
    Query("Q1 member history", """
        SELECT l.id, b.title, l.loaned FROM loans l JOIN books b ON b.id = l.book_id
        WHERE l.member_id = ? ORDER BY l.loaned DESC""", listOf(42)),
    Query("Q2 long overdue", """
        SELECT id, member_id, loaned FROM loans
        WHERE returned IS NULL AND loaned < '2024-06-01'""", listOf()),
    Query("Q3 cheap 2015 books", """
        SELECT title, price_cents FROM books
        WHERE year = ? AND price_cents < 800 ORDER BY price_cents""", listOf(2015)),
    Query("Q4 login by email", """
        SELECT id, name FROM members WHERE lower(email) = lower(?)""", listOf("M42@Example.org")),
    Query("Q5 top books in Jan", """
        SELECT book_id, count(*) AS n FROM loans
        WHERE loaned BETWEEN '2026-01-01' AND '2026-01-31'
        GROUP BY book_id ORDER BY n DESC, book_id LIMIT 5""", listOf()),
)

fun plan(c: Connection, q: Query): String =
    c.prepareStatement("EXPLAIN QUERY PLAN ${q.sql}").use { ps ->
        q.params.forEachIndexed { i, p -> ps.setObject(i + 1, p) }
        ps.executeQuery().use { rs -> buildList { while (rs.next()) add(rs.getString("detail")) } }
    }.joinToString(" | ")

// Run until ~300 ms have passed (at least 5 runs); return (rows, µs per run).
fun time(c: Connection, q: Query): Pair<Int, Double> {
    var rows = 0; var runs = 0
    c.prepareStatement(q.sql).use { ps ->
        q.params.forEachIndexed { i, p -> ps.setObject(i + 1, p) }
        repeat(3) { ps.executeQuery().use { rs -> while (rs.next()) { } } }   // warm-up
        val start = System.nanoTime()
        while (runs < 5 || System.nanoTime() - start < 300_000_000) {
            rows = 0
            ps.executeQuery().use { rs -> while (rs.next()) rows++ }
            runs++
        }
        return rows to (System.nanoTime() - start) / 1_000.0 / runs
    }
}

fun main(args: Array<String>) {
    DriverManager.getConnection("jdbc:sqlite:${args[0]}").use { c ->
        for (q in QUERIES) {
            val (rows, micros) = time(c, q)
            println("%-20s %5d rows %10.1f µs".format(q.name, rows, micros))
            println("    ${plan(c, q)}")
        }
    }
}
