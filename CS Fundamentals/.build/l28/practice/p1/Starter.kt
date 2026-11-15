// Amdahl's law: the best speed-up on n cores when a fraction p of the work can run in parallel.
fun amdahlSpeedup(p: Double, n: Int): Double {
    TODO("check the inputs, then apply the formula")
}

// Little's law: how many threads are busy on average, rounded up to whole threads.
fun poolSizeFor(requestsPerSecond: Int, latencyMs: Int): Int {
    TODO("L = λW, with λ in requests per second and W in milliseconds")
}
