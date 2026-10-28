package shelf.load

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.time.Clock
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.pow
import kotlin.random.Random
import shelf.Config
import shelf.Shelf
import shelf.domain.Book
import shelf.store.BookStore
import shelf.store.Db
import shelf.store.SqliteBookStore

/** Pretends the database is across a network: every read takes [delayMs] extra, and is counted. */
class SlowBookStore(private val inner: BookStore, private val delayMs: Long) : BookStore by inner {
    val reads = AtomicInteger()
    private fun <T> slow(read: () -> T): T { reads.incrementAndGet(); Thread.sleep(delayMs); return read() }
    override fun find(id: Long): Book? = slow { inner.find(id) }
    override fun list(limit: Int, offset: Int, author: String?): List<Book> = slow { inner.list(limit, offset, author) }
    override fun count(author: String?): Int = slow { inner.count(author) }
}

data class Hit(val client: String, val startMs: Long, val ms: Double, val status: Int)

const val BOOKS = 200
val DB_DELAY_MS = System.getenv("LOAD_DB_DELAY_MS")?.toLong() ?: 2L   // Exercise 2: try 20

fun main() {
    val before = Config(poolSize = 0, cacheSize = 0, rateCapacity = 0.0, drainSeconds = 0)   // features off
    val after = Config(drainSeconds = 0)                                                       // the defaults
    val rows = listOf("before (no pool, no cache, no limit)" to run(before), "after (pool 8, cache, limit 50/s)" to run(after))
    println("16 paced clients x 200 requests (85% GET /books/{id}, 10% lists, 5% PUT) + 1 greedy client x 1000 GETs")
    println("Every database read costs an extra $DB_DELAY_MS ms. Latency percentiles are for the 16 paced clients.")
    println("%-38s %6s %6s %6s %7s %8s %6s %6s %5s".format("", "p50", "p95", "p99", "req/s", "DB reads", "hit%", "429s", "5xx"))  // hit% = GET /books/{id} only
    for ((name, r) in rows) println("%-38s %6.1f %6.1f %6.1f %7d %8d %6s %6d %5d".format(name, r.p50, r.p95, r.p99, r.rps, r.dbReads, r.hit, r.r429, r.r5xx))
    println("(ms; on my machine, on localhost)")
    for ((name, r) in rows) println("SERIES ${name.substringBefore(' ')} ${r.series}")
}

class Result(val p50: Double, val p95: Double, val p99: Double, val rps: Int, val dbReads: Int,
             val hit: String, val r429: Int, val r5xx: Int, val series: String)

fun run(config: Config): Result {
    val file = Files.createTempFile("shelf-load", ".db")
    Db(file).use { db -> db.createSchema(); val s = SqliteBookStore(db); repeat(BOOKS) { s.add("Book ${it + 1}", "Author ${it % 20}", 1950 + it % 70) } }
    var slow: SlowBookStore? = null
    val shelf = Shelf(config.copy(port = 0, dbFile = file), Clock.systemUTC(), log = {}) { SlowBookStore(it, DB_DELAY_MS).also { s -> slow = s } }.start()
    val base = "http://127.0.0.1:${shelf.port}"
    val client = HttpClient.newBuilder().executor(Executors.newVirtualThreadPerTaskExecutor()).build()
    val hits = ConcurrentLinkedQueue<Hit>()
    val t0 = System.nanoTime()

    fun send(who: String, method: String, path: String, body: String? = null) {
        val start = System.nanoTime()
        val req = HttpRequest.newBuilder(URI(base + path)).header("X-Client-Id", who).header("Content-Type", "application/json")
            .method(method, body?.let { HttpRequest.BodyPublishers.ofString(it) } ?: HttpRequest.BodyPublishers.noBody()).build()
        val status = client.send(req, HttpResponse.BodyHandlers.discarding()).statusCode()
        hits += Hit(who, (start - t0) / 1_000_000, (System.nanoTime() - start) / 1e6, status)
    }

    Executors.newVirtualThreadPerTaskExecutor().use { pool ->
        repeat(16) { c ->
            pool.submit {
                val rnd = Random(c)                                         // fixed seed: the same requests every run
                repeat(200) {
                    val roll = rnd.nextDouble()
                    when {
                        roll < 0.85 -> send("client-$c", "GET", "/books/${zipf(rnd)}")
                        roll < 0.95 -> send("client-$c", "GET", "/books?limit=10&offset=${rnd.nextInt(20) * 10}")
                        else -> zipf(rnd).let { id -> send("client-$c", "PUT", "/books/$id", """{"title":"Book $id","author":"Author ${id % 20}"}""") }
                    }
                    Thread.sleep(20)                                        // think time: ~40 requests/s per client
                }
            }
        }
        pool.submit { val rnd = Random(99); repeat(1000) { send("greedy", "GET", "/books/${zipf(rnd)}") } }   // no pause
    }
    val seconds = (System.nanoTime() - t0) / 1e9
    val metrics = client.send(HttpRequest.newBuilder(URI("$base/metrics")).build(), HttpResponse.BodyHandlers.ofString()).body()
    val hitRatio = Regex("shelf_cache_hit_ratio (\\S+)").find(metrics)?.groupValues?.get(1)?.toDouble()
    shelf.close()
    Files.deleteIfExists(file)

    val paced = hits.filter { it.client != "greedy" }.map { it.ms }.sorted()
    fun pct(p: Double) = paced[((paced.size - 1) * p).toInt()]
    val all = hits.toList()
    val reads = slow!!.reads.get()
    val series = all.groupBy { it.startMs / 500 }.toSortedMap().map { (bucket, hs) ->
        val p = hs.filter { it.client != "greedy" }.map { it.ms }.sorted()
        """{"t":${bucket * 0.5},"n":${hs.size},"p95":${"%.1f".format(if (p.isEmpty()) 0.0 else p[((p.size - 1) * 0.95).toInt()])},"r429":${hs.count { it.status == 429 }},"err":${hs.count { it.status >= 500 }}}"""
    }.joinToString(",", "[", "]")
    return Result(pct(0.50), pct(0.95), pct(0.99), (all.size / seconds).toInt(), reads,
                  hitRatio?.let { "%.0f".format(it * 100) } ?: "-",     // the cache's own count, from /metrics
                  all.count { it.status == 429 }, all.count { it.status >= 500 }, series)
}

/** Popular books get most of the traffic (Zipf-like, as in L41): book 1 most of all. */
fun zipf(rnd: Random): Long = (BOOKS * rnd.nextDouble().pow(3.0)).toLong() + 1
