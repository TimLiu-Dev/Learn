import java.nio.file.Files
import java.sql.DriverManager
import kotlin.io.path.deleteRecursively
import kotlin.random.Random

// How many bytes does a B-tree database write per small row? Count what lands in SQLite's write-ahead log.
@OptIn(kotlin.io.path.ExperimentalPathApi::class)
fun main() {
    val dir = Files.createTempDirectory("l56")
    val rows = 2_000
    for ((label, perCommit) in listOf("1 row per commit" to 1, "100 rows per commit" to 100, "all rows in 1 commit" to rows)) {
        val db = dir.resolve("$perCommit.db")
        DriverManager.getConnection("jdbc:sqlite:$db").use { c ->
            c.createStatement().use { s ->
                s.execute("PRAGMA journal_mode = WAL")
                s.execute("PRAGMA wal_autocheckpoint = 0")        // keep every frame in the WAL so we can measure it
                s.execute("CREATE TABLE kv (k TEXT PRIMARY KEY, v TEXT) WITHOUT ROWID")   // one B-tree
            }
            c.autoCommit = false
            val rnd = Random(56)
            var logBytes = 0L                                     // what Part 1's log would have written
            c.prepareStatement("INSERT OR REPLACE INTO kv VALUES (?, ?)").use { ins ->
                for (i in 1..rows) {
                    val k = "user%05d".format(rnd.nextInt(50_000)); val v = "v$i"
                    ins.setString(1, k); ins.setString(2, v); ins.executeUpdate()
                    logBytes += LogKV.encode(k, v).remaining()
                    if (i % perCommit == 0) c.commit()
                }
            }
            val wal = Files.size(dir.resolve("$perCommit.db-wal"))
            println("%-21s WAL %,10d bytes = %,6d per row, %6.1f× a plain log".format(
                label, wal, wal / rows, wal.toDouble() / logBytes))
        }
    }
    dir.deleteRecursively()
}
