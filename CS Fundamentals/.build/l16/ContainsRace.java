import java.util.*;
import java.util.function.IntPredicate;

// n lookups against a collection of n numbers: List vs HashSet.
public class ContainsRace {
    static int lookups(int n, IntPredicate contains) {
        int found = 0;
        for (int i = 0; i < n; i++) if (contains.test(i * 2)) found++;  // half hit, half miss
        return found;
    }

    public static void main(String[] args) {
        int sink = 0;
        System.out.printf("%8s %12s %12s%n", "n", "List ms", "HashSet ms");
        for (int n : new int[] {5_000, 10_000, 20_000, 40_000, 80_000}) {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < n; i++) list.add(i);
            Set<Integer> set = new HashSet<>(list);
            for (int w = 0; w < 2; w++) sink += lookups(n, list::contains) + lookups(n, set::contains);
            long t0 = System.nanoTime();
            sink += lookups(n, list::contains);
            long t1 = System.nanoTime();
            sink += lookups(n, set::contains);
            long t2 = System.nanoTime();
            System.out.printf("%8d %12.2f %12.2f%n", n, (t1 - t0) / 1e6, (t2 - t1) / 1e6);
        }
        System.out.println("(sink " + sink + ")");
    }
}
