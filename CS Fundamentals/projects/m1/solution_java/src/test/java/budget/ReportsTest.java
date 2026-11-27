package budget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import budget.Reports.Categorised;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReportsTest {
    private static final YearMonth MARCH = YearMonth.of(2026, 3);
    private int line = 1;

    private Categorised item(String date, String amount, String category) {
        return new Categorised(new Transaction(++line, LocalDate.parse(date), "x", Money.parse(amount)), category);
    }

    @Test
    void monthIncludesItsFirstAndLastDayOnly() {
        List<Categorised> items = List.of(
            item("2026-02-28", "-1", "A"), item("2026-03-01", "-2", "A"),
            item("2026-03-31", "-4", "A"), item("2026-04-01", "-8", "A"));
        assertEquals(List.of("-2.00", "-4.00"),
            Reports.inMonth(items, MARCH).stream().map(c -> c.tx().amount().toString()).toList());
    }

    @Test
    void reportTotalsAndOrdersCategories() {
        var report = Reports.monthReport(List.of(
            item("2026-03-01", "2500", "Salary"),
            item("2026-03-02", "-950", "Rent"),
            item("2026-03-03", "-15", Rules.UNCATEGORISED),
            item("2026-03-04", "-20", "Coffee"),
            item("2026-03-05", "5", "Coffee"), // a refund nets off
            item("2026-03-06", "-15", "Books")), MARCH);
        assertEquals(List.of("Report 2026-03", "Income 2505.00", "Spending -1000.00", "Net 1505.00",
                "Salary 2500.00 (1)", "Rent -950.00 (1)", "Books -15.00 (1)", "Coffee -15.00 (2)", "Uncategorised -15.00 (1)"),
            Render.report(report));
    }

    @Test
    void anEmptyMonthStillPrintsZeroTotals() {
        assertEquals(List.of("Report 2026-03", "Income 0.00", "Spending 0.00", "Net 0.00", "No transactions"),
            Render.report(Reports.monthReport(List.of(), MARCH)));
    }

    @Test
    void topBreaksTiesByDateThenLine() {
        Categorised a = item("2026-03-09", "-5", "A");
        Categorised b = item("2026-03-02", "-5", "B");
        Categorised c = item("2026-03-02", "-5", "C");
        Categorised big = item("2026-03-20", "-9", "D");
        assertEquals(List.of(big, b, c),
            Reports.topExpenses(List.of(a, b, c, big, item("2026-03-01", "1", "In")), MARCH, 3));
    }

    @Test
    void budgetLinesShowUnderOverOnBudgetAndATotal() {
        List<Categorised> items = List.of(
            item("2026-03-01", "-249", "Food"), item("2026-03-02", "-60", "Fun"), item("2026-03-03", "-10", "Bus"));
        Map<String, Money> budgets = new LinkedHashMap<>();
        budgets.put("Food", Money.parse("400"));
        budgets.put("Fun", Money.parse("50"));
        budgets.put("Bus", Money.parse("10"));
        budgets.put("Gym", Money.parse("20"));
        assertEquals(List.of(
                "Budget 2026-03",
                "Food | 400.00 | 249.00 | 151.00 under | 62.2%",
                "Fun | 50.00 | 60.00 | 10.00 over | 120.0%",
                "Bus | 10.00 | 10.00 | on budget | 100.0%",
                "Gym | 20.00 | 0.00 | 20.00 under | 0.0%",
                "Total | 480.00 | 319.00 | 161.00 under | 66.5%"),
            Render.budget(Reports.budgetLines(items, MARCH, budgets), MARCH));
    }
}
