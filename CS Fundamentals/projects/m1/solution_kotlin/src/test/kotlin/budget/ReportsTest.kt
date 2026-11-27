package budget

import java.time.LocalDate
import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals

class ReportsTest {
    private val march = YearMonth.of(2026, 3)
    private var line = 1
    private fun item(date: String, cents: Long, category: String) =
        Categorised(Transaction(++line, LocalDate.parse(date), "x", Money(cents)), category)

    @Test
    fun `month includes its first and last day only`() {
        val items = listOf(
            item("2026-02-28", -1, "A"),
            item("2026-03-01", -2, "A"),
            item("2026-03-31", -4, "A"),
            item("2026-04-01", -8, "A"),
        )
        assertEquals(listOf(-2L, -4L), items.inMonth(march).map { it.tx.amount.cents })
    }

    @Test
    fun `report totals and orders categories`() {
        val report = monthReport(
            listOf(
                item("2026-03-01", 250000, "Salary"),
                item("2026-03-02", -95000, "Rent"),
                item("2026-03-03", -1500, UNCATEGORISED),
                item("2026-03-04", -2000, "Coffee"),
                item("2026-03-05", 500, "Coffee"), // a refund nets off
                item("2026-03-06", -1500, "Books"),
            ),
            march,
        )
        assertEquals(
            listOf("Report 2026-03", "Income 2505.00", "Spending -1000.00", "Net 1505.00",
                "Salary 2500.00 (1)", "Rent -950.00 (1)", "Books -15.00 (1)", "Coffee -15.00 (2)", "Uncategorised -15.00 (1)"),
            renderReport(report),
        )
    }

    @Test
    fun `an empty month still prints zero totals`() {
        assertEquals(
            listOf("Report 2026-03", "Income 0.00", "Spending 0.00", "Net 0.00", "No transactions"),
            renderReport(monthReport(emptyList(), march)),
        )
    }

    @Test
    fun `top breaks ties by date then line`() {
        val a = item("2026-03-09", -500, "A")
        val b = item("2026-03-02", -500, "B")
        val c = item("2026-03-02", -500, "C")
        val big = item("2026-03-20", -900, "D")
        assertEquals(listOf(big, b, c), topExpenses(listOf(a, b, c, big, item("2026-03-01", 100, "In")), march, 3))
    }

    @Test
    fun `budget lines show under, over, on budget and a total`() {
        val items = listOf(item("2026-03-01", -24900, "Food"), item("2026-03-02", -6000, "Fun"), item("2026-03-03", -1000, "Bus"))
        val budgets = linkedMapOf("Food" to Money(40000), "Fun" to Money(5000), "Bus" to Money(1000), "Gym" to Money(2000))
        assertEquals(
            listOf(
                "Budget 2026-03",
                "Food | 400.00 | 249.00 | 151.00 under | 62.2%",
                "Fun | 50.00 | 60.00 | 10.00 over | 120.0%",
                "Bus | 10.00 | 10.00 | on budget | 100.0%",
                "Gym | 20.00 | 0.00 | 20.00 under | 0.0%",
                "Total | 480.00 | 319.00 | 161.00 under | 66.5%",
            ),
            renderBudget(budgetLines(items, march, budgets), march),
        )
    }
}
