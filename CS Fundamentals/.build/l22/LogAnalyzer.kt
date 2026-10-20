import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.Random
import kotlin.io.path.bufferedWriter
import kotlin.io.path.deleteExisting
import kotlin.io.path.fileSize
import kotlin.io.path.useLines

val PATHS = listOf("/api/users", "/api/orders", "/api/products", "/api/search",
                   "/api/login", "/api/cart", "/api/checkout", "/health")
val BASE_MS = intArrayOf(8, 20, 12, 35, 25, 10, 60, 1)    // typical latency per path
const val LINES = 1_200_000

fun rssMb(): Long {                                   // ask the OS, via ps, how much RAM we hold
    val ps = ProcessBuilder("ps", "-o", "rss=", "-p", ProcessHandle.current().pid().toString()).start()
    return ps.inputStream.bufferedReader().use { it.readText().trim().toLong() } / 1024
}

fun generate(file: Path) {
    val rnd = Random(22)
    file.bufferedWriter().use { out ->
        for (i in 0 until LINES) {
            val s = i / 14                            // about 14 requests per second
            val p = minOf(rnd.nextInt(10), PATHS.size - 1)  // /health gets 3 in 10
            val path = PATHS[p]
            val method = if (rnd.nextInt(4) == 0) "POST" else "GET"
            val status = when (rnd.nextInt(100)) { in 0..1 -> 500; in 2..6 -> 404; else -> 200 }
            val ms = BASE_MS[p] + rnd.nextInt(30) + (if (rnd.nextInt(12) == 0) rnd.nextInt(900) else 0)
            out.write("2026-10-20T%02d:%02d:%02d %s %s %d %dms\n"
                .format(s / 3600 % 24, s / 60 % 60, s % 60, method, path, status, ms))
        }
    }
}

fun p95(sorted: List<Int>) = sorted[Math.ceil(0.95 * sorted.size).toInt() - 1]   // nearest rank

inline fun <T> timed(label: String, block: () -> T): T {
    val t = System.nanoTime()
    val result = block()
    println("%-28s %7.0f ms".format(label, (System.nanoTime() - t) / 1e6))
    return result
}

fun main() {
    println("RSS at start: ${rssMb()} MB")
    val file = Files.createTempFile("access", ".log")
    generate(file)
    println("log: $LINES lines, ${file.fileSize() / 1024 / 1024} MB")

    // 1. Unbuffered: one system call per byte. Only the first 1 MB, or we'd wait minutes.
    val oneMb = 1024 * 1024
    val t1 = System.nanoTime()
    val newlines = timed("unbuffered, first 1 MB") {
        FileInputStream(file.toFile()).use { input ->
            var count = 0
            repeat(oneMb) { if (input.read() == '\n'.code) count++ }
            count
        }
    }
    val wholeSec = (System.nanoTime() - t1) / 1e9 * file.fileSize() / oneMb
    println("  ($newlines lines; at that speed the whole file would take ~%.0f s)".format(wholeSec))

    // 2. Buffered, line by line, with a mutable map.
    val byLoop = timed("buffered readLine loop") {
        val stats = HashMap<String, MutableList<Int>>()
        Files.newBufferedReader(file).use { r ->
            while (true) {
                val line = r.readLine() ?: break
                val parts = line.split(' ')
                stats.getOrPut(parts[2]) { mutableListOf() } += parts[4].dropLast(2).toInt()
            }
        }
        stats
    }

    // 3. Lazy pipeline: useLines gives a Sequence and closes the file for you.
    val byPipeline = timed("useLines pipeline") {
        file.useLines { lines ->
            lines.map { it.split(' ') }
                .groupBy({ it[2] }, { it[4].dropLast(2).toInt() })
        }
    }
    check(byLoop == byPipeline) { "the two readers disagree" }

    println("top 5 paths:")
    byPipeline.entries
        .sortedWith(compareByDescending<Map.Entry<String, List<Int>>> { it.value.size }.thenBy { it.key })
        .take(5)
        .forEach { (path, ms) -> println("  %-14s %7d requests  p95 %4d ms".format(path, ms.size, p95(ms.sorted()))) }

    println("RSS at end: ${rssMb()} MB")
    file.deleteExisting()
}
