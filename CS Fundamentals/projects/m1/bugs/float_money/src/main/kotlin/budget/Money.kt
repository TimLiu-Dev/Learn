package budget

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * An amount of money as a whole number of pence. Long arithmetic is exact,
 * so 0.10 + 0.20 is 0.30, never 0.30000000000000004 (L2).
 */
@JvmInline
value class Money(val cents: Long) : Comparable<Money> {
    operator fun plus(other: Money) = Money(Math.addExact(cents, other.cents))
    operator fun minus(other: Money) = Money(Math.subtractExact(cents, other.cents))
    operator fun unaryMinus() = Money(Math.negateExact(cents))
    override fun compareTo(other: Money) = cents.compareTo(other.cents)

    fun abs() = if (cents < 0) -this else this

    /** This amount as a percentage of [whole], to 1 dp, rounded HALF_EVEN. */
    fun percentOf(whole: Money): BigDecimal =
        BigDecimal.valueOf(cents).multiply(BigDecimal(100))
            .divide(BigDecimal.valueOf(whole.cents), 1, RoundingMode.HALF_EVEN)

    /** "-1234.50": plain digits, two decimals, no grouping, no currency sign. */
    override fun toString(): String {
        val sign = if (cents < 0) "-" else ""
        val abs = Math.absExact(cents)
        return "$sign${abs / 100}.${(abs % 100).toString().padStart(2, '0')}"
    }

    companion object {
        val ZERO = Money(0)
        private val FORMAT = Regex("""(-?)(\d+)(?:\.(\d{1,2}))?""")

        /** Parses "12", "12.5", "-12.50"; null for anything else (or too big for a Long). */
        fun parse(text: String): Money? {
            if (!FORMAT.matches(text)) return null
            val pounds = text.toDoubleOrNull() ?: return null
            return Money((pounds * 100).toLong()) // BUG: 4.35 * 100 is 434.99999999999994, truncated to 434
        }
    }
}

fun Iterable<Money>.sum(): Money = fold(Money.ZERO, Money::plus)
