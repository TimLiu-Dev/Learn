// Amdahl's law: the best speed-up on n cores when a fraction p of the work can run in parallel.
fun amdahlSpeedup(p: Double, n: Int): Double {
    require(p in 0.0..1.0) { "p must be between 0 and 1, was $p" }
    require(n >= 1) { "n must be at least 1, was $n" }
    return 1 / ((1 - p) + p / n)
}

// Little's law: how many threads are busy on average, rounded up to whole threads.
fun poolSizeFor(requestsPerSecond: Int, latencyMs: Int): Int {
    val busyMs = requestsPerSecond.toLong() * latencyMs   // Long: 50,000 × 60,000 overflows an Int
    return ((busyMs + 999) / 1000).toInt()                // ceiling division: 1.5 threads means 2
}
