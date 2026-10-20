package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** A hash map with separate chaining. Average O(1) put/get/remove; worst case O(n). */
public class MyHashMap<K, V> {
    private static final int INITIAL_CAPACITY = 8;
    private static final double LOAD_FACTOR = 0.75;

    private static final class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;
        Entry(K key, V value, Entry<K, V> next) { this.key = key; this.value = value; this.next = next; }
    }

    private Entry<K, V>[] buckets = newTable(INITIAL_CAPACITY);
    private int size = 0;

    @SuppressWarnings("unchecked")   // safe: the array never leaves this class
    private static <K, V> Entry<K, V>[] newTable(int capacity) {
        return (Entry<K, V>[]) new Entry[capacity];
    }

    public int size() { return size; }
    public int capacity() { return buckets.length; }

    private static int indexFor(Object key, int cap) { return Math.floorMod(key.hashCode(), cap); }

    private Entry<K, V> findEntry(Object key) {
        Objects.requireNonNull(key, "key");
        for (Entry<K, V> e = buckets[indexFor(key, buckets.length)]; e != null; e = e.next) {
            if (e.key.equals(key)) return e;
        }
        return null;
    }

    /** Returns the previous value for key, or null if it was absent. */
    public V put(K key, V value) {
        Entry<K, V> existing = findEntry(key);
        if (existing != null) {
            V old = existing.value;
            existing.value = value;
            return old;
        }
        int i = indexFor(key, buckets.length);
        buckets[i] = new Entry<>(key, value, buckets[i]);
        size++;
        if (size > buckets.length * LOAD_FACTOR) resize();
        return null;
    }

    public V get(Object key) {
        Entry<K, V> e = findEntry(key);
        return e == null ? null : e.value;
    }

    public boolean containsKey(Object key) { return findEntry(key) != null; }

    /** Unlinks the entry for key from its chain. Returns its value, or null if absent. */
    public V remove(Object key) {
        Objects.requireNonNull(key, "key");
        int i = indexFor(key, buckets.length);
        Entry<K, V> prev = null;
        for (Entry<K, V> e = buckets[i]; e != null; prev = e, e = e.next) {
            if (e.key.equals(key)) {
                if (prev == null) buckets[i] = e.next; else prev.next = e.next;
                size--;
                return e.value;
            }
        }
        return null;
    }

    public List<K> keys() {
        List<K> out = new ArrayList<>(size);
        for (Entry<K, V> head : buckets)
            for (Entry<K, V> e = head; e != null; e = e.next) out.add(e.key);
        return out;
    }

    private void resize() {
        Entry<K, V>[] old = buckets;
        buckets = newTable(old.length * 2);
        for (Entry<K, V> head : old) {
            Entry<K, V> e = head;
            while (e != null) {
                Entry<K, V> next = e.next;
                int i = indexFor(e.key, buckets.length);
                e.next = buckets[i];
                buckets[i] = e;
                e = next;
            }
        }
    }
}
