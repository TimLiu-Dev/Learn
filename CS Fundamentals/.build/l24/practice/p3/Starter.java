import java.util.*;

public class Practice {
    // Every Money is made through Money.of, which counts it: an exact allocation profile.
    static final class Money {
        static int allocations = 0;
        private final long cents;

        private Money(long cents) { this.cents = cents; }
        static Money of(long cents) { allocations++; return new Money(cents); }

        long cents() { return cents; }
        Money plus(Money other) { return of(cents + other.cents); }
        Money times(int qty) { return of(cents * qty); }
        @Override public boolean equals(Object o) { return o instanceof Money m && m.cents == cents; }
        @Override public int hashCode() { return Long.hashCode(cents); }
        @Override public String toString() { return "Money(" + cents + ")"; }
    }

    // The hot loop. It's correct, but it makes two new Money objects per line of the basket.
    static Money total(List<Money> prices, int[] quantities) {
        Money sum = Money.of(0);
        for (int i = 0; i < prices.size(); i++) sum = sum.plus(prices.get(i).times(quantities[i]));
        return sum;
    }

    // CHECKS
    static List<Money> prices(int n) {
        List<Money> p = new ArrayList<>();
        for (int i = 0; i < n; i++) p.add(Money.of(100 + i % 7));
        return p;
    }

    static int[] quantities(int n) {
        int[] q = new int[n];
        for (int i = 0; i < n; i++) q[i] = 1 + i % 3;
        return q;
    }

    static Money original(List<Money> prices, int[] quantities) {
        Money sum = Money.of(0);
        for (int i = 0; i < prices.size(); i++) sum = sum.plus(prices.get(i).times(quantities[i]));
        return sum;
    }

    public static void main(String[] args) {
        check("a small basket totals correctly", Money.of(250 * 2 + 199 * 3 + 1000), () -> total(List.of(Money.of(250), Money.of(199), Money.of(1000)), new int[] {2, 3, 1}));
        check("one call allocates exactly one Money, however long the basket", 1, () -> { var p = prices(10_000); var q = quantities(10_000); int before = Money.allocations; total(p, q); return Money.allocations - before; });
        check("an empty basket is zero, and still one allocation", "Money(0), 1 allocation", () -> { int before = Money.allocations; Money m = total(List.of(), new int[0]); return m + ", " + (Money.allocations - before) + " allocation"; });
        check("the new total matches the original loop on a big basket", original(prices(10_000), quantities(10_000)), () -> total(prices(10_000), quantities(10_000)));
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
