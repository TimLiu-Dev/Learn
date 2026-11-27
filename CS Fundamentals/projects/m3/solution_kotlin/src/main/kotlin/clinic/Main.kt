package clinic

import java.io.File
import kotlin.system.exitProcess

/**
 * logreport: reads an access log and prints a traffic report.
 *
 *   logreport access.log
 *
 * Same output, byte for byte, as the original; see the FIX comments for what changed.
 */
fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("usage: logreport <access.log>")
        exitProcess(2)
    }
    val file = File(args[0])
    if (!file.isFile || !file.canRead()) {
        System.err.println("error: cannot read ${args[0]}")
        exitProcess(2)
    }

    val analyzer = Analyzer()
    // FIX (I/O): a 64 KiB buffered reader and readLine() instead of read() per char plus
    // `line += ch`, which copied every line once per character. Lines are now streamed:
    // nothing keeps the whole file in memory.
    file.bufferedReader(Charsets.UTF_8, 1 shl 16).use { reader ->
        while (true) {
            val line = reader.readLine() ?: break
            analyzer.add(line)
        }
    }
    print(ReportWriter().render(analyzer.finish()))
}
