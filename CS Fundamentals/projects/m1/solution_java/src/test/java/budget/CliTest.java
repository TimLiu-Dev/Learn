package budget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class CliTest {
    private static Parsed<Command> parse(String line) {
        return Cli.parse(line.isBlank() ? List.of() : Arrays.asList(line.split(" ")));
    }

    @Test
    void optionsMayComeInAnyOrderAndNDefaultsTo5() {
        assertEquals(Parsed.ok(new Command.Top("s.csv", "r.txt", YearMonth.of(2026, 3), 5)),
            parse("top --month 2026-03 s.csv --rules r.txt"));
    }

    @Test
    void helpWinsOverEverythingElse() {
        assertEquals(Parsed.ok(new Command.Help()), parse("report --bogus --help"));
    }

    @Test
    void usageErrorsSayWhatIsWrong() {
        assertEquals(Parsed.bad("missing command (try 'budget --help')"), parse(""));
        assertEquals(Parsed.bad("unknown command 'reports'"), parse("reports s.csv"));
        assertEquals(Parsed.bad("missing statement file"), parse("validate"));
        assertEquals(Parsed.bad("missing --month"), parse("report s.csv --rules r.txt"));
        assertEquals(Parsed.bad("unknown option '--n' for report"), parse("report s.csv --n 3"));
        assertEquals(Parsed.bad("invalid --month '2026-13' (expected YYYY-MM)"), parse("report s.csv --rules r --month 2026-13"));
        assertEquals(Parsed.bad("invalid --n '0' (expected a positive integer)"), parse("top s.csv --rules r --month 2026-01 --n 0"));
        assertEquals(Parsed.bad("missing value for --rules"), parse("categorise s.csv --rules"));
    }
}
