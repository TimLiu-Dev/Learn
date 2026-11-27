package budget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** End to end through the pure core, with files faked by a map. */
class ExecuteTest {
    private static final Map<String, List<String>> FILES = Map.of(
        "s.csv", List.of(Statement.HEADER, "2026-01-05,Tesco,-10.00", "2026-01-06,Tesco,-oops", "2026-01-07,Boots,-2.50"),
        "r.txt", List.of("tesco -> Groceries"),
        "bad.txt", List.of("tesco Groceries"));

    private static Outcome run(Command command) {
        return Execute.run(command, FILES::get);
    }

    @Test
    void validateExits2WhenALineIsBad() {
        assertEquals(new Outcome.Done(List.of("line 3: invalid amount '-oops'", "2 valid, 1 malformed"), List.of(), 2),
            run(new Command.Validate("s.csv")));
    }

    @Test
    void otherCommandsSkipBadLinesWithAWarning() {
        var done = (Outcome.Done) run(new Command.Categorise("s.csv", "r.txt"));
        assertEquals(List.of("2026-01-05 | -10.00 | Groceries | Tesco", "2026-01-07 | -2.50 | Uncategorised | Boots",
            "2 transactions, 1 uncategorised"), done.lines());
        assertEquals(List.of("warning: skipped 1 malformed line(s); run 'budget validate' for details"), done.warnings());
    }

    @Test
    void missingFilesAndBadRulesFailWithAMessage() {
        assertEquals(new Outcome.Failed("cannot read 'nope.csv'"), run(new Command.Validate("nope.csv")));
        assertEquals(new Outcome.Failed("bad.txt line 1: missing '->'"), run(new Command.Categorise("s.csv", "bad.txt")));
    }
}
