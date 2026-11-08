import kotlin.math.floor
import kotlin.math.log10

fun dayAfter(day: Int, delta: Int): Int = (day + delta).mod(7)              // 0 = Monday

fun bucket(key: Any, buckets: Int): Int = key.hashCode().mod(buckets)

fun digitCount(n: Long): Int {
    require(n >= 0)
    var x = n
    var count = 1
    while (x >= 10) { x /= 10; count++ }
    return count
}

fun digitCountByLog(n: Long): Int = floor(log10(n.toDouble())).toInt() + 1   // the tempting version

fun main() {
    // Brute force: step one day at a time, forwards or backwards
    for (day in 0..6) for (delta in -30..30) {
        var d = day
        repeat(Math.abs(delta)) { d = if (delta > 0) (d + 1) % 7 else (d + 6) % 7 }
        check(dayAfter(day, delta) == d) { "dayAfter($day, $delta)" }
    }
    println("dayAfter matches stepping for 427 cases")

    val words = listOf("android", "polygenelubricants", "kotlin", "java", "spring boot")
    println("buckets: " + words.map { bucket(it, 10) })

    for (n in 0L..1_000_000L) check(digitCount(n) == n.toString().length)
    println("digitCount matches toString().length for 0..1000000")
    var nines = 9L
    for (k in 1..18) {
        if (digitCountByLog(nines) != k) { println("log10 version first fails at $nines: says ${digitCountByLog(nines)}"); break }
        nines = nines * 10 + 9
    }
}
