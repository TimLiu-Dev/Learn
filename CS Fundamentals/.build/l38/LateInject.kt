import java.time.Clock

// Field/setter injection: the dependency is assigned AFTER construction... if someone remembers.
class ReminderService {
    lateinit var clock: Clock                       // "will be set later"

    fun stamp() = "reminder at ${clock.instant()}"
}

fun main() {
    val wired = ReminderService().apply { clock = Clock.systemUTC() }
    println(wired.stamp().take(23) + "...")
    val forgotten = ReminderService()               // compiles fine
    println(forgotten.stamp())                      // fails only when it runs
}
