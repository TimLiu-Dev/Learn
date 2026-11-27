import java.util.zip.CRC32

data class Migration(val version: Int, val script: String) {
    val checksum: Long get() = CRC32().apply { update(script.toByteArray()) }.value
}

class FakeDatabase {
    val tables = mutableSetOf<String>()
    val history = mutableMapOf<Int, Long>()

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
        } catch (e: Exception) {
            tables.clear(); tables.addAll(savedTables)
            history.clear(); history.putAll(savedHistory)
            throw e
        }
    }
}

// The mistake: the script runs outside the transaction, so a failure halfway leaves "members" behind.
fun migrate(db: FakeDatabase, migrations: List<Migration>): List<Int> {
    for (m in migrations) {
        val recorded = db.history[m.version] ?: continue
        check(recorded == m.checksum) { "V${m.version} was edited after it ran" }
    }
    val pending = migrations.filter { it.version !in db.history }.sortedBy { it.version }
    for (m in pending) {
        db.run(m.script)
        db.transaction { db.history[m.version] = m.checksum }
    }
    return pending.map { it.version }
}
