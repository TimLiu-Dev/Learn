package shelf.load;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import shelf.domain.Book;
import shelf.store.BookStore;

/** Pretends the database is across a network: every read takes delayMs extra, and is counted. */
public final class SlowBookStore implements BookStore {
    private final BookStore inner;
    private final long delayMs;
    final AtomicInteger reads = new AtomicInteger();

    public SlowBookStore(BookStore inner, long delayMs) {
        this.inner = inner;
        this.delayMs = delayMs;
    }

    private <T> T slow(Supplier<T> read) {
        reads.incrementAndGet();
        try { Thread.sleep(delayMs); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return read.get();
    }

    @Override public Optional<Book> find(long id) { return slow(() -> inner.find(id)); }
    @Override public List<Book> list(int limit, int offset, String author) { return slow(() -> inner.list(limit, offset, author)); }
    @Override public int count(String author) { return slow(() -> inner.count(author)); }
    @Override public Book add(String t, String a, Integer y) { return inner.add(t, a, y); }
    @Override public Optional<Book> update(long id, String t, String a, Integer y) { return inner.update(id, t, a, y); }
    @Override public boolean delete(long id) { return inner.delete(id); }
}
