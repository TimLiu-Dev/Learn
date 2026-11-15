import java.util.*;

public class Practice {
    // An LRU cache bounded by total weight: the sum of its values' lengths.
    static class WeightedLru {
        private static class Node {
            final String key;
            String value;
            Node prev, next;
            Node(String key, String value) { this.key = key; this.value = value; }
        }

        private final int maxWeight;
        private final Map<String, Node> map = new HashMap<>();
        private final Node head = new Node(null, "");     // most recently used side
        private final Node tail = new Node(null, "");     // least recently used side
        private int totalWeight = 0;
        final List<String> evicted = new ArrayList<>();   // keys pushed out to make room, in order

        WeightedLru(int maxWeight) {
            this.maxWeight = maxWeight;
            head.next = tail;
            tail.prev = head;
        }

        int totalWeight() { return totalWeight; }

        String get(String key) {
            Node n = map.get(key);
            if (n == null) return null;
            unlink(n);
            addFront(n);
            return n.value;
        }

        void put(String key, String value) {
            Node old = map.get(key);
            if (value.length() > maxWeight) {             // can never fit: drop the stale copy too
                if (old != null) { unlink(old); map.remove(key); totalWeight -= old.value.length(); }
                return;
            }
            if (old != null) {
                totalWeight += value.length() - old.value.length();
                old.value = value;
                unlink(old);
                addFront(old);
            } else {
                Node n = new Node(key, value);
                map.put(key, n);
                addFront(n);
                totalWeight += value.length();
            }
            while (totalWeight > maxWeight) {             // one big entry may push out several small ones
                Node lru = tail.prev;                     // never the new entry: it fits on its own
                unlink(lru);
                map.remove(lru.key);
                totalWeight -= lru.value.length();
                evicted.add(lru.key);
            }
        }

        // Keys, most recently used first.
        List<String> keys() {
            List<String> out = new ArrayList<>();
            for (Node n = head.next; n != tail; n = n.next) out.add(n.key);
            return out;
        }

        private void unlink(Node n) { n.prev.next = n.next; n.next.prev = n.prev; }
        private void addFront(Node n) { n.next = head.next; n.prev = head; head.next.prev = n; head.next = n; }
    }

    // CHECKS
    static WeightedLru filled() {
        WeightedLru c = new WeightedLru(10);
        c.put("a", "aaaa");    // weight 4
        c.put("b", "bbb");     // weight 3
        c.put("c", "ccc");     // weight 3: total 10, exactly full
        return c;
    }

    public static void main(String[] args) {
        check("get moves an entry to the front", "aaaa [a, c, b] null", () -> {
            WeightedLru c = filled();
            String v = c.get("a");
            return v + " " + c.keys() + " " + c.get("zzz");
        });
        check("a heavy entry evicts LRU entries until it fits", "evicted [a, b], keys [d, c], weight 8", () -> {
            WeightedLru c = filled();
            c.put("d", "ddddd");
            return "evicted " + c.evicted + ", keys " + c.keys() + ", weight " + c.totalWeight();
        });
        check("a get saves an entry from eviction", "evicted [b, c], keys [d, a]", () -> {
            WeightedLru c = filled();
            c.get("a");
            c.put("d", "ddddd");
            return "evicted " + c.evicted + ", keys " + c.keys();
        });
        check("updating a key changes its weight, never evicts itself", "evicted [a, c], keys [b], weight 8", () -> {
            WeightedLru c = filled();
            c.put("b", "bbbbbbbb");
            return "evicted " + c.evicted + ", keys " + c.keys() + ", weight " + c.totalWeight();
        });
        check("a too-heavy value is not stored and drops the stale copy", "null, keys [c, b], weight 6, evicted []", () -> {
            WeightedLru c = filled();
            c.put("a", "x".repeat(11));
            return c.get("a") + ", keys " + c.keys() + ", weight " + c.totalWeight() + ", evicted " + c.evicted;
        });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
