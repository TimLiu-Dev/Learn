# Seeded bug: off_by_one_month_boundary

Applies to: `solution_kotlin/` (copy it, then copy `src/main/kotlin/budget/Reports.kt` from this folder over the same path).

Filters a month with a half-open range that ends at `atEndOfMonth()`, so transactions on the last day (31 March, 28 February) vanish from report, top and budget.

```diff
--- a/src/main/kotlin/budget/Reports.kt
+++ b/src/main/kotlin/budget/Reports.kt
@@ -8,7 +8,10 @@
     transactions.map { Categorised(it, rules.categorise(it.description)) }
 
 /** Comparing YearMonth values has no "last day" to get wrong. */
-fun List<Categorised>.inMonth(month: YearMonth) = filter { YearMonth.from(it.tx.date) == month }
+fun List<Categorised>.inMonth(month: YearMonth) = filter {
+    // BUG: isBefore(atEndOfMonth()) excludes the last day of the month
+    !it.tx.date.isBefore(month.atDay(1)) && it.tx.date.isBefore(month.atEndOfMonth())
+}
 
 data class CategoryTotal(val category: String, val total: Money, val count: Int)
 
```
