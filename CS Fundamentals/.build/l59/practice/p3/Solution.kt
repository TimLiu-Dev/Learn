enum class Verdict { WAIT, PROMOTE, ROLLBACK }

data class Stats(val requests: Int, val errors: Int) {
    val errorRate: Double get() = if (requests == 0) 0.0 else errors.toDouble() / requests
}

/** True if this user is in the canary slice. Same user, same percent: same answer, every time. */
fun inCanary(userId: String, percent: Int): Boolean =
    Math.floorMod(userId.hashCode(), 100) < percent      // floorMod is never negative, even for Int.MIN_VALUE

/** Compare the canary with the control group (the servers still on the old release). */
fun verdict(control: Stats, canary: Stats, minRequests: Int = 100, tolerance: Double = 0.01): Verdict = when {
    canary.requests < minRequests -> Verdict.WAIT          // too little evidence either way
    canary.errorRate > control.errorRate + tolerance -> Verdict.ROLLBACK
    else -> Verdict.PROMOTE
}
