class Duo<A, B>(val first: A, val second: B) {
    fun swap(): Duo<B, A> = Duo(second, first)
    override fun toString() = "($first, $second)"
}

fun <T> lastOrNull(items: List<T>): T? = if (items.isEmpty()) null else items[items.size - 1]

fun <T : Comparable<T>> isSorted(items: List<T>): Boolean {
    for (i in 1 until items.size) {
        if (items[i - 1] > items[i]) return false
    }
    return true
}

fun main() {
    println(lastOrNull(listOf("a", "b", "c")))
    println(lastOrNull(emptyList<String>()))
    val d = Duo(1, "one")
    println("$d -> ${d.swap()}")
    println("${isSorted(listOf(1, 2, 2, 5))} ${isSorted(listOf("b", "a"))} ${isSorted(emptyList<Int>())}")
}
