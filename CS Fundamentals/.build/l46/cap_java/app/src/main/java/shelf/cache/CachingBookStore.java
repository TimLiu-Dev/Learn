package shelf.cache;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
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
    private final ConcurrentHashMap<Object, CompletableFuture<Object>> loading = new ConcurrentHashMap<>();

    public CachingBookStore(BookStore store, LruCache<Long, Book> byId, LruCache<String, List<Book>> pages,
                            LruCache<String, Integer> counts) {
        this.store = store;
        this.byId = byId;
        this.pages = pages;
        this.counts = counts;
    }

    @Override
    public Optional<Book> find(long id) {
        return Optional.ofNullable(load(id, byId, () -> store.find(id).orElse(null)));
    }

    @Override
    public List<Book> list(int limit, int offset, String author) {
        return load(limit + "/" + offset + "/" + author, pages, () -> store.list(limit, offset, author));
    }

    @Override
    public int count(String author) {
        return load("count/" + author, counts, () -> store.count(author));
    }

    /**
     * Request coalescing (L41): concurrent misses for one key become one store call.
     * The slow load runs OUTSIDE the cache's lock: a lock held during I/O makes every
     * other caller wait for it (Exercise 2 found exactly that bug in my first version).
     */
    @SuppressWarnings("unchecked")
    private <K, V> V load(K key, LruCache<K, V> cache, Supplier<V> fetch) {
        V cached = cache.get(key);
        if (cached != null) return cached;
        var mine = new CompletableFuture<Object>();
        var running = loading.putIfAbsent(key, mine);
        if (running != null) return (V) running.join();          // someone else is loading it
        try {
            V v = fetch.get();
            if (v != null) cache.put(key, v);
            mine.complete(v);
            return v;
        } catch (RuntimeException e) {
            mine.completeExceptionally(e);
            throw e;
        } finally {
            loading.remove(key, mine);
        }
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
