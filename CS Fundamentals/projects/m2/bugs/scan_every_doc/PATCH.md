# scan_every_doc

AND looks at every document instead of using the postings lists. Correct answers, but O(D) per query: the inverted index is built and then ignored.

Apply: copy `solution_java/` somewhere, then copy this folder's `src/` over it
(or run `bugs/verify_bugs.sh`, which does it for every bug).

```diff
--- solution_java/src/main/java/search/Index.java
+++ bugs/scan_every_doc/src/main/java/search/Index.java
@@ -109,14 +109,10 @@
      * others with O(1) hash lookups, so the cost is O(min df × |terms|), not O(documents).
      */
     public List<Hit> and(List<String> terms) {
-        List<String> byDf = new ArrayList<>(terms);
-        byDf.sort(Comparator.comparingInt(this::df));
-        List<Posting> rarest = postings.getOrDefault(byDf.getFirst(), List.of());
         List<Hit> hits = new ArrayList<>();
-        for (Posting p : rarest) {
-            Document d = docs.get(p.doc());
+        for (Document d : docs) { // BUG: looks at every document, ignoring the postings
             boolean all = true;
-            for (String t : byDf) if (!d.tf().containsKey(t)) { all = false; break; }
+            for (String t : terms) if (!d.tf().containsKey(t)) { all = false; break; }
             if (all) hits.add(new Hit(d.name(), score(d, terms)));
         }
         return hits;
```
