package gateway

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Clock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

const val SECONDS = 10

/** One scripted run: 3 polite clients (~8 req/s each) and 1 greedy one (~60 req/s) for 10 s. */
fun main() {
    val pricing = PricingService(seed = 43, outage = 4000L until 6000L)
    pricing.server.start()
    val gateway = Gateway(pricing.server.address.port, Clock.systemUTC())
    gateway.server.start()
    val base = "http://127.0.0.1:${gateway.server.address.port}"
    val http = HttpClient.newHttpClient()

    data class Result(val second: Int, val status: Int, val stale: Boolean, val ms: Long)
    val results = ConcurrentLinkedQueue<Result>()
    val states = ConcurrentHashMap<Int, MutableSet<String>>()
    val start = System.nanoTime()
    fun second() = ((System.nanoTime() - start) / 1_000_000_000).toInt()

    val sampler = Thread.ofVirtual().start {                                  // breaker state, 10 times a second
        while (second() < SECONDS) {
            states.computeIfAbsent(second()) { ConcurrentHashMap.newKeySet() } += gateway.breaker.state().name
            Thread.sleep(100)
        }
    }
    fun client(name: String, pauseMs: Long) = Thread.ofVirtual().start {
        var n = 0
        while (second() < SECONDS) {
            val sec = second(); val t = System.nanoTime()
            val r = http.send(HttpRequest.newBuilder(URI.create("$base/api/price/${n++ % 5}")).header("X-Client", name).build(),
                HttpResponse.BodyHandlers.ofString())
            results += Result(sec, r.statusCode(), r.headers().firstValue("X-Fallback").isPresent, (System.nanoTime() - t) / 1_000_000)
            Thread.sleep(pauseMs)
        }
    }
    val clients = listOf(client("alice", 120), client("bob", 120), client("carol", 120), client("greedy", 15))
    clients.forEach { it.join() }; sampler.join()

    println("\nsec  req  fresh stale  429  503   p95 ms  circuit")
    for (s in 0 until SECONDS) {
        val rs = results.filter { it.second == s }
        val served = rs.filter { it.status != 429 }.map { it.ms }.sorted()
        val p95 = if (served.isEmpty()) 0 else served[((served.size - 1) * 0.95).toInt()]
        println("%3d %4d %5d %5d %4d %4d %8d  %s".format(s, rs.size, rs.count { it.status == 200 && !it.stale },
            rs.count { it.stale }, rs.count { it.status == 429 }, rs.count { it.status == 503 }, p95,
            states[s].orEmpty().sortedBy { CircuitBreaker.State.valueOf(it).ordinal }.joinToString("/")))
    }

    // One request that timed out and was retried, drawn as a waterfall
    val spans = Tracer.spans.toList()
    val roots = spans.filter { it.service == "gateway" && it.parentId == null }
    val retried = roots.firstOrNull { root -> spans.any { it.parentId == root.spanId && it.outcome == "timeout" } && spans.count { it.parentId == root.spanId } >= 2 }
        ?: roots.firstOrNull { root -> spans.count { it.parentId == root.spanId } >= 2 }
    if (retried != null) {
        println("\ntrace ${retried.traceId}: ${retried.name} -> ${retried.outcome} in ${retried.durMs} ms")
        val tree = mutableListOf(retried to 0)
        spans.filter { it.parentId == retried.spanId }.sortedBy { it.startMs }.forEach { a ->
            tree += a to 1
            spans.filter { it.parentId == a.spanId }.forEach { tree += it to 2 }
        }
        val scale = maxOf(retried.durMs, tree.maxOf { it.first.startMs + it.first.durMs - retried.startMs })
        for ((s, depth) in tree) {
            val from = ((s.startMs - retried.startMs) * 32 / scale).toInt()
            val width = maxOf(1, (s.durMs * 32 / scale).toInt())
            println("%-20s %s%s%s %5d ms  %s".format("  ".repeat(depth) + if (depth == 1) s.name else s.service,
                " ".repeat(from), "█".repeat(width), " ".repeat(maxOf(0, 32 - from - width)), s.durMs, s.outcome))
        }
    }

    println("\nGET /metrics")
    println(http.send(HttpRequest.newBuilder(URI.create("$base/metrics")).build(), HttpResponse.BodyHandlers.ofString()).body())
    println("p95 from the histogram: ≤ ${gateway.latency.percentileBound(0.95)} ms")
    gateway.server.stop(0); pricing.server.stop(0)
    System.exit(0)                                                             // hung 2 s pricing handlers may still be sleeping
}
