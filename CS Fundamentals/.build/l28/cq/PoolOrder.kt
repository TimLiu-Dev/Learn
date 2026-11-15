import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

fun main() {
    val gate = CountDownLatch(1)                       // every task waits here until we open it
    val pool = ThreadPoolExecutor(1, 2, 1, TimeUnit.SECONDS, ArrayBlockingQueue(1))
    var rejected = 0
    try {
        repeat(4) {
            try {
                pool.execute { gate.await() }
            } catch (e: RejectedExecutionException) {
                rejected++
            }
        }
        println("threads=${pool.poolSize} queued=${pool.queue.size} rejected=$rejected")
    } finally {
        gate.countDown()
        pool.shutdown()
        pool.awaitTermination(5, TimeUnit.SECONDS)
    }
}
