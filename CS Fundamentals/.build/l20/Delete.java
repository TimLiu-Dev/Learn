public class Delete {
    static class Node {
        int key;
        Node left, right;
        Node(int key) { this.key = key; }
    }

    static Node insert(Node node, int key) {
        if (node == null) return new Node(key);
        if (key < node.key) node.left = insert(node.left, key);
        else if (key > node.key) node.right = insert(node.right, key);
        return node;
    }

    // Like insert, delete returns the new root of this subtree and the caller re-links it.
    static Node delete(Node node, int key) {
        if (node == null) return null;                  // key not in the tree
        int c = Integer.compare(key, node.key);
        if (c < 0) node.left = delete(node.left, key);
        else if (c > 0) node.right = delete(node.right, key);
        else {
            // Cases 1 and 2: at most one child. Hand the other side up (null for a leaf).
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            // Case 3: two children. Copy in the in-order successor (the smallest key
            // on the right), then delete that successor from the right subtree.
            Node succ = node.right;
            while (succ.left != null) succ = succ.left;
            node.key = succ.key;
            node.right = delete(node.right, succ.key);
        }
        return node;
    }

    // Compact picture of the tree: key(left right), "-" for an empty subtree.
    static String shape(Node n) {
        if (n == null) return "-";
        if (n.left == null && n.right == null) return "" + n.key;
        return n.key + "(" + shape(n.left) + " " + shape(n.right) + ")";
    }

    public static void main(String[] args) {
        Node root = null;
        for (int k : new int[] {50, 30, 70, 20, 40, 60, 80, 65}) root = insert(root, k);
        System.out.println("start      " + shape(root));
        root = delete(root, 20); System.out.println("delete 20  " + shape(root));   // leaf
        root = delete(root, 60); System.out.println("delete 60  " + shape(root));   // one child
        root = delete(root, 50); System.out.println("delete 50  " + shape(root));   // two children
        root = delete(root, 99); System.out.println("delete 99  " + shape(root));   // not there
    }
}
