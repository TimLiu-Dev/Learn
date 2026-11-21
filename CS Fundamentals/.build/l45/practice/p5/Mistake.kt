import kotlin.random.Random

// Any function shaped like BookStore.list: the items from offset on, at most limit of them.
typealias Pager = (items: List<Int>, limit: Int, offset: Int) -> List<Int>

fun page(items: List<Int>, limit: Int, offset: Int): List<Int> = items.drop(offset).take(limit)

// Checks only the first page: it passes the correct pager, and misses every bug further on.
fun checkPaging(pager: Pager, seed: Long, runs: Int = 300): String? {
    val rnd = Random(seed)
    repeat(runs) {
        val items = List(rnd.nextInt(0, 31)) { rnd.nextInt(1000) }
        val limit = rnd.nextInt(1, 11)
        val first = try { pager(items, limit, 0) } catch (e: Exception) { return "threw $e" }
        if (first != items.take(limit)) return "first page was $first for items=$items, limit=$limit"
    }
    return null
}
