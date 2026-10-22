const val CENTS_PER_DAY = 25

/** Returns the late fee in dollars. */          // true in version 1...
fun lateFee(daysLate: Int): Int = daysLate * CENTS_PER_DAY   // ...version 2 switched to cents

fun main() {
    // The caller trusted the comment:
    println("Late fee for 4 days: $" + lateFee(4))
}
