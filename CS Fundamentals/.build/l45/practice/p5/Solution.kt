import kotlin.random.Random

// Any function shaped like BookStore.list: the items from offset on, at most limit of them.
typealias Pager = (items: List<Int>, limit: Int, offset: Int) -> List<Int>

fun page(items: List<Int>, limit: Int, offset: Int): List<Int> = items.drop(offset).take(limit)

// null: the property held for every case. Otherwise: what failed, and for which input.
fun checkPaging(pager: Pager, seed: Long, runs: Int = 300): String? {
    val rnd = Random(seed)                                   // same seed, same cases: a failure can be replayed
    repeat(runs) {
        val items = List(rnd.nextInt(0, 31)) { rnd.nextInt(1000) }
        val limit = rnd.nextInt(1, 11)
        val case = "items=$items, limit=$limit"
        val read = mutableListOf<Int>()
        var offset = 0
        try {
            while (offset <= items.size + limit) {           // a bound, so a pager that never ends can't hang us
                val p = pager(items, limit, offset)
                if (p.size > limit) return "page at offset $offset has ${p.size} items: $case"
                if (p.isEmpty()) break
                read += p
                offset += limit
            }
        } catch (e: Exception) {
            return "threw $e at offset $offset: $case"           // an exception is a counterexample too
        }
        if (read != items) return "pages gave $read: $case"
    }
    return null
}
