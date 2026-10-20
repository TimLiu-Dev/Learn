// Simulate n adds and count how many elements get copied during resizes.
fun copiesFor(n: Int, grow: (Int) -> Int): Long {
    var capacity = 4
    var size = 0
    var copies = 0L
    repeat(n) {
        if (size == capacity) {           // full: allocate a bigger array, copy everything over
            copies += size
            capacity = grow(capacity)
        }
        size++
    }
    return copies
}

fun main() {
    println("%9s %22s %22s %22s".format("n adds", "x2 copies (per add)", "x1.5 copies (per add)", "+10 copies (per add)"))
    for (n in listOf(1_000, 10_000, 100_000, 1_000_000)) {
        val cells = listOf<(Int) -> Int>({ it * 2 }, { it + it / 2 }, { it + 10 }).map { g ->
            val c = copiesFor(n, g)
            "%,d (%.1f)".format(c, c.toDouble() / n)
        }
        println("%,9d %22s %22s %22s".format(n, cells[0], cells[1], cells[2]))
    }
}
