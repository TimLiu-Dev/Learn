import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

// 1000 coroutines x 1000 increments = 1,000,000 expected
suspend fun massiveRun(action: suspend () -> Unit) = coroutineScope {
    repeat(1000) { launch { repeat(1000) { action() } } }
}

fun main() = runBlocking {
    repeat(3) {
        var plain = 0
        withContext(Dispatchers.Default) { massiveRun { plain++ } }
        println("plain var, run ${it + 1}: $plain")
    }

    val mutex = Mutex(); var locked = 0
    var ms = measureTimeMillis {
        withContext(Dispatchers.Default) { massiveRun { mutex.withLock { locked++ } } }
    }
    println("Mutex:           $locked  ($ms ms)")

    val atomic = AtomicInteger()
    ms = measureTimeMillis {
        withContext(Dispatchers.Default) { massiveRun { atomic.incrementAndGet() } }
    }
    println("AtomicInteger:   ${atomic.get()}  ($ms ms)")

    val oneThread = Dispatchers.Default.limitedParallelism(1)
    var confined = 0
    ms = measureTimeMillis {
        withContext(oneThread) { massiveRun { confined++ } }
    }
    println("confined:        $confined  ($ms ms)")

    val inbox = Channel<Int>(Channel.UNLIMITED); var owned = 0
    ms = measureTimeMillis {
        val owner = launch { for (n in inbox) owned += n }  // one owner
        withContext(Dispatchers.Default) { massiveRun { inbox.send(1) } }
        inbox.close(); owner.join()
    }
    println("Channel owner:   $owned  ($ms ms)")
}
