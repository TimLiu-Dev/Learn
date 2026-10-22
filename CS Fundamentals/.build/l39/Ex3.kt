interface BookCatalog { fun find(isbn: String): String?; fun size(): Int }

class FakeCatalog : BookCatalog {
    override fun find(isbn: String) = "title-$isbn"
    override fun size() = 1
}
class CountingCatalog(private val inner: BookCatalog) : BookCatalog by inner {
    var calls = 0
    override fun find(isbn: String): String? { calls++; return inner.find(isbn) }
}
class CachingCatalog(private val inner: BookCatalog) : BookCatalog by inner {
    private val cache = HashMap<String, String?>()
    override fun find(isbn: String): String? =
        if (isbn in cache) cache[isbn] else inner.find(isbn).also { cache[isbn] = it }
}

fun main() {
    val lookups = listOf("a", "b", "a", "a", "b")
    val outside = CountingCatalog(CachingCatalog(FakeCatalog()))     // counts every request
    lookups.forEach { outside.find(it) }
    val inside = CountingCatalog(FakeCatalog())
    val cachedInside = CachingCatalog(inside)                         // counts only cache misses
    lookups.forEach { cachedInside.find(it) }
    println("counter outside the cache: ${outside.calls}")
    println("counter inside the cache:  ${inside.calls}")
}
