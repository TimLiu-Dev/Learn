// A leader lease: "I may act as leader for the next durationMs milliseconds."
// Both clocks are passed in, so the checks can control them:
//   wallMillis() works like System.currentTimeMillis(): NTP may step it, even backwards.
//   monoNanos()  works like System.nanoTime(): it only moves forward, counting nanoseconds.
class Lease(
    private val durationMs: Long,
    private val wallMillis: () -> Long,
    private val monoNanos: () -> Long,
) {
    private val startedNanos = monoNanos()   // a duration starts on the monotonic clock

    // Milliseconds left on the lease (zero or less once it has run out).
    fun remainingMs(): Long = durationMs - (monoNanos() - startedNanos) / 1_000_000

    fun isValid(): Boolean = remainingMs() > 0

    // For a log line: the time of day at which the lease runs out.
    // "What time is it?" is the time-of-day clock's job; the amount left comes from the monotonic one.
    fun expiresAtWall(): Long = wallMillis() + remainingMs()
}
