import java.util.Random

// Exercise solution: how far off is a percentile read from histogram buckets?
fun bucketEstimate(sorted: LongArray, bounds: LongArray, p: Double): Long {
    val target = p * sorted.size
    for (b in bounds) if (sorted.count { it <= b } >= target) return b
    return Long.MAX_VALUE
}

fun main() {
    val random = Random(43)
    // 10,000 request times: mostly 8–37 ms, 4% slow ones between 300 and 999 ms
    val ms = LongArray(10_000) { if (random.nextInt(100) < 4) 300L + random.nextInt(700) else 8L + random.nextInt(30) }
    val sorted = ms.sortedArray()
    fun exact(p: Double) = sorted[(p * sorted.size).toInt().coerceAtMost(sorted.size - 1)]
    val coarse = longArrayOf(5, 10, 25, 50, 100, 250, 500, 1000)
    val fine = longArrayOf(5, 10, 15, 20, 25, 30, 35, 40, 50, 100, 250, 400, 600, 800, 1000)
    println("     exact   coarse buckets   fine buckets")
    for (p in listOf(0.50, 0.95, 0.99)) {
        println("p%-3d %4d ms   ≤ %4d ms        ≤ %4d ms".format((p * 100).toInt(), exact(p), bucketEstimate(sorted, coarse, p), bucketEstimate(sorted, fine, p)))
    }
}
