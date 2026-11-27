import java.time.LocalDate
import java.time.temporal.ChronoUnit.DAYS

// Each state is its own type and carries only the data that state has.
sealed interface Loan {
    data class Reserved(val member: String) : Loan
    data class OnLoan(val member: String, val due: LocalDate) : Loan
    data class Returned(val member: String, val feeCents: Int) : Loan
    data object Cancelled : Loan
}

// Transitions exist only on the state they start from, and return the next state.
fun Loan.Reserved.pickUp(today: LocalDate) = Loan.OnLoan(member, today.plusDays(14))
fun Loan.Reserved.cancel() = Loan.Cancelled
fun Loan.OnLoan.giveBack(today: LocalDate) =
    Loan.Returned(member, maxOf(DAYS.between(due, today), 0).toInt() * 25)

// Exhaustive: no else branch. Add a fifth state and this stops compiling.
fun describe(loan: Loan): String = when (loan) {
    is Loan.Reserved -> "held for ${loan.member}"
    is Loan.OnLoan -> "with ${loan.member} until ${loan.due}"
    is Loan.Returned -> "returned by ${loan.member}, fee ${loan.feeCents}"
    Loan.Cancelled -> "cancelled"
}

// Command: each request is an object, so it can be queued, logged and replayed.
sealed interface LoanCommand {
    data class PickUp(val today: LocalDate) : LoanCommand
    data object Cancel : LoanCommand
    data class GiveBack(val today: LocalDate) : LoanCommand
}

// When you hold only a Loan (say, loaded from a database), every pair is decided here.
fun apply(loan: Loan, cmd: LoanCommand): Loan? = when (loan) {
    is Loan.Reserved -> when (cmd) {
        is LoanCommand.PickUp -> loan.pickUp(cmd.today)
        LoanCommand.Cancel -> loan.cancel()
        is LoanCommand.GiveBack -> null                    // illegal: nothing to give back
    }
    is Loan.OnLoan -> if (cmd is LoanCommand.GiveBack) loan.giveBack(cmd.today) else null
    is Loan.Returned, Loan.Cancelled -> null              // final states accept nothing
}

fun main() {
    Loan.Returned("Ada", 0).pickUp(LocalDate.of(2026, 3, 1))   // illegal transition
    val reserved = Loan.Reserved("Ada")
    val onLoan = reserved.pickUp(LocalDate.of(2026, 3, 1))
    println(describe(onLoan))
    println(describe(onLoan.giveBack(LocalDate.of(2026, 3, 18))))

    val queue = listOf(LoanCommand.PickUp(LocalDate.of(2026, 3, 1)), LoanCommand.Cancel,
                       LoanCommand.GiveBack(LocalDate.of(2026, 3, 10)))
    var state: Loan = reserved
    val history = mutableListOf(state)                   // undo = go back to an earlier state
    for (cmd in queue) {
        val next = apply(state, cmd)
        println("$cmd -> ${next?.let(::describe) ?: "rejected"}")
        if (next != null) { state = next; history += next }
    }
    history.removeAt(history.lastIndex)
    println("after undo: ${describe(history.last())}")
}
