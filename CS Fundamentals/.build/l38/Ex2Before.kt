import java.time.LocalDate

// Exercise 2 starting point: everything is reached for, nothing is passed in.
object Mailer {                                               // a global singleton
    fun send(to: String, text: String) = println("MAIL to $to: $text")
}

class ReminderJob {
    private val loans = mapOf(                                // pretend this is the database
        "ada@example.com" to LocalDate.parse("2026-10-01"),
        "alan@example.com" to LocalDate.parse("2026-10-18"),
    )

    fun run(): Int {
        val today = LocalDate.now()                           // the real calendar
        var sent = 0
        for ((who, loaned) in loans) {
            val due = loaned.plusDays(14)
            if (due < today) { Mailer.send(who, "overdue since $due"); sent++ }
        }
        return sent
    }
}

fun main() = println("sent ${ReminderJob().run()}")
