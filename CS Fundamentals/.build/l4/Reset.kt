class Counter(var count: Int)

fun resetAll(counters: List<Counter>) {
    for (c in counters) c.count = 0        // mutates the shared objects: caller sees it
}

fun replaceList(counters: MutableList<Counter>) {
    var local = counters
    local = mutableListOf(Counter(42))     // only the local arrow moves
}

fun main() {
    val counters = mutableListOf(Counter(3), Counter(7))
    resetAll(counters)
    println("${counters[0].count} ${counters[1].count}")
    replaceList(counters)
    println("${counters.size} ${counters[0].count}")
}
