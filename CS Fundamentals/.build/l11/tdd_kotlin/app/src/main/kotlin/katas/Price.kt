package katas

fun formatPrice(cents: Long, symbol: String = "$", showCents: Boolean = true): String {
    val whole = cents / 100
    val rest = cents % 100
    return if (showCents) "$symbol$whole.${"%02d".format(rest)}" else "$symbol$whole"
}
