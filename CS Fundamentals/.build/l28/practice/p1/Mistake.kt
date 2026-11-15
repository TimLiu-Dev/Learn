fun amdahlSpeedup(p: Double, n: Int): Double {
    require(p in 0.0..1.0) { "p must be between 0 and 1, was $p" }
    require(n >= 1) { "n must be at least 1, was $n" }
    return 1 / ((1 - p) + p / n)
}

fun poolSizeFor(requestsPerSecond: Int, latencyMs: Int): Int =
    requestsPerSecond * latencyMs / 1000                  // Int division rounds down
