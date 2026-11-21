import kotlin.random.Random

// Any function shaped like BookStore.list: the items from offset on, at most limit of them.
typealias Pager = (items: List<Int>, limit: Int, offset: Int) -> List<Int>

fun page(items: List<Int>, limit: Int, offset: Int): List<Int> = items.drop(offset).take(limit)

// null: the property held for every case. Otherwise: what failed, and for which input.
fun checkPaging(pager: Pager, seed: Long, runs: Int = 300): String? =
    TODO("generate runs cases from Random(seed), read every page, compare")
