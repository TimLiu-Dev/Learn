import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager

// v1 renames by writing name only; v2 reads COALESCE(full_name, name).
fun run(label: String, v4: Migration) {
    val file = Files.createTempFile("stale", ".db")
    DriverManager.getConnection("jdbc:sqlite:$file").use { c ->
        Migrator.migrate(c, Migrator.load(Path.of("db")).filter { it.version <= 3 } + v4)
        c.sql("INSERT INTO members (name, full_name, email) VALUES ('Grace Hopper', 'Grace Hopper', 'g@example.org')")  // v2 adds
        c.sql("UPDATE members SET name = 'Grace B. Hopper' WHERE id = 1")                                                 // v1 renames
        val read = c.createStatement().use { st ->
            st.executeQuery("SELECT COALESCE(full_name, name) FROM members WHERE id = 1").use { it.next(); it.getString(1) }
        }
        println("%-22s v2 reads: %s".format(label, read))
    }
}

fun main() {
    val plain = Migrator.load(Path.of("db")).single { it.version == 4 }
    val synced = Migrator.load(Path.of("ex/sync")).single()
    run("V4 without a trigger", plain)
    run("V4 with the trigger", synced)
}

fun Connection.sql(s: String) = createStatement().use { it.executeUpdate(s) }
