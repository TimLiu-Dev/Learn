import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

// Uses MyHashMap.java from Part 2 in the same folder: java Timing.java
public class Timing {
    record GoodKey(int id) {}
    record BadKey(int id) { @Override public int hashCode() { return 42; } }   // legal, terrible
    record BadComparableKey(int id) implements Comparable<BadComparableKey> {
        @Override public int hashCode() { return 42; }
        @Override public int compareTo(BadComparableKey o) { return Integer.compare(id, o.id); }
    }

    // Insert n keys, then look each one up. Returns milliseconds.
    static <K> long timeMine(int n, IntFunction<K> key) {
        long start = System.nanoTime();
        MyHashMap<K, Integer> m = new MyHashMap<>();
        for (int i = 0; i < n; i++) m.put(key.apply(i), i);
        for (int i = 0; i < n; i++) if (m.get(key.apply(i)) != i) throw new AssertionError();
        return (System.nanoTime() - start) / 1_000_000;
    }

    static <K> long timeJdk(int n, IntFunction<K> key) {
        long start = System.nanoTime();
        Map<K, Integer> m = new HashMap<>();
        for (int i = 0; i < n; i++) m.put(key.apply(i), i);
        for (int i = 0; i < n; i++) if (m.get(key.apply(i)) != i) throw new AssertionError();
        return (System.nanoTime() - start) / 1_000_000;
    }

    public static void main(String[] args) {
        for (int r = 0; r < 3; r++) { timeMine(4000, BadKey::new); timeJdk(4000, BadKey::new); } // JIT warm-up
        System.out.println("milliseconds to put n keys, then get each one");
        System.out.printf("%6s %9s %9s %9s %9s %12s%n", "n", "mine/good", "mine/bad", "jdk/good", "jdk/bad", "jdk/bad+cmp");
        for (int n : new int[] {2_000, 4_000, 8_000, 16_000}) {
            System.out.printf("%6d %9d %9d %9d %9d %12d%n", n,
                timeMine(n, GoodKey::new), timeMine(n, BadKey::new),
                timeJdk(n, GoodKey::new), timeJdk(n, BadKey::new), timeJdk(n, BadComparableKey::new));
        }
    }
}
