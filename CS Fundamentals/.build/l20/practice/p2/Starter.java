import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Practice {
    static class Node {
        final int key;
        Node left, right;
        Node(int key) { this.key = key; }
    }

    static Node insert(Node node, int key) {
        if (node == null) return new Node(key);
        if (key < node.key) node.left = insert(node.left, key);
        else if (key > node.key) node.right = insert(node.right, key);
        return node;
    }

    static List<Integer> inOrder(Node root) {
        throw new UnsupportedOperationException("TODO: left subtree, then the node, then the right subtree");
    }

    static Integer kthSmallest(Node root, int k) {
        throw new UnsupportedOperationException("TODO: the in-order list already counts for you");
    }

    static List<Integer> between(Node root, int lo, int hi) {
        throw new UnsupportedOperationException("TODO: an in-order walk that only goes where a match could be");
    }

    // CHECKS
    static Node tree(int... keys) {
        Node root = null;
        for (int k : keys) root = insert(root, k);
        return root;
    }

    public static void main(String[] args) {
        Node seven = tree(50, 30, 70, 20, 40, 60, 80);
        check("in-order gives the keys sorted", List.of(20, 30, 40, 50, 60, 70, 80), () -> inOrder(seven));
        check("in-order of an empty tree", List.of(), () -> inOrder(null));
        check("1st smallest", 20, () -> kthSmallest(seven, 1));
        check("4th smallest", 50, () -> kthSmallest(seven, 4));
        check("7th smallest", 80, () -> kthSmallest(seven, 7));
        check("0th smallest is null", null, () -> kthSmallest(seven, 0));
        check("8th of 7 is null", null, () -> kthSmallest(seven, 8));
        check("between 35 and 65", List.of(40, 50, 60), () -> between(seven, 35, 65));
        check("between includes both bounds", List.of(30, 40, 50, 60, 70), () -> between(seven, 30, 70));
        check("nothing between 41 and 49", List.of(), () -> between(seven, 41, 49));
        check("between in an empty tree", List.of(), () -> between(null, 0, 100));
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
