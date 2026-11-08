import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// A trie: one node per distinct prefix. The path from the root spells the prefix;
// isWord marks the nodes where a whole word ends.
public class Trie {
    private static class Node {
        final Map<Character, Node> children = new TreeMap<>();   // sorted, so words come out in order
        boolean isWord = false;
    }

    private final Node root = new Node();
    private int nodes = 1;

    int nodes() { return nodes; }

    void insert(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> { nodes++; return new Node(); });
        }
        node.isWord = true;
    }

    private Node find(String prefix) {
        Node node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return null;               // fell off the trie
        }
        return node;
    }

    boolean contains(String word) { Node n = find(word); return n != null && n.isWord; }
    boolean hasPrefix(String prefix) { return find(prefix) != null; }

    List<String> withPrefix(String prefix) {
        List<String> out = new ArrayList<>();
        Node start = find(prefix);
        if (start != null) collect(start, new StringBuilder(prefix), out);
        return out;
    }

    private static void collect(Node node, StringBuilder sb, List<String> out) {   // DFS (L20 pre-order)
        if (node.isWord) out.add(sb.toString());
        for (Map.Entry<Character, Node> e : node.children.entrySet()) {
            sb.append(e.getKey()); collect(e.getValue(), sb, out); sb.setLength(sb.length() - 1);
        }
    }

    public static void main(String[] args) {
        Trie t = new Trie();
        List<String> words = Arrays.asList("she sells sea shells by the sea shore".split(" "));
        words.forEach(t::insert);
        var distinct = new LinkedHashSet<>(words);
        int letters = distinct.stream().mapToInt(String::length).sum();
        System.out.println(distinct.size() + " distinct words, " + letters + " letters, " + t.nodes() + " nodes");
        System.out.println("contains sea: " + t.contains("sea") + "   contains se: " + t.contains("se")
                + "   hasPrefix se: " + t.hasPrefix("se"));
        System.out.println("withPrefix sh: " + t.withPrefix("sh"));
        System.out.println("withPrefix s:  " + t.withPrefix("s"));
        System.out.println("withPrefix x:  " + t.withPrefix("x"));
    }
}
