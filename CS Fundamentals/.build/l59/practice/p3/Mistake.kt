enum class Verdict { WAIT, PROMOTE, ROLLBACK }

data class Stats(val requests: Int, val errors: Int) {
    val errorRate: Double get() = if (requests == 0) 0.0 else errors.toDouble() / requests
}

// The mistake: abs() of Int.MIN_VALUE is still negative, so one unlucky user lands in bucket -48.
fun inCanary(userId: String, percent: Int): Boolean =
    Math.abs(userId.hashCode()) % 100 < percent

fun verdict(control: Stats, canary: Stats, minRequests: Int = 100, tolerance: Double = 0.01): Verdict = when {
    canary.requests < minRequests -> Verdict.WAIT
    canary.errorRate > control.errorRate + tolerance -> Verdict.ROLLBACK
    else -> Verdict.PROMOTE
}
