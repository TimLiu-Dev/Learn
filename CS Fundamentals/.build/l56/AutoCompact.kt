import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteRecursively

// LogKV that compacts itself once garbage is more than half of a file bigger than minBytes.
class AutoKV(path: Path, private val minBytes: Long = 64 * 1024) : AutoCloseable {
    private val db = LogKV(path)
    var compactions = 0; private set
    var largestFile = 0L; private set

    fun put(key: String, value: String) { db.put(key, value); afterWrite() }
    fun delete(key: String) { db.delete(key); afterWrite() }
    fun get(key: String) = db.get(key)
    val fileSize get() = db.fileSize

    private fun afterWrite() {
        largestFile = maxOf(largestFile, db.fileSize)
        if (db.fileSize >= minBytes && db.deadBytes * 2 > db.fileSize) { db.compact(); compactions++ }
    }

    override fun close() = db.close()
}

@OptIn(kotlin.io.path.ExperimentalPathApi::class)
fun main() {
    val dir = Files.createTempDirectory("l56")
    AutoKV(dir.resolve("auto.log")).use { db ->
        repeat(100_000) { i -> db.put("key${i % 1_000}", "value-$i") }
        println("compactions ${db.compactions}, largest file ${db.largestFile / 1024} KB, " +
                "now ${db.fileSize / 1024} KB, get(key7) = ${db.get("key7")}")
    }
    dir.deleteRecursively()
}
