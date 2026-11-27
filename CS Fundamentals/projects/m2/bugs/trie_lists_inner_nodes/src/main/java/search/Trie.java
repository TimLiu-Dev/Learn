package search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A trie of terms, each word-end node carrying the term's corpus frequency (L50). */
public final class Trie {
    public record Entry(String term, long count) {}

    private static final class Node {
        final Map<Character, Node> children = new HashMap<>();
        long count; // 0 means "no term ends here"
    }

    private final Node root = new Node();

    public void add(String term, long count) {
        if (count < 1) throw new IllegalArgumentException("count must be positive: " + term);
        Node node = root;
        for (int i = 0; i < term.length(); i++) {
            node = node.children.computeIfAbsent(term.charAt(i), c -> new Node());
        }
        node.count += count;
    }

    /** Every term that starts with {@code prefix}, including the prefix itself if it is a term. */
    public List<Entry> withPrefix(String prefix) {
        Node node = root;
        for (int i = 0; i < prefix.length() && node != null; i++) {
            node = node.children.get(prefix.charAt(i));
        }
        List<Entry> out = new ArrayList<>();
        if (node != null) collect(node, new StringBuilder(prefix), out);
        return out;
    }

    private static void collect(Node node, StringBuilder path, List<Entry> out) {
        out.add(new Entry(path.toString(), node.count)); // BUG: every node, even where no term ends
        for (Map.Entry<Character, Node> child : node.children.entrySet()) {
            path.append(child.getKey());
            collect(child.getValue(), path, out);
            path.setLength(path.length() - 1);
        }
    }
}
