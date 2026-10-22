import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.sql.Types

data class Book(val id: Int, val title: String, val year: Int?)

// Read every row with map, then return a List. The ResultSet is still open while we read it.
fun <T> ResultSet.toList(map: (ResultSet) -> T): List<T> = buildList { while (next()) add(map(this@toList)) }

// getInt returns 0 for SQL NULL; wasNull() tells you which it really was.
fun ResultSet.intOrNull(column: String): Int? = getInt(column).takeUnless { wasNull() }

fun main() {
    DriverManager.getConnection("jdbc:sqlite:library.db").use { c ->
        // 1. A query with parameters, mapped to a data class.
        val sql = "SELECT b.id, b.title, b.year FROM books b JOIN authors a ON a.id = b.author_id " +
                  "WHERE a.name = ? ORDER BY b.year"
        val books = c.prepareStatement(sql).use { ps ->
            ps.setString(1, "Anonymous")
            ps.executeQuery().use { rs -> rs.toList { Book(it.getInt("id"), it.getString("title"), it.intOrNull("year")) } }
        }
        books.forEach(::println)

        c.autoCommit = false                                  // everything below gets rolled back
        // 2. INSERT and read back the generated key; setNull for an unknown value.
        val newId = c.prepareStatement("INSERT INTO members (name, email, joined) VALUES (?, ?, ?)",
                                       Statement.RETURN_GENERATED_KEYS).use { ps ->
            ps.setString(1, "Frances Allen"); ps.setString(2, "frances@example.com"); ps.setNull(3, Types.VARCHAR)
            println("rows inserted: ${ps.executeUpdate()}")
            ps.generatedKeys.use { keys -> keys.next(); keys.getInt(1) }
        }
        println("new member id: $newId")

        // 3. executeUpdate returns how many rows changed.
        val returned = c.prepareStatement("UPDATE loans SET returned = ? WHERE returned IS NULL AND member_id = ?").use { ps ->
            ps.setString(1, "2026-10-21"); ps.setInt(2, 1); ps.executeUpdate()
        }
        println("loans returned for member 1: $returned")

        // 4. Streaming: a Sequence reads rows on demand, but only while the ResultSet is open.
        c.createStatement().use { st ->
            st.executeQuery("SELECT title FROM books ORDER BY price_cents DESC, title").use { rs ->
                val titles = generateSequence { if (rs.next()) rs.getString(1) else null }
                println("priciest: " + titles.take(3).joinToString())
            }
        }
        c.rollback()
        c.autoCommit = true
        println("members after rollback: " + c.createStatement().use { st ->
            st.executeQuery("SELECT COUNT(*) FROM members").use { it.next(); it.getInt(1) } })
    }
}
