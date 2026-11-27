# Seeded bug: last_match_rules

Applies to: `solution_kotlin/` (copy it, then copy `src/main/kotlin/budget/Rules.kt` from this folder over the same path).

The loop keeps overwriting the category instead of stopping at the first match, so a general rule later in the file beats a specific one above it.

```diff
--- a/src/main/kotlin/budget/Rules.kt
+++ b/src/main/kotlin/budget/Rules.kt
@@ -23,8 +23,13 @@
 
 /** Ordered rules: the first match wins. */
 class Rules(val rules: List<Rule>) {
-    fun categorise(description: String): String =
-        rules.firstOrNull { it.matcher.matches(description) }?.category ?: UNCATEGORISED
+    fun categorise(description: String): String {
+        var category = UNCATEGORISED
+        for (rule in rules) {
+            if (rule.matcher.matches(description)) category = rule.category // BUG: keeps going, so the LAST match wins
+        }
+        return category
+    }
 }
 
 /** All-or-nothing: one bad line rejects the whole file, naming that line. */
```
