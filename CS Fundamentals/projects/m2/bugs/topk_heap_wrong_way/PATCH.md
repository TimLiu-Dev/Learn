# topk_heap_wrong_way

The heap is ordered best-first, so poll() throws away the best item each time and top-k keeps the k worst.

Apply: copy `solution_java/` somewhere, then copy this folder's `src/` over it
(or run `bugs/verify_bugs.sh`, which does it for every bug).

```diff
--- solution_java/src/main/java/search/TopK.java
+++ bugs/topk_heap_wrong_way/src/main/java/search/TopK.java
@@ -17,7 +17,7 @@
         if (k < 1) throw new IllegalArgumentException("k must be at least 1, was " + k);
         // A min-heap under the ranking: its root is the weakest of the current top k,
         // the first one to evict when something better arrives.
-        PriorityQueue<T> heap = new PriorityQueue<>(bestFirst.reversed());
+        PriorityQueue<T> heap = new PriorityQueue<>(bestFirst); // BUG: root is the BEST, so it evicts the best
         for (T item : items) {
             heap.add(item);
             if (heap.size() > k) heap.poll();
```
