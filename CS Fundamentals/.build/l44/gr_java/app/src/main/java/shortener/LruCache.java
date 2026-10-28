package shortener;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/** Thread-safe LRU (L41): an access-ordered LinkedHashMap behind one lock. capacity 0 = cache off. */
public class LruCache<K, V> {
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final LinkedHashMap<K, V> map;
    public final AtomicLong hits = new AtomicLong();
    public final AtomicLong misses = new AtomicLong();

    public LruCache(int capacity) {
        this.capacity = capacity;
        this.map = new LinkedHashMap<>(16, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) { return size() > LruCache.this.capacity; }
        };
    }

    public V get(K key) {
        V v;
        lock.lock();
        try { v = map.get(key); } finally { lock.unlock(); }
        (v == null ? misses : hits).incrementAndGet();
        return v;
    }

    public void put(K key, V value) {
        if (capacity == 0) return;
        lock.lock();
        try { map.put(key, value); } finally { lock.unlock(); }
    }

    public void remove(K key) {
        lock.lock();
        try { map.remove(key); } finally { lock.unlock(); }
    }
}
