# tiebreak_reversed

Equal scores are ordered by name descending instead of ascending.

Apply: copy `solution_java/` somewhere, then copy this folder's `src/` over it
(or run `bugs/verify_bugs.sh`, which does it for every bug).

```diff
--- solution_java/src/main/java/search/Index.java
+++ bugs/tiebreak_reversed/src/main/java/search/Index.java
@@ -28,7 +28,7 @@
     /** Score descending (compared at the printed 4 dp), then name ascending. */
     public static final Comparator<Hit> RANKING = Comparator
             .comparingLong((Hit h) -> -Math.round(h.score() * 10_000))
-            .thenComparing(Hit::name);
+            .thenComparing(Hit::name, Comparator.reverseOrder()); // BUG: ties Z→A
 
     private final List<Document> docs = new ArrayList<>();
     private final Map<String, Document> byName = new HashMap<>();
```
