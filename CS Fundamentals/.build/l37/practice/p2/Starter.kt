// The membership code is switched on twice. A new tier means editing both.
fun loanLimit(code: String): Int =
    if (code == "basic") 3 else if (code == "student") 5 else if (code == "premium") 10 else 0

fun lateFeeCents(code: String, daysLate: Int): Int {
    if (daysLate <= 0) return 0
    if (code == "student") {
        if (daysLate <= 3) return 0          // three days' grace
        return (daysLate - 3) * 25
    } else if (code == "premium") {
        val fee = daysLate * 25 - 100        // 100 cents off, never below zero
        if (fee < 0) return 0
        return fee
    } else {
        return daysLate * 25
    }
}

interface Tier {
    val loanLimit: Int
    fun lateFeeCents(daysLate: Int): Int

    companion object {
        fun parse(code: String): Tier? = TODO("one object per tier; null for an unknown code")
    }
}
