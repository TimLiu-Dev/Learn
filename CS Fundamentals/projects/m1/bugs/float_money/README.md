# Seeded bug: float_money

Applies to: `solution_kotlin/` (copy it, then copy `src/main/kotlin/budget/Money.kt` from this folder over the same path).

Parses the amount as a Double and converts to pence with `(pounds * 100).toLong()`. The double nearest 4.35 is slightly below it, so 4.35 becomes 434 pence: a classic float-money bug that passes most hand-picked examples.

```diff
--- a/src/main/kotlin/budget/Money.kt
+++ b/src/main/kotlin/budget/Money.kt
@@ -34,15 +34,9 @@
 
         /** Parses "12", "12.5", "-12.50"; null for anything else (or too big for a Long). */
         fun parse(text: String): Money? {
-            val match = FORMAT.matchEntire(text) ?: return null
-            val (sign, whole, fraction) = match.destructured
-            val pounds = whole.toLongOrNull() ?: return null
-            return try {
-                val cents = Math.addExact(Math.multiplyExact(pounds, 100L), fraction.padEnd(2, '0').toLong())
-                Money(if (sign == "-") -cents else cents)
-            } catch (e: ArithmeticException) {
-                null
-            }
+            if (!FORMAT.matches(text)) return null
+            val pounds = text.toDoubleOrNull() ?: return null
+            return Money((pounds * 100).toLong()) // BUG: 4.35 * 100 is 434.99999999999994, truncated to 434
         }
     }
 }
```
