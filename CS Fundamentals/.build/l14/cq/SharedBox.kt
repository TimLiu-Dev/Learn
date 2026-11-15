fun makePair(): Pair<() -> Unit, () -> Int> {
    var n = 0
    return Pair({ n++ }, { n * 10 })
}

fun main() {
    val (inc, peek) = makePair()
    inc()
    inc()
    val (inc2, _) = makePair()
    inc2()
    println(peek())
}
