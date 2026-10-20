fun rssMb(): Long {
    val pid = ProcessHandle.current().pid().toString()
    val ps = ProcessBuilder("ps", "-o", "rss=", "-p", pid).start()   // rss= : value only, no header
    val kb = ps.inputStream.bufferedReader().use { it.readText().trim().toLong() }
    ps.waitFor()
    return kb / 1024
}

fun main() {
    println("pid ${ProcessHandle.current().pid()}, Java threads: ${Thread.getAllStackTraces().size}")
    println("RSS before: ${rssMb()} MB")
    val block = ByteArray(100 * 1024 * 1024)
    println("RSS after allocating 100 MB: ${rssMb()} MB")
    for (i in block.indices step 4096) block[i] = 1                 // write one byte per 4 KB
    println("RSS after touching it:       ${rssMb()} MB")
}
