import java.util.*;

public class Practice {
    // One index entry: the indexed value, and the rowid of the table row it points to.
    record Entry(int key, int rowid) { }

    // The leaf level of an index, laid end to end: entries sorted by (key, rowid).
    static class SortedIndex {
        private final List<Entry> sorted;
        private int reads = 0;   // entries looked at so far: our stand-in for page reads

        SortedIndex(List<Entry> entries) {
            sorted = new ArrayList<>(entries);
            sorted.sort(Comparator.comparingInt(Entry::key).thenComparingInt(Entry::rowid));
        }

        int size() { return sorted.size(); }
        int reads() { return reads; }

        private Entry at(int i) {
            reads++;
            return sorted.get(i);
        }

        // The first position whose key is >= key, or size() if every key is smaller.
        int lowerBound(int key) {
            throw new UnsupportedOperationException("TODO: binary search on at(mid).key(); don't stop at the first equal key");
        }

        // The rowids of every entry with this key, in index order.
        List<Integer> lookup(int key) {
            throw new UnsupportedOperationException("TODO: start at lowerBound(key), walk right while the key matches");
        }

        // The rowids of every entry with lo <= key <= hi, in index order.
        List<Integer> range(int lo, int hi) {
            throw new UnsupportedOperationException("TODO: same walk, a different stop condition");
        }
    }

    // CHECKS
    // keys 10, 20, 20, 20, 30 (rowids 5, 1, 2, 9, 3), given in a shuffled order
    static SortedIndex small() {
        return new SortedIndex(List.of(new Entry(20, 9), new Entry(30, 3), new Entry(10, 5), new Entry(20, 1), new Entry(20, 2)));
    }

    // 200,000 loans, 20,000 members, 10 loans each: like idx_loans_member on big.db
    static SortedIndex big() {
        List<Entry> es = new ArrayList<>();
        for (int i = 1; i <= 200_000; i++) es.add(new Entry(i % 20_000 + 1, i));
        return new SortedIndex(es);
    }

    public static void main(String[] args) {
        check("lowerBound finds the first of several equal keys", 1, () -> small().lowerBound(20));
        check("lowerBound(25): 30 is the first key >= 25", 4, () -> small().lowerBound(25));
        check("lowerBound(5): every key is bigger", 0, () -> small().lowerBound(5));
        check("lowerBound(99): every key is smaller", 5, () -> small().lowerBound(99));
        check("lookup returns every rowid for a key, in rowid order", List.of(1, 2, 9), () -> small().lookup(20));
        check("lookup of a key that isn't there is empty", List.of(), () -> small().lookup(25));
        check("range walks across keys and stops after hi", List.of(1, 2, 9, 3), () -> small().range(15, 30));
        check("range below every key is empty", List.of(), () -> small().range(0, 5));
        check("a lookup in 200,000 entries reads under 40", "10 rows, cheap", () -> {
            SortedIndex idx = big();
            int n = idx.lookup(42).size();
            return n + " rows, " + (idx.reads() < 40 ? "cheap" : "read " + idx.reads() + " entries: a scan");
        });
        check("a narrow range reads under 50", "20 rows, cheap", () -> {
            SortedIndex idx = big();
            int n = idx.range(19_999, 20_000).size();
            return n + " rows, " + (idx.reads() < 50 ? "cheap" : "read " + idx.reads() + " entries: a scan");
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
