package cache;

import java.nio.file.Path;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// The catalog types live together in one file to keep the final short to read.
final class Catalog {
    private Catalog() {}

    record Book(int id, String title) {}

    interface BookCatalog {
        Book find(int id);                               // null when there's no such book
        void save(Book book);
    }

    /** The slow source of truth: SQLite, plus an injected delay per read. */
    static final class SqliteCatalog implements BookCatalog {
        private final Path file;
        private final long delayMillis;
        final AtomicLong queries = new AtomicLong();

        SqliteCatalog(Path file, long delayMillis) { this.file = file; this.delayMillis = delayMillis; }

        // One connection per call keeps the example simple; a real app uses a pool (L36).
        private Connection open() throws SQLException { return DriverManager.getConnection("jdbc:sqlite:" + file); }

        void seed(int count) throws SQLException {
            try (var c = open()) {
                try (var s = c.createStatement()) { s.execute("CREATE TABLE IF NOT EXISTS books(id INTEGER PRIMARY KEY, title TEXT NOT NULL)"); }
                c.setAutoCommit(false);
                try (var ins = c.prepareStatement("INSERT OR REPLACE INTO books VALUES (?, ?)")) {
                    for (int id = 1; id <= count; id++) { ins.setInt(1, id); ins.setString(2, "Book " + id); ins.addBatch(); }
                    ins.executeBatch();
                }
                c.commit();
            }
        }

        @Override public Book find(int id) {
            queries.incrementAndGet();
            try {
                Thread.sleep(delayMillis);
                try (var c = open(); var q = c.prepareStatement("SELECT title FROM books WHERE id = ?")) {
                    q.setInt(1, id);
                    try (var rs = q.executeQuery()) { return rs.next() ? new Book(id, rs.getString(1)) : null; }
                }
            } catch (SQLException | InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override public void save(Book book) {
            try (var c = open(); var u = c.prepareStatement("INSERT OR REPLACE INTO books VALUES (?, ?)")) {
                u.setInt(1, book.id()); u.setString(2, book.title()); u.executeUpdate();
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /** Cache-aside in front of any catalog, with request coalescing for concurrent misses. */
    static final class CachedCatalog implements BookCatalog {
        private final BookCatalog db;
        private final LruCache<Integer, Book> cache;
        private final Map<Integer, CompletableFuture<Book>> inFlight = new ConcurrentHashMap<>();
        final AtomicLong coalesced = new AtomicLong();

        CachedCatalog(BookCatalog db, LruCache<Integer, Book> cache) { this.db = db; this.cache = cache; }

        @Override public Book find(int id) {
            Book cached = cache.get(id);
            if (cached != null) return cached;
            var mine = new CompletableFuture<Book>();
            var running = inFlight.putIfAbsent(id, mine);
            if (running != null) {                       // someone is already loading this id
                coalesced.incrementAndGet();
                return running.join();
            }
            try {
                Book book = db.find(id);
                if (book != null) cache.put(id, book);
                mine.complete(book);
                return book;
            } catch (RuntimeException e) {
                mine.completeExceptionally(e);
                throw e;
            } finally {
                inFlight.remove(id, mine);
            }
        }

        @Override public void save(Book book) {
            db.save(book);                               // write the source of truth first...
            cache.remove(book.id());                     // ...then invalidate the cached copy
        }

        Stats stats() { return cache.stats(); }
    }
}
