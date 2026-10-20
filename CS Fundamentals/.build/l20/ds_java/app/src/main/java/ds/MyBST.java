package ds;

import java.util.ArrayList;
import java.util.List;

// A map kept in binary-search-tree order. Not balanced: sorted input makes it a list.
public class MyBST<K extends Comparable<K>, V> {
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left, right;
        Node(K key, V value) { this.key = key; this.value = value; }
    }

    private Node<K, V> root;
    private int size = 0;

    public int size() { return size; }

    public void put(K key, V value) { root = put(root, key, value); }

    private Node<K, V> put(Node<K, V> n, K key, V value) {
        if (n == null) { size++; return new Node<>(key, value); }
        int c = key.compareTo(n.key);
        if (c < 0) n.left = put(n.left, key, value);
        else if (c > 0) n.right = put(n.right, key, value);
        else n.value = value;                            // existing key: replace the value
        return n;
    }

    private Node<K, V> find(K key) {
        Node<K, V> n = root;
        while (n != null) {
            int c = key.compareTo(n.key);
            if (c < 0) n = n.left;
            else if (c > 0) n = n.right;
            else return n;
        }
        return null;
    }

    public V get(K key) {
        Node<K, V> n = find(key);
        return n == null ? null : n.value;
    }

    public boolean contains(K key) { return find(key) != null; }

    public void delete(K key) {
        if (!contains(key)) return;
        root = delete(root, key);
        size--;
    }

    private Node<K, V> delete(Node<K, V> n, K key) {
        if (n == null) return null;
        int c = key.compareTo(n.key);
        if (c < 0) n.left = delete(n.left, key);
        else if (c > 0) n.right = delete(n.right, key);
        else {
            if (n.left == null) return n.right;          // case 1 (leaf) or 2 (right child only)
            if (n.right == null) return n.left;          // case 2 (left child only)
            Node<K, V> succ = n.right;                   // case 3: in-order successor
            while (succ.left != null) succ = succ.left;
            n.key = succ.key;
            n.value = succ.value;
            n.right = delete(n.right, succ.key);
        }
        return n;
    }

    // null when empty, like get() for a missing key.
    public K min() {
        if (root == null) return null;
        Node<K, V> n = root;
        while (n.left != null) n = n.left;
        return n.key;
    }

    public int height() { return height(root); }
    private int height(Node<K, V> n) {
        return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right));
    }

    public List<K> inOrderKeys() {
        List<K> out = new ArrayList<>();
        inOrder(root, out);
        return out;
    }
    private void inOrder(Node<K, V> n, List<K> out) {
        if (n == null) return;
        inOrder(n.left, out); out.add(n.key); inOrder(n.right, out);
    }
}
