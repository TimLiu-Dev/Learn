fun season(month: Int): String = when (month) {
    12, 1, 2 -> "winter"
    in 3..5 -> "spring"
    in 6..8 -> "summer"
    in 9..11 -> "autumn"
    else -> "invalid"
}

fun main() {
    for (m in 1..12) print("${season(m)} ")
    println()
    println(season(13))
    for (i in 20 downTo 0 step 5) print("$i ")
    println("liftoff")
}
