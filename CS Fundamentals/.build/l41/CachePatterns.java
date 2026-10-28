import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

// Cache-aside, read-through, write-through, negative caching and TTL staleness,
// all in front of the same slow SQLite table.
public class CachePatterns {
    // The "database": a SQLite file, 5 ms extra per query so the cache matters
    static class BookDb implements AutoCloseable {
        final Connection c;
        int queries = 0;
        BookDb(Path file) throws SQLException {
            c = DriverManager.getConnection("jdbc:sqlite:" + file);
            try (var s = c.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS books(id INTEGER PRIMARY KEY, title TEXT NOT NULL)");
                s.execute("INSERT OR REPLACE INTO books VALUES (1,'Dune'),(2,'Emma'),(3,'Ulysses'),(4,'Beloved')");
            }
        }
        synchronized Optional<String> find(int id) {
            queries++;
            sleep(5);
            try (var q = c.prepareStatement("SELECT title FROM books WHERE id = ?")) {
                q.setInt(1, id);
                try (var rs = q.executeQuery()) { return rs.next() ? Optional.of(rs.getString(1)) : Optional.empty(); }
            } catch (SQLException e) { throw new IllegalStateException(e); }
        }
        synchronized void save(int id, String title) {
            try (var u = c.prepareStatement("INSERT OR REPLACE INTO books VALUES (?, ?)")) {
                u.setInt(1, id); u.setString(2, title); u.executeUpdate();
            } catch (SQLException e) { throw new IllegalStateException(e); }
        }
        public void close() throws SQLException { c.close(); }
    }

    // A tiny TTL cache: value plus an expiry time. Optional.empty() = "known missing".
    record Entry(Optional<String> value, long expiresAt) {}
    static class TtlCache {
        final Map<Integer, Entry> map = new ConcurrentHashMap<>();
        final long ttlNanos;
        int hits = 0, misses = 0;
        TtlCache(long ttlMillis) { ttlNanos = ttlMillis * 1_000_000; }
        Entry get(int id) {
            Entry e = map.get(id);
            if (e != null && e.expiresAt() > System.nanoTime()) { hits++; return e; }
            misses++; return null;
        }
        void put(int id, Optional<String> v) { map.put(id, new Entry(v, System.nanoTime() + ttlNanos)); }
        void invalidate(int id) { map.remove(id); }
    }

    // Read-through: the cache itself knows how to load. Callers never see the DB.
    static class ReadThrough {
        final TtlCache cache; final Function<Integer, Optional<String>> loader;
        ReadThrough(TtlCache cache, Function<Integer, Optional<String>> loader) { this.cache = cache; this.loader = loader; }
        Optional<String> get(int id) {
            Entry e = cache.get(id);
            if (e != null) return e.value();
            Optional<String> v = loader.apply(id);
            cache.put(id, v);
            return v;
        }
    }

    static final int[] READS = {1, 2, 1, 1, 3, 2, 1, 4, 1, 2};

    public static void main(String[] args) throws Exception {
        Path file = Files.createTempFile("l41-patterns", ".db");
        try (var db = new BookDb(file)) {
            // 0. No cache: every read goes to the database
            long t = System.nanoTime();
            for (int id : READS) db.find(id);
            System.out.printf("%-15s reads %d, db reads %d, %d ms%n", "no cache", READS.length, db.queries, (System.nanoTime() - t) / 1_000_000);

            // 1. Cache-aside: the application checks the cache, then the DB, then fills the cache
            db.queries = 0;
            var cache = new TtlCache(60_000);
            t = System.nanoTime();
            for (int id : READS) {
                Entry e = cache.get(id);
                if (e == null) { Optional<String> v = db.find(id); cache.put(id, v); }
            }
            report("cache-aside", t, cache, db);
            // ...and on a write: update the DB, then invalidate (not update) the cached copy
            db.save(2, "Emma (2nd ed.)");
            cache.invalidate(2);
            Entry e2 = cache.get(2);
            String after = e2 != null ? e2.value().orElseThrow() : db.find(2).orElseThrow();
            System.out.println("  after write + invalidate, id 2 = " + after);

            // 2. Read-through: same counts, but the loading logic moved into the cache
            db.queries = 0;
            var rt = new ReadThrough(new TtlCache(60_000), db::find);
            t = System.nanoTime();
            for (int id : READS) rt.get(id);
            report("read-through", t, rt.cache, db);

            // 3. Write-through: every write goes to the DB and the cache together
            db.queries = 0;
            var wt = new TtlCache(60_000);
            db.save(5, "Middlemarch"); wt.put(5, Optional.of("Middlemarch"));
            Entry e5 = wt.get(5);
            System.out.printf("write-through   read after write: %s, db reads %d%n", e5.value().orElseThrow(), db.queries);

            // 4. Negative caching: remember "not found" too
            db.queries = 0;
            for (int i = 0; i < 5; i++) db.find(99);
            System.out.printf("no negative cache: 5 lookups of id 99 -> %d db reads%n", db.queries);
            db.queries = 0;
            var neg = new ReadThrough(new TtlCache(60_000), db::find);
            for (int i = 0; i < 5; i++) neg.get(99);
            System.out.printf("negative cache:    5 lookups of id 99 -> %d db read%n", db.queries);
            db.save(99, "Persuasion");                          // the book arrives...
            System.out.println("  after insert, cache still says: " + neg.get(99).orElse("not found"));

            // 5. TTL staleness: someone updates the DB behind the cache's back
            var ttl = new ReadThrough(new TtlCache(300), db::find);
            ttl.get(3);
            long changed = System.nanoTime();
            db.save(3, "Ulysses (annotated)");
            String seen;
            do { sleep(50); seen = ttl.get(3).orElseThrow(); }
            while (seen.equals("Ulysses"));
            System.out.printf("TTL 300 ms: stale for %d ms, then saw '%s'%n", (System.nanoTime() - changed) / 1_000_000, seen);
        }
        Files.delete(file);
    }

    static void report(String name, long start, TtlCache c, BookDb db) {
        System.out.printf("%-15s reads %d, hits %d, misses %d, db reads %d, %d ms%n",
                name, READS.length, c.hits, c.misses, db.queries, (System.nanoTime() - start) / 1_000_000);
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
