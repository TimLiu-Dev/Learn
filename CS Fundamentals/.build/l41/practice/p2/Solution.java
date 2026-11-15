import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Practice {
    // A read-through cache in front of a slow loader (a database query, say).
    static class CoalescingCache<K, V> {
        private final Function<K, V> loader;
        private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<K, CompletableFuture<V>> inFlight = new ConcurrentHashMap<>();

        CoalescingCache(Function<K, V> loader) { this.loader = loader; }

        V get(K key) {
            V cached = cache.get(key);
            if (cached != null) return cached;
            CompletableFuture<V> mine = new CompletableFuture<>();
            CompletableFuture<V> running = inFlight.putIfAbsent(key, mine);  // atomic: one caller gets null
            if (running != null) return running.join();   // someone else is loading: wait for their result
            try {
                // Re-check: a load may have finished between our miss and our putIfAbsent.
                V value = cache.get(key);
                if (value == null) value = loader.apply(key);
                cache.put(key, value);                     // cache first, then leave inFlight
                mine.complete(value);
                return value;
            } catch (RuntimeException e) {
                mine.completeExceptionally(e);             // every waiter sees the failure
                throw e;
            } finally {
                inFlight.remove(key);                      // success or failure: never wedge the key
            }
        }
    }

    // CHECKS
    // A loader that counts its calls and blocks until the check releases it.
    static class SlowDb {
        final AtomicInteger calls = new AtomicInteger();
        final CountDownLatch release = new CountDownLatch(1);
        final boolean failFirst;
        SlowDb(boolean failFirst) { this.failFirst = failFirst; }
        String load(int id) {
            int n = calls.incrementAndGet();
            try { release.await(); } catch (InterruptedException e) { throw new RuntimeException(e); }
            if (failFirst && n == 1) throw new IllegalStateException("db down");
            return "book " + id;
        }
    }

    // Starts n readers of one key, waits until each is parked, then releases the loader.
    static List<String> readAtOnce(int n, CoalescingCache<Integer, String> cache, int id, SlowDb db) throws Exception {
        List<String> results = Collections.synchronizedList(new ArrayList<>());
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Thread t = new Thread(() -> {
                try { results.add(cache.get(id)); } catch (RuntimeException e) { results.add("failed"); }
            });
            t.setDaemon(true);
            t.start();
            threads.add(t);
        }
        try {
            long deadline = System.nanoTime() + 1_000_000_000L;
            while (threads.stream().anyMatch(t -> t.getState() != Thread.State.WAITING && t.getState() != Thread.State.TERMINATED)) {
                if (System.nanoTime() > deadline) throw new IllegalStateException("after 1 s, some readers were still not waiting on a load");
                Thread.yield();
            }
        } finally {
            db.release.countDown();
            for (Thread t : threads) t.join();
        }
        return results;
    }

    public static void main(String[] args) throws Exception {
        new CoalescingCache<Integer, String>(k -> "warm-up").get(0);
        check("a second read is served from the cache", 1, () -> {
            SlowDb db = new SlowDb(false);
            db.release.countDown();
            CoalescingCache<Integer, String> cache = new CoalescingCache<>(db::load);
            cache.get(1);
            cache.get(1);
            return db.calls.get();
        });
        check("8 concurrent misses for one key cause one load", "1 load, answers [book 7, book 7, book 7, book 7, book 7, book 7, book 7, book 7]", () -> {
            SlowDb db = new SlowDb(false);
            List<String> answers = readAtOnce(8, new CoalescingCache<>(db::load), 7, db);
            return db.calls.get() + " load, answers " + answers;
        });
        check("a failed load reaches every waiter, then the key can be retried", "1 load, answers [failed, failed, failed, failed], retry book 7, 2 loads", () -> {
            SlowDb db = new SlowDb(true);
            CoalescingCache<Integer, String> cache = new CoalescingCache<>(db::load);
            List<String> answers = readAtOnce(4, cache, 7, db);
            String first = db.calls.get() + " load, answers " + answers;
            return first + ", retry " + cache.get(7) + ", " + db.calls.get() + " loads";
        });
        check("a slow load does not block other keys", false, () -> {
            SlowDb db = new SlowDb(false);
            CoalescingCache<Integer, String> cache = new CoalescingCache<>(id -> id == 1 ? db.load(id) : "book " + id);
            Thread slow = new Thread(() -> cache.get(1));
            slow.setDaemon(true);
            slow.start();
            try {
                while (db.calls.get() == 0) Thread.yield();
                Thread other = new Thread(() -> cache.get(2));
                other.setDaemon(true);
                other.start();
                other.join(2000);
                return other.isAlive();
            } finally {
                db.release.countDown();
                slow.join();
            }
        });
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
