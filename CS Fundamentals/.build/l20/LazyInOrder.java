import java.util.*;

public class LazyInOrder {
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

    // Java has no yield for iterators, so the paused recursion becomes an explicit stack.
    static class InOrderIterator implements Iterator<Integer> {
        private final Deque<Node> stack = new ArrayDeque<>();
        InOrderIterator(Node root) { pushLeft(root); }

        private void pushLeft(Node n) {              // dive left, saving the path
            while (n != null) { stack.push(n); n = n.left; }
        }
        @Override public boolean hasNext() { return !stack.isEmpty(); }
        @Override public Integer next() {
            if (stack.isEmpty()) throw new NoSuchElementException();
            Node n = stack.pop();                    // the leftmost unvisited node
            System.out.println("  visit " + n.key);
            pushLeft(n.right);                       // then do its right subtree
            return n.key;
        }
    }

    public static void main(String[] args) {
        Node root = null;
        for (int k : new int[] {50, 30, 70, 20, 40, 60, 80}) root = insert(root, k);
        Iterator<Integer> it = new InOrderIterator(root);
        int found = -1;
        while (it.hasNext()) {
            int k = it.next();
            if (k > 35) { found = k; break; }
        }
        System.out.println("first key > 35: " + found);
    }
}
