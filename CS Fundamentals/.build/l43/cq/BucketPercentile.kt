// Estimate a percentile from histogram buckets, as Part 3's Metrics does.
fun estimate(bounds: List<Int>, observed: List<Int>, p: Double): String {
    val target = p * observed.size
    for (le in bounds) {
        if (observed.count { it <= le } >= target) return "<= $le"
    }
    return "> ${bounds.last()}"
}

fun main() {
    val bounds = listOf(10, 25, 50, 100)
    val observed = List(18) { 8 } + listOf(60, 400)     // 20 requests, two slow ones
    println("p50 ${estimate(bounds, observed, 0.50)}, p95 ${estimate(bounds, observed, 0.95)}, p99 ${estimate(bounds, observed, 0.99)}")
}
