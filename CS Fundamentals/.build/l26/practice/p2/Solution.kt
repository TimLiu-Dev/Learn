// Each thread runs count++ as three steps: 0 = read, 1 = add, 2 = write.
// schedule[k] is the thread ('A' or 'B') that takes its next step.
fun finalCount(start: Int, schedule: String): Int {
    var count = start                          // shared, on the heap
    val register = IntArray(2)                 // one per thread: registers are never shared
    val step = IntArray(2)
    for (who in schedule) {
        val t = who - 'A'                      // 'A' -> 0, 'B' -> 1
        when (step[t]) {
            0 -> register[t] = count           // getfield
            1 -> register[t] += 1              // iconst_1; iadd
            2 -> count = register[t]           // putfield
        }
        step[t]++
    }
    return count
}

// Every string with exactly a 'A's and b 'B's, e.g. allSchedules(1, 1) = [AB, BA].
fun allSchedules(a: Int, b: Int): List<String> {
    if (a == 0 && b == 0) return listOf("")    // base case: one empty schedule
    val result = mutableListOf<String>()
    if (a > 0) allSchedules(a - 1, b).forEach { result += "A$it" }
    if (b > 0) allSchedules(a, b - 1).forEach { result += "B$it" }
    return result
}
