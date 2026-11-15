data class C(val name: String, val start: Int, val end: Int)   // half-open [start, end)

fun main() {
    val cs = listOf(C("a", 0, 6), C("b", 1, 3), C("c", 3, 5), C("d", 6, 8))
    val taken = mutableListOf<String>()
    var lastEnd = Int.MIN_VALUE
    for (c in cs.sortedBy { it.start }) {
        if (c.start >= lastEnd) {
            taken += c.name
            lastEnd = c.end
        }
    }
    println(taken)
}
