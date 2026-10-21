import java.sql.Connection
import java.sql.DriverManager

fun searchUnsafe(db: Connection, term: String): List<String> =
    db.createStatement().use { st ->
        st.executeQuery("SELECT title FROM books WHERE title LIKE '%$term%' AND published").use { rs ->
            buildList { while (rs.next()) add(rs.getString(1)) }
        }
    }

// The wildcards go into the *parameter*, not the SQL text
fun searchSafe(db: Connection, term: String): List<String> =
    db.prepareStatement("SELECT title FROM books WHERE title LIKE ? AND published").use { ps ->
        ps.setString(1, "%$term%")
        ps.executeQuery().use { rs -> buildList { while (rs.next()) add(rs.getString(1)) } }
    }

fun toHtml(titles: List<String>) = titles.joinToString("") { "<li>${escapeHtml(it)}</li>" }

fun escapeHtml(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    .replace("\"", "&quot;").replace("'", "&#x27;")

fun main() {
    DriverManager.getConnection("jdbc:h2:mem:books").use { db ->
        db.createStatement().use {
            it.execute("CREATE TABLE books(title VARCHAR(60), published BOOLEAN)")
            it.execute("INSERT INTO books VALUES ('Kotlin in Action', TRUE), ('Java <Basics> & More', TRUE), ('Unreleased Draft', FALSE)")
        }
        val crafted = "' OR TRUE OR title LIKE '"
        println("unsafe 'Kot':   ${searchUnsafe(db, "Kot")}")
        println("unsafe crafted: ${searchUnsafe(db, crafted)}")
        println("safe 'Kot':     ${searchSafe(db, "Kot")}")
        println("safe crafted:   ${searchSafe(db, crafted)}")
        println("safe 'a' as HTML: ${toHtml(searchSafe(db, "a"))}")
    }
}
