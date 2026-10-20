public class Contains {
    // One node of a binary search tree: a key and two subtrees (null = empty).
    static class Node {
        final int key;
        Node left, right;
        Node(int key) { this(key, null, null); }
        Node(int key, Node left, Node right) { this.key = key; this.left = left; this.right = right; }
    }

    // Recursive. Base cases: an empty tree (not found) and a match.
    static boolean containsRec(Node node, int key) {
        if (node == null) return false;
        int c = Integer.compare(key, node.key);
        if (c < 0) return containsRec(node.left, key);   // smaller: only the left can hold it
        if (c > 0) return containsRec(node.right, key);  // bigger: only the right can hold it
        return true;
    }

    // Iterative: walk ONE path down, printing each key we compare against.
    static boolean containsLoop(Node root, int key) {
        Node cur = root;
        while (cur != null) {
            System.out.print(cur.key + " ");
            int c = Integer.compare(key, cur.key);
            if (c < 0) cur = cur.left;
            else if (c > 0) cur = cur.right;
            else return true;
        }
        return false;
    }

    public static void main(String[] args) {
        //         50
        //       /    \
        //     30      70
        //    /  \    /  \
        //   20  40  60  80
        Node root = new Node(50,
            new Node(30, new Node(20), new Node(40)),
            new Node(70, new Node(60), new Node(80)));
        System.out.println("rec 60: " + containsRec(root, 60) + ", rec 45: " + containsRec(root, 45));
        System.out.print("loop 60: "); System.out.println(containsLoop(root, 60));
        System.out.print("loop 45: "); System.out.println(containsLoop(root, 45));
    }
}
