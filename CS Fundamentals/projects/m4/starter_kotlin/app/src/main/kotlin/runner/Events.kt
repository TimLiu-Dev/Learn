package runner

import java.io.PrintStream

/** The one place that prints event lines: `t=NNNN text`, milliseconds since the run started. */
class Events(private val out: PrintStream, private val nanoTime: () -> Long = System::nanoTime) {
    fun emit(text: String): Unit = TODO("Milestone 1: print one event line; think about two threads printing at once")

    /** The last line of every run. */
    fun summary(text: String): Unit = TODO("Milestone 1 (Milestone 5: nothing may print after it)")
}
