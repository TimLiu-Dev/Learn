import kotlin.concurrent.thread

var count = 0     // a top-level var compiles to a static field of LostUpdateKt

fun main() {
    repeat(5) { run ->
        count = 0
        val workers = List(2) { thread { repeat(1_000_000) { count++ } } }
        workers.forEach { it.join() }
        println("run ${run + 1}: count = $count (expected 2000000)")
    }
}
