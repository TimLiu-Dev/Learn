import java.util.Objects;

// Minimal stand-in for your L19 MyHashMap (separate chaining). Swap in your own.
public class MyHashMap<K, V> {
    private static final class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;
        Node(K key, V value, Node<K, V> next) { this.key = key; this.value = value; this.next = next; }
    }

    @SuppressWarnings("unchecked")
    private Node<K, V>[] buckets = (Node<K, V>[]) new Node[16];
    private int size = 0;

    public int size() { return size; }

    private static int indexFor(Object key, int n) { return (key.hashCode() & 0x7fffffff) % n; }

    public void put(K key, V value) {
        int i = indexFor(key, buckets.length);
        for (Node<K, V> node = buckets[i]; node != null; node = node.next) {
            if (Objects.equals(node.key, key)) { node.value = value; return; }
        }
        buckets[i] = new Node<>(key, value, buckets[i]);
        size++;
        if (size > buckets.length * 3 / 4) resize();              // load factor 0.75
    }

    public V get(K key) {
        for (Node<K, V> node = buckets[indexFor(key, buckets.length)]; node != null; node = node.next) {
            if (Objects.equals(node.key, key)) return node.value;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] old = buckets;
        buckets = (Node<K, V>[]) new Node[old.length * 2];
        for (Node<K, V> head : old) {
            Node<K, V> node = head;
            while (node != null) {
                Node<K, V> next = node.next;
                int i = indexFor(node.key, buckets.length);
                node.next = buckets[i];
                buckets[i] = node;
                node = next;
            }
        }
    }
}
