import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

const val MB = 8

fun readByteByByte(open: () -> InputStream): Long {
    var sum = 0L
    open().use { input ->
        while (true) {
            val b = input.read()            // one byte per call
            if (b == -1) break
            sum += b
        }
    }
    return sum
}

fun time(label: String, f: () -> Long) {
    var best = Double.MAX_VALUE
    var sum = 0L
    repeat(3) {
        val t = System.nanoTime()
        sum = f()
        best = minOf(best, (System.nanoTime() - t) / 1e6)
    }
    println("%-34s %8.1f ms  %7.0f MB/s  (sum $sum)".format(label, best, MB / (best / 1000)))
}

fun main() {
    val file: Path = Files.createTempFile("l22", ".bin")
    Files.write(file, ByteArray(MB * 1024 * 1024) { (it % 251).toByte() })

    time("FileInputStream.read()") { readByteByByte { FileInputStream(file.toFile()) } }
    time("BufferedInputStream.read()") { readByteByByte { BufferedInputStream(FileInputStream(file.toFile())) } }
    time("Files.readAllBytes") { Files.readAllBytes(file).sumOf { (it.toInt() and 0xff).toLong() } }
    Files.delete(file)
}
