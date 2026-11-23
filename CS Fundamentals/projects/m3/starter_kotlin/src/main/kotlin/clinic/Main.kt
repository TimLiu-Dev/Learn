package clinic

import java.io.File
import kotlin.system.exitProcess

/**
 * logreport: reads an access log and prints a traffic report.
 *
 *   logreport access.log
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

    val lines = LogReader(file).readLines()
    val stats = Analyzer().analyze(lines)
    print(ReportWriter().render(stats))
}
