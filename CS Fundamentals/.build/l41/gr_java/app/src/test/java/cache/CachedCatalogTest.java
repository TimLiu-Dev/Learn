package cache;

import static org.junit.jupiter.api.Assertions.*;

import cache.Catalog.Book;
import cache.Catalog.BookCatalog;
import cache.Catalog.CachedCatalog;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class CachedCatalogTest {
    /** A fake slow catalog (L38): counts reads, takes 50 ms each. */
    static final class SlowFakeCatalog implements BookCatalog {
        final Map<Integer, Book> books = new ConcurrentHashMap<>(Map.of(1, new Book(1, "Dune"), 2, new Book(2, "Emma")));
        final AtomicInteger reads = new AtomicInteger();
        @Override public Book find(int id) {
            reads.incrementAndGet();
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return books.get(id);
        }
        @Override public void save(Book book) { books.put(book.id(), book); }
    }

    private final SlowFakeCatalog db = new SlowFakeCatalog();
    private final CachedCatalog catalog = new CachedCatalog(db, new LruCache<>(10, Duration.ofMinutes(1)));

    @Test void secondReadIsServedFromTheCache() {
        catalog.find(1); catalog.find(1);
        assertEquals(1, db.reads.get());
    }

    @Test void concurrentMissesForOneKeyHitTheDatabaseOnce() {
        var gate = new CountDownLatch(1);
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 50; i++) pool.submit(() -> { gate.await(); return catalog.find(2); });
            gate.countDown();
        }
        assertEquals(1, db.reads.get());
    }

    @Test void saveInvalidatesSoTheNextReadSeesTheNewValue() {
        catalog.find(1);
        catalog.save(new Book(1, "Dune Messiah"));
        assertEquals("Dune Messiah", catalog.find(1).title());
        assertEquals(2, db.reads.get());
    }

    @Test void missingBooksAreNotCached() {
        for (int i = 0; i < 3; i++) catalog.find(99);
        assertEquals(3, db.reads.get());
    }
}
