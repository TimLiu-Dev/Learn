import java.io.RandomAccessFile
import java.nio.file.Files

const val N = 200_000

fun main() {
    val file = Files.createTempFile("l22", ".bin")
    Files.write(file, ByteArray(N) { it.toByte() })
    val bytes = Files.readAllBytes(file)

    var bestMem = Double.MAX_VALUE
    var bestSys = Double.MAX_VALUE
    var sum = 0L
    repeat(5) {                                       // best of 5: the first rounds warm up the JIT
        var t = System.nanoTime()
        for (i in 0 until N) sum += bytes[i]          // plain memory, no kernel
        bestMem = minOf(bestMem, (System.nanoTime() - t).toDouble() / N)

        t = System.nanoTime()
        RandomAccessFile(file.toFile(), "r").use { raf ->
            for (i in 0 until N) sum += raf.read()    // one read() system call per byte
        }
        bestSys = minOf(bestSys, (System.nanoTime() - t).toDouble() / N)
    }
    println("array:                  %.2f ns per byte".format(bestMem))
    println("RandomAccessFile.read(): %.0f ns per byte".format(bestSys))
    println("ratio: about %.0fx  (checksum $sum)".format(bestSys / bestMem))
    Files.delete(file)
}
