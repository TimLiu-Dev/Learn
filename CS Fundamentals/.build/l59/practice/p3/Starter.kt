enum class Verdict { WAIT, PROMOTE, ROLLBACK }

data class Stats(val requests: Int, val errors: Int) {
    val errorRate: Double get() = if (requests == 0) 0.0 else errors.toDouble() / requests
}

/** True if this user is in the canary slice. Same user, same percent: same answer, every time. */
fun inCanary(userId: String, percent: Int): Boolean {
    TODO("bucket = the user's hashCode mapped to 0..99; canary if bucket < percent")
}

/** Compare the canary with the control group (the servers still on the old release). */
fun verdict(control: Stats, canary: Stats, minRequests: Int = 100, tolerance: Double = 0.01): Verdict {
    TODO("WAIT, ROLLBACK or PROMOTE")
}
