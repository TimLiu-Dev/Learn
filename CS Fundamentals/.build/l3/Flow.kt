fun grade(score: Int): String = when (score) {
    in 90..100 -> "A"
    in 80..<90 -> "B"
    in 70..<80 -> "C"
    in 60..<70 -> "D"
    in 0..<60 -> "F"
    else -> "invalid"
}

fun dayKind(day: String): String = when (day) {
    "SAT", "SUN" -> "weekend"
    "MON", "TUE", "WED", "THU", "FRI" -> "weekday"
    else -> "unknown"
}

fun main() {
    val scores = listOf(95, 83, 70, 42, 101)
    for (s in scores) println("$s -> ${grade(s)}")

    val a = 7
    val b = 3
    val bigger = if (a > b) a else b      // if is an expression
    println("bigger: $bigger, SAT is a ${dayKind("SAT")}")

    for (i in 10 downTo 0 step 3) print("$i ")
    println()
    for (i in 0..<3) print("$i ")
    println()
    for ((index, s) in scores.withIndex()) {
        if (index == 2) break
        println("#$index = $s")
    }

    var tries = 0
    do {
        tries++
    } while (tries < 3)
    println("tries: $tries")
}
