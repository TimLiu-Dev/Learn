package shelf.cache;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import shelf.domain.Book;
import shelf.store.BookStore;

/**
 * Cache-aside in front of any BookStore (a decorator, L39): same interface, so nothing else changes.
 * Reads try the cache first; writes go to the store, then invalidate (L41).
 */
public final class CachingBookStore implements BookStore {
    private final BookStore store;
    private final LruCache<Long, Book> byId;
    private final LruCache<String, List<Book>> pages;          // list() results, keyed by their arguments
    private final LruCache<String, Integer> counts;
    private final ConcurrentHashMap<Long, CompletableFuture<Optional<Book>>> loading = new ConcurrentHashMap<>();

    public CachingBookStore(BookStore store, LruCache<Long, Book> byId, LruCache<String, List<Book>> pages,
                            LruCache<String, Integer> counts) {
        this.store = store;
        this.byId = byId;
        this.pages = pages;
        this.counts = counts;
    }

    // Request coalescing (L41): 50 concurrent misses for one id become one store call.
    @Override
    public Optional<Book> find(long id) {
        Book cached = byId.get(id);
        if (cached != null) return Optional.of(cached);
        var mine = new CompletableFuture<Optional<Book>>();
        var running = loading.putIfAbsent(id, mine);
        if (running != null) return running.join();               // someone else is loading it
        try {
            Optional<Book> book = store.find(id);
            book.ifPresent(b -> byId.put(id, b));
            mine.complete(book);
            return book;
        } catch (RuntimeException e) {
            mine.completeExceptionally(e);
            throw e;
        } finally {
            loading.remove(id, mine);
        }
    }

    @Override
    public List<Book> list(int limit, int offset, String author) {
        return pages.getOrPut(limit + "/" + offset + "/" + author, () -> store.list(limit, offset, author));
    }

    @Override
    public int count(String author) {
        return counts.getOrPut(String.valueOf(author), () -> store.count(author));
    }

    // Any write can change any page or count, so those are dropped wholesale; single books by id.
    @Override
    public Book add(String title, String author, Integer year) {
        Book book = store.add(title, author, year);
        dropLists();
        return book;
    }

    @Override
    public Optional<Book> update(long id, String title, String author, Integer year) {
        Optional<Book> book = store.update(id, title, author, year);
        byId.remove(id);
        dropLists();
        return book;
    }

    @Override
    public boolean delete(long id) {
        boolean deleted = store.delete(id);
        byId.remove(id);
        dropLists();
        return deleted;
    }

    private void dropLists() {
        pages.clear();
        counts.clear();
    }

    public double hitRate() {
        return byId.stats().hitRate();
    }
}
