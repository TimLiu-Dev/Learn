import java.util.List;
import java.util.Objects;

public class Practice {
    // The bound lets the code call compareTo on any T.
    static class Span<T extends Comparable<T>> {
        final T lo, hi;

        Span(T lo, T hi) {
            if (lo.compareTo(hi) > 0) throw new IllegalArgumentException("lo (" + lo + ") must not be greater than hi (" + hi + ")");
            this.lo = lo;
            this.hi = hi;
        }

        boolean contains(T x) { return lo.compareTo(x) <= 0 && x.compareTo(hi) <= 0; }

        T clamp(T x) {
            if (x.compareTo(lo) < 0) return lo;
            if (x.compareTo(hi) > 0) return hi;
            return x;
        }

        @Override public String toString() { return lo + ".." + hi; }
    }

    static <T extends Comparable<T>> Span<T> spanOf(List<T> items) {
        if (items.isEmpty()) return null;
        T lo = items.get(0), hi = items.get(0);
        for (T item : items) {
            if (item.compareTo(lo) < 0) lo = item;
            if (item.compareTo(hi) > 0) hi = item;
        }
        return new Span<>(lo, hi);
    }

    // CHECKS
    public static void main(String[] args) {
        check("both ends are inside the span", "true true true false false", () -> {
            Span<Integer> s = new Span<>(1, 5);
            return s.contains(1) + " " + s.contains(3) + " " + s.contains(5) + " " + s.contains(6) + " " + s.contains(0);
        });
        check("strings compare alphabetically", "true false", () -> {
            Span<String> s = new Span<>("b", "d");
            return s.contains("cat") + " " + s.contains("apple");
        });
        check("clamp pulls outsiders to the nearest end", "1 5 4", () -> {
            Span<Integer> s = new Span<>(1, 5);
            return s.clamp(-3) + " " + s.clamp(9) + " " + s.clamp(4);
        });
        check("lo greater than hi is rejected", "rejected", () -> {
            try { new Span<>(5, 1); return "accepted"; } catch (IllegalArgumentException e) { return "rejected"; }
        });
        check("a single point is fine", true, () -> new Span<>(3, 3).contains(3));
        check("spanOf finds the smallest and largest item", "2..9 fig..pear",
                () -> spanOf(List.of(4, 9, 2)) + " " + spanOf(List.of("pear", "fig", "kiwi")));
        check("spanOf an empty list is null", null, () -> spanOf(List.<Integer>of()));
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
