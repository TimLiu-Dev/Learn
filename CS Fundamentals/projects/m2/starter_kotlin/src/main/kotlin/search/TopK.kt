package search

/**
 * The [k] best items, best first, without sorting all of them (L20). Used by doc, q, or, prefix and top.
 * [bestFirst] orders items the way they are printed: negative means the first ranks higher.
 */
fun <T> Iterable<T>.topK(k: Int, bestFirst: Comparator<in T>): List<T> =
    TODO("topK (milestone 1): a heap of at most k items")
