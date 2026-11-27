package runner

import java.io.PrintStream

/**
 * The one place that prints. Every line is `t=NNNN text`, where the time is taken
 * inside the lock, so lines come out in time order and never interleave.
 */
class Events(private val out: PrintStream, private val nanoTime: () -> Long = System::nanoTime) {
    private val lock = Any()
    private val startNanos = nanoTime()
    private var stopping = false        // guarded by lock
    private var closed = false          // guarded by lock: the summary has been printed

    fun emit(text: String) = synchronized(lock) { print(text) }

    /** Prints `start` and returns true, unless shutdown has begun. Atomic, so no start ever follows `shutdown`. */
    fun startAttempt(id: String, attempt: Int): Boolean = synchronized(lock) {
        if (stopping) return false
        print("start $id attempt=$attempt")
        true
    }

    /** Prints the last line. After it, [beginShutdown] does nothing, so `summary` is always last. */
    fun summary(text: String) = synchronized(lock) {
        print(text)
        closed = true
    }

    /** Prints `shutdown` the first time it's called; false if shutdown had begun or the run is over. */
    fun beginShutdown(): Boolean = synchronized(lock) {
        if (stopping || closed) return false
        stopping = true
        print("shutdown")
        true
    }

    val isStopping: Boolean get() = synchronized(lock) { stopping }

    private fun print(text: String) {
        val ms = (nanoTime() - startNanos) / 1_000_000
        out.println("t=%04d %s".format(ms, text))
        out.flush()
    }
}
