import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// A hot key expires and 50 requests arrive at once. Plain cache-aside sends all
// 50 to the database; request coalescing sends one and lets the other 49 wait for it.
val dbQueries = AtomicInteger()

fun slowDbFind(id: Int): String {                   // the database: 50 ms per query (blocking, like JDBC)
    dbQueries.incrementAndGet()
    Thread.sleep(50)
    return "Dune"
}

val cache = ConcurrentHashMap<Int, String>()

// Plain cache-aside: check, miss, load, put
suspend fun getPlain(id: Int): String =
    cache[id] ?: withContext(Dispatchers.IO) { slowDbFind(id) }.also { cache[id] = it }

// Coalescing: the first miss starts one async load; everyone else awaits the same Deferred
val inFlight = HashMap<Int, Deferred<String>>()
val inFlightLock = Mutex()
val loaderScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

suspend fun getCoalesced(id: Int): String {
    cache[id]?.let { return it }
    val load = inFlightLock.withLock {
        inFlight.getOrPut(id) {                         // safe here: the Mutex makes it atomic
            loaderScope.async { slowDbFind(id).also { cache[id] = it } }
        }
    }
    try {
        return load.await()                             // a failed load throws here for every waiter
    } finally {
        inFlightLock.withLock { if (inFlight[id] === load) inFlight.remove(id) }
    }
}

fun main() = runBlocking {
    burst("plain") { getPlain(1) }
    cache.clear()                                       // the hot key expires again
    burst("coalesced") { getCoalesced(1) }
    loaderScope.cancel()
}

suspend fun burst(name: String, request: suspend () -> String) = coroutineScope {
    dbQueries.set(0)
    val gate = CompletableDeferred<Unit>()
    val jobs = List(50) { async(Dispatchers.Default) { gate.await(); request() } }
    val t = System.nanoTime()
    gate.complete(Unit)                                 // release all 50 at once
    jobs.awaitAll()
    println("%-10s 50 requests -> %2d database queries, %d ms"
        .format(name, dbQueries.get(), (System.nanoTime() - t) / 1_000_000))
}
