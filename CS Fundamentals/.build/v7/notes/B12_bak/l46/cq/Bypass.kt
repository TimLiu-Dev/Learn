interface BookStore {
    fun find(id: Int): String
    fun findAll(ids: List<Int>): List<String> = ids.map { find(it) }
}

class DbStore : BookStore {
    var reads = 0
    override fun find(id: Int): String {
        reads++
        return "book $id"
    }
}

class CachingStore(private val inner: BookStore) : BookStore by inner {
    private val cache = mutableMapOf<Int, String>()
    override fun find(id: Int) = cache.getOrPut(id) { inner.find(id) }
}

fun main() {
    val db = DbStore()
    val store: BookStore = CachingStore(db)
    store.find(1)
    store.find(1)
    store.findAll(listOf(1, 1))
    println("reads: ${db.reads}")
}
