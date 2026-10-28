// Spring Framework 7.0 cache abstraction, following "Declarative Annotation-based Caching". Not run here.
// Spring Boot needs @EnableCaching on a @Configuration class (not on the main class, the docs advise).
@Service
class BookCatalog(private val store: BookStore) {

    @Cacheable(cacheNames = ["books"], sync = true)      // sync: one thread loads a missing key
    fun find(id: Long): Book? = store.find(id)

    @CacheEvict(cacheNames = ["books"], key = "#book.id")
    fun save(book: Book) = store.update(book.id, book.title, book.author, book.year)
}
