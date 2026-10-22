import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

var queries = 0                                           // every SELECT goes through query(), which counts

fun <T> Connection.query(sql: String, vararg args: Any, map: (ResultSet) -> T): List<T> {
    queries++
    return prepareStatement(sql).use { ps ->
        args.forEachIndexed { i, a -> ps.setObject(i + 1, a) }
        ps.executeQuery().use { rs -> buildList { while (rs.next()) add(map(rs)) } }
    }
}

data class Row(val title: String, val author: String)

fun nPlusOne(c: Connection): List<Row> {
    val books = c.query("SELECT title, author_id FROM books ORDER BY id") { it.getString(1) to it.getInt(2) }
    return books.map { (title, authorId) ->                // one more query PER book
        Row(title, c.query("SELECT name FROM authors WHERE id = ?", authorId) { it.getString(1) }.single())
    }
}

fun join(c: Connection): List<Row> =
    c.query("SELECT b.title, a.name FROM books b JOIN authors a ON a.id = b.author_id ORDER BY b.id") {
        Row(it.getString(1), it.getString(2))
    }

fun main() {
    DriverManager.getConnection("jdbc:sqlite:library.db").use { c ->
        for ((name, load) in listOf("N+1" to ::nPlusOne, "JOIN" to ::join)) {
            queries = 0
            val t0 = System.nanoTime()
            val rows = load(c)
            println("%-4s %2d rows, %2d queries, %5.2f ms   first: %s".format(
                name, rows.size, queries, (System.nanoTime() - t0) / 1e6, rows.first()))
        }
    }
}
