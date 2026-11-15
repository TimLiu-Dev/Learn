// Thin wrappers: old callers keep working. An unknown code can't borrow,
// but still pays the basic rate. That's what the old code did, so we keep it.
fun loanLimit(code: String): Int = Tier.parse(code)?.loanLimit ?: 0

fun lateFeeCents(code: String, daysLate: Int): Int =
    (Tier.parse(code) ?: Basic).lateFeeCents(daysLate)

interface Tier {
    val loanLimit: Int
    fun lateFeeCents(daysLate: Int): Int

    companion object {
        fun parse(code: String): Tier? = when (code) {
            "basic" -> Basic
            "student" -> Student
            "premium" -> Premium
            else -> null
        }
    }
}

private const val CENTS_PER_DAY = 25

object Basic : Tier {
    override val loanLimit = 3
    override fun lateFeeCents(daysLate: Int) = maxOf(0, daysLate) * CENTS_PER_DAY
}

object Student : Tier {
    private const val GRACE_DAYS = 3
    override val loanLimit = 5
    override fun lateFeeCents(daysLate: Int) = maxOf(0, daysLate - GRACE_DAYS) * CENTS_PER_DAY
}

object Premium : Tier {
    private const val DISCOUNT_CENTS = 100
    override val loanLimit = 10
    override fun lateFeeCents(daysLate: Int) = maxOf(0, Basic.lateFeeCents(daysLate) - DISCOUNT_CENTS)
}
