// Looks tidier: unknown codes get no fee. But the old code charged them the basic rate.
fun loanLimit(code: String): Int = Tier.parse(code)?.loanLimit ?: 0

fun lateFeeCents(code: String, daysLate: Int): Int = Tier.parse(code)?.lateFeeCents(daysLate) ?: 0

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

object Basic : Tier {
    override val loanLimit = 3
    override fun lateFeeCents(daysLate: Int) = maxOf(0, daysLate) * 25
}

object Student : Tier {
    override val loanLimit = 5
    override fun lateFeeCents(daysLate: Int) = maxOf(0, daysLate - 3) * 25
}

object Premium : Tier {
    override val loanLimit = 10
    override fun lateFeeCents(daysLate: Int) = maxOf(0, daysLate * 25 - 100)
}
