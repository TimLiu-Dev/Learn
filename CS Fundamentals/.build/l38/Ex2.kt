import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

// Exercise 2 solution: every dependency arrives through the constructor.
fun interface Mailer { fun send(to: String, text: String) }
fun interface OpenLoans { fun all(): Map<String, LocalDate> }

class ReminderJob(private val loans: OpenLoans, private val mailer: Mailer, private val clock: Clock) {
    fun run(): Int {
        val today = LocalDate.now(clock)
        val overdue = loans.all().map { (who, loaned) -> who to loaned.plusDays(14) }.filter { (_, due) -> due < today }
        overdue.forEach { (who, due) -> mailer.send(who, "overdue since $due") }
        return overdue.size
    }
}

class RecordingMailer : Mailer {                        // a spy: records what it was asked to do
    val sent = mutableListOf<String>()
    override fun send(to: String, text: String) { sent += "$to: $text" }
}

fun main() {
    val loans = OpenLoans { mapOf("ada@example.com" to LocalDate.parse("2026-10-01"), "alan@example.com" to LocalDate.parse("2026-10-18")) }

    // Production wiring (composition root): the real clock, mail printed to the console.
    println("production: sent " + ReminderJob(loans, { to, text -> println("MAIL to $to: $text") }, Clock.systemUTC()).run())

    // Test wiring: a fixed day and a recording mailer. Same answer on any day you run it.
    val spy = RecordingMailer()
    val nov5 = Clock.fixed(Instant.parse("2026-11-05T08:00:00Z"), ZoneOffset.UTC)
    println("test on 2026-11-05: sent ${ReminderJob(loans, spy, nov5).run()} -> ${spy.sent}")
}
