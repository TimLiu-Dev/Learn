import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    val rejected = AtomicInteger()
    val countingHandler = RejectedExecutionHandler { _, _ -> rejected.incrementAndGet() }
    val pool = ThreadPoolExecutor(
        2, 4, 1, TimeUnit.SECONDS,
        ArrayBlockingQueue(2),
        Thread.ofPlatform().name("worker-", 1).factory(),
        countingHandler
    )
    for (id in 1..10) {
        pool.execute {
            println("task $id on ${Thread.currentThread().name}")
            Thread.sleep(200)
        }
    }
    pool.shutdown()
    pool.awaitTermination(5, TimeUnit.SECONDS)
    println("largest pool size: ${pool.largestPoolSize}")
    println("completed: ${pool.completedTaskCount}, rejected: ${rejected.get()}")
}
