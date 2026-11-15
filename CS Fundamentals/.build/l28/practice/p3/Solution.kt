import java.util.concurrent.Semaphore

// Lets at most `permits` callers run a block at the same time.
class Gate(permits: Int) {
    private val semaphore = Semaphore(permits)

    fun available(): Int = semaphore.availablePermits()

    fun <T> enter(block: () -> T): T {
        semaphore.acquire()                 // waits here while every permit is taken
        try {
            return block()
        } finally {
            semaphore.release()             // runs on return and on throw
        }
    }
}
