import java.sql.Connection
import java.sql.DriverManager

// Time one query before and after adding an index.
// Usage: java -cp indextiming.jar:sqlite-jdbc.jar IndexTimingKt big.db
const val SQL = "SELECT id, book_id, loaned FROM loans WHERE member_id = ?"

fun timeQueries(conn: Connection, runs: Int): Double {
    var rows = 0
    val start = System.nanoTime()
    conn.prepareStatement(SQL).use { ps ->
        for (i in 0 until runs) {
            ps.setInt(1, (i * 97) % 20_000 + 1)      // a different member each time
            ps.executeQuery().use { rs -> while (rs.next()) rows++ }
        }
    }
    val micros = (System.nanoTime() - start) / 1_000.0 / runs
    check(rows == runs * 10) { "expected 10 loans per member, got $rows" }
    return micros
}

fun main(args: Array<String>) {
    DriverManager.getConnection("jdbc:sqlite:${args[0]}").use { conn ->
        conn.createStatement().use { it.execute("DROP INDEX IF EXISTS idx_loans_member") }
        timeQueries(conn, 20)                                   // warm-up
        println("no index:   %8.1f µs per query".format(timeQueries(conn, 200)))

        val t0 = System.nanoTime()
        conn.createStatement().use { it.execute("CREATE INDEX idx_loans_member ON loans(member_id)") }
        println("CREATE INDEX took %.0f ms".format((System.nanoTime() - t0) / 1e6))

        timeQueries(conn, 200)                                  // warm-up
        println("with index: %8.1f µs per query".format(timeQueries(conn, 2_000)))
        conn.createStatement().use { it.execute("DROP INDEX idx_loans_member") }  // leave db as found
    }
}
