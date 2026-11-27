# idf_log10

idf uses log base 10 instead of the natural log the spec requires. Rankings often survive; every score is wrong.

Apply: copy `solution_java/` somewhere, then copy this folder's `src/` over it
(or run `bugs/verify_bugs.sh`, which does it for every bug).

```diff
--- solution_java/src/main/java/search/Index.java
+++ bugs/idf_log10/src/main/java/search/Index.java
@@ -101,7 +101,7 @@
 
     /** idf(t) = ln((1 + D) / (1 + df(t))) + 1: smoothed, so a term in every document still counts 1. */
     public double idf(String term) {
-        return Math.log((1.0 + docs.size()) / (1.0 + df(term))) + 1.0;
+        return Math.log10((1.0 + docs.size()) / (1.0 + df(term))) + 1.0; // BUG: base 10
     }
 
     /**
```
