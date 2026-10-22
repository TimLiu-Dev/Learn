import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

// The AI's cache pattern, isolated: 8 request threads filling one shared map.
// Returns the final size, or null if a worker is still running after 5 s.
fun fill(cache: MutableMap<String, Int>): Int? {
    val workers = (1..8).map { t ->
        thread(isDaemon = true) { repeat(10_000) { k -> cache.getOrPut("t$t-p$k") { k } } }
    }
    val deadline = System.nanoTime() + 5_000_000_000
    workers.forEach { it.join(maxOf(1, (deadline - System.nanoTime()) / 1_000_000)) }
    val stuck = workers.firstOrNull { it.isAlive } ?: return cache.size
    println("  STUCK: ${workers.count { it.isAlive }} thread(s) still running after 5 s, e.g.")
    stuck.stackTrace.take(3).forEach { println("    at $it") }
    return null
}

fun main() {
    for (run in 1..5) {
        val size = fill(HashMap())
        if (size == null) { println("run $run: HashMap never finished"); break }
        println("run $run: HashMap lost ${80_000 - size} of 80000 entries, ConcurrentHashMap has ${fill(ConcurrentHashMap())}")
    }
}
