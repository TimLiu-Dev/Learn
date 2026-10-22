import java.nio.file.Files
import java.sql.Connection
import java.sql.DriverManager

const val ROWS = 10_000

fun freshDb(): Connection {
    val file = Files.createTempFile("batch", ".db").also { it.toFile().deleteOnExit() }
    return DriverManager.getConnection("jdbc:sqlite:$file").apply {
        createStatement().use { it.executeUpdate("CREATE TABLE loans (id INTEGER PRIMARY KEY, book_id INTEGER, member_id INTEGER, loaned TEXT)") }
    }
}

fun timed(label: String, work: (Connection) -> Unit) = freshDb().use { c ->
    val t0 = System.nanoTime()
    work(c)
    val ms = (System.nanoTime() - t0) / 1_000_000
    val n = c.createStatement().use { st -> st.executeQuery("SELECT COUNT(*) FROM loans").use { it.next(); it.getInt(1) } }
    println("%-30s %6d ms  (%d rows)".format(label, ms, n))
}

const val INSERT = "INSERT INTO loans (book_id, member_id, loaned) VALUES (?, ?, ?)"

fun main() {
    timed("one by one, autocommit") { c ->
        c.prepareStatement(INSERT).use { ps ->
            repeat(ROWS) { i -> ps.setInt(1, i % 20 + 1); ps.setInt(2, i % 8 + 1); ps.setString(3, "2026-10-21"); ps.executeUpdate() }
        }
    }
    timed("addBatch, autocommit") { c ->
        c.prepareStatement(INSERT).use { ps ->
            repeat(ROWS) { i -> ps.setInt(1, i % 20 + 1); ps.setInt(2, i % 8 + 1); ps.setString(3, "2026-10-21"); ps.addBatch() }
            ps.executeBatch()
        }
    }
    timed("one by one, one transaction") { c ->
        c.autoCommit = false
        c.prepareStatement(INSERT).use { ps ->
            repeat(ROWS) { i -> ps.setInt(1, i % 20 + 1); ps.setInt(2, i % 8 + 1); ps.setString(3, "2026-10-21"); ps.executeUpdate() }
        }
        c.commit()
    }
    timed("addBatch, one transaction") { c ->
        c.autoCommit = false
        c.prepareStatement(INSERT).use { ps ->
            repeat(ROWS) { i -> ps.setInt(1, i % 20 + 1); ps.setInt(2, i % 8 + 1); ps.setString(3, "2026-10-21"); ps.addBatch() }
            ps.executeBatch()
        }
        c.commit()
    }
}
