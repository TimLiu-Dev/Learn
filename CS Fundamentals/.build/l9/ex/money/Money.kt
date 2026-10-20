@file:JvmName("MoneyUtils")

fun cents(amount: Double): Long = Math.round(amount * 100)

class Money(val cents: Long) {
    @JvmOverloads
    fun format(symbol: String = "$", showCents: Boolean = true): String {
        val whole = cents / 100
        val rest = cents % 100
        return if (showCents) "$symbol$whole.${"%02d".format(rest)}" else "$symbol$whole"
    }

    companion object {
        @JvmStatic
        fun of(amount: Double): Money = Money(cents(amount))
    }
}
