class Counter(var count: Int)

// a and b are copies of the caller's arrows, so they reach the caller's objects.
fun swapCounts(a: Counter, b: Counter) {
    val tmp = a.count
    a.count = b.count
    b.count = tmp
}

// sortedDescending() returns a NEW list, so the caller's list is never touched.
// A read-only List<Int> parameter makes the compiler refuse an in-place sort.
fun topThree(scores: List<Int>): List<Int> =
    scores.sortedDescending().take(3)
