// A leader lease: "I may act as leader for the next durationMs milliseconds."
// Both clocks are passed in, so the checks can control them:
//   wallMillis() works like System.currentTimeMillis(): NTP may step it, even backwards.
//   monoNanos()  works like System.nanoTime(): it only moves forward, counting nanoseconds.
class Lease(
    private val durationMs: Long,
    private val wallMillis: () -> Long,
    private val monoNanos: () -> Long,
) {
    private val startedAt = wallMillis()

    // Milliseconds left on the lease (zero or less once it has run out).
    fun remainingMs(): Long = durationMs - (wallMillis() - startedAt)

    fun isValid(): Boolean = remainingMs() > 0

    // For a log line: the time of day at which the lease runs out.
    fun expiresAtWall(): Long = TODO("the wall clock's reading now, plus the time left")
}
