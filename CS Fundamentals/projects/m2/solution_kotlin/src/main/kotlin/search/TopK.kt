package search

import java.util.PriorityQueue

/**
 * The [k] best items, best first, without sorting all of them: O(n log k) time, O(k) memory (L20).
 * [bestFirst] orders items the way they are printed: negative means the first ranks higher.
 */
fun <T> Iterable<T>.topK(k: Int, bestFirst: Comparator<in T>): List<T> {
    require(k >= 1) { "k must be at least 1, was $k" }
    // A min-heap under the ranking: its root is the weakest of the current top k,
    // the first one to evict when something better arrives.
    val heap = PriorityQueue<T>(bestFirst.reversed())
    for (item in this) {
        heap.add(item)
        if (heap.size > k) heap.poll()
    }
    return buildList { while (heap.isNotEmpty()) add(heap.poll()) }.asReversed()
}
