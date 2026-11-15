import java.util.*;

public class Practice {
    static final class LastN<T> {
        private final T[] items;
        private int next = 0;                       // the slot the next add writes to
        private int size = 0;

        @SuppressWarnings("unchecked")              // safe: only T values are ever stored
        LastN(int capacity) {
            items = (T[]) new Object[capacity];
        }

        void add(T item) {
            throw new UnsupportedOperationException("TODO: write at next, move next on, and count up to the capacity");
        }

        List<T> toList() {
            throw new UnsupportedOperationException("TODO: oldest first: where is the oldest item once the buffer has wrapped?");
        }

        int size() { return size; }
    }

    // CHECKS
    public static void main(String[] args) {
        check("a new buffer is empty", "[] 0", () -> { var log = new LastN<String>(3); return log.toList() + " " + log.size(); });
        check("items keep their arrival order until it is full", "[a, b] [a, b, c] 3", () -> { var log = new LastN<String>(3); log.add("a"); log.add("b"); var two = log.toList(); log.add("c"); return two + " " + log.toList() + " " + log.size(); });
        check("when full, the oldest item is dropped", "[c, d, e] 3", () -> { var log = new LastN<String>(3); for (var s : List.of("a", "b", "c", "d", "e")) log.add(s); return log.toList() + " " + log.size(); });
        check("capacity 1 keeps only the latest", List.of("z"), () -> { var log = new LastN<String>(1); log.add("x"); log.add("y"); log.add("z"); return log.toList(); });
        check("it wraps around many times", List.of(7, 8, 9, 10), () -> { var log = new LastN<Integer>(4); for (int i = 1; i <= 10; i++) log.add(i); return log.toList(); });
        check("capacity 0 is rejected", "IllegalArgumentException", () -> { try { new LastN<String>(0); return "no exception"; } catch (IllegalArgumentException e) { return "IllegalArgumentException"; } });
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
