import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.StandardOpenOption.WRITE

const val WRITES = 200

fun timeWrites(force: Boolean): Double {
    val file = Files.createTempFile("l22", ".log")
    val record = ByteArray(64) { 'x'.code.toByte() }
    val t = System.nanoTime()
    FileChannel.open(file, WRITE).use { ch ->
        repeat(WRITES) {
            ch.write(ByteBuffer.wrap(record))         // returns once the kernel has the bytes
            if (force) ch.force(false)                // waits until the storage device has them
        }
    }
    val ms = (System.nanoTime() - t) / 1e6
    Files.delete(file)
    return ms
}

fun main() {
    repeat(2) { timeWrites(false); timeWrites(true) } // warm-up rounds
    val plain = timeWrites(false)
    val forced = timeWrites(true)
    println("$WRITES writes, no force:     %7.1f ms  (%.1f µs each)".format(plain, plain * 1000 / WRITES))
    println("$WRITES writes, force each:   %7.1f ms  (%.1f µs each)".format(forced, forced * 1000 / WRITES))
}
