import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteRecursively
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes
import kotlin.random.Random

fun keysAfterReopen(file: Path, bytes: ByteArray): List<String> {
    file.writeBytes(bytes)
    return LogKV(file).use { db -> db.indexSnapshot().keys.toList() }
}

@OptIn(kotlin.io.path.ExperimentalPathApi::class)
fun main() {
    val dir = Files.createTempDirectory("l56")
    val good = dir.resolve("good.log")
    LogKV(good).use { db -> for (i in 1..3) db.put("k$i", "v$i") }   // three 16-byte records
    val full = good.readBytes()
    val t = dir.resolve("t.log")

    // 1. A crash can cut the file anywhere. Try every length inside the last record.
    val byLength = (32..48).groupBy { len -> keysAfterReopen(t, full.copyOf(len)) }
    for ((keys, lens) in byLength) println("length ${lens.first()}..${lens.last()} -> $keys")

    // 2. The OS may grow the file before the data lands: garbage at the end.
    val junk = Random(56).nextBytes(16)
    println("full + 16 junk bytes -> ${keysAfterReopen(t, full + junk)}")

    // 3. One flipped bit in the middle record.
    val flipped = full.copyOf().also { it[16 + 13] = (it[16 + 13].toInt() xor 1).toByte() }
    println("bit flip in record 2 -> ${keysAfterReopen(t, flipped)}")

    // 4. The bug: reopen a torn file, then keep writing.
    t.writeBytes(full.copyOf(40))                       // 2 good records + 8 bytes of a torn third
    LogKV(t).use { db -> db.put("k4", "v4"); println("wrote k4, get(k4) = ${db.get("k4")}") }
    LogKV(t).use { db -> println("reopen: keys = ${db.indexSnapshot().keys}, get(k4) = ${db.get("k4")}") }
    dir.deleteRecursively()
}
