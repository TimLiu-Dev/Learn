import java.util.*;
import java.util.function.Supplier;

public class Smells {
    static <T> void time(String label, Supplier<T> block) {
        long t0 = System.nanoTime();
        T result = block.get();
        System.out.printf("%-34s %8.1f ms   -> %s%n", label, (System.nanoTime() - t0) / 1e6, result);
    }

    public static void main(String[] args) {
        Random rnd = new Random(42);
        List<Integer> a = new ArrayList<>(), b = new ArrayList<>();
        for (int i = 0; i < 20_000; i++) { a.add(rnd.nextInt(1_000_000)); b.add(rnd.nextInt(1_000_000)); }

        for (int round = 0; round < 2; round++) {       // round 0 is a JIT warm-up; read round 1
            System.out.println("-- round " + round);
            time("common: List.contains in a loop", () -> b.stream().filter(a::contains).count());
            time("common: HashSet", () -> { Set<Integer> set = new HashSet<>(a); return b.stream().filter(set::contains).count(); });

            List<Integer> linked = new LinkedList<>(a);
            time("LinkedList.get(i) in a for loop", () -> { long s = 0; for (int i = 0; i < linked.size(); i++) s += linked.get(i); return s; });
            time("LinkedList for-each", () -> { long s = 0; for (int x : linked) s += x; return s; });

            List<Integer> big = new ArrayList<>();
            for (int i = 0; i < 1_000_000; i++) big.add(rnd.nextInt());
            time("min by sorting", () -> big.stream().sorted().findFirst().get());
            time("min with one pass", () -> Collections.min(big));
        }
    }
}
