// T must be comparable to itself, so > is allowed on T
fun <T : Comparable<T>> largest(items: List<T>): T {
    var best = items[0]
    for (item in items) if (item > best) best = item
    return best
}

// two bounds need a where clause
fun <T> shortestSorted(items: List<T>): T
        where T : CharSequence, T : Comparable<T> {
    var best = items[0]
    for (item in items) {
        if (item.length < best.length || (item.length == best.length && item < best)) best = item
    }
    return best
}

fun main() {
    println(largest(listOf(3, 9, 4)))                 // Int is Comparable<Int>
    println(largest(listOf("pear", "apple", "fig")))  // String is Comparable<String>
    println(shortestSorted(listOf("kiwi", "fig", "date", "ash")))
}
