import kotlin.concurrent.thread

fun main() {
    repeat(5) { run ->
        val counts = HashMap<String, Int>()
        val keys = HashMap<Int, Int>()
        val workers = List(2) { w ->
            thread {
                repeat(100_000) { i ->
                    val word = "w${i % 10}"
                    counts[word] = (counts[word] ?: 0) + 1     // read-modify-write
                    keys[w * 100_000 + i] = i                  // structural change: inserts, resizes
                }
            }
        }
        workers.forEach { it.join() }
        println("run ${run + 1}: total count = ${counts.values.sum()} (expected 200000), " +
                "keys.size = ${keys.size} (expected 200000)")
    }
}
