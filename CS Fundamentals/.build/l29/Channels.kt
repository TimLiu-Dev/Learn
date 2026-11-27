// L27's Pipeline again: one producer, two consumers, a buffer of 5. No poison pill this time.
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

@OptIn(ExperimentalCoroutinesApi::class)          // produce is still marked experimental
fun main() = runBlocking {
    val jobs: ReceiveChannel<Int> = produce(capacity = 5) {
        for (job in 1..1000) send(job)            // suspends while the buffer is full
    }                                             // block ends -> channel closes

    val totals = List(2) { n ->                   // fan-out: two consumers share one channel
        async(Dispatchers.Default) {
            var sum = 0L
            var taken = 0
            for (job in jobs) { sum += job; taken++ }   // the loop ends when the channel is closed and empty
            println("consumer-${n + 1} took $taken jobs")
            sum
        }
    }
    println("total ${totals.awaitAll().sum()} (expected 500500)")

    val log = Channel<String>(Channel.UNLIMITED)
    log.send("a"); log.send("b"); log.close()
    log.consumeEach { print("$it ") }             // one consumer that owns the channel
    println("| send after close fails: ${log.trySend("c").isClosed}")
}
