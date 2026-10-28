import java.util.*;
import java.util.function.BiConsumer;

// Exercise 3: two features real caches have. peek() reads without changing recency,
// and onEvict tells you which entry was pushed out (to log it, or to close a resource).
public class Ex3 {
    static final class Lru<K, V> {
        private final class Node { K key; V value; Node prev, next; }
        private final int capacity;
        private final BiConsumer<K, V> onEvict;
        private final Map<K, Node> map = new HashMap<>();
        private final Node head = new Node(), tail = new Node();

        Lru(int capacity, BiConsumer<K, V> onEvict) {
            this.capacity = capacity; this.onEvict = onEvict;
            head.next = tail; tail.prev = head;
        }

        V get(K key) {
            Node n = map.get(key);
            if (n == null) return null;
            unlink(n); addFront(n);
            return n.value;
        }

        V peek(K key) {                                   // same lookup, no move to front
            Node n = map.get(key);
            return n == null ? null : n.value;
        }

        void put(K key, V value) {
            Node n = map.get(key);
            if (n != null) { n.value = value; unlink(n); addFront(n); return; }
            if (map.size() == capacity) {
                Node oldest = tail.prev;
                unlink(oldest); map.remove(oldest.key);
                onEvict.accept(oldest.key, oldest.value);  // tell the owner
            }
            n = new Node(); n.key = key; n.value = value;
            map.put(key, n); addFront(n);
        }

        List<K> keys() {
            var keys = new ArrayList<K>();
            for (Node n = head.next; n != tail; n = n.next) keys.add(n.key);
            return keys;
        }

        private void unlink(Node n) { n.prev.next = n.next; n.next.prev = n.prev; }
        private void addFront(Node n) { n.next = head.next; n.prev = head; head.next.prev = n; head.next = n; }
    }

    public static void main(String[] args) {
        var cache = new Lru<String, String>(2, (k, v) -> System.out.println("  evicted " + k + "=" + v));
        cache.put("dune", "Herbert"); cache.put("emma", "Austen");
        System.out.println("peek(dune) = " + cache.peek("dune") + ", order " + cache.keys());
        cache.put("ulysses", "Joyce");                     // dune is still the oldest
        System.out.println("order " + cache.keys());
        cache.get("emma");
        cache.put("beloved", "Morrison");                  // ulysses is now the oldest
        System.out.println("order " + cache.keys());
    }
}
