package budget;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

final class Cli {
    private Cli() {}

    static final String USAGE = """
        Usage: budget <command> <statement.csv> [options]

        Commands:
          validate   <statement.csv>                          report malformed lines (exit 2 if any)
          categorise <statement.csv> --rules FILE             label every transaction
          report     <statement.csv> --rules FILE --month YYYY-MM
                                                              income, spending and totals per category
          top        <statement.csv> --rules FILE --month YYYY-MM [--n N]
                                                              the N biggest expenses (default 5)
          budget     <statement.csv> --rules FILE --budgets FILE --month YYYY-MM
                                                              spending against each budget

        Options:
          -h, --help   show this help""";

    /** Which options each command accepts; all but --n are required. */
    private static final Map<String, List<String>> OPTIONS = Map.of(
        "validate", List.of(),
        "categorise", List.of("--rules"),
        "report", List.of("--rules", "--month"),
        "top", List.of("--rules", "--month", "--n"),
        "budget", List.of("--rules", "--budgets", "--month"));

    private static final Pattern MONTH_SHAPE = Pattern.compile("\\d{4}-\\d{2}");

    static Parsed<Command> parse(List<String> args) {
        if (args.contains("--help") || args.contains("-h")) return Parsed.ok(new Command.Help());
        if (args.isEmpty()) return Parsed.bad("missing command (try 'budget --help')");
        String name = args.getFirst();
        List<String> allowed = OPTIONS.get(name);
        if (allowed == null) return Parsed.bad("unknown command '" + name + "'");

        String statement = null;
        Map<String, String> options = new HashMap<>();
        for (int i = 1; i < args.size(); i++) {
            String arg = args.get(i);
            if (arg.startsWith("--")) {
                if (!allowed.contains(arg)) return Parsed.bad("unknown option '" + arg + "' for " + name);
                if (i + 1 >= args.size()) return Parsed.bad("missing value for " + arg);
                options.put(arg, args.get(++i));
            } else if (statement == null) {
                statement = arg;
            } else {
                return Parsed.bad("unexpected argument '" + arg + "'");
            }
        }
        if (statement == null) return Parsed.bad("missing statement file");
        for (String option : allowed) {
            if (!option.equals("--n") && !options.containsKey(option)) return Parsed.bad("missing " + option);
        }

        YearMonth month = null;
        if (options.containsKey("--month")) {
            month = parseMonth(options.get("--month"));
            if (month == null) return Parsed.bad("invalid --month '" + options.get("--month") + "' (expected YYYY-MM)");
        }
        int n = 5;
        if (options.containsKey("--n")) {
            n = parsePositive(options.get("--n"));
            if (n <= 0) return Parsed.bad("invalid --n '" + options.get("--n") + "' (expected a positive integer)");
        }
        String rules = options.get("--rules");
        return Parsed.ok(switch (name) {
            case "validate" -> new Command.Validate(statement);
            case "categorise" -> new Command.Categorise(statement, rules);
            case "report" -> new Command.Report(statement, rules, month);
            case "top" -> new Command.Top(statement, rules, month, n);
            default -> new Command.Budget(statement, rules, options.get("--budgets"), month);
        });
    }

    private static YearMonth parseMonth(String text) {
        if (!MONTH_SHAPE.matcher(text).matches()) return null;
        try {
            return YearMonth.parse(text);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** The number, or 0 if it isn't a positive int. */
    private static int parsePositive(String text) {
        try {
            return Math.max(0, Integer.parseInt(text));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
