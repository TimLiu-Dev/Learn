class Counter(var count: Int)

fun swapCounts(a: Counter, b: Counter) {
    TODO("swap a.count and b.count")
}

// The three highest scores, highest first.
fun topThree(scores: MutableList<Int>): List<Int> {
    scores.sortDescending()
    return scores.take(3)
}
