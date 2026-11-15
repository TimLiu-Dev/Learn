import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

public class Practice {
    static Function<Integer, Long> memoize(Function<Integer, Long> f) {
        Map<Integer, Long> cache = new HashMap<>();   // never reassigned, so the lambda may capture it
        return n -> cache.computeIfAbsent(n, f);
    }

    static List<IntUnaryOperator> multipliers() {
        List<IntUnaryOperator> result = new ArrayList<>();
        for (int j = 1; j <= 3; j++) {
            int k = j;                                // an effectively final copy for each lambda
            result.add(x -> x * k);
        }
        return result;
    }

    // CHECKS
    public static void main(String[] args) throws Exception {
        check("memoize gives the same answers as f", List.of(49L, 49L, 0L), () -> {
            Function<Integer, Long> square = memoize(n -> (long) n * n);
            return List.of(square.apply(7), square.apply(7), square.apply(0));
        });
        check("memoize calls f once per distinct input", 2, () -> {
            AtomicInteger calls = new AtomicInteger();          // Java needs a box to count from a lambda
            Function<Integer, Long> slow = memoize(n -> { calls.incrementAndGet(); return n * 10L; });
            for (int n : new int[] {1, 2, 1, 1, 2}) slow.apply(n);
            return calls.get();
        });
        check("two memoized functions keep separate caches", List.of(10L, 15L), () -> {
            Function<Integer, Long> twice = memoize(n -> n * 2L);
            Function<Integer, Long> thrice = memoize(n -> n * 3L);
            return List.of(twice.apply(5), thrice.apply(5));
        });
        check("each multiplier keeps its own factor", List.of(10, 20, 30),
                () -> multipliers().stream().map(m -> m.applyAsInt(10)).toList());
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
