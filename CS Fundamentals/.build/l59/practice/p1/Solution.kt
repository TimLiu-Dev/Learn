import java.util.zip.CRC32

data class Migration(val version: Int, val script: String) {
    // Flyway stores a CRC32 for each SQL migration; we do the same.
    val checksum: Long get() = CRC32().apply { update(script.toByteArray()) }.value
}

// A stand-in for SQLite. Don't change it.
class FakeDatabase {
    val tables = mutableSetOf<String>()
    val history = mutableMapOf<Int, Long>()        // version -> checksum, like schema_version

    fun run(script: String) {
        for (line in script.lines().map { it.trim() }.filter { it.isNotEmpty() }) {
            val (verb, table) = line.split(" ")
            when (verb) {
                "create" -> check(tables.add(table)) { "table $table already exists" }
                "drop" -> check(tables.remove(table)) { "no such table: $table" }
                else -> error("syntax error near $verb")
            }
        }
    }

    fun <T> transaction(block: () -> T): T {
        val savedTables = tables.toSet()
        val savedHistory = history.toMap()
        try {
            return block()
        } catch (e: Exception) {                     // roll back: put everything back as it was
            tables.clear(); tables.addAll(savedTables)
            history.clear(); history.putAll(savedHistory)
            throw e
        }
    }
}

fun migrate(db: FakeDatabase, migrations: List<Migration>): List<Int> {
    // Validate first: an edited migration means this database and a fresh one would differ.
    for (m in migrations) {
        val recorded = db.history[m.version] ?: continue
        check(recorded == m.checksum) { "V${m.version} was edited after it ran" }
    }
    val pending = migrations.filter { it.version !in db.history }.sortedBy { it.version }
    for (m in pending) {
        db.transaction {                             // the script and its history row: both or neither
            db.run(m.script)
            db.history[m.version] = m.checksum
        }
    }
    return pending.map { it.version }
}
