fun memoize(f: (Int) -> Long): (Int) -> Long {
    val cache = HashMap<Int, Long>()          // one cache per memoize call, kept alive by the lambda
    return { n -> cache.getOrPut(n) { f(n) } }
}

fun multipliers(): List<(Int) -> Int> {
    val result = mutableListOf<(Int) -> Int>()
    for (k in 1..3) {                         // a fresh, read-only k on each iteration
        result.add { x -> x * k }
    }
    return result
}
