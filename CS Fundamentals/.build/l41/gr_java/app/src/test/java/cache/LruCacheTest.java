package cache;

import static org.junit.jupiter.api.Assertions.*;

import java.time.*;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;

class LruCacheTest {
    /** A clock the test moves by hand (L38: inject time). */
    static final class FakeClock extends Clock {
        private Instant now = Instant.parse("2026-10-26T12:00:00Z");
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
        void advance(Duration d) { now = now.plus(d); }
    }

    private final FakeClock clock = new FakeClock();
    private LruCache<String, Integer> cache(int cap) { return new LruCache<>(cap, Duration.ofSeconds(10), clock); }

    @Test void evictsTheLeastRecentlyUsedEntryWhenFull() {
        var c = cache(3);
        c.put("a", 1); c.put("b", 2); c.put("c", 3);
        c.put("d", 4);                                   // "a" is the oldest
        assertNull(c.get("a"));
        assertEquals(List.of("d", "c", "b"), c.keys());
    }

    @Test void getMakesAnEntryRecentlyUsed() {
        var c = cache(3);
        c.put("a", 1); c.put("b", 2); c.put("c", 3);
        c.get("a");                                      // now "b" is the oldest
        c.put("d", 4);
        assertNull(c.get("b"));
        assertEquals(1, c.get("a"));
    }

    @Test void putOnAnExistingKeyUpdatesWithoutEvicting() {
        var c = cache(3);
        c.put("a", 1); c.put("b", 2); c.put("c", 3);
        c.put("a", 10);
        assertEquals(3, c.size());
        assertEquals(10, c.get("a"));
        assertEquals(0, c.stats().evictions());
    }

    @Test void entriesExpireAfterTheTtl() {
        var c = cache(3);
        c.put("a", 1);
        clock.advance(Duration.ofSeconds(9));
        assertEquals(1, c.get("a"));
        clock.advance(Duration.ofSeconds(1));            // exactly 10 s: expired
        assertNull(c.get("a"));
        assertEquals(0, c.size());                       // an expired entry is dropped on read
    }

    @Test void statsCountHitsMissesAndEvictions() {
        var c = cache(1);
        c.put("a", 1); c.get("a"); c.get("x"); c.put("b", 2);
        assertEquals(new Stats(1, 1, 1), c.stats());
    }

    @Test void staysWithinCapacityUnder8Threads() throws Exception {
        var c = new LruCache<Integer, Integer>(100, Duration.ofHours(1));
        var pool = Executors.newFixedThreadPool(8);
        var gate = new CountDownLatch(1);
        var errors = new ConcurrentLinkedQueue<Throwable>();
        for (int t = 0; t < 8; t++) {
            final long seed = 41 + t;
            pool.submit(() -> {
                try {
                    gate.await();
                    var rnd = new Random(seed);
                    for (int i = 0; i < 100_000; i++) {
                        int k = rnd.nextInt(1_000);
                        if (rnd.nextBoolean()) c.put(k, k);
                        else { Integer v = c.get(k); if (v != null && v != k) throw new IllegalStateException("wrong value"); }
                    }
                } catch (Throwable e) { errors.add(e); }
            });
        }
        gate.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));
        assertTrue(errors.isEmpty(), "errors: " + errors);
        assertEquals(100, c.size());
        assertEquals(100, Set.copyOf(c.keys()).size());   // list and map agree: no lost or duplicate nodes
    }
}
