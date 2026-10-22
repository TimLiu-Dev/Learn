import java.sql.Connection
import java.sql.DriverManager

fun Connection.titles(sql: String, bind: String? = null): String = prepareStatement(sql).use { ps ->
    if (bind != null) ps.setString(1, bind)
    ps.executeQuery().use { rs -> buildList { while (rs.next()) add(rs.getString(1)) }.joinToString(" | ") }
}

// The only column names a user may sort by, mapped to real SQL. Anything else is rejected.
val SORTABLE = mapOf("title" to "title", "year" to "year", "price" to "price_cents")

fun safeOrderBy(sort: String, descending: Boolean): String {
    val column = SORTABLE[sort] ?: throw IllegalArgumentException("can't sort by '$sort'")
    return "ORDER BY $column ${if (descending) "DESC" else "ASC"}, id"
}

fun main() {
    DriverManager.getConnection("jdbc:sqlite:library.db").use { c ->
        val base = "SELECT title FROM books WHERE author_id = 5 "

        println("1. bound   ORDER BY ?  ('year'):  " + c.titles(base + "ORDER BY ?", "year"))
        println("2. glued   ORDER BY year:         " + c.titles(base + "ORDER BY " + "year"))

        // Blind injection: the attacker can't see members, but the sort ORDER answers a yes/no question.
        for (guess in listOf("a", "b")) {
            val evil = "CASE WHEN (SELECT substr(email, 1, 1) FROM members WHERE id = 1) = '$guess' THEN title ELSE year END"
            println("3. glued   ORDER BY <guess '$guess'>: " + c.titles(base + "ORDER BY " + evil))
        }
        val evil = "(SELECT email FROM members WHERE id = 1)"
        for (sort in listOf("year", "price", "email", evil)) {
            try { println("4. allowlist '$sort': " + c.titles(base + safeOrderBy(sort, descending = true))) }
            catch (e: IllegalArgumentException) { println("4. allowlist rejected: ${e.message}") }
        }
    }
}
