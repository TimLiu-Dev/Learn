import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager

fun main(args: Array<String>) {
    val dir = Files.createTempDirectory("migrations")       // a copy of V1-V3 we're allowed to break
    Files.list(Path.of(args.getOrElse(0) { "db" })).use { files ->
        files.filter { it.fileName.toString().matches(Regex("V[123]__.*")) }
            .forEach { Files.copy(it, dir.resolve(it.fileName)) }
    }
    val dbFile = Files.createTempFile("library", ".db")
    DriverManager.getConnection("jdbc:sqlite:$dbFile").use { conn ->
        conn.createStatement().use { it.execute("PRAGMA foreign_keys = ON") }

        println("== 1. A fresh database")
        report(Migrator.migrate(conn, Migrator.load(dir)))
        conn.createStatement().use { it.execute("INSERT INTO members (name, email) VALUES ('Ada', 'ada@example.com')") }

        println("== 2. The next start: nothing to do")
        report(Migrator.migrate(conn, Migrator.load(dir)))

        println("== 3. Someone edits V2 after it ran")
        val v2 = dir.resolve("V2__add_joined.sql")
        val original = Files.readString(v2)
        Files.writeString(v2, original.replace("joined TEXT", "joined TEXT DEFAULT 'today'"))
        attempt(conn, dir)
        Files.writeString(v2, original)

        println("== 4. A new migration whose second statement fails")
        Files.writeString(dir.resolve("V4__add_fines.sql"),
            "CREATE TABLE fines (id INTEGER PRIMARY KEY, amount INTEGER);\nALTER TABLE members ADD COLUMN email TEXT;\n")
        attempt(conn, dir)
        println("fines table exists? ${tables(conn).contains("fines")}")

        println("== 5. The history table")
        conn.createStatement().use { st ->
            st.executeQuery("SELECT version, description, checksum FROM schema_version ORDER BY version").use { rs ->
                while (rs.next()) println("V${rs.getInt(1)}  %-14s %s".format(rs.getString(2), rs.getString(3)))
            }
        }
        println("members: " + conn.createStatement().use { st ->
            st.executeQuery("SELECT sql FROM sqlite_schema WHERE name = 'members'").use { it.next(); it.getString(1) }
        }.replace(Regex("\\s+"), " "))
    }
}

fun report(applied: List<Migration>) =
    println(if (applied.isEmpty()) "applied nothing" else applied.joinToString("\n") { "applied V${it.version} ${it.description}" })

fun attempt(conn: Connection, dir: Path) {
    try {
        report(Migrator.migrate(conn, Migrator.load(dir)))
    } catch (e: MigrationException) {
        println("REFUSED: ${e.message}")
    }
}

fun tables(conn: Connection): List<String> = conn.createStatement().use { st ->
    st.executeQuery("SELECT name FROM sqlite_schema WHERE type = 'table'").use { rs ->
        buildList { while (rs.next()) add(rs.getString(1)) }
    }
}
