import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files

fun openFds() = File("/dev/fd").list()!!.size          // macOS and Linux: one entry per open descriptor

fun main(args: Array<String>) {
    val n = args.firstOrNull()?.toInt() ?: 300
    val file = Files.createTempFile("l22", ".txt").toFile()
    val leaked = mutableListOf<FileInputStream>()
    println("open descriptors at start: ${openFds()}")
    try {
        repeat(n) { leaked += FileInputStream(file) }  // opened, never closed
        println("after opening $n streams:   ${openFds()}")
    } catch (e: IOException) {
        println("failed after ${leaked.size} streams: $e")
    } finally {
        leaked.forEach { it.close() }
        println("after closing them:         ${openFds()}")
        file.delete()
    }
}
