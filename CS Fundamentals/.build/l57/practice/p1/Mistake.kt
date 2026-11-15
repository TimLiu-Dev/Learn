class Lease(
    private val durationMs: Long,
    private val wallMillis: () -> Long,
    private val monoNanos: () -> Long,
) {
    private val startedNanos = monoNanos()

    // Treats nanoseconds as milliseconds.
    fun remainingMs(): Long = durationMs - (monoNanos() - startedNanos)

    fun isValid(): Boolean = remainingMs() > 0

    fun expiresAtWall(): Long = wallMillis() + remainingMs()
}
