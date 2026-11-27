# and_from_first_term

AND walks the postings of the first query term instead of the rarest one. Correct answers, but "q common rare" costs O(df(common)), which grows with the corpus.

Apply: copy `solution_java/` somewhere, then copy this folder's `src/` over it
(or run `bugs/verify_bugs.sh`, which does it for every bug).

```diff
--- solution_java/src/main/java/search/Index.java
+++ bugs/and_from_first_term/src/main/java/search/Index.java
@@ -110,7 +110,7 @@
      */
     public List<Hit> and(List<String> terms) {
         List<String> byDf = new ArrayList<>(terms);
-        byDf.sort(Comparator.comparingInt(this::df));
+        // BUG: no sort, so it walks the FIRST term's postings even when that term is common
         List<Posting> rarest = postings.getOrDefault(byDf.getFirst(), List.of());
         List<Hit> hits = new ArrayList<>();
         for (Posting p : rarest) {
```
