# trie_lists_inner_nodes

Every trie node under the prefix is reported, including paths that are not terms ("carto 0").

Apply: copy `solution_java/` somewhere, then copy this folder's `src/` over it
(or run `bugs/verify_bugs.sh`, which does it for every bug).

```diff
--- solution_java/src/main/java/search/Trie.java
+++ bugs/trie_lists_inner_nodes/src/main/java/search/Trie.java
@@ -37,7 +37,7 @@
     }
 
     private static void collect(Node node, StringBuilder path, List<Entry> out) {
-        if (node.count > 0) out.add(new Entry(path.toString(), node.count));
+        out.add(new Entry(path.toString(), node.count)); // BUG: every node, even where no term ends
         for (Map.Entry<Character, Node> child : node.children.entrySet()) {
             path.append(child.getKey());
             collect(child.getValue(), path, out);
```
