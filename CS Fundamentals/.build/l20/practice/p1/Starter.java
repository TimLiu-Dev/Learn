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

    static int height(Node node) {
        return node == null ? 0 : 1 + Math.max(height(node.left), height(node.right));
    }

    static boolean isBalanced(Node node) {
        throw new UnsupportedOperationException("TODO: compare the two subtree heights, and check both subtrees too");
    }

    static Integer floor(Node root, int key) {
        throw new UnsupportedOperationException("TODO: walk down; remember the best key <= key so far");
    }

    // CHECKS
    static Node tree(int... keys) {
        Node root = null;
        for (int k : keys) root = insert(root, k);
        return root;
    }

    public static void main(String[] args) {
        Node seven = tree(50, 30, 70, 20, 40, 60, 80);
        check("the seven-node tree is balanced", true, () -> isBalanced(seven));
        check("an empty tree is balanced", true, () -> isBalanced(null));
        check("a single node is balanced", true, () -> isBalanced(tree(42)));
        check("sorted inserts are not balanced", false, () -> isBalanced(tree(1, 2, 3, 4)));
        check("a leaning subtree is caught even when the root looks fine", false, () -> isBalanced(tree(50, 30, 20, 10, 70, 80, 90)));
        check("floor of an exact key", 60, () -> floor(seven, 60));
        check("floor of 45 is 40", 40, () -> floor(seven, 45));
        check("floor of 65 is 60", 60, () -> floor(seven, 65));
        check("floor above the maximum", 80, () -> floor(seven, 99));
        check("floor below the minimum is null", null, () -> floor(seven, 10));
        check("floor in an empty tree is null", null, () -> floor(null, 5));
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
