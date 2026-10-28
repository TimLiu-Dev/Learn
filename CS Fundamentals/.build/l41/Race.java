import java.util.*;
import java.util.concurrent.*;

// The classic cache-aside race, forced into one exact interleaving with latches (L26):
// reader misses, reads the old value, the writer updates + invalidates, then the
// reader puts the OLD value into the cache. Then a fix: versioned values.
public class Race {
    record Row(String title, long version) {}

    public static void main(String[] args) throws Exception {
        System.out.println("plain cache-aside:  cache = " + run(false) + "   database = Emma (2nd ed.)");
        System.out.println("versioned cache:    cache = " + run(true) + "   database = Emma (2nd ed.)");
    }

    static String run(boolean versioned) throws Exception {
        var db = new ConcurrentHashMap<Integer, Row>(Map.of(2, new Row("Emma", 1)));
        var cache = new ConcurrentHashMap<Integer, Row>();
        var readerHasOldValue = new CountDownLatch(1);
        var writerDone = new CountDownLatch(1);

        Thread reader = Thread.ofVirtual().start(() -> {
            Row fromDb = db.get(2);                     // cache miss, so read the database: v1
            readerHasOldValue.countDown();
            await(writerDone);                          // ...the reader gets descheduled here...
            if (versioned) {
                // keep whichever row is newer: an old read can never overwrite a newer one
                cache.merge(2, fromDb, (old, nu) -> nu.version() > old.version() ? nu : old);
            } else {
                cache.put(2, fromDb);                   // writes v1 back into the cache: stale!
            }
        });
        Thread writer = Thread.ofVirtual().start(() -> {
            await(readerHasOldValue);
            Row updated = new Row("Emma (2nd ed.)", 2);
            db.put(2, updated);                         // update the database...
            if (versioned) cache.merge(2, updated, (old, nu) -> nu.version() > old.version() ? nu : old);
            else cache.remove(2);                       // ...then invalidate the cache
            writerDone.countDown();
        });
        reader.join(); writer.join();
        return cache.get(2).title();
    }

    static void await(CountDownLatch l) {
        try { l.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
