package katas

fun average(vararg xs: Int): Double {
    if (xs.isEmpty()) return 0.0
    var sum = 0L                 // Long: two big Ints can't overflow it
    for (x in xs) sum += x
    return sum.toDouble() / xs.size
}

fun isLeap(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
