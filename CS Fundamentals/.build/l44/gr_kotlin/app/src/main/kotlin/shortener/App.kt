package shortener

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.util.Random
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.pow

fun main(args: Array<String>) = when (args.firstOrNull()) {
    "demo" -> demo(args.getOrNull(1)?.toLong() ?: 20)
    "load" -> load()
    else -> println("usage: demo [seconds] | load")
}

/** A real server on a fixed port, for the curl session. Stops itself. */
fun demo(seconds: Long) {
    val db = Files.createTempFile("shortener", ".db")
    val server = ShortenerServer(LinkStore(db.toString()), port = 18044, verbose = true,
                                 limiter = RateLimiter(capacity = 3.0, perSecond = 0.1))
    server.start()
    println("listening on http://127.0.0.1:${server.port} for $seconds s")
    Thread.sleep(seconds * 1000)
    server.stop()
    Files.deleteIfExists(db)
}

/** Zipf-like popularity: rank r is chosen with weight 1/r (a few links get most clicks). */
fun zipfCodes(codes: List<String>, n: Int, seed: Long): List<String> {
    val weights = DoubleArray(codes.size) { 1.0 / (it + 1) }
    val cdf = weights.runningReduce { a, b -> a + b }.map { it / weights.sum() }
    val rnd = Random(seed)
    return List(n) {
        val u = rnd.nextDouble()
        codes[cdf.indexOfFirst { it >= u }.coerceAtLeast(0)]
    }
}

fun load() {
    println("%-28s %8s %8s %8s %9s %11s".format("config", "p50", "p95", "p99", "hit rate", "DB queries"))
    for ((latencyMs, cacheSize) in listOf(0L to 0, 0L to 10_000, 1L to 0, 1L to 10_000)) {
        val db = Files.createTempFile("load", ".db")
        val store = LinkStore(db.toString(), simulatedLatencyMs = latencyMs)
        val codes = (1..1000).map { store.create("https://example.com/page/$it", null, 0, null).code }
        val server = ShortenerServer(store, cacheCapacity = cacheSize).also { it.start() }
        val client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build()

        fun run(requests: List<String>): LongArray {
            val nanos = LongArray(requests.size)
            val next = AtomicInteger()
            Executors.newVirtualThreadPerTaskExecutor().use { pool ->
                repeat(32) {                                           // 32 concurrent clients
                    pool.submit {
                        while (true) {
                            val i = next.getAndIncrement()
                            if (i >= requests.size) break
                            val req = HttpRequest.newBuilder(URI("http://127.0.0.1:${server.port}/${requests[i]}")).build()
                            val t0 = System.nanoTime()
                            val res = client.send(req, HttpResponse.BodyHandlers.discarding())
                            nanos[i] = System.nanoTime() - t0
                            check(res.statusCode() == 302) { "got ${res.statusCode()}" }
                        }
                    }
                }
            }
            return nanos
        }

        run(zipfCodes(codes, 2_000, seed = 7))                         // warm-up: JIT, connections, cache
        server.cache.hits.set(0); server.cache.misses.set(0); store.queries.set(0)
        val sorted = run(zipfCodes(codes, 10_000, seed = 44)).sorted()
        fun p(q: Double) = "%.2fms".format(sorted[(q * (sorted.size - 1)).toInt()] / 1e6)
        val hits = server.cache.hits.get()
        val hitRate = hits.toDouble() / (hits + server.cache.misses.get())
        val config = (if (cacheSize > 0) "LRU cache" else "no cache") + ", DB +${latencyMs}ms"
        println("%-28s %8s %8s %8s %8.1f%% %11d".format(config, p(0.50), p(0.95), p(0.99), hitRate * 100, store.queries.get()))
        server.stop()
        Files.deleteIfExists(db)
    }
}
