fun finalCount(start: Int, schedule: String): Int {
    var count = start
    var register = 0                           // one register shared by both threads
    val step = IntArray(2)
    for (who in schedule) {
        val t = who - 'A'
        when (step[t]) {
            0 -> register = count
            1 -> register += 1
            2 -> count = register
        }
        step[t]++
    }
    return count
}

fun allSchedules(a: Int, b: Int): List<String> {
    if (a == 0 && b == 0) return listOf("")
    val result = mutableListOf<String>()
    if (a > 0) allSchedules(a - 1, b).forEach { result += "A$it" }
    if (b > 0) allSchedules(a, b - 1).forEach { result += "B$it" }
    return result
}
