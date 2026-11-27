package fees

/** Late fee in cents: 30 cents a day. */
fun lateFeeCents(daysLate: Int): Int = maxOf(daysLate, 0) * 30
