package shelf

import com.sun.net.httpserver.HttpServer
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.file.Path
import java.time.Clock
import java.util.concurrent.Executors
import shelf.cache.CachingBookStore
import shelf.cache.LruCache
import shelf.http.AccessLogFilter
import shelf.http.Metrics
import shelf.http.RateLimitFilter
import shelf.http.Router
import shelf.http.jsonLine
import shelf.http.opsRoutes
import shelf.http.shelfRoutes
import shelf.service.BookService
import shelf.service.LendingService
import shelf.store.BookStore
import shelf.store.Db
import shelf.store.SqliteBookStore
import shelf.store.SqliteLoanStore
import shelf.store.SqliteMemberStore

// The composition root (L38): the one place that names concrete classes and wires them together.
// v2 adds a connection pool, the L41 cache, the L43 rate limiter and telemetry, all chosen by Config.
class Shelf(
    private val config: Config,
    clock: Clock,
    log: (String) -> Unit = ::println,
    decorateBooks: (BookStore) -> BookStore = { it },      // lets a load test slow or count the store
) : AutoCloseable {
    // v1's constructor still works: the L45 tests use it unchanged.
    constructor(dbFile: Path, port: Int, clock: Clock) : this(Config(port = port, dbFile = dbFile, drainSeconds = 0), clock)

    private val executor = Executors.newVirtualThreadPerTaskExecutor()     // one virtual thread per request (L28)
    private val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), config.port), 0)
    private val db = Db(config.dbFile, config.poolSize, config.busyTimeoutMs).also { it.createSchema() }
    private val metrics = Metrics()

    init {
        val sqliteBooks = decorateBooks(SqliteBookStore(db))
        val cache = if (config.cacheSize == 0) null else CachingBookStore(
            sqliteBooks,
            LruCache(config.cacheSize, config.cacheTtl, clock),
            LruCache(config.cacheSize, config.cacheTtl, clock),
            LruCache(config.cacheSize, config.cacheTtl, clock),
        )
        val bookStore = cache ?: sqliteBooks
        val loanStore = SqliteLoanStore(db)
        val router = Router(log)
        router.shelfRoutes(
            BookService(bookStore, loanStore),
            LendingService(bookStore, SqliteMemberStore(db), loanStore, clock),
        )
        router.opsRoutes(db::ping) {
            metrics.render(buildMap {
                db.idleConnections?.let { put("shelf_db_idle_connections", it) }      // saturation of the pool
                cache?.let { put("shelf_cache_hit_ratio", Math.round(it.hitRate() * 1000) / 1000.0) }
            })
        }
        val context = server.createContext("/", router::handle)
        context.filters += AccessLogFilter(metrics, clock, log)               // outermost: sees every request
        if (config.rateCapacity > 0) context.filters += RateLimitFilter(config.rateCapacity, config.ratePerSecond, clock)
        server.executor = executor
    }

    val port: Int get() = server.address.port

    fun start(): Shelf = apply { server.start() }

    // Graceful: stop accepting, let in-flight requests finish (up to drainSeconds), then release resources.
    override fun close() {
        server.stop(config.drainSeconds)
        executor.close()
        db.close()
    }
}

fun main() {
    val config = Config.fromEnv()
    val shelf = Shelf(config, Clock.systemUTC()).start()
    Runtime.getRuntime().addShutdownHook(Thread {                           // Ctrl-C or kill (SIGTERM)
        println(jsonLine("level" to "INFO", "msg" to "shutting down", "drainSeconds" to config.drainSeconds))
        shelf.close()
        println(jsonLine("level" to "INFO", "msg" to "stopped"))
    })
    println(jsonLine("level" to "INFO", "msg" to "listening", "port" to shelf.port, "db" to config.dbFile,
                     "pool" to config.poolSize, "cache" to config.cacheSize, "rateBurst" to config.rateCapacity))
}
