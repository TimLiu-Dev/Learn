class Counter(var count: Int)

// A new list AND a new Counter for every element: no arrow leads back to the originals.
fun copyCounters(counters: List<Counter>): List<Counter> =
    counters.map { Counter(it.count) }
