package shelf.load;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import shelf.store.Db;
import shelf.store.SqliteBookStore;

// Part 1 measurements: SQLite write contention, and connection-per-call vs a pool.
public final class DbDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("Busy timeout: 200 writers add a book at the same instant");
        for (int timeout : new int[] {0, 3000}) {
            Path file = Files.createTempFile("shelf-busy", ".db");
            try (Db db = new Db(file, 0, timeout)) {
                db.createSchema();
                int pragma = db.read(c -> Db.query(c, "PRAGMA busy_timeout", rs -> rs.getInt(1)).getFirst());
                AtomicInteger busy = new AtomicInteger(), ok = new AtomicInteger();
                together(200, i -> {
                    try {
                        new SqliteBookStore(db).add("Book " + i, "Author " + (i % 10), null);
                        ok.incrementAndGet();
                    } catch (RuntimeException e) {
                        if (e.toString().contains("SQLITE_BUSY")) busy.incrementAndGet(); else throw e;
                    }
                });
                System.out.println("  busy_timeout=" + pragma + " ms: " + ok + " added, " + busy + " failed with SQLITE_BUSY");
            }
            Files.deleteIfExists(file);
        }

        System.out.println("Connections: 64 virtual threads x 500 book lookups each");
        Path file = Files.createTempFile("shelf-pool", ".db");
        try (Db db = new Db(file)) {
            db.createSchema();
            var s = new SqliteBookStore(db);
            for (int i = 0; i < 200; i++) s.add("Book " + i, "A", null);
        }
        for (int poolSize : new int[] {0, 4, 8, 16, 0, 4, 8, 16}) {      // twice: the first pass warms up the JIT
            try (Db db = new Db(file, poolSize, 3000)) {
                var store = new SqliteBookStore(db);
                long t0 = System.nanoTime();
                together(64, i -> { for (int k = 0; k < 500; k++) store.find((i * 500L + k) % 200 + 1); });
                long ms = (System.nanoTime() - t0) / 1_000_000;
                String label = poolSize == 0 ? "new connection per call" : "pool of " + poolSize;
                System.out.printf("  %-24s %5d ms  (%,d lookups/s)%n", label, ms, 32_000L * 1000 / ms);
            }
        }
        Files.deleteIfExists(file);
    }

    private static void together(int n, IntConsumer work) throws Exception {
        CountDownLatch start = new CountDownLatch(1);
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                int k = i;
                futures.add(pool.submit(() -> { start.await(); work.accept(k); return null; }));
            }
            start.countDown();
            for (Future<?> f : futures) f.get();
        }
    }
}
