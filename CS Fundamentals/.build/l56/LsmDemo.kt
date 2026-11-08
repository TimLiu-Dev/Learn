import java.nio.file.Files
import kotlin.io.path.deleteRecursively
import kotlin.random.Random

@OptIn(kotlin.io.path.ExperimentalPathApi::class)
fun main() {
    val dir = Files.createTempDirectory("l56")
    val db = Lsm(dir, memLimit = 10_000)
    val rnd = Random(56)
    val latest = HashMap<String, String>()                         // the truth, to check against
    repeat(100_000) { i ->
        val key = "user%05d".format(rnd.nextInt(50_000))
        db.put(key, "v$i"); latest[key] = "v$i"
    }
    db.delete("user00042"); latest.remove("user00042")
    db.flush()
    println("100,000 puts to ${latest.size} keys -> ${db.tableCount} SSTables, write amplification %.2f"
        .format(db.diskBytes.toDouble() / db.userBytes))

    fun probe(label: String, keys: List<String>) {
        db.stats.blocksRead = 0; db.stats.bloomSkips = 0
        val wrong = keys.count { db.get(it) != latest[it] }
        println("%-28s blocks read per get %5.2f  bloom skips per get %5.2f  wrong %d".format(label,
            db.stats.blocksRead.toDouble() / keys.size, db.stats.bloomSkips.toDouble() / keys.size, wrong))
    }
    val present = latest.keys.shuffled(Random(1)).take(10_000)
    val absent = List(10_000) { "user%05d-x".format(it * 5) } + listOf("user00042")   // between real keys
    probe("present keys", present)
    probe("absent keys, Bloom on", absent)
    db.useBloom = false
    probe("absent keys, Bloom off", absent)
    db.useBloom = true

    println("range(user01000, user01004) = ${db.range("user01000", "user01004")}")

    db.compact()
    println("after compact: ${db.tableCount} SSTable, write amplification %.2f"
        .format(db.diskBytes.toDouble() / db.userBytes))
    probe("present keys, compacted", present)
    dir.deleteRecursively()
}
