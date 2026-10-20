import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

fun log(msg: String) = println("  [${Thread.currentThread().name}] $msg")

fun run(policy: RejectedExecutionHandler) {
    val pool = ThreadPoolExecutor(
        2, 2,                              // core and max: 2 workers
        0L, TimeUnit.MILLISECONDS,         // keep-alive (unused when core == max)
        ArrayBlockingQueue(2),             // bounded queue: at most 2 waiting tasks
        policy
    )
    for (id in 1..6) {
        try {
            pool.execute { Thread.sleep(300); log("finished task $id") }
            println("submitted task $id, queue size ${pool.queue.size}")
        } catch (e: RejectedExecutionException) {
            println("REJECTED task $id")
            if (id == 5) println("  ${e.message}")
        }
    }
    pool.shutdown()
    pool.awaitTermination(5, TimeUnit.SECONDS)
}

fun main() {
    println("== AbortPolicy (the default)")
    run(ThreadPoolExecutor.AbortPolicy())
    println("== CallerRunsPolicy")
    run(ThreadPoolExecutor.CallerRunsPolicy())
}
