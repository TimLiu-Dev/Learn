package cache;

import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A fixed-capacity LRU cache with a time-to-live per entry.
 * HashMap (L19) for O(1) lookup + doubly linked list (L17) for O(1) "move to front"
 * and "drop the oldest". One lock guards both, so every method is thread-safe.
 */
public final class LruCache<K, V> {
    private static final class Node<K, V> {
        final K key;
        V value;
        long expiresAt;
        Node<K, V> prev, next;
        Node(K key, V value, long expiresAt) { this.key = key; this.value = value; this.expiresAt = expiresAt; }
    }

    private final int capacity;
    private final long ttlMillis;
    private final Clock clock;
    private final Map<K, Node<K, V>> map = new HashMap<>();
    private final Node<K, V> head = new Node<>(null, null, 0);   // sentinel: most recently used side
    private final Node<K, V> tail = new Node<>(null, null, 0);   // sentinel: least recently used side
    private final ReentrantLock lock = new ReentrantLock();
    private long hits, misses, evictions;

    public LruCache(int capacity, Duration ttl) { this(capacity, ttl, Clock.systemUTC()); }

    public LruCache(int capacity, Duration ttl, Clock clock) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        this.capacity = capacity;
        this.ttlMillis = ttl.toMillis();
        this.clock = clock;
        head.next = tail;
        tail.prev = head;
    }

    public V get(K key) {
        lock.lock();
        try {
            Node<K, V> node = map.get(key);
            if (node == null || node.expiresAt <= clock.millis()) {
                if (node != null) { unlink(node); map.remove(key); }   // expired: drop it now
                misses++;
                return null;
            }
            hits++;
            unlink(node);
            addFront(node);                                            // now the most recently used
            return node.value;
        } finally {
            lock.unlock();
        }
    }

    public void put(K key, V value) {
        lock.lock();
        try {
            long expiresAt = clock.millis() + ttlMillis;
            Node<K, V> existing = map.get(key);
            if (existing != null) {
                existing.value = value;
                existing.expiresAt = expiresAt;
                unlink(existing);
                addFront(existing);
                return;
            }
            if (map.size() == capacity) {                              // full: evict the LRU entry
                Node<K, V> oldest = tail.prev;
                unlink(oldest);
                map.remove(oldest.key);
                evictions++;
            }
            Node<K, V> node = new Node<>(key, value, expiresAt);
            map.put(key, node);
            addFront(node);
        } finally {
            lock.unlock();
        }
    }

    public void remove(K key) {
        lock.lock();
        try {
            Node<K, V> node = map.remove(key);
            if (node != null) unlink(node);
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try { return map.size(); } finally { lock.unlock(); }
    }

    public Stats stats() {
        lock.lock();
        try { return new Stats(hits, misses, evictions); } finally { lock.unlock(); }
    }

    /** Keys from most to least recently used. */
    public List<K> keys() {
        lock.lock();
        try {
            var keys = new ArrayList<K>();
            for (Node<K, V> n = head.next; n != tail; n = n.next) keys.add(n.key);
            return keys;
        } finally {
            lock.unlock();
        }
    }

    private void unlink(Node<K, V> n) {
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    private void addFront(Node<K, V> n) {
        n.next = head.next;
        n.prev = head;
        head.next.prev = n;
        head.next = n;
    }
}
