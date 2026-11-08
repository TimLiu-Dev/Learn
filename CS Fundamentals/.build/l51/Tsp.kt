import java.util.Random
import kotlin.math.hypot
import kotlin.time.measureTime

class Cities(n: Int, seed: Long) {
    private val rnd = Random(seed)
    val x = DoubleArray(n) { rnd.nextDouble() * 100 }
    val y = DoubleArray(n) { rnd.nextDouble() * 100 }
    val n get() = x.size
    fun d(a: Int, b: Int) = hypot(x[a] - x[b], y[a] - y[b])
}

// Exact: try every tour that starts at city 0, built by backtracking (L50 permutations).
fun bruteForce(c: Cities): Double {
    var best = Double.MAX_VALUE
    val used = BooleanArray(c.n).also { it[0] = true }
    fun extend(last: Int, count: Int, length: Double) {
        if (length >= best) return                                  // prune: already too long
        if (count == c.n) { best = minOf(best, length + c.d(last, 0)); return }
        for (next in 1 until c.n) if (!used[next]) {
            used[next] = true; extend(next, count + 1, length + c.d(last, next)); used[next] = false
        }
    }
    extend(0, 1, 0.0)
    return best
}

// Heuristic: always walk to the nearest city you haven't visited. O(n²), no guarantee.
fun nearestNeighbour(c: Cities): Double {
    val used = BooleanArray(c.n).also { it[0] = true }
    var at = 0; var length = 0.0
    repeat(c.n - 1) {
        var best = -1
        for (j in 0 until c.n) if (!used[j] && (best < 0 || c.d(at, j) < c.d(at, best))) best = j
        used[best] = true; length += c.d(at, best); at = best
    }
    return length + c.d(at, 0)
}

fun main() {
    repeat(5) { bruteForce(Cities(8, it.toLong())) }                // warm-up
    println(" n  tours to try (n-1)!   exact ms   exact len   greedy len   greedy/exact")
    for (n in 8..15) {
        val c = Cities(n, n.toLong())
        var exact = 0.0
        val t = measureTime { exact = bruteForce(c) }
        val greedy = nearestNeighbour(c)
        val tours = (1L..n - 1).fold(1L) { a, k -> a * k }
        println("%2d  %19s  %9d  %10.1f  %11.1f  %13.2f".format(
            n, "%,d".format(tours), t.inWholeMilliseconds, exact, greedy, greedy / exact))
    }
    val big = Cities(10_000, 1)
    val t = measureTime { nearestNeighbour(big) }
    println("nearest neighbour on 10,000 cities: ${t.inWholeMilliseconds} ms")
}
