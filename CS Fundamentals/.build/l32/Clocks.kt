import java.time.*
import kotlin.time.TimeSource

// A clock you can set, the way NTP or a user can set the real one
class SettableClock(var now: Instant) : Clock() {
    override fun getZone(): ZoneId = ZoneOffset.UTC
    override fun withZone(zone: ZoneId): Clock = this
    override fun instant(): Instant = now
}

// Inject the clock instead of calling Instant.now() inside the logic (L38 builds on this)
class Loans(private val clock: Clock) {
    fun isOverdue(due: Instant): Boolean = Instant.now(clock).isAfter(due)
}

fun main() {
    val due = Instant.parse("2026-11-03T00:00:00Z")
    val fixed = Clock.fixed(Instant.parse("2026-11-05T12:00:00Z"), ZoneOffset.UTC)
    println("overdue on Nov 5?   ${Loans(fixed).isOverdue(due)}")
    println("overdue on Nov 2?   ${Loans(Clock.offset(fixed, Duration.ofDays(-3))).isOverdue(due)}")

    // Wall clock: "what time is it?" Fine for timestamps, wrong for measuring durations
    val wall = SettableClock(Instant.parse("2026-11-05T12:00:00Z"))
    val start = wall.millis()
    wall.now = wall.now.plusMillis(250)            // 250 ms of work...
    wall.now = wall.now.minusSeconds(2)            // ...then NTP steps the clock back 2 s
    println("wall-clock elapsed  ${wall.millis() - start} ms")

    // Monotonic clock: only moves forward, so use it for elapsed time and timeouts
    val mark = TimeSource.Monotonic.markNow()
    Thread.sleep(250)
    println("monotonic elapsed   ${mark.elapsedNow().inWholeMilliseconds} ms")
}
