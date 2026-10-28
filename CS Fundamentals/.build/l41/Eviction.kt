import java.util.Random

// LRU vs LFU vs FIFO on a skewed (Zipf-like) workload: a few keys are very popular.
// Capacity 100, 1,000 keys, 200,000 requests, fixed seed.
typealias Policy = (Int) -> Boolean                   // true = hit

fun lru(cap: Int): Policy {                           // L19: access-order LinkedHashMap
    val m = object : LinkedHashMap<Int, Boolean>(16, 0.75f, true) {
        override fun removeEldestEntry(e: MutableMap.MutableEntry<Int, Boolean>?) = size > cap
    }
    return { k -> if (m[k] != null) true else { m[k] = true; false } }
}

fun fifo(cap: Int): Policy {                          // insertion order: oldest goes first
    val m = object : LinkedHashMap<Int, Boolean>() {
        override fun removeEldestEntry(e: MutableMap.MutableEntry<Int, Boolean>?) = size > cap
    }
    return { k -> if (k in m) true else { m[k] = true; false } }
}

fun lfu(cap: Int): Policy {                           // evict the cached key used least often
    val count = HashMap<Int, Int>()
    return { k ->
        if (k in count) { count.merge(k, 1, Int::plus); true }
        else {
            if (count.size == cap) count.remove(count.minBy { it.value }.key)
            count[k] = 1
            false
        }
    }
}

fun main() {
    val keys = 1_000; val cap = 100; val requests = 200_000
    val cdf = DoubleArray(keys)                       // Zipf: P(rank r) is proportional to 1/r
    var total = 0.0
    for (r in 1..keys) { total += 1.0 / r; cdf[r - 1] = total }

    for (scans in listOf(false, true)) {
        println(if (scans) "Zipf + a one-off scan of 500 cold keys every 1,000 requests" else "Zipf only")
        for ((name, make) in listOf("LRU" to ::lru, "LFU" to ::lfu, "FIFO" to ::fifo)) {
            val p = make(cap)
            val rnd = Random(41)
            var hits = 0
            for (i in 0 until requests) {
                if (scans && i % 1_000 == 0)
                    for (cold in 0 until 500) p(10_000 + i * 500 + cold)       // never seen again
                val idx = cdf.binarySearch(rnd.nextDouble() * total)
                val key = if (idx >= 0) idx else -idx - 1
                if (p(key)) hits++
            }
            println("  %-4s hit rate %.1f%%".format(name, 100.0 * hits / requests))
        }
    }
}
