import java.sql.Connection
import java.sql.DriverManager

var queries = 0

fun Connection.ints(sql: String, vararg args: Int): List<Pair<String, Int>> =
    prepareStatement(sql).use { st ->
        args.forEachIndexed { i, a -> st.setInt(i + 1, a) }
        queries++
        st.executeQuery().use { rs ->
            buildList { while (rs.next()) add(rs.getString(1) to rs.getInt(2)) }
        }
    }

fun main() {
    DriverManager.getConnection("jdbc:sqlite:library.db").use { conn ->
        // N+1: one query for the authors, then one more query per author
        val authors = conn.ints("SELECT name, id FROM authors ORDER BY id")
        val slow = authors.map { (name, id) ->
            name to conn.ints("SELECT 'n', count(*) FROM books WHERE author_id = ?", id).single().second
        }
        println("N+1 : $queries queries -> $slow")

        // One query: let the database join and count
        queries = 0
        val fast = conn.ints("""
            SELECT a.name, count(b.id) FROM authors a
            LEFT JOIN books b ON b.author_id = a.id
            GROUP BY a.id ORDER BY a.id""")
        println("JOIN: $queries query   -> $fast")
        println("same answer: ${slow == fast}")
    }
}
