package org.example;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Keeps at most capacity entries, evicting the least recently used. Wraps, not extends (L8). */
public class LruCache<K, V> {
    private final Map<K, V> map;

    public LruCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive: " + capacity);
        this.map = new LinkedHashMap<>(16, 0.75f, true) {               // true = access order
            @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }

    public V get(K key) { return map.get(key); }                // counts as a use
    public void put(K key, V value) { map.put(key, value); }
    public int size() { return map.size(); }
    public List<K> keys() { return new ArrayList<>(map.keySet()); }  // least recently used first
}
