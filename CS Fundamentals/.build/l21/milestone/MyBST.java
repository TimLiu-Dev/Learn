// Minimal stand-in for your L20 MyBST (unbalanced). Swap in your own.
public class MyBST<K extends Comparable<K>, V> {
    private static final class Node<K, V> {
        final K key;
        V value;
        Node<K, V> left, right;
        Node(K key, V value) { this.key = key; this.value = value; }
    }

    private Node<K, V> root;

    public void put(K key, V value) {
        if (root == null) { root = new Node<>(key, value); return; }
        Node<K, V> node = root;
        while (true) {
            int cmp = key.compareTo(node.key);
            if (cmp == 0) { node.value = value; return; }
            if (cmp < 0) {
                if (node.left == null) { node.left = new Node<>(key, value); return; }
                node = node.left;
            } else {
                if (node.right == null) { node.right = new Node<>(key, value); return; }
                node = node.right;
            }
        }
    }

    public V get(K key) {
        Node<K, V> node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp == 0) return node.value;
            node = cmp < 0 ? node.left : node.right;
        }
        return null;
    }
}
