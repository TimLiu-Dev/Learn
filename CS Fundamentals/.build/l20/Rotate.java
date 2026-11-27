import java.util.ArrayList;
import java.util.List;

public class Rotate {
    static class Node {
        final int key;
        Node left, right;
        Node(int key) { this.key = key; }
    }

    // Left rotation around x: its right child y moves up, and x becomes y's left child.
    // y's old left subtree (keys between x and y) moves across to become x's right.
    // Returns the new root of this subtree, so the caller relinks it, as insert does.
    static Node rotateLeft(Node x) {
        Node y = x.right;
        if (y == null) return x;             // no right child: nothing to rotate
        x.right = y.left;
        y.left = x;
        return y;
    }

    static Node insert(Node node, int key) {
        if (node == null) return new Node(key);
        if (key < node.key) node.left = insert(node.left, key);
        else if (key > node.key) node.right = insert(node.right, key);
        return node;
    }

    static int height(Node n) { return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right)); }

    static void inOrder(Node n, List<Integer> out) {
        if (n == null) return;
        inOrder(n.left, out); out.add(n.key); inOrder(n.right, out);
    }

    static String shape(Node n) {
        if (n == null) return ".";
        if (n.left == null && n.right == null) return String.valueOf(n.key);
        return "(" + shape(n.left) + " " + n.key + " " + shape(n.right) + ")";
    }

    static void report(String label, Node root) {
        List<Integer> keys = new ArrayList<>();
        inOrder(root, keys);
        System.out.println(String.format("%-8s %-24s height=%d in-order=%s", label, shape(root), height(root), keys));
    }

    public static void main(String[] args) {
        Node stick = null;
        for (int k = 1; k <= 3; k++) stick = insert(stick, k);   // sorted input: a stick
        report("stick", stick);
        stick = rotateLeft(stick);
        report("rotated", stick);

        Node t = null;
        for (int k : new int[] {10, 5, 20, 15, 30}) t = insert(t, k);
        report("before", t);
        t = rotateLeft(t);                                         // 15 moves from 20's left to 10's right
        report("after", t);
    }
}
