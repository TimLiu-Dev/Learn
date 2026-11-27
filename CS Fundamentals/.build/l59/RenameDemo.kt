import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

// Three releases of the same "members" code. Each one runs with its own connection,
// the way two servers would share one database.
interface Release {
    val name: String
    fun add(c: Connection, fullName: String, email: String)
    fun nameOf(c: Connection, id: Long): String?
}

object V1 : Release {                       // knows only the old column
    override val name = "v1"
    override fun add(c: Connection, fullName: String, email: String) =
        c.exec("INSERT INTO members (name, email) VALUES (?, ?)", fullName, email)
    override fun nameOf(c: Connection, id: Long) = c.one("SELECT name FROM members WHERE id = ?", id)
}

object V2 : Release {                       // writes both, reads the new one with a fallback
    override val name = "v2"
    override fun add(c: Connection, fullName: String, email: String) =
        c.exec("INSERT INTO members (name, full_name, email) VALUES (?, ?, ?)", fullName, fullName, email)
    override fun nameOf(c: Connection, id: Long) = c.one("SELECT COALESCE(full_name, name) FROM members WHERE id = ?", id)
}

object V3 : Release {                       // knows only the new column
    override val name = "v3"
    override fun add(c: Connection, fullName: String, email: String) =
        c.exec("INSERT INTO members (full_name, email) VALUES (?, ?)", fullName, email)
    override fun nameOf(c: Connection, id: Long) = c.one("SELECT full_name FROM members WHERE id = ?", id)
}

data class Traffic(val live: String, val failed: Int, val wrong: Int) {
    override fun toString() = "%-8s %3d   %3d".format(live, failed, wrong)
}

class World(private val dbFile: Path) {
    val expected = mutableMapOf<Long, String>()     // what every read should return
    private var next = 0

    fun open(): Connection = DriverManager.getConnection("jdbc:sqlite:$dbFile")

    /** 20 requests spread over the live releases: each adds a member, then reads every member. */
    fun traffic(live: List<Release>): Traffic {
        var failed = 0; var wrong = 0
        val conns = live.associateWith { open() }
        repeat(20) { i ->
            val r = live[i % live.size]; val c = conns.getValue(r)
            try {
                val n = "Member ${++next}"
                r.add(c, n, "m$next@example.com")
                expected[c.lastId()] = n
                wrong += expected.count { (id, name) -> r.nameOf(c, id) != name }
            } catch (e: SQLException) {
                failed++
            }
        }
        conns.values.forEach { it.close() }
        return Traffic(live.joinToString("+") { it.name }, failed, wrong)
    }

    fun migrateTo(version: Int, dir: Path) = open().use { c ->
        Migrator.migrate(c, Migrator.load(dir).filter { it.version <= version })
    }

    /** Copies name into full_name a few rows per transaction, so no lock is held for long. */
    fun backfill(batch: Int): List<Int> = open().use { c ->
        generateSequence {
            c.update("""UPDATE members SET full_name = name WHERE id IN
                        (SELECT id FROM members WHERE full_name IS NULL LIMIT ?)""", batch)
        }.takeWhile { it > 0 }.toList()
    }
}

fun main(args: Array<String>) {
    val dir = Path.of(args.getOrElse(0) { "db" })

    println("== The naive way: rename the column in one step while v1 still serves")
    World(Files.createTempFile("naive", ".db")).run {
        migrateTo(3, dir)
        traffic(listOf(V1))
        open().use { it.exec("ALTER TABLE members RENAME COLUMN name TO full_name") }
        println("v1 after the rename: ${traffic(listOf(V1)).failed} of 20 requests failed")
        open().use { c ->
            try { V1.nameOf(c, 1) } catch (e: SQLException) { println("v1 sees: ${e.message}") }
        }
    }

    println("\n== Expand, migrate, contract: never more than two releases live")
    println("step                          live     failed wrong")
    World(Files.createTempFile("parallel", ".db")).run {
        migrateTo(3, dir)
        println("%-29s %s".format("0 v1 only", traffic(listOf(V1))))
        migrateTo(4, dir)
        println("%-29s %s".format("1 migrate V4 (expand)", traffic(listOf(V1))))
        println("%-29s %s".format("2 roll out v2", traffic(listOf(V1, V2))))
        println("%-29s %s".format("  v2 everywhere", traffic(listOf(V2))))
        val batches = backfill(20)
        println("3 backfill: ${batches.sum()} rows in ${batches.size} batches $batches")
        println("%-29s %s".format("4 roll out v3", traffic(listOf(V2, V3))))
        println("%-29s %s".format("  v3 everywhere", traffic(listOf(V3))))
        migrateTo(5, dir)
        println("%-29s %s".format("5 migrate V5 (contract)", traffic(listOf(V3))))
        val right = open().use { c -> expected.count { (id, name) -> V3.nameOf(c, id) == name } }
        println("v3 reads $right of ${expected.size} members correctly")
    }

    println("\n== Three mistakes the steps protect you from")
    World(Files.createTempFile("early", ".db")).run {
        migrateTo(3, dir); traffic(listOf(V1)); migrateTo(4, dir)
        println("v1 and v3 live together: ${traffic(listOf(V1, V3)).wrong} wrong reads in 20 requests")
        open().use { println("v3 before the backfill reads member 1 as: ${V3.nameOf(it, 1)}") }
        try {
            migrateTo(5, dir)
        } catch (e: MigrationException) {
            println("contract before the backfill: ${e.message}")
        }
    }
}

fun Connection.exec(sql: String, vararg args: Any?) { update(sql, *args) }

fun Connection.update(sql: String, vararg args: Any?): Int = prepareStatement(sql).use { ps ->
    args.forEachIndexed { i, a -> ps.setObject(i + 1, a) }
    ps.executeUpdate()
}

fun Connection.one(sql: String, vararg args: Any?): String? = prepareStatement(sql).use { ps ->
    args.forEachIndexed { i, a -> ps.setObject(i + 1, a) }
    ps.executeQuery().use { rs -> if (rs.next()) rs.getString(1) else null }
}

fun Connection.lastId(): Long = createStatement().use { st ->
    st.executeQuery("SELECT last_insert_rowid()").use { it.next(); it.getLong(1) }
}
