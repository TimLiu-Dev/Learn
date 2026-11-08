import java.nio.file.Files
import kotlin.io.path.deleteRecursively
import kotlin.time.measureTime

@OptIn(kotlin.io.path.ExperimentalPathApi::class)
fun main() {
    val dir = Files.createTempDirectory("l56")
    val n = 2_000
    LogKV(dir.resolve("warm.log")).use { db -> repeat(500) { db.put("w$it", "x"); db.sync() } }   // warm-up (L16)
    for ((label, every) in listOf("sync every put" to 1, "sync every 10" to 10, "sync every 100" to 100, "never sync" to 0)) {
        LogKV(dir.resolve("$every.log")).use { db ->
            val t = measureTime {
                for (i in 1..n) {
                    db.put("k$i", "value-$i")
                    if (every > 0 && i % every == 0) db.sync()
                }
            }
            val us = t.inWholeMicroseconds.toDouble() / n
            println("%-15s %8.1f µs per put  %,10d puts/s".format(label, us, (1_000_000 / us).toLong()))
        }
    }
    dir.deleteRecursively()
}
