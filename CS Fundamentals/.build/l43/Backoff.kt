import java.util.PriorityQueue
import kotlin.math.min

// A simulation, like the one in AWS's "Exponential Backoff and Jitter" post:
// 100 clients all fail at t=0. The server is down until t=1000 ms, then it can
// serve 10 requests per 100 ms window; anything beyond that fails too.
// Every failed client sleeps, then tries again.

const val CLIENTS = 100
const val BASE = 50L        // first backoff, ms
const val CAP = 3200L       // backoff never grows beyond this

fun simulate(jitter: Boolean): Pair<IntArray, Int> {
    val random = java.util.Random(43)
    val attemptsPerWindow = IntArray(600)                      // 600 windows of 100 ms = 60 s
    val servedPerWindow = IntArray(600)
    val queue = PriorityQueue<LongArray>(compareBy { it[0] })  // [time, client, attempt]
    repeat(CLIENTS) { queue += longArrayOf(0, it.toLong(), 0) }
    var totalAttempts = 0
    var lastSuccess = 0L
    while (queue.isNotEmpty()) {
        val (t, c, attempt) = queue.poll()
        val w = (t / 100).toInt()
        totalAttempts++
        attemptsPerWindow[w]++
        val ok = t >= 1000 && servedPerWindow[w] < 10
        if (ok) { servedPerWindow[w]++; lastSuccess = maxOf(lastSuccess, t); continue }
        val backoff = min(CAP, BASE shl attempt.toInt().coerceAtMost(20))
        val sleep = if (jitter) (random.nextDouble() * backoff).toLong() else backoff   // full jitter: 0..backoff
        queue += longArrayOf(t + sleep, c, attempt + 1)
    }
    println("  total attempts: $totalAttempts, last client served at ${lastSuccess} ms")
    return attemptsPerWindow to totalAttempts
}

fun show(attempts: IntArray) {
    for (w in 0 until 40 step 2) {
        val n = attempts[w] + attempts[w + 1]
        println("  %4d ms %4d %s".format(w * 100, n, "#".repeat((n + 7) / 8)))
    }
}

fun main() {
    println("== exponential backoff, no jitter (attempts per 200 ms)")
    show(simulate(jitter = false).first)
    println("== exponential backoff with full jitter")
    show(simulate(jitter = true).first)
}
