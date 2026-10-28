import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager

// Leader + one follower, two SQLite files. The follower copies the leader's change log `lagMs` late.
class ReplicatedBooks(private val dir: Path, private val lagMs: Long) : AutoCloseable {
    private fun open(file: String): Connection =
        DriverManager.getConnection("jdbc:sqlite:${dir.resolve(file)}?busy_timeout=3000&journal_mode=WAL")
    private val leader = open("leader.db")          // used by writers
    private val leaderLog = open("leader.db")       // used by the replicator to read the change log
    private val follower = open("follower.db")      // used by the replicator to apply changes
    private val followerRead = open("follower.db")  // used by readers
    @Volatile private var running = true

    init {
        leader.createStatement().use {
            it.executeUpdate("CREATE TABLE books(id INTEGER PRIMARY KEY, title TEXT NOT NULL)")
            it.executeUpdate("CREATE TABLE changes(version INTEGER PRIMARY KEY, at INTEGER, id INTEGER, title TEXT)")
        }
        follower.createStatement().use {
            it.executeUpdate("CREATE TABLE books(id INTEGER PRIMARY KEY, title TEXT NOT NULL)")
            it.executeUpdate("CREATE TABLE applied(version INTEGER)")
            it.executeUpdate("INSERT INTO applied VALUES (0)")
        }
        Thread.ofVirtual().start { replicate() }
    }

    // A write goes to the leader: the row and its change-log entry in one transaction (L36).
    data class Written(val id: Long, val version: Long)

    @Synchronized fun write(title: String): Written {
        leader.autoCommit = false
        try {
            val id = leader.prepareStatement("INSERT INTO books(title) VALUES (?)").use {
                it.setString(1, title); it.executeUpdate()
                leader.createStatement().executeQuery("SELECT last_insert_rowid()").use { rs -> rs.getLong(1) }
            }
            leader.prepareStatement("INSERT INTO changes(at, id, title) VALUES (?, ?, ?)").use {
                it.setLong(1, System.currentTimeMillis()); it.setLong(2, id); it.setString(3, title); it.executeUpdate()
            }
            val version = leader.createStatement().executeQuery("SELECT last_insert_rowid()").use { it.getLong(1) }
            leader.commit()
            return Written(id, version)
        } finally {
            leader.autoCommit = true
        }
    }

    fun readLeader(id: Long): String? = titleOf(leader, id)
    fun readFollower(id: Long): String? = titleOf(followerRead, id)
    fun followerVersion(): Long = followerRead.createStatement().executeQuery("SELECT version FROM applied").use { it.getLong(1) }
    @Synchronized fun leaderVersion(): Long =
        leader.createStatement().executeQuery("SELECT coalesce(max(version), 0) FROM changes").use { it.getLong(1) }

    private fun titleOf(c: Connection, id: Long): String? =
        c.prepareStatement("SELECT title FROM books WHERE id = ?").use {
            it.setLong(1, id)
            it.executeQuery().use { rs -> if (rs.next()) rs.getString(1) else null }
        }

    // The replication stream: apply every change, in order, once it is `lagMs` old.
    private fun replicate() {
        while (running) {
            val from = followerVersion()
            leaderLog.prepareStatement("SELECT version, at, id, title FROM changes WHERE version > ? ORDER BY version").use { st ->
                st.setLong(1, from)
                st.executeQuery().use { rs ->
                    while (rs.next()) {
                        val wait = rs.getLong("at") + lagMs - System.currentTimeMillis()
                        if (wait > 0) Thread.sleep(wait)                   // the artificial network delay
                        follower.autoCommit = false
                        follower.prepareStatement("INSERT OR REPLACE INTO books(id, title) VALUES (?, ?)").use {
                            it.setLong(1, rs.getLong("id")); it.setString(2, rs.getString("title")); it.executeUpdate()
                        }
                        follower.prepareStatement("UPDATE applied SET version = ?").use { it.setLong(1, rs.getLong("version")); it.executeUpdate() }
                        follower.commit()
                        follower.autoCommit = true
                    }
                }
            }
            Thread.sleep(10)
        }
    }

    override fun close() { running = false; Thread.sleep(50); listOf(leader, leaderLog, follower, followerRead).forEach { it.close() } }
}

fun main() {
    val dir = Files.createTempDirectory("l42")
    ReplicatedBooks(dir, lagMs = 300).use { db ->
        println("-- read from the follower right after a write")
        val v1 = db.write("Dune").version
        println("write 'Dune'           -> leader version $v1")
        println("read book 1, follower  -> ${db.readFollower(1) ?: "not found"}   (follower at version ${db.followerVersion()})")
        Thread.sleep(400)
        println("400 ms later, follower -> ${db.readFollower(1)}   (follower at version ${db.followerVersion()})")

        println("-- read-your-writes: remember your write's version, read where it is visible")
        val mine = db.write("Emma").version
        fun readMine(id: Long, minVersion: Long): String {
            val fv = db.followerVersion()
            return if (fv >= minVersion) "${db.readFollower(id)} from follower (at $fv)"
                   else "${db.readLeader(id)} from leader (follower only at $fv, need $minVersion)"
        }
        println("write 'Emma' -> version $mine; read now -> ${readMine(2, mine)}")
        Thread.sleep(400)
        println("400 ms later           -> ${readMine(2, mine)}")

        println("-- lag under a burst of 20 writes")
        repeat(20) { db.write("book $it") }
        val t0 = System.currentTimeMillis()
        while (true) {
            val lv = db.leaderVersion()
            val fv = db.followerVersion()
            println("  +%3d ms  leader v%d  follower v%d  behind by %d".format(System.currentTimeMillis() - t0, lv, fv, lv - fv))
            if (fv == lv) break
            Thread.sleep(100)
        }
    }
    dir.toFile().deleteRecursively()
}
