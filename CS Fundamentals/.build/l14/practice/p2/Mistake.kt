fun memoize(f: (Int) -> Long): (Int) -> Long = { n ->
    val cache = HashMap<Int, Long>()          // a new, empty cache on every call
    cache.getOrPut(n) { f(n) }
}

fun multipliers(): List<(Int) -> Int> {
    val result = mutableListOf<(Int) -> Int>()
    for (k in 1..3) {
        result.add { x -> x * k }
    }
    return result
}
