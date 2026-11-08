import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteRecursively

const val ACKED = 20_000

// The child writes ACKED records, says so, then waits to be killed.
fun child(mode: String, file: Path) {
    if (mode == "direct") {
        val db = LogKV(file)                                    // one write() system call per record
        for (i in 0 until ACKED) db.put("k$i", "value-$i")
    } else {
        val out = BufferedOutputStream(FileOutputStream(file.toFile()), 8192)   // 8 KB buffer in the JVM
        for (i in 0 until ACKED) out.write(LogKV.encode("k$i", "value-$i").array())
    }
    println("acked $ACKED")
    System.out.flush()
    Thread.sleep(60_000)
}

// Re-run this same program as a child process, with different arguments.
fun startChild(mode: String, file: Path): Process {
    val me = ProcessHandle.current().info()
    val args = me.arguments().orElseThrow().dropLast(1)          // drop "parent"
    return ProcessBuilder(listOf(me.command().orElseThrow()) + args + listOf("child", mode, file.toString()))
        .redirectErrorStream(true).start()
}

@OptIn(kotlin.io.path.ExperimentalPathApi::class)
fun main(args: Array<String>) {
    if (args.firstOrNull() == "child") return child(args[1], Path.of(args[2]))
    val dir = Files.createTempDirectory("l56")
    for (mode in listOf("buffered", "direct")) {
        val file = dir.resolve("$mode.log")
        val p = startChild(mode, file)
        val line = p.inputReader().readLine()                   // blocks until the child has acked
        p.destroyForcibly()                                     // SIGKILL, like kill -9 (L53)
        val exit = p.waitFor()
        val size = Files.size(file)
        val valid = FileChannel.open(file).use { validPrefix(it) }
        val keys = LogKV(file).use { it.keys }
        println("%-8s child said \"%s\", exit %d | file %,d bytes, torn tail %d bytes | recovered %,d of %,d"
            .format(mode, line, exit, size, size - valid, keys, ACKED))
    }
    dir.deleteRecursively()
}
