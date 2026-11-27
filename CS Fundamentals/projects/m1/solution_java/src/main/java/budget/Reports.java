package budget;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

final class Reports {
    private Reports() {}

    record Categorised(Transaction tx, String category) {}

    record CategoryTotal(String category, Money total, int count) {}

    record MonthReport(YearMonth month, Money income, Money spending, List<CategoryTotal> categories) {
        Money net() {
            return income.plus(spending);
        }
    }

    record BudgetLine(String category, Money budget, Money spent) {
        Money left() {
            return budget.minus(spent);
        }

        BigDecimal percent() {
            return spent.percentOf(budget);
        }
    }

    static List<Categorised> categoriseAll(List<Transaction> transactions, Rules rules) {
        return transactions.stream().map(tx -> new Categorised(tx, rules.categorise(tx.description()))).toList();
    }

    /** Comparing YearMonth values has no "last day" to get wrong. */
    static List<Categorised> inMonth(List<Categorised> all, YearMonth month) {
        return all.stream().filter(c -> YearMonth.from(c.tx().date()).equals(month)).toList();
    }

    /** Biggest totals first (by size, either sign), names break ties, Uncategorised last. */
    private static final Comparator<CategoryTotal> REPORT_ORDER =
        Comparator.comparing((CategoryTotal c) -> c.category().equals(Rules.UNCATEGORISED))
            .thenComparing((CategoryTotal c) -> c.total().abs(), Comparator.reverseOrder())
            .thenComparing(CategoryTotal::category);

    static MonthReport monthReport(List<Categorised> all, YearMonth month) {
        List<Categorised> items = inMonth(all, month);
        List<Money> amounts = items.stream().map(c -> c.tx().amount()).toList();
        Map<String, List<Money>> byCategory = items.stream()
            .collect(groupingBy(Categorised::category, mapping(c -> c.tx().amount(), toList())));
        List<CategoryTotal> categories = byCategory.entrySet().stream()
            .map(e -> new CategoryTotal(e.getKey(), Money.sum(e.getValue()), e.getValue().size()))
            .sorted(REPORT_ORDER)
            .toList();
        return new MonthReport(month,
            Money.sum(amounts.stream().filter(m -> m.signum() > 0).toList()),
            Money.sum(amounts.stream().filter(m -> m.signum() < 0).toList()),
            categories);
    }

    /** Most negative first; then earlier date, then earlier line, so ties are deterministic. */
    private static final Comparator<Categorised> EXPENSE_ORDER =
        Comparator.comparing((Categorised c) -> c.tx().amount())
            .thenComparing(c -> c.tx().date())
            .thenComparingInt(c -> c.tx().line());

    static List<Categorised> topExpenses(List<Categorised> all, YearMonth month, int n) {
        return inMonth(all, month).stream()
            .filter(c -> c.tx().amount().signum() < 0)
            .sorted(EXPENSE_ORDER)
            .limit(n)
            .toList();
    }

    static List<BudgetLine> budgetLines(List<Categorised> all, YearMonth month, Map<String, Money> budgets) {
        Map<String, List<Money>> byCategory = inMonth(all, month).stream()
            .collect(groupingBy(Categorised::category, mapping(c -> c.tx().amount(), toList())));
        return budgets.entrySet().stream()
            .map(e -> new BudgetLine(e.getKey(), e.getValue(),
                Money.sum(byCategory.getOrDefault(e.getKey(), List.of())).negate()))
            .toList();
    }
}
