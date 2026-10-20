fun <T, R> Iterable<T>.myMap(transform: (T) -> R): List<R> {
    val result = ArrayList<R>()
    for (item in this) result.add(transform(item))
    return result
}

fun <T> Iterable<T>.myFilter(keep: (T) -> Boolean): List<T> {
    val result = ArrayList<T>()
    for (item in this) if (keep(item)) result.add(item)
    return result
}

fun <T, A> Iterable<T>.myFold(initial: A, combine: (A, T) -> A): A {
    var acc = initial
    for (item in this) acc = combine(acc, item)
    return acc
}

// map and filter are both special cases of fold
fun <T, R> Iterable<T>.mapViaFold(f: (T) -> R): List<R> =
    myFold(listOf()) { acc, item -> acc + f(item) }

fun <T> Iterable<T>.filterViaFold(keep: (T) -> Boolean): List<T> =
    myFold(listOf()) { acc, item -> if (keep(item)) acc + item else acc }

fun main() {
    val words = listOf("fold", "map", "filter", "zip")
    println(words.myMap { it.length } == words.map { it.length })
    println(words.myFilter { it.length > 3 })
    println(words.myFold(0) { total, w -> total + w.length })
    println(words.mapViaFold { it.uppercase() })
    println(words.filterViaFold { 'i' in it })

    // No wildcards needed: function types already have in/out variance
    val describe: (Any) -> String = { "<$it>" }
    val tags: List<CharSequence> = words.myMap(describe)
    println(tags)
}
