package budget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Starter for Mini project M1 · Budget CLI. The spec is on the project page.
// Everything marked TODO is yours. Rename, split and restructure freely:
// the checker only looks at what the program prints and its exit code.
public final class Main {
    private Main() {}

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

    /**
     * The command line split into pieces, with nothing checked yet.
     * {@code report s.csv --month 2026-03} gives command "report", positionals [s.csv]
     * and options {--month=2026-03}. An option with no value maps to null.
     */
    record RawArgs(String command, List<String> positionals, Map<String, String> options, boolean help) {}

    static RawArgs splitArgs(List<String> args) {
        List<String> positionals = new ArrayList<>();
        Map<String, String> options = new HashMap<>();
        int i = 1;
        while (i < args.size()) {
            if (args.get(i).startsWith("--")) {
                options.put(args.get(i), i + 1 < args.size() ? args.get(i + 1) : null);
                i += 2;
            } else {
                positionals.add(args.get(i++));
            }
        }
        String command = args.isEmpty() ? null : args.getFirst();
        return new RawArgs(command, positionals, options, args.contains("--help") || args.contains("-h"));
    }

    public static void main(String[] args) {
        RawArgs raw = splitArgs(List.of(args));
        if (raw.help()) {
            System.out.println(USAGE);
            return;
        }
        // Milestone 1: replace this switch with a sealed Command type built from RawArgs
        // (or a usage error), then run it. See the page for the design questions.
        switch (raw.command() == null ? "" : raw.command()) {
            case "validate" -> throw new UnsupportedOperationException("TODO: Milestone 1: validate <statement.csv>");
            case "categorise" -> throw new UnsupportedOperationException("TODO: Milestone 2: categorise");
            case "report" -> throw new UnsupportedOperationException("TODO: Milestone 3: report");
            case "top" -> throw new UnsupportedOperationException("TODO: Milestone 4: top");
            case "budget" -> throw new UnsupportedOperationException("TODO: Milestone 4: budget");
            default -> throw new UnsupportedOperationException(
                "TODO: Milestone 1: usage errors. Print 'error: ...' to stderr and exit 1");
        }
    }
}
