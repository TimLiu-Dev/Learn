package fees

/** Late fee in cents: 25 cents a day, at most 10 dollars. Early or on-time returns pay nothing. */
fun lateFeeCents(daysLate: Int): Int = if (daysLate <= 0) 0 else minOf(daysLate * 25, 1000)
