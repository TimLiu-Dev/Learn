package budget;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Turns results into the exact text of the spec, apart from the calculations so each can be tested alone. */
final class Render {
    private Render() {}

    static List<String> validate(Statement statement) {
        List<String> out = new ArrayList<>();
        statement.problems().stream()
            .sorted(Comparator.comparingInt(LineProblem::line))
            .forEach(p -> out.add("line " + p.line() + ": " + p.reason()));
        out.add(statement.transactions().size() + " valid, " + statement.problems().size() + " malformed");
        return out;
    }

    static List<String> categorise(List<Reports.Categorised> items) {
        List<String> out = new ArrayList<>();
        for (Reports.Categorised c : items) {
            Transaction tx = c.tx();
            out.add(tx.date() + " | " + tx.amount() + " | " + c.category() + " | " + tx.description());
        }
        long uncategorised = items.stream().filter(c -> c.category().equals(Rules.UNCATEGORISED)).count();
        out.add(items.size() + " transactions, " + uncategorised + " uncategorised");
        return out;
    }

    static List<String> report(Reports.MonthReport report) {
        List<String> out = new ArrayList<>(List.of(
            "Report " + report.month(),
            "Income " + report.income(),
            "Spending " + report.spending(),
            "Net " + report.net()));
        if (report.categories().isEmpty()) out.add("No transactions");
        report.categories().forEach(c -> out.add(c.category() + " " + c.total() + " (" + c.count() + ")"));
        return out;
    }

    static List<String> top(List<Reports.Categorised> top, YearMonth month, int n) {
        List<String> out = new ArrayList<>();
        out.add("Top " + n + " expenses " + month);
        if (top.isEmpty()) out.add("No expenses");
        for (int i = 0; i < top.size(); i++) {
            Transaction tx = top.get(i).tx();
            out.add((i + 1) + " | " + tx.date() + " | " + tx.amount() + " | " + top.get(i).category() + " | " + tx.description());
        }
        return out;
    }

    static List<String> budget(List<Reports.BudgetLine> lines, YearMonth month) {
        var total = new Reports.BudgetLine("Total",
            Money.sum(lines.stream().map(Reports.BudgetLine::budget).toList()),
            Money.sum(lines.stream().map(Reports.BudgetLine::spent).toList()));
        List<String> out = new ArrayList<>();
        out.add("Budget " + month);
        for (Reports.BudgetLine b : concat(lines, total)) {
            out.add(b.category() + " | " + b.budget() + " | " + b.spent() + " | " + status(b.left()) + " | "
                + b.percent().toPlainString() + "%");
        }
        return out;
    }

    private static List<Reports.BudgetLine> concat(List<Reports.BudgetLine> lines, Reports.BudgetLine last) {
        List<Reports.BudgetLine> all = new ArrayList<>(lines);
        all.add(last);
        return all;
    }

    private static String status(Money left) {
        return switch (left.signum()) {
            case 1 -> left + " under";
            case -1 -> left.negate() + " over";
            default -> "on budget";
        };
    }
}
