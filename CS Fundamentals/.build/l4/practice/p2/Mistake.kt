class Counter(var count: Int)

fun copyCounters(counters: List<Counter>): List<Counter> =
    counters.toList()
