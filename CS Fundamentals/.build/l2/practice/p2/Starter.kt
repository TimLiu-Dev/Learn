fun isLeap(year: Int): Boolean = TODO("L1's leap-year rule")

fun daysIn(year: Int): Int = TODO("366 in a leap year, otherwise 365")

// Seconds from 1 January 1970 to 1 January of year (a day has 86,400 seconds)
fun secondsSince1970(year: Int): Long {
    var total = 0
    for (y in 1970 until year) {      // 1970, 1971, ..., year - 1
        total += daysIn(y) * 86_400
    }
    return total.toLong()
}
