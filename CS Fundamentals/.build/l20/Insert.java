public class Insert {
    static class Node {
        final int key;
        Node left, right;
        Node(int key) { this.key = key; }
    }

    // Recursive insert returns the (possibly new) root of this subtree.
    // The caller re-links it: that's how a null link becomes a new node.
    static Node insert(Node node, int key) {
        if (node == null) return new Node(key);       // base case: empty spot found
        int c = Integer.compare(key, node.key);
        if (c < 0) node.left = insert(node.left, key);
        else if (c > 0) node.right = insert(node.right, key);
        return node;                                  // c == 0: duplicate, ignore it
    }

    // Iterative insert: walk down remembering the parent, then hang the node off it.
    static Node insertLoop(Node root, int key) {
        if (root == null) return new Node(key);
        Node cur = root;
        while (true) {
            int c = Integer.compare(key, cur.key);
            if (c == 0) return root;
            if (c < 0) {
                if (cur.left == null) { cur.left = new Node(key); return root; }
                cur = cur.left;
            } else {
                if (cur.right == null) { cur.right = new Node(key); return root; }
                cur = cur.right;
            }
        }
    }

    // Smallest key: keep going left. Largest: keep going right.
    static int min(Node root) { Node n = root; while (n.left != null) n = n.left; return n.key; }
    static int max(Node root) { Node n = root; while (n.right != null) n = n.right; return n.key; }

    // Height = nodes on the longest root-to-leaf path. Empty tree = 0.
    static int height(Node node) {
        return node == null ? 0 : 1 + Math.max(height(node.left), height(node.right));
    }

    public static void main(String[] args) {
        Node root = null;
        for (int k : new int[] {50, 30, 70, 20, 40, 60, 80}) root = insert(root, k);
        root = insertLoop(root, 65);
        root = insert(root, 40);                      // duplicate, ignored
        System.out.println("min=" + min(root) + " max=" + max(root) + " height=" + height(root));
        System.out.println("70's left is " + root.right.left.key + ", whose right is " + root.right.left.right.key);
    }
}
