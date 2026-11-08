import java.io.RandomAccessFile
import java.nio.file.Files
import kotlin.io.path.deleteRecursively
import kotlin.time.measureTime

// Same 64-byte appends, two ways to "sync" them on macOS.
@OptIn(kotlin.io.path.ExperimentalPathApi::class)
fun main() {
    val dir = Files.createTempDirectory("l56")
    val record = ByteArray(64) { 'x'.code.toByte() }
    val n = 1_000
    val kinds = listOf<Pair<String, (RandomAccessFile) -> Unit>>(
        "FileDescriptor.sync()" to { f -> f.fd.sync() },               // fsync(2)
        "FileChannel.force()" to { f -> f.channel.force(false) },     // fcntl(F_FULLFSYNC) on macOS
    )
    for (round in 1..2) {                                              // round 1 is the warm-up
        for ((label, sync) in kinds) {
            RandomAccessFile(dir.resolve("$label.$round").toFile(), "rw").use { f ->
                val t = measureTime { repeat(n) { f.write(record); sync(f) } }
                if (round == 2) println("%-22s %7.1f µs per write".format(label, t.inWholeMicroseconds.toDouble() / n))
            }
        }
    }
    dir.deleteRecursively()
}
