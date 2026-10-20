import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

fun main() {
    Executors.newFixedThreadPool(2).use { pool ->
        // 1. submit: the exception is stored in the Future... which nobody reads
        pool.submit(Runnable { throw IllegalStateException("boom in submit") })

        // 2. execute: no Future, so the exception reaches the thread's uncaught handler
        pool.execute { throw IllegalStateException("boom in execute") }
        Thread.sleep(200)

        // 3. submit + get: the exception comes back, wrapped
        val f = pool.submit(Callable<Int> { error("boom, kept in the Future") })
        try {
            f.get()
        } catch (e: ExecutionException) {
            println("caught ${e.javaClass.simpleName}")
            println("  cause: ${e.cause}")
        }
    }
}
