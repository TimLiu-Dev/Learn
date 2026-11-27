package shelf.store

// The migration runner from L59, unchanged apart from this package line.
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.sql.Connection
import java.time.Instant

/** One numbered SQL script: V3__unique_email.sql is version 3, "unique email". */
data class Migration(val version: Int, val description: String, val sql: String) {
    val checksum: String = MessageDigest.getInstance("SHA-256")
        .digest(sql.toByteArray()).joinToString("") { "%02x".format(it) }.take(16)
}

class MigrationException(message: String) : RuntimeException(message)

/** A forward-only migration runner for SQLite over plain JDBC. */
object Migrator {
    private val FILE_NAME = Regex("""V(\d+)__(\w+)\.sql""")

    /** Reads every V<n>__<words>.sql file in [dir], sorted by version as a number. */
    fun load(dir: Path): List<Migration> {
        val found = Files.list(dir).use { files ->
            files.toList().mapNotNull { file ->
                FILE_NAME.matchEntire(file.fileName.toString())?.let { m ->
                    Migration(m.groupValues[1].toInt(), m.groupValues[2].replace('_', ' '), Files.readString(file))
                }
            }
        }
        found.groupBy { it.version }.values.firstOrNull { it.size > 1 }?.let {
            throw MigrationException("two migrations claim version ${it[0].version}")
        }
        return found.sortedBy { it.version }
    }

    /** Applies the pending migrations in order and returns them. Safe to call on every start. */
    fun migrate(conn: Connection, migrations: List<Migration>): List<Migration> {
        conn.createStatement().use {
            it.execute("""CREATE TABLE IF NOT EXISTS schema_version (
                version INTEGER PRIMARY KEY, description TEXT NOT NULL,
                checksum TEXT NOT NULL, applied_at TEXT NOT NULL)""")
        }
        val applied = conn.createStatement().use { st ->
            st.executeQuery("SELECT version, checksum FROM schema_version").use { rs ->
                buildMap { while (rs.next()) put(rs.getInt(1), rs.getString(2)) }
            }
        }
        val known = migrations.associateBy { it.version }
        val newestKnown = migrations.maxOfOrNull { it.version } ?: 0
        for ((version, checksum) in applied) {
            val m = known[version]
            when {
                // Newer code already migrated further. Older code keeps running (Part 2).
                m == null && version > newestKnown -> continue
                m == null -> throw MigrationException("V$version was applied but its file is missing")
                m.checksum != checksum -> throw MigrationException(
                    "V$version (${m.description}) was edited after it ran: checksum $checksum in the database, ${m.checksum} on disk")
            }
        }
        val pending = migrations.filter { it.version !in applied }
        val newestApplied = applied.keys.maxOrNull() ?: 0
        pending.firstOrNull { it.version < newestApplied }?.let {
            throw MigrationException("V${it.version} is older than V$newestApplied, which already ran")
        }
        pending.forEach { apply(conn, it) }
        return pending
    }

    // Steps 1, 2, 10, 11 and 12 of SQLite's table-rebuild recipe wrap every migration,
    // so a migration file only has to contain steps 3 to 9.
    private fun apply(conn: Connection, m: Migration) {
        val fkWasOn = conn.createStatement().use { st ->
            st.executeQuery("PRAGMA foreign_keys").use { it.next() && it.getInt(1) == 1 }
        }
        conn.createStatement().use { it.execute("PRAGMA foreign_keys = OFF") }  // a no-op inside a transaction
        conn.autoCommit = false
        try {
            conn.createStatement().use { st ->
                statements(m.sql).forEach { st.execute(it) }
                st.executeQuery("PRAGMA foreign_key_check").use { rs ->
                    if (rs.next()) throw MigrationException("V${m.version} broke a foreign key in table ${rs.getString(1)}")
                }
            }
            conn.prepareStatement("INSERT INTO schema_version VALUES (?, ?, ?, ?)").use { ps ->
                ps.setInt(1, m.version); ps.setString(2, m.description)
                ps.setString(3, m.checksum); ps.setString(4, Instant.now().toString())
                ps.executeUpdate()
            }
            conn.commit()
        } catch (e: Exception) {
            conn.rollback()                         // SQLite DDL is transactional: all of it, or none
            throw if (e is MigrationException) e else MigrationException("V${m.version} failed and was rolled back: ${e.message}")
        } finally {
            conn.autoCommit = true
            if (fkWasOn) conn.createStatement().use { it.execute("PRAGMA foreign_keys = ON") }
        }
    }

    /**
     * Splits a script into statements. A statement ends with a line ending in ";", except that a
     * CREATE TRIGGER runs on until a line ending in "END;". Lines starting with "--" are dropped.
     */
    fun statements(sql: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        for (line in sql.lines().filterNot { it.trimStart().startsWith("--") }) {
            current.append(line).append('\n')
            val text = current.trim().toString()
            val inTrigger = text.startsWith("CREATE TRIGGER", ignoreCase = true)
            val ends = if (inTrigger) text.endsWith("END;", ignoreCase = true) else text.endsWith(";")
            if (ends) { result += text.removeSuffix(";"); current.clear() }
        }
        current.trim().takeIf { it.isNotEmpty() }?.let { result += it.toString() }
        return result
    }
}
