import java.util.HashMap;
import java.util.Map;

// Count the words that start with a prefix in O(prefix length), without visiting them:
// every node remembers how many inserted words pass through it.
public class CountingTrie {
    private static class Node {
        final Map<Character, Node> children = new HashMap<>();
        int pass = 0;                                    // words that go through this node
        boolean isWord = false;
    }

    private final Node root = new Node();

    void insert(String word) {
        if (contains(word)) return;                      // a set: count each word once
        Node node = root;
        node.pass++;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new Node());
            node.pass++;
        }
        node.isWord = true;
    }

    private Node find(String prefix) {
        Node node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return null;
        }
        return node;
    }

    boolean contains(String word) { Node n = find(word); return n != null && n.isWord; }
    int countPrefix(String prefix) { Node n = find(prefix); return n == null ? 0 : n.pass; }

    public static void main(String[] args) {
        CountingTrie t = new CountingTrie();
        for (String w : "she sells sea shells by the sea shore".split(" ")) t.insert(w);
        for (String p : new String[] {"", "s", "sh", "she", "shel", "x"})
            System.out.println("countPrefix(\"" + p + "\") = " + t.countPrefix(p));
    }
}
