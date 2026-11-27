enum class Verdict { WAIT, PROMOTE, ROLLBACK }

data class Stats(val requests: Int, val errors: Int) {
    val errorRate: Double get() = if (requests == 0) 0.0 else errors.toDouble() / requests
}

// The mistake: % keeps the sign, and most of these users' hash codes are negative.
fun inCanary(userId: String, percent: Int): Boolean =
    userId.hashCode() % 100 < percent

fun verdict(control: Stats, canary: Stats, minRequests: Int = 100, tolerance: Double = 0.01): Verdict = when {
    canary.requests < minRequests -> Verdict.WAIT
    canary.errorRate > control.errorRate + tolerance -> Verdict.ROLLBACK
    else -> Verdict.PROMOTE
}
