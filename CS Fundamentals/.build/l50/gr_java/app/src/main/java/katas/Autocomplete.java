package katas;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

// A trie whose word-end nodes carry a count; suggest() returns the k most frequent completions.
public class Autocomplete {
    private static class Node {
        final Map<Character, Node> children = new HashMap<>();
        int count = 0;                                   // > 0 means a word ends here
    }

    // Weakest first: lowest count, and for equal counts the word that sorts LAST.
    private static final Comparator<Suggestion> WEAKEST_FIRST =
            Comparator.comparingInt(Suggestion::count).thenComparing(Suggestion::word, Comparator.reverseOrder());

    private final Node root = new Node();
    private int size = 0;                                // distinct words

    public int size() { return size; }

    public void add(String word) { add(word, 1); }

    public void add(String word, int times) {
        if (word.isEmpty()) throw new IllegalArgumentException("word must not be empty");
        if (times <= 0) throw new IllegalArgumentException("times must be positive");
        Node node = root;
        for (char c : word.toLowerCase().toCharArray()) node = node.children.computeIfAbsent(c, k -> new Node());
        if (node.count == 0) size++;
        node.count += times;
    }

    // L3's rule for a word: a run of letters a-z, compared case-insensitively.
    public void addText(String text) {
        for (String w : text.toLowerCase().split("[^a-z]+")) if (!w.isEmpty()) add(w);
    }

    public List<Suggestion> suggest(String prefix, int k) {
        if (k <= 0) throw new IllegalArgumentException("k must be positive");
        String p = prefix.toLowerCase();
        Node node = root;
        for (char c : p.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return List.of();
        }
        // L20's top-k: a min-heap holding the best k so far, with the weakest at the root.
        PriorityQueue<Suggestion> heap = new PriorityQueue<>(WEAKEST_FIRST);
        collect(node, new StringBuilder(p), heap, k);
        List<Suggestion> out = new ArrayList<>(heap);
        out.sort(WEAKEST_FIRST.reversed());              // most frequent first, ties A to Z
        return out;
    }

    private static void collect(Node n, StringBuilder sb, PriorityQueue<Suggestion> heap, int k) {
        if (n.count > 0) {
            heap.add(new Suggestion(sb.toString(), n.count));
            if (heap.size() > k) heap.poll();            // drop the weakest: O(log k)
        }
        for (Map.Entry<Character, Node> e : n.children.entrySet()) {
            sb.append(e.getKey()); collect(e.getValue(), sb, heap, k); sb.setLength(sb.length() - 1);
        }
    }
}
