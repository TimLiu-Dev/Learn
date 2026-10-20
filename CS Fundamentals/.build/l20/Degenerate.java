import java.util.*;
import java.util.stream.*;

public class Degenerate {
    static class Node {
        final int key;
        Node left, right;
        Node(int key) { this.key = key; }
    }

    // Iterative insert that also counts comparisons, so deep trees can't overflow the stack.
    static long comparisons = 0;
    static Node insert(Node root, int key) {
        if (root == null) return new Node(key);
        Node cur = root;
        while (true) {
            comparisons++;
            if (key < cur.key) {
                if (cur.left == null) { cur.left = new Node(key); return root; }
                cur = cur.left;
            } else {
                if (cur.right == null) { cur.right = new Node(key); return root; }
                cur = cur.right;
            }
        }
    }

    static int height(Node n) { return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right)); }

    static Node build(List<Integer> keys) {
        Node root = null;
        for (int k : keys) root = insert(root, k);
        return root;
    }

    static List<Integer> range(int n) { return IntStream.rangeClosed(1, n).boxed().collect(Collectors.toList()); }

    public static void main(String[] args) {
        List<Integer> sorted = range(1000);
        List<Integer> shuffled = new ArrayList<>(sorted);
        Collections.shuffle(shuffled, new Random(42));
        System.out.println("n=1000  sorted height=" + height(build(sorted)) + "  shuffled height=" + height(build(shuffled)));

        for (int n : new int[] {10_000, 20_000, 40_000}) {
            List<Integer> keys = range(n);
            List<Integer> mixed = new ArrayList<>(keys);
            Collections.shuffle(mixed, new Random(42));
            comparisons = 0; long t = System.nanoTime(); build(keys);
            long sortedMs = (System.nanoTime() - t) / 1_000_000, sortedCmp = comparisons;
            comparisons = 0; t = System.nanoTime(); build(mixed);
            long mixedMs = (System.nanoTime() - t) / 1_000_000, mixedCmp = comparisons;
            System.out.println("n=" + n + "  sorted: " + sortedCmp + " comparisons, " + sortedMs + " ms   shuffled: " + mixedCmp + " comparisons, " + mixedMs + " ms");
        }
    }
}
