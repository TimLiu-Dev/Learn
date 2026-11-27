# Seeded bug: half_up_percent

Applies to: `solution_java/` (copy it, then copy `src/main/java/budget/Money.java` from this folder over the same path).

Rounds percentages HALF_UP, the school rule. Exact ties such as 62.25% then round to 62.3 instead of the spec's banker's rounding (62.2).

```diff
--- a/src/main/java/budget/Money.java
+++ b/src/main/java/budget/Money.java
@@ -50,7 +50,7 @@
 
     /** This amount as a percentage of {@code whole}, to 1 dp, rounded HALF_EVEN. */
     public BigDecimal percentOf(Money whole) {
-        return amount.multiply(HUNDRED).divide(whole.amount, 1, RoundingMode.HALF_EVEN);
+        return amount.multiply(HUNDRED).divide(whole.amount, 1, RoundingMode.HALF_UP); // BUG: spec says HALF_EVEN
     }
 
     @Override
```
