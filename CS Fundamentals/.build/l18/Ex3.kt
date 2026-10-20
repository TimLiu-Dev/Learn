// Merge two sorted lists into one sorted list. Ties take from `a` first (stable).
fun <T : Comparable<T>> merge(a: List<T>, b: List<T>): List<T> {
    val out = ArrayList<T>(a.size + b.size)
    var i = 0
    var j = 0
    while (i < a.size && j < b.size) {
        if (b[j] < a[i]) out.add(b[j++]) else out.add(a[i++])
    }
    while (i < a.size) out.add(a[i++])
    while (j < b.size) out.add(b[j++])
    return out
}

var shifts = 0

fun insertionSortCounting(a: IntArray) {
    for (i in 1 until a.size) {
        val key = a[i]
        var j = i - 1
        while (j >= 0 && key < a[j]) { a[j + 1] = a[j]; j--; shifts++ }
        a[j + 1] = key
    }
}

fun main() {
    println(merge(listOf(1, 4, 9), listOf(2, 3, 10, 11)))
    println(merge(listOf("b", "d"), emptyList()))

    val nearly = IntArray(1_000) { it }.also { it[10] = 500; it[500] = 10 }   // one swap away from sorted
    insertionSortCounting(nearly)
    println("nearly sorted: $shifts shifts")
    shifts = 0
    insertionSortCounting(IntArray(1_000) { 999 - it })                       // reversed
    println("reversed: $shifts shifts")
}
