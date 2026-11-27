package katas

import java.time.Clock
import java.time.LocalTime

// Reads the real clock: the answer depends on when, and where, you run it.
fun greetingNow(): String = greetingAt(LocalTime.now())

// The caller hands in the clock. Production passes Clock.systemDefaultZone(); a test passes a frozen one.
class Greeter(private val clock: Clock) {
    fun greeting(): String = greetingAt(LocalTime.now(clock))
}

fun greetingAt(time: LocalTime): String =
    if (time.hour < 12) "Good morning" else "Good afternoon"
