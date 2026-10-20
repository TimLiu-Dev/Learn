import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import kotlin.random.Random

fun copyByteByByte(src: Path, dst: Path) {
    FileInputStream(src.toFile()).use { input ->
        FileOutputStream(dst.toFile()).use { output ->
            while (true) {
                val b = input.read()
                if (b == -1) break
                output.write(b)
            }
        }
    }
}

fun copyWithBuffer(src: Path, dst: Path, size: Int) {
    val buffer = ByteArray(size)
    FileInputStream(src.toFile()).use { input ->
        FileOutputStream(dst.toFile()).use { output ->
            while (true) {
                val n = input.read(buffer)          // up to buffer.size bytes per system call
                if (n == -1) break
                output.write(buffer, 0, n)          // write only the n bytes we got
            }
        }
    }
}

fun timed(label: String, src: Path, dst: Path, copy: () -> Unit) {
    val t = System.nanoTime()
    copy()
    val ms = (System.nanoTime() - t) / 1e6
    println("%-24s %8.1f ms  identical: ${Files.mismatch(src, dst) == -1L}".format(label, ms))
}

fun main() {
    val src = Files.createTempFile("src", ".bin")
    val dst = Files.createTempFile("dst", ".bin")
    Files.write(src, Random(7).nextBytes(4 * 1024 * 1024))      // 4 MB of random bytes

    timed("byte by byte", src, dst) { copyByteByByte(src, dst) }
    timed("16-byte buffer", src, dst) { copyWithBuffer(src, dst, 16) }
    timed("8 KB buffer", src, dst) { copyWithBuffer(src, dst, 8192) }
    timed("Files.copy", src, dst) { Files.copy(src, dst, REPLACE_EXISTING) }
    Files.delete(src); Files.delete(dst)
}
