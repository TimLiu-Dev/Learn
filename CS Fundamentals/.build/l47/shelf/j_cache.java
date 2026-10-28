// Excerpt of L41 Catalog.java, lines 67–100 (tested there; not a standalone file)
    static final class CachedCatalog implements BookCatalog {
        private final BookCatalog db;
        private final LruCache<Integer, Book> cache;
        private final Map<Integer, CompletableFuture<Book>> inFlight = new ConcurrentHashMap<>();
        final AtomicLong coalesced = new AtomicLong();

        CachedCatalog(BookCatalog db, LruCache<Integer, Book> cache) { this.db = db; this.cache = cache; }

        @Override public Book find(int id) {
            Book cached = cache.get(id);
            if (cached != null) return cached;
            var mine = new CompletableFuture<Book>();
            var running = inFlight.putIfAbsent(id, mine);
            if (running != null) {                       // someone is already loading this id
                coalesced.incrementAndGet();
                return running.join();
            }
            try {
                Book book = db.find(id);
                if (book != null) cache.put(id, book);
                mine.complete(book);
                return book;
            } catch (RuntimeException e) {
                mine.completeExceptionally(e);
                throw e;
            } finally {
                inFlight.remove(id, mine);
            }
        }

        @Override public void save(Book book) {
            db.save(book);                               // write the source of truth first...
            cache.remove(book.id());                     // ...then invalidate the cached copy
        }
