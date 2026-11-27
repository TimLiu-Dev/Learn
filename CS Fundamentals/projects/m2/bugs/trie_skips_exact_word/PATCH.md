# trie_skips_exact_word

Autocomplete starts collecting at the prefix node's children, so "prefix car" never suggests "car" itself.

Apply: copy `solution_java/` somewhere, then copy this folder's `src/` over it
(or run `bugs/verify_bugs.sh`, which does it for every bug).

```diff
--- solution_java/src/main/java/search/Trie.java
+++ bugs/trie_skips_exact_word/src/main/java/search/Trie.java
@@ -32,7 +32,10 @@
             node = node.children.get(prefix.charAt(i));
         }
         List<Entry> out = new ArrayList<>();
-        if (node != null) collect(node, new StringBuilder(prefix), out);
+        if (node == null) return out;
+        // BUG: starts from the children, so a term equal to the prefix is never collected
+        for (Map.Entry<Character, Node> child : node.children.entrySet())
+            collect(child.getValue(), new StringBuilder(prefix).append(child.getKey()), out);
         return out;
     }
 
```
