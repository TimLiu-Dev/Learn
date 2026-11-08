class Counter(var count: Int)

fun swapCounts(a: Counter, b: Counter) {
    val tmp = a.count
    a.count = b.count
    b.count = tmp
}

fun topThree(scores: MutableList<Int>): List<Int> {
    val sorted = scores
    sorted.sortDescending()
    return sorted.take(3)
}
