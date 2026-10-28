// Spring Framework 7.0 cache abstraction, following "Declarative Annotation-based Caching". Not run here.
// Spring Boot needs @EnableCaching on a @Configuration class (not on the main class, the docs advise).
@Service
class BookCatalog {
    private final BookStore store;

    BookCatalog(BookStore store) { this.store = store; }

    @Cacheable(cacheNames = "books", sync = true)       // sync: one thread loads a missing key
    public Optional<Book> find(long id) { return store.find(id); }

    @CacheEvict(cacheNames = "books", key = "#book.id")
    public void save(Book book) { store.update(book.id(), book.title(), book.author(), book.year()); }
}
