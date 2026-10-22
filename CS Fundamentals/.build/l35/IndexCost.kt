import java.sql.Connection
import java.sql.DriverManager

// What indexes cost: slower inserts and more disk.
fun Connection.exec(sql: String) = createStatement().use { it.execute(sql) }

fun Connection.longOf(sql: String): Long =
    createStatement().use { st -> st.executeQuery(sql).use { it.next(); it.getLong(1) } }

fun insertMillis(conn: Connection, indexes: List<String>): Double {
    conn.exec("DROP TABLE IF EXISTS loans_copy")
    conn.exec("CREATE TABLE loans_copy (id INTEGER PRIMARY KEY, book_id INTEGER, member_id INTEGER, loaned TEXT, returned TEXT)")
    indexes.forEach { conn.exec("CREATE INDEX idx_copy_$it ON loans_copy($it)") }
    conn.autoCommit = false                       // one transaction for all 50,000 rows
    val start = System.nanoTime()
    conn.prepareStatement("INSERT INTO loans_copy VALUES (?, ?, ?, ?, NULL)").use { ps ->
        for (i in 1..50_000) {
            ps.setInt(1, i); ps.setInt(2, (i * 104729) % 50_000 + 1)
            ps.setInt(3, (i * 7907) % 20_000 + 1); ps.setString(4, "2026-%02d-%02d".format(i % 12 + 1, i % 28 + 1))
            ps.addBatch()
        }
        ps.executeBatch()
    }
    conn.commit()
    conn.autoCommit = true
    return (System.nanoTime() - start) / 1e6
}

fun main(args: Array<String>) {
    DriverManager.getConnection("jdbc:sqlite:${args[0]}").use { conn ->
        insertMillis(conn, emptyList())                        // warm-up
        println("insert 50k, 0 indexes: %5.0f ms".format(insertMillis(conn, emptyList())))
        println("insert 50k, 3 indexes: %5.0f ms".format(insertMillis(conn, listOf("book_id", "member_id", "loaned"))))
        conn.exec("DROP TABLE loans_copy")
        conn.exec("VACUUM")

        val pageSize = conn.longOf("PRAGMA page_size")
        val before = conn.longOf("PRAGMA page_count") * pageSize
        listOf("book_id", "member_id", "loaned").forEach { conn.exec("CREATE INDEX idx_loans_$it ON loans($it)") }
        val after = conn.longOf("PRAGMA page_count") * pageSize
        println("file before: %.1f MB, after 3 indexes on loans: %.1f MB".format(before / 1e6, after / 1e6))
        listOf("book_id", "member_id", "loaned").forEach { conn.exec("DROP INDEX idx_loans_$it") }
        conn.exec("VACUUM")                                    // leave db as found
    }
}
