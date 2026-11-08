fun isLeap(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0

fun daysIn(year: Int): Int = if (isLeap(year)) 366 else 365

// Converts at the end, after the Int total has already wrapped
fun secondsSince1970(year: Int): Long {
    var total = 0
    for (y in 1970 until year) {
        total += daysIn(y) * 86_400
    }
    return total.toLong()
}
