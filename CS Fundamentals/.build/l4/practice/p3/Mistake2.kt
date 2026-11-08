class Counter(var count: Int)

fun swapCounts(a: Counter, b: Counter) {
    a.count = a.count + b.count
    b.count = a.count - b.count
    a.count = a.count - b.count
}

fun topThree(scores: List<Int>): List<Int> =
    scores.sortedDescending().take(3)
