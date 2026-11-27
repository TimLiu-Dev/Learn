# Seeded bug: ascii_case_regex

Applies to: `solution_java/` (copy it, then copy `src/main/java/budget/Rules.java` from this folder over the same path).

Compiles rules with `Pattern.CASE_INSENSITIVE` but without `Pattern.UNICODE_CASE`. Java then folds only ASCII letters, so `café` no longer matches `CAFÉ NERO` and `/^zoë/` misses `ZOË'S BAKERY`.

```diff
--- a/src/main/java/budget/Rules.java
+++ b/src/main/java/budget/Rules.java
@@ -10,7 +10,7 @@
     public static final String UNCATEGORISED = "Uncategorised";
 
     /** CASE_INSENSITIVE alone folds only ASCII; UNICODE_CASE makes /café/ match "CAFÉ". */
-    private static final int IGNORE_CASE = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
+    private static final int IGNORE_CASE = Pattern.CASE_INSENSITIVE; // BUG: folds ASCII letters only
 
     public record Rule(Pattern pattern, String category) {}
 
```
