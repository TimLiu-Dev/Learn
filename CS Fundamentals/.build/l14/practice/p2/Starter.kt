// Same answers as f, but f runs at most once per distinct input
fun memoize(f: (Int) -> Long): (Int) -> Long =
    TODO("make a map, then return a lambda that captures it")

// Should be [x * 1, x * 2, x * 3]. Every one multiplies by 4. Why?
fun multipliers(): List<(Int) -> Int> {
    val result = mutableListOf<(Int) -> Int>()
    var k = 1
    while (k <= 3) {
        result.add { x -> x * k }
        k++
    }
    return result
}
