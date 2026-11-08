fun isLeap(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0

fun daysIn(year: Int): Int = if (isLeap(year)) 366 else 365

// Seconds from 1 January 1970 to 1 January of year (a day has 86,400 seconds)
fun secondsSince1970(year: Int): Long {
    var total = 0L                    // Long from the start: the sum passes Int.MAX_VALUE in 2038
    for (y in 1970 until year) {      // 1970, 1971, ..., year - 1
        total += daysIn(y) * 86_400
    }
    return total
}
