package budget

/**
 * An amount of money as a whole number of pence. Never a Double (L2).
 * Add what you need: parsing, formatting, adding, comparing.
 */
@JvmInline
value class Money(val cents: Long) {
    companion object {
        /** "12", "12.5" and "-12.50" are valid; anything else gives null. */
        fun parse(text: String): Money? = TODO("Milestone 1: parse an amount without going through Double")
    }
}
