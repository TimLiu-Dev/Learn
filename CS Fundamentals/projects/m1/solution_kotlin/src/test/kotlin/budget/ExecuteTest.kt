package budget

import kotlin.test.Test
import kotlin.test.assertEquals

/** End to end through the pure core, with files faked by a map. */
class ExecuteTest {
    private val files = mapOf(
        "s.csv" to listOf(HEADER, "2026-01-05,Tesco,-10.00", "2026-01-06,Tesco,-oops", "2026-01-07,Boots,-2.50"),
        "r.txt" to listOf("tesco -> Groceries"),
        "bad.txt" to listOf("tesco Groceries"),
    )
    private fun run(command: Command) = execute(command, files::get)

    @Test
    fun `validate exits 2 when a line is bad`() {
        assertEquals(
            Outcome.Done(listOf("line 3: invalid amount '-oops'", "2 valid, 1 malformed"), exitCode = 2),
            run(Command.Validate("s.csv")),
        )
    }

    @Test
    fun `other commands skip bad lines with a warning`() {
        val outcome = run(Command.Categorise("s.csv", "r.txt")) as Outcome.Done
        assertEquals(
            listOf("2026-01-05 | -10.00 | Groceries | Tesco", "2026-01-07 | -2.50 | Uncategorised | Boots", "2 transactions, 1 uncategorised"),
            outcome.lines,
        )
        assertEquals(listOf("warning: skipped 1 malformed line(s); run 'budget validate' for details"), outcome.warnings)
    }

    @Test
    fun `missing files and bad rules fail with a message`() {
        assertEquals(Outcome.Failed("cannot read 'nope.csv'"), run(Command.Validate("nope.csv")))
        assertEquals(Outcome.Failed("bad.txt line 1: missing '->'"), run(Command.Categorise("s.csv", "bad.txt")))
    }
}
