// @Volatile makes every write visible, but count++ is still read, add, write.
import kotlin.concurrent.thread

@Volatile var count = 0

fun main() {
    for (run in 1..5) {
        count = 0
        val threads = List(4) {
            thread { repeat(1_000_000) { count++ } }
        }
        threads.forEach { it.join() }
        println("run $run: $count (expected 4000000)")
    }
}
