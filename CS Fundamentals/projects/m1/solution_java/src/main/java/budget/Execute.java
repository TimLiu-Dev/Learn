package budget;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The pure core: given a checked command and a way to read files, compute the
 * outcome. No printing, no exiting, no real files.
 */
final class Execute {
    private Execute() {}

    /** Reads a file as lines, or returns null if it can't. Injected so tests never touch the disk. */
    interface ReadLines extends Function<String, List<String>> {}

    static Outcome run(Command command, ReadLines read) {
        return switch (command) {
            case Command.Help h -> new Outcome.Done(Cli.USAGE.lines().toList());
            case Command.Validate v -> {
                List<String> lines = read.apply(v.statement());
                if (lines == null) yield cannotRead(v.statement());
                Statement statement = Statement.parse(lines);
                yield new Outcome.Done(Render.validate(statement), List.of(), statement.problems().isEmpty() ? 0 : 2);
            }
            case Command.WithRules w -> runWithRules(w, read);
        };
    }

    private static Outcome runWithRules(Command.WithRules command, ReadLines read) {
        List<String> statementLines = read.apply(command.statement());
        if (statementLines == null) return cannotRead(command.statement());
        Statement statement = Statement.parse(statementLines);

        List<String> ruleLines = read.apply(command.rules());
        if (ruleLines == null) return cannotRead(command.rules());
        Rules rules;
        switch (Rules.parse(ruleLines)) {
            case Parsed.Ok<Rules>(Rules r) -> rules = r;
            case Parsed.Bad<Rules>(String reason) -> {
                return new Outcome.Failed(command.rules() + " " + reason);
            }
        }
        List<Reports.Categorised> items = Reports.categoriseAll(statement.transactions(), rules);

        List<String> lines;
        switch (command) {
            case Command.Categorise c -> lines = Render.categorise(items);
            case Command.Report r -> lines = Render.report(Reports.monthReport(items, r.month()));
            case Command.Top t -> lines = Render.top(Reports.topExpenses(items, t.month(), t.n()), t.month(), t.n());
            case Command.Budget b -> {
                List<String> budgetLines = read.apply(b.budgets());
                if (budgetLines == null) return cannotRead(b.budgets());
                Map<String, Money> budgets;
                switch (Budgets.parse(budgetLines)) {
                    case Parsed.Ok<Map<String, Money>>(Map<String, Money> m) -> budgets = m;
                    case Parsed.Bad<Map<String, Money>>(String reason) -> {
                        return new Outcome.Failed(b.budgets() + " " + reason);
                    }
                }
                lines = Render.budget(Reports.budgetLines(items, b.month(), budgets), b.month());
            }
        }
        int bad = statement.problems().size();
        List<String> warnings = bad == 0 ? List.of()
            : List.of("warning: skipped " + bad + " malformed line(s); run 'budget validate' for details");
        return new Outcome.Done(lines, warnings, 0);
    }

    private static Outcome cannotRead(String path) {
        return new Outcome.Failed("cannot read '" + path + "'");
    }
}
