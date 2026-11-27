package budget

import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals

class CliTest {
    private fun parse(line: String) = parseCommand(line.split(" ").filter { it.isNotEmpty() })

    @Test
    fun `options may come in any order and --n defaults to 5`() {
        assertEquals(
            Parsed.Ok(Command.Top("s.csv", "r.txt", YearMonth.of(2026, 3), 5)),
            parse("top --month 2026-03 s.csv --rules r.txt"),
        )
    }

    @Test
    fun `help wins over everything else`() {
        assertEquals(Parsed.Ok(Command.Help), parse("report --bogus --help"))
    }

    @Test
    fun `usage errors say what is wrong`() {
        assertEquals(Parsed.Bad("missing command (try 'budget --help')"), parse(""))
        assertEquals(Parsed.Bad("unknown command 'reports'"), parse("reports s.csv"))
        assertEquals(Parsed.Bad("missing statement file"), parse("validate"))
        assertEquals(Parsed.Bad("missing --month"), parse("report s.csv --rules r.txt"))
        assertEquals(Parsed.Bad("unknown option '--n' for report"), parse("report s.csv --n 3"))
        assertEquals(Parsed.Bad("invalid --month '2026-13' (expected YYYY-MM)"), parse("report s.csv --rules r --month 2026-13"))
        assertEquals(Parsed.Bad("invalid --n '0' (expected a positive integer)"), parse("top s.csv --rules r --month 2026-01 --n 0"))
        assertEquals(Parsed.Bad("missing value for --rules"), parse("categorise s.csv --rules"))
    }
}
