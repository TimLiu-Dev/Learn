package budget;

import java.time.YearMonth;

/** Everything the user can ask for, already checked. */
public sealed interface Command {
    record Help() implements Command {}

    record Validate(String statement) implements Command {}

    /** The commands that categorise transactions, so they all need a statement and rules. */
    sealed interface WithRules extends Command {
        String statement();

        String rules();
    }

    record Categorise(String statement, String rules) implements WithRules {}

    record Report(String statement, String rules, YearMonth month) implements WithRules {}

    record Top(String statement, String rules, YearMonth month, int n) implements WithRules {}

    record Budget(String statement, String rules, String budgets, YearMonth month) implements WithRules {}
}
