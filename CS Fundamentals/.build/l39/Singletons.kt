import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

object IdGenerator {                          // the whole Singleton pattern is one keyword
    private var next = 1L
    @Synchronized fun nextId() = next++
}

object Config {
    init { println("  (Config loading...)") } // runs once, on first access, thread-safely
    val loanDays = 21
}

// Hidden dependencies vs injected ones, exactly as in Java
fun isOverdueHidden(loaned: LocalDate) = loaned.plusDays(Config.loanDays.toLong()).isBefore(LocalDate.now())
fun isOverdue(loaned: LocalDate, loanDays: Int, clock: Clock) =
    loaned.plusDays(loanDays.toLong()).isBefore(LocalDate.now(clock))

fun main() {
    println("${IdGenerator.nextId()} ${IdGenerator.nextId()}")
    println("before first use")
    println("loan days: ${Config.loanDays}")
    println("same instance: ${Config === Config}")
    val nov1 = Clock.fixed(Instant.parse("2026-11-01T00:00:00Z"), ZoneOffset.UTC)
    println("loaned Oct 1, overdue on Nov 1? ${isOverdue(LocalDate.parse("2026-10-01"), 21, nov1)}")
    println("loaned Oct 20, overdue on Nov 1? ${isOverdue(LocalDate.parse("2026-10-20"), 21, nov1)}")
}
