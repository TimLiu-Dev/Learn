import java.util.*;
import java.util.concurrent.*;

// Nearest Java equivalent: no dispatchers and no suspend, so the IO executor is a
// parameter, and loadCounts returns a future instead of suspending.
public class Practice {
    // A blocking call you can't change.
    interface Db {
        int countRows(String table);
    }

    // Called on the UI thread. It must return at once and never block that thread:
    // every countRows call runs concurrently on io.
    static CompletableFuture<Map<String, Integer>> loadCounts(Db db, List<String> tables, Executor io) {
        throw new UnsupportedOperationException("TODO: one supplyAsync per table on io, then combine them");
    }

    // CHECKS
    // Like a batch server: no table is answered until every table has been asked for.
    // A call that waits more than half a second gives up and returns -1.
    static class FakeDb implements Db {
        final CountDownLatch asked;
        final List<String> threads = Collections.synchronizedList(new ArrayList<>());
        FakeDb(int tables) { asked = new CountDownLatch(tables); }

        public int countRows(String table) {
            threads.add(Thread.currentThread().getName());
            asked.countDown();
            try {
                return asked.await(500, TimeUnit.MILLISECONDS) ? table.length() * 100 : -1;
            } catch (InterruptedException e) {
                return -1;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService ui = Executors.newSingleThreadExecutor(r -> new Thread(r, "ui"));
        ExecutorService io = Executors.newCachedThreadPool();
        List<String> tables = List.of("t1", "t2", "t3", "t4", "t5");
        Map<String, Integer> all200 = new TreeMap<>();
        for (String t : tables) all200.put(t, 200);
        try {
            check("every table gets its count", all200, () -> new TreeMap<>(loadCounts(new FakeDb(5), tables, io).join()));
            FakeDb db = new FakeDb(5);
            check("no blocking call runs on the UI thread", false, () ->
                ui.submit(() -> loadCounts(db, tables, io)).get().thenApply(m -> db.threads.contains("ui")).join());
            check("loadCounts returns at once, without waiting", true, () -> {
                FakeDb slow = new FakeDb(6);        // one table is never asked for, so every call waits
                CompletableFuture<Map<String, Integer>> f = loadCounts(slow, tables, io);
                boolean returnedEarly = !f.isDone();
                f.join();
                return returnedEarly;
            });
            check("no tables, no calls", Map.of(), () -> loadCounts(new FakeDb(0), List.of(), io).join());
        } finally {
            ui.shutdown();
            io.shutdown();
        }
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
