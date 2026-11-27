# case_sensitive_tokens

Tokens keep their case, so Fox, FOX and fox are three terms, and "The" escapes the stop-word list.

Apply: copy `solution_java/` somewhere, then copy this folder's `src/` over it
(or run `bugs/verify_bugs.sh`, which does it for every bug).

```diff
--- solution_java/src/main/java/search/Tokenizer.java
+++ bugs/case_sensitive_tokens/src/main/java/search/Tokenizer.java
@@ -43,7 +43,7 @@
 
     private static void flush(StringBuilder current, List<String> out) {
         if (current.isEmpty()) return;
-        String token = current.toString().toLowerCase(Locale.ROOT);
+        String token = current.toString(); // BUG: no lower-casing
         if (!STOP_WORDS.contains(token)) out.add(token);
         current.setLength(0);
     }
```
