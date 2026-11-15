import java.util.concurrent.Semaphore

// Lets at most `permits` callers run a block at the same time.
class Gate(permits: Int) {
    private val semaphore = Semaphore(permits)

    fun available(): Int = semaphore.availablePermits()

    fun <T> enter(block: () -> T): T {
        TODO("take a permit, run the block, and always give the permit back")
    }
}
