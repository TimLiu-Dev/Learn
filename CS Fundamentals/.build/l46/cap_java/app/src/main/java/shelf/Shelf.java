package shelf;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import shelf.cache.CachingBookStore;
import shelf.cache.LruCache;
import shelf.http.AccessLogFilter;
import shelf.http.Metrics;
import shelf.http.RateLimitFilter;
import shelf.http.Router;
import shelf.http.Routes;
import shelf.service.BookService;
import shelf.service.LendingService;
import shelf.store.BookStore;
import shelf.store.Db;
import shelf.store.SqliteBookStore;
import shelf.store.SqliteLoanStore;
import shelf.store.SqliteMemberStore;

// The composition root (L38): the one place that names concrete classes and wires them together.
// v2 adds a connection pool, the L41 cache, the L43 rate limiter and telemetry, all chosen by Config.
public final class Shelf implements AutoCloseable {
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();   // L28
    private final HttpServer server;
    private final Db db;
    private final Config config;

    // v1's constructor still works: the L45 tests use it unchanged.
    public Shelf(Path dbFile, int port, Clock clock) throws IOException {
        this(Config.defaults().withPort(port).withDbFile(dbFile).withDrain(0), clock, System.out::println, UnaryOperator.identity());
    }

    public Shelf(Config config, Clock clock, Consumer<String> log,
                 UnaryOperator<BookStore> decorateBooks) throws IOException {     // lets a load test slow or count the store
        this.config = config;
        this.db = new Db(config.dbFile(), config.poolSize(), config.busyTimeoutMs());
        db.createSchema();
        BookStore sqliteBooks = decorateBooks.apply(new SqliteBookStore(db));
        CachingBookStore cache = config.cacheSize() == 0 ? null : new CachingBookStore(
            sqliteBooks,
            new LruCache<>(config.cacheSize(), config.cacheTtl(), clock),
            new LruCache<>(config.cacheSize(), config.cacheTtl(), clock),
            new LruCache<>(config.cacheSize(), config.cacheTtl(), clock));
        BookStore bookStore = cache != null ? cache : sqliteBooks;
        var loanStore = new SqliteLoanStore(db);
        Router router = new Router(log);
        Routes.register(router,
            new BookService(bookStore, loanStore),
            new LendingService(bookStore, new SqliteMemberStore(db), loanStore, clock));
        Metrics metrics = new Metrics();
        Routes.ops(router, db::ping, () -> {
            Map<String, Number> gauges = new LinkedHashMap<>();
            if (db.idleConnections() >= 0) gauges.put("shelf_db_idle_connections", db.idleConnections());   // pool saturation
            if (cache != null) gauges.put("shelf_cache_hit_ratio", Math.round(cache.hitRate() * 1000) / 1000.0);
            return metrics.render(gauges);
        });
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), config.port()), 0);
        HttpContext context = server.createContext("/", router::handle);
        context.getFilters().add(new AccessLogFilter(metrics, clock, log));          // outermost: sees every request
        if (config.rateCapacity() > 0) context.getFilters().add(new RateLimitFilter(config.rateCapacity(), config.ratePerSecond(), clock));
        server.setExecutor(executor);
    }

    public int port() {
        return server.getAddress().getPort();
    }

    public Shelf start() {
        server.start();
        return this;
    }

    // Graceful: stop accepting, let in-flight requests finish (up to drainSeconds), then release resources.
    @Override
    public void close() {
        server.stop(config.drainSeconds());
        executor.close();
        db.close();
    }
}
