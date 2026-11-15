import java.util.concurrent.Semaphore

class Gate(permits: Int) {
    private val semaphore = Semaphore(permits)

    fun available(): Int = semaphore.availablePermits()

    fun <T> enter(block: () -> T): T {
        semaphore.acquire()
        val result = block()                // if this throws, the permit is gone for good
        semaphore.release()
        return result
    }
}
