import java.util.*;

public class Levels {
    static class Node {
        final int key;
        Node left, right;
        Node(int key) { this(key, null, null); }
        Node(int key, Node left, Node right) { this.key = key; this.left = left; this.right = right; }
    }

    // One line per level: process the queue in batches of "everything on this level".
    static void printLevels(Node root) {
        Deque<Node> queue = new ArrayDeque<>();
        if (root != null) queue.addLast(root);
        int depth = 0;
        while (!queue.isEmpty()) {
            List<Integer> line = new ArrayList<>();
            int size = queue.size();                    // fixed BEFORE we add children
            for (int i = 0; i < size; i++) {
                Node n = queue.removeFirst();
                line.add(n.key);
                if (n.left != null) queue.addLast(n.left);
                if (n.right != null) queue.addLast(n.right);
            }
            System.out.println("depth " + depth + ": " + line);
            depth++;
        }
    }

    // The trap: comparing each node only with its own children.
    static boolean isBstNaive(Node n) {
        if (n == null) return true;
        if (n.left != null && n.left.key >= n.key) return false;
        if (n.right != null && n.right.key <= n.key) return false;
        return isBstNaive(n.left) && isBstNaive(n.right);
    }

    // Correct: every key must fit the (lo, hi) window inherited from ALL its ancestors.
    // Integer (not int) so that null can mean "no bound yet".
    static boolean isBst(Node n, Integer lo, Integer hi) {
        if (n == null) return true;
        if (lo != null && n.key <= lo) return false;
        if (hi != null && n.key >= hi) return false;
        return isBst(n.left, lo, n.key) && isBst(n.right, n.key, hi);
    }

    public static void main(String[] args) {
        Node good = new Node(50, new Node(30, new Node(20), new Node(40)), new Node(70, new Node(60), new Node(80)));
        printLevels(good);
        //        50
        //      /    \
        //    30      70
        //   /  \
        //  20   60    <- 60 > 30 is fine locally, but it sits LEFT of 50
        Node sneaky = new Node(50, new Node(30, new Node(20), new Node(60)), new Node(70));
        System.out.println("good:   naive=" + isBstNaive(good) + " correct=" + isBst(good, null, null));
        System.out.println("sneaky: naive=" + isBstNaive(sneaky) + " correct=" + isBst(sneaky, null, null));
    }
}
