# idf_no_smoothing

Textbook idf = ln(D / df) without the spec's +1 smoothing: a term in every document scores 0, and all scores shift.

Apply: copy `solution_java/` somewhere, then copy this folder's `src/` over it
(or run `bugs/verify_bugs.sh`, which does it for every bug).

```diff
--- solution_java/src/main/java/search/Index.java
+++ bugs/idf_no_smoothing/src/main/java/search/Index.java
@@ -101,7 +101,7 @@
 
     /** idf(t) = ln((1 + D) / (1 + df(t))) + 1: smoothed, so a term in every document still counts 1. */
     public double idf(String term) {
-        return Math.log((1.0 + docs.size()) / (1.0 + df(term))) + 1.0;
+        return Math.log((double) docs.size() / df(term)); // BUG: textbook idf, no smoothing
     }
 
     /**
```
