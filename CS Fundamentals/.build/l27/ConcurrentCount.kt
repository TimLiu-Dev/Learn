// 4 threads count the same words into one ConcurrentHashMap: get-then-put vs merge.
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

val WORDS = "the cat sat on the mat the end".split(" ")

fun count(add: (ConcurrentHashMap<String, Int>, String) -> Unit): Map<String, Int> {
    val counts = ConcurrentHashMap<String, Int>()
    val threads = List(4) {
        thread { repeat(250_000) { for (w in WORDS) add(counts, w) } }
    }
    threads.forEach { it.join() }
    return counts.toSortedMap()
}

fun main() {
    // Two separate atomic calls: another thread can update between them
    val broken = count { m, w -> m[w] = (m[w] ?: 0) + 1 }
    println("get + put: $broken")

    // One atomic read-modify-write
    val merged = count { m, w -> m.merge(w, 1, Int::plus) }
    println("merge:     $merged")
}
