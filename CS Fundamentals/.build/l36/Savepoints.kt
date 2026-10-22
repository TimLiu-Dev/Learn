import java.sql.DriverManager
import java.sql.SQLException

fun main() {
    DriverManager.getConnection("jdbc:sqlite::memory:").use { c ->
        c.createStatement().use { it.executeUpdate("CREATE TABLE members (id INTEGER PRIMARY KEY, name TEXT NOT NULL, email TEXT UNIQUE NOT NULL)") }
        val signups = listOf("Ada" to "ada@example.com", "Alan" to "alan@example.com",
                             "Ada again" to "ada@example.com", "Grace" to "grace@example.com")

        c.autoCommit = false
        c.prepareStatement("INSERT INTO members (name, email) VALUES (?, ?)").use { ps ->
            for ((name, email) in signups) {
                val sp = c.setSavepoint()              // a bookmark inside the transaction
                try {
                    ps.setString(1, name); ps.setString(2, email); ps.executeUpdate()
                    c.releaseSavepoint(sp)
                } catch (e: SQLException) {
                    c.rollback(sp)                     // undo only this row
                    println("skipped $name: ${e.message}")
                }
            }
        }
        c.commit()
        c.autoCommit = true

        c.createStatement().use { st ->
            st.executeQuery("SELECT id, name FROM members ORDER BY id").use { rs ->
                while (rs.next()) println("${rs.getInt(1)} ${rs.getString(2)}")
            }
        }
    }
}
