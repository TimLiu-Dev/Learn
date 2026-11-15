import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Practice {
    // Open addressing: one key per slot. On a collision, try the next slot.
    static class ProbingMap {
        private final String[] keys;
        private final int[] values;
        private int size = 0;

        ProbingMap(int capacity) {
            keys = new String[capacity];
            values = new int[capacity];
        }

        int size() { return size; }

        List<String> slots() { return Arrays.asList(keys.clone()); }

        // The slot holding key, or the first empty slot on its probe path. -1 if the table is full.
        private int find(String key) {
            int i = Math.floorMod(key.hashCode(), keys.length);   // not %: hash codes can be negative
            for (int step = 0; step < keys.length; step++) {     // at most one visit per slot
                if (keys[i] == null || keys[i].equals(key)) return i;
                i = (i + 1) % keys.length;
            }
            return -1;
        }

        void put(String key, int value) {
            int i = find(key);
            if (i < 0) throw new IllegalStateException("table is full");
            if (keys[i] == null) {
                keys[i] = key;
                size++;
            }
            values[i] = value;
        }

        Integer get(String key) {
            int i = find(key);
            return i >= 0 && key.equals(keys[i]) ? values[i] : null;
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("get returns what put stored", "1 2 null", () -> {
            ProbingMap m = new ProbingMap(8);
            m.put("cat", 1);
            m.put("dog", 2);
            return m.get("cat") + " " + m.get("dog") + " " + m.get("cow");
        });
        check("putting an existing key replaces its value", "5, size 1", () -> {
            ProbingMap m = new ProbingMap(8);
            m.put("cat", 1);
            m.put("cat", 5);
            return m.get("cat") + ", size " + m.size();
        });
        check("colliding keys sit in neighbouring slots", Arrays.asList("Aa", "BB", null, null, null, null, null, null), () -> {
            ProbingMap m = new ProbingMap(8);
            m.put("Aa", 1);
            m.put("BB", 2);
            return m.slots();
        });
        check("a negative hash code still gets a valid slot", "banana", () -> {
            ProbingMap m = new ProbingMap(16);
            m.put("banana", 6);
            return m.slots().get(5);
        });
        check("probing wraps around to slot 0", Arrays.asList("g", null, null, "c"), () -> {
            ProbingMap m = new ProbingMap(4);
            m.put("c", 1);
            m.put("g", 2);
            return m.slots();
        });
        check("a full table still answers but rejects new keys", "null 9 IllegalStateException", () -> {
            ProbingMap m = new ProbingMap(2);
            m.put("a", 1);
            m.put("b", 2);
            String missing = String.valueOf(m.get("z"));
            m.put("a", 9);
            try {
                m.put("c", 3);
                return missing + " " + m.get("a") + " no exception";
            } catch (IllegalStateException e) {
                return missing + " " + m.get("a") + " IllegalStateException";
            }
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
