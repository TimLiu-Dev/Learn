// One producer, two consumers, a bounded queue of 5, and a poison pill to shut down.
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

const val POISON = -1

fun main() {
    val queue = ArrayBlockingQueue<Int>(5)
    val total = AtomicLong()

    val consumers = List(2) { n ->
        thread(name = "consumer-${n + 1}") {
            var taken = 0
            while (true) {
                val job = queue.take()                  // blocks while the queue is empty
                if (job == POISON) break
                total.addAndGet(job.toLong())
                taken++
            }
            println("${Thread.currentThread().name} took $taken jobs")
        }
    }

    for (job in 1..1000) queue.put(job)                 // blocks while the queue is full
    repeat(consumers.size) { queue.put(POISON) }        // one pill per consumer

    consumers.forEach { it.join() }
    println("total ${total.get()} (expected 500500)")
}
