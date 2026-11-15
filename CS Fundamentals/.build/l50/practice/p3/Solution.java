import java.util.*;

public class Practice {
    static class PrefixTable {
        private static class Node {
            final Map<Character, Node> children = new HashMap<>();
            boolean isWord = false;
        }

        private final Node root = new Node();

        void insert(String word) {
            Node node = root;
            for (char c : word.toCharArray()) node = node.children.computeIfAbsent(c, k -> new Node());
            node.isWord = true;
        }

        // The longest inserted word that is a prefix of query, or null.
        String longestPrefixOf(String query) {
            Node node = root;
            int bestLen = root.isWord ? 0 : -1;              // -1: no word end passed yet
            for (int i = 0; i < query.length(); i++) {
                node = node.children.get(query.charAt(i));
                if (node == null) break;                     // fell off: the answer is the last word end
                if (node.isWord) bestLen = i + 1;
            }
            return bestLen < 0 ? null : query.substring(0, bestLen);
        }
    }

    // CHECKS
    static PrefixTable table(String... words) {
        PrefixTable t = new PrefixTable();
        for (String w : words) t.insert(w);
        return t;
    }

    public static void main(String[] args) {
        check("a stored word is its own longest prefix", "shells", () -> table("she", "shells").longestPrefixOf("shells"));
        check("falling off the trie keeps the last word end passed", "she", () -> table("she", "shells").longestPrefixOf("shelf"));
        check("a longer query still finds the longest word", "shells", () -> table("she", "shells").longestPrefixOf("shellsfish"));
        check("a path with no word end gives null", null, () -> table("sea").longestPrefixOf("se"));
        check("a word ending inside the query is found", "sea", () -> table("sea").longestPrefixOf("seat"));
        check("a query outside the trie gives null", null, () -> table("she", "sea").longestPrefixOf("xyz"));
        check("the most specific route wins", "128.112.136", () -> table("128.112", "128.112.136").longestPrefixOf("128.112.136.12"));
        check("a shorter route catches the rest", "128.112", () -> table("128.112", "128.112.136").longestPrefixOf("128.112.5.1"));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
