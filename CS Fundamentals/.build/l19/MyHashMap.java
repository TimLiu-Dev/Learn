import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

// Part 2: separate chaining, resize at load factor 0.75
public class MyHashMap<K, V> {
    private static final class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;
        Entry(K key, V value, Entry<K, V> next) { this.key = key; this.value = value; this.next = next; }
    }

    private Entry<K, V>[] buckets = newTable(8);
    private int size = 0;

    @SuppressWarnings("unchecked")   // safe: the array never escapes this class (L13)
    private static <K, V> Entry<K, V>[] newTable(int capacity) {
        return (Entry<K, V>[]) new Entry[capacity];
    }

    public int size() { return size; }
    public int capacity() { return buckets.length; }

    private static int indexFor(Object key, int cap) { return Math.floorMod(key.hashCode(), cap); }

    private Entry<K, V> findEntry(Object key) {
        Objects.requireNonNull(key, "key");               // our choice: no null keys
        for (Entry<K, V> e = buckets[indexFor(key, buckets.length)]; e != null; e = e.next) {
            if (e.key.equals(key)) return e;
        }
        return null;
    }

    public V put(K key, V value) {
        Entry<K, V> existing = findEntry(key);
        if (existing != null) {                           // same key: replace the value
            V old = existing.value;
            existing.value = value;
            return old;
        }
        int i = indexFor(key, buckets.length);
        buckets[i] = new Entry<>(key, value, buckets[i]); // new key: prepend to the chain
        size++;
        if (size > buckets.length * 0.75) resize();
        return null;
    }

    public V get(Object key) {
        Entry<K, V> e = findEntry(key);
        return e == null ? null : e.value;
    }

    public boolean containsKey(Object key) { return findEntry(key) != null; }

    private void resize() {
        Entry<K, V>[] old = buckets;
        buckets = newTable(old.length * 2);
        for (Entry<K, V> head : old) {
            Entry<K, V> e = head;
            while (e != null) {
                Entry<K, V> next = e.next;
                int i = indexFor(e.key, buckets.length);  // the index depends on capacity
                e.next = buckets[i];
                buckets[i] = e;
                e = next;
            }
        }
    }

    public int[] chainLengths() {
        int[] lengths = new int[buckets.length];
        for (int i = 0; i < buckets.length; i++)
            for (Entry<K, V> e = buckets[i]; e != null; e = e.next) lengths[i]++;
        return lengths;
    }

    public static void main(String[] args) {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        for (String w : "one two three four five six seven".split(" ")) {
            m.put(w, w.length());
            System.out.println("put " + w + ": size " + m.size() + ", capacity " + m.capacity());
        }
        System.out.println("put(two, 22) returned " + m.put("two", 22) + "; now " + m.get("two"));
        System.out.println("get(ten) = " + m.get("ten") + ", containsKey(six) = " + m.containsKey("six"));

        MyHashMap<String, Integer> big = new MyHashMap<>();
        for (int i = 0; i < 1000; i++) big.put("key" + i, i);
        int[] lengths = big.chainLengths();
        Map<Integer, Integer> byLength = new TreeMap<>();
        int longest = 0;
        for (int n : lengths) { byLength.merge(n, 1, Integer::sum); longest = Math.max(longest, n); }
        System.out.println("1000 keys: capacity " + big.capacity() + ", longest chain " + longest);
        System.out.println("buckets by chain length: " + byLength);
    }
}
