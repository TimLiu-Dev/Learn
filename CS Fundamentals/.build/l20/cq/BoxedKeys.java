public class BoxedKeys {
    static class Node {
        Integer key;
        Node left, right;
        Node(Integer key) { this.key = key; }
    }

    static Node insert(Node n, Integer key) {
        if (n == null) return new Node(key);
        if (key < n.key) n.left = insert(n.left, key);
        else if (key > n.key) n.right = insert(n.right, key);
        return n;
    }

    static boolean contains(Node n, Integer key) {
        while (n != null) {
            if (key == n.key) return true;
            n = key < n.key ? n.left : n.right;
        }
        return false;
    }

    public static void main(String[] args) {
        Node root = null;
        for (int k : new int[] {500, 100, 1000}) root = insert(root, k);
        System.out.println(contains(root, 100) + " " + contains(root, 1000));
    }
}
