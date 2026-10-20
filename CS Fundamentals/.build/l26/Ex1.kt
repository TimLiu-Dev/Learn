import kotlin.concurrent.thread

fun main() {
    val data = IntArray(40_000_000) { it % 7 }
    val parts = 4
    val partial = LongArray(parts)                  // one slot per thread: nothing shared
    val chunk = data.size / parts

    val start = System.nanoTime()
    val workers = List(parts) { p ->
        thread(name = "summer-$p") {
            var sum = 0L                            // a local: lives on this thread's own stack
            for (i in p * chunk until (p + 1) * chunk) sum += data[i]
            partial[p] = sum                        // each thread writes only its own slot
        }
    }
    workers.forEach { it.join() }                   // join also makes the slots visible to main
    val total = partial.sum()
    val ms = (System.nanoTime() - start) / 1_000_000

    val s2 = System.nanoTime()
    val check = data.fold(0L) { acc, x -> acc + x }
    val ms2 = (System.nanoTime() - s2) / 1_000_000

    println("4 threads: $total in $ms ms; 1 thread: $check in $ms2 ms")
    println(workers.joinToString { "${it.name}=${it.state}" })
}
