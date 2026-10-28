package cache

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.time.Duration
import java.util.Collections
import java.util.Random
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.math.ceil

fun main(args: Array<String>) {
    if (args.firstOrNull() == "bench") bench() else finalDemo()
}

// ── Final: the same SQLite catalog served with and without the cache ─────────────
fun finalDemo() {
    val file = Files.createTempFile("l41-final", ".db")
    SqliteCatalog(file, 0).seed(200)

    val plainDb = SqliteCatalog(file, delayMillis = 20)
    val plainServer = startServer(plainDb) { """{"dbQueries":${plainDb.queries.get()}}""" }

    val cachedDb = SqliteCatalog(file, delayMillis = 20)
    val cached = CachedCatalog(cachedDb, LruCache(capacity = 50, ttl = Duration.ofSeconds(60)))
    val cachedServer = startServer(cached) {
        val s = cached.stats()
        """{"hits":${s.hits},"misses":${s.misses},"evictions":${s.evictions},""" +
            """"coalesced":${cached.coalesced.get()},"dbQueries":${cachedDb.queries.get()}}"""
    }
    val client = HttpClient.newHttpClient()
    val base = "http://127.0.0.1:${cachedServer.address.port}"

    fun call(method: String, path: String, body: String? = null): String {
        val req = HttpRequest.newBuilder(URI.create(base + path))
            .method(method, if (body == null) HttpRequest.BodyPublishers.noBody() else HttpRequest.BodyPublishers.ofString(body))
            .build()
        val res = client.send(req, HttpResponse.BodyHandlers.ofString())
        return "$method $path -> ${res.statusCode()} ${res.body()}".trim()
    }

    println(call("GET", "/books/1"))
    println(call("GET", "/books/1"))
    println(call("PUT", "/books/1", "Dune"))
    println(call("GET", "/books/1"))
    println(call("GET", "/books/999"))
    println(call("GET", "/stats"))

    // 50 concurrent requests for a cold key: coalescing sends one query to the database
    val before = cachedDb.queries.get()
    val gate = CountDownLatch(1)
    Executors.newVirtualThreadPerTaskExecutor().use { pool ->
        repeat(50) { pool.submit { gate.await(); call("GET", "/books/7") } }
        gate.countDown()
    }
    println("50 concurrent GET /books/7 -> ${cachedDb.queries.get() - before} database query, coalesced ${cached.coalesced.get()}")

    println()
    loadTest("without cache", plainServer.address.port, client)
    println("  ${plainDb.queries.get()} database queries")
    val q0 = cachedDb.queries.get()
    val s0 = cached.stats()
    loadTest("with cache", cachedServer.address.port, client)
    val s1 = cached.stats()
    val hits = s1.hits - s0.hits; val misses = s1.misses - s0.misses
    println("  ${cachedDb.queries.get() - q0} database queries, hit rate %.1f%%, %d evictions"
        .format(100.0 * hits / (hits + misses), s1.evictions - s0.evictions))

    plainServer.stop(0); cachedServer.stop(0)
    Files.delete(file)
}

/** 16 clients x 125 requests, ids drawn from a Zipf-like distribution over 200 books. */
fun loadTest(name: String, port: Int, client: HttpClient) {
    val cdf = DoubleArray(200)
    var total = 0.0
    for (r in 1..200) { total += 1.0 / r; cdf[r - 1] = total }
    val latencies = Collections.synchronizedList(ArrayList<Double>())
    val t = System.nanoTime()
    Executors.newVirtualThreadPerTaskExecutor().use { pool ->
        for (c in 0 until 16) pool.submit {
            val rnd = Random(41L + c)
            repeat(125) {
                val idx = cdf.binarySearch(rnd.nextDouble() * total)
                val id = (if (idx >= 0) idx else -idx - 1) + 1
                val req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:$port/books/$id")).build()
                val start = System.nanoTime()
                client.send(req, HttpResponse.BodyHandlers.discarding())
                latencies += (System.nanoTime() - start) / 1e6
            }
        }
    }
    val wall = (System.nanoTime() - t) / 1_000_000
    val sorted = latencies.sorted()
    fun p(x: Double) = sorted[maxOf(0, ceil(x / 100 * sorted.size).toInt() - 1)]
    println("%-13s %d requests in %d ms: p50 %.1f ms, p95 %.1f ms, p99 %.1f ms"
        .format(name, sorted.size, wall, p(50.0), p(95.0), p(99.0)))
}

// ── Part 3: my LruCache vs a synchronized LinkedHashMap under contention ─────────
fun bench() {
    val cap = 1_000
    val mine = LruCache<Int, Int>(cap, Duration.ofHours(1))
    val jdk = Collections.synchronizedMap(object : LinkedHashMap<Int, Int>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, Int>?) = size > cap
    })
    val impls = listOf<Pair<String, (Int, Boolean) -> Unit>>(
        "LruCache (ReentrantLock)" to { k, read -> if (read) mine.get(k) else mine.put(k, k) },
        "synchronized LinkedHashMap" to { k, read -> if (read) jdk[k] else jdk[k] = k },
    )
    for (threads in listOf(1, 8)) {
        for ((name, op) in impls) {
            val best = (1..4).minOf { run(threads, op) }      // first runs warm up the JIT
            println("%-27s %d thread%s: %4d ms for %,d ops".format(name, threads, if (threads == 1) " " else "s", best, threads * 400_000))
        }
    }
    println("LruCache: size ${mine.size}, ${mine.stats()}")
}

private fun run(threads: Int, op: (Int, Boolean) -> Unit): Long {
    val gate = CountDownLatch(1)
    val pool = Executors.newFixedThreadPool(threads)
    val done = CountDownLatch(threads)
    for (t in 0 until threads) pool.submit {
        val rnd = Random(41L + t)
        gate.await()
        repeat(400_000) { op(rnd.nextInt(2_000), rnd.nextInt(10) < 8) }   // 80% reads, 20% writes
        done.countDown()
    }
    val start = System.nanoTime()
    gate.countDown()
    done.await()
    pool.shutdown()
    return (System.nanoTime() - start) / 1_000_000
}
