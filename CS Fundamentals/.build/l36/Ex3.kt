import java.sql.Connection
import java.sql.DriverManager

data class Hit(val title: String, val author: String, val year: Int?)

val SORTS = mapOf("title" to "b.title", "year" to "b.year", "author" to "a.name")

// Build the SQL from fixed pieces only; every user VALUE goes in as a ? parameter.
fun findBooks(c: Connection, authorContains: String? = null, minYear: Int? = null, sort: String = "title"): List<Hit> {
    val where = mutableListOf<String>()
    val params = mutableListOf<Any>()
    if (authorContains != null) { where += "a.name LIKE ?"; params += "%$authorContains%" }
    if (minYear != null) { where += "b.year >= ?"; params += minYear }
    val orderBy = SORTS[sort] ?: throw IllegalArgumentException("can't sort by '$sort'")
    val sql = "SELECT b.title, a.name, b.year FROM books b JOIN authors a ON a.id = b.author_id" +
              (if (where.isEmpty()) "" else " WHERE " + where.joinToString(" AND ")) +
              " ORDER BY $orderBy, b.id"
    return c.prepareStatement(sql).use { ps ->
        params.forEachIndexed { i, p -> ps.setObject(i + 1, p) }
        ps.executeQuery().use { rs ->
            buildList { while (rs.next()) add(Hit(rs.getString(1), rs.getString(2), rs.getInt(3).takeUnless { rs.wasNull() })) }
        }
    }
}

fun main() {
    DriverManager.getConnection("jdbc:sqlite:library.db").use { c ->
        println(findBooks(c, authorContains = "Chiang").map { it.title })
        println(findBooks(c, minYear = 2010, sort = "year").map { "${it.year} ${it.title}" })
        println(findBooks(c, authorContains = "Le Guin", minYear = 1970).map { it.title })
        println(findBooks(c, authorContains = "' OR '1'='1").size)
        println(runCatching { findBooks(c, sort = "price_cents; DROP TABLE books") }.exceptionOrNull()?.message)
    }
}
