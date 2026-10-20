import java.util.*;

public class Traversals {
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

    // The three depth-first orders differ only in WHERE the node itself is visited.
    static void preOrder(Node n, List<Integer> out) {
        if (n == null) return;
        out.add(n.key);              // node first
        preOrder(n.left, out); preOrder(n.right, out);
    }
    static void inOrder(Node n, List<Integer> out) {
        if (n == null) return;
        inOrder(n.left, out);
        out.add(n.key);              // node in the middle
        inOrder(n.right, out);
    }
    static void postOrder(Node n, List<Integer> out) {
        if (n == null) return;
        postOrder(n.left, out); postOrder(n.right, out);
        out.add(n.key);              // node last
    }

    // Breadth-first: a FIFO queue (L17) visits the tree level by level.
    static List<Integer> levelOrder(Node root) {
        List<Integer> out = new ArrayList<>();
        Deque<Node> queue = new ArrayDeque<>();
        if (root != null) queue.addLast(root);
        while (!queue.isEmpty()) {
            Node n = queue.removeFirst();
            out.add(n.key);
            if (n.left != null) queue.addLast(n.left);
            if (n.right != null) queue.addLast(n.right);
        }
        return out;
    }

    public static void main(String[] args) {
        Node root = null;
        for (int k : new int[] {50, 30, 70, 20, 40, 60, 80}) root = insert(root, k);
        List<Integer> pre = new ArrayList<>(), in = new ArrayList<>(), post = new ArrayList<>();
        preOrder(root, pre); inOrder(root, in); postOrder(root, post);
        System.out.println("pre   " + pre);
        System.out.println("in    " + in);
        System.out.println("post  " + post);
        System.out.println("level " + levelOrder(root));
    }
}
