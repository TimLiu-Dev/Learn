// Same lazy Sequence as LateSequence.kt, on H2 instead of SQLite: H2 follows the JDBC spec and throws.
import java.sql.DriverManager
fun main() {
    DriverManager.getConnection("jdbc:h2:mem:x").use { c ->
        c.createStatement().use { it.execute("CREATE TABLE books(id INT, title VARCHAR(20)); INSERT INTO books VALUES (1,'A'),(2,'B')") }
        val titles: Sequence<String> = c.createStatement().use { st ->
            st.executeQuery("SELECT title FROM books ORDER BY id").use { rs -> generateSequence { if (rs.next()) rs.getString(1) else null } }
        }
        println(runCatching { titles.take(2).toList() })
    }
}
