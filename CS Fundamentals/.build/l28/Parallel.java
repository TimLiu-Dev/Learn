import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Parallel {
    static boolean prime(int n) {
        if (n < 2) return false;
        for (int d = 2; d * d <= n; d++) if (n % d == 0) return false;
        return true;
    }

    static long ms(int times, LongSupplier block) {
        long t = System.nanoTime();
        for (int i = 0; i < times; i++) if (block.getAsLong() <= 0) throw new AssertionError();  // use the result
        return (System.nanoTime() - t) / 1_000_000;
    }

    // Light work (square and sum, 20 times) and heavy work (a prime test, once) on one source.
    static String row(String name, Supplier<Stream<Integer>> source) {
        long light = ms(20, () -> source.get().mapToLong(i -> (long) i * i).sum());
        long heavy = ms(1, () -> source.get().filter(Parallel::prime).count());
        return String.format("%-14s light %4d ms   heavy %4d ms", name, light, heavy);
    }

    public static void main(String[] args) {
        // 1. Who runs a parallel stream? The calling thread plus common-pool workers.
        List<String> names = IntStream.range(0, 1_000).parallel()
            .mapToObj(i -> Thread.currentThread().getName().split("-worker")[0]).distinct().sorted().toList();
        System.out.println("threads: " + names);

        // 2. The same 5,000,000 numbers from three sources, sequential and parallel.
        int n = 5_000_000;
        List<Integer> array = new ArrayList<>(IntStream.range(0, n).boxed().toList());
        List<Integer> linked = new LinkedList<>(array);
        Supplier<Stream<Integer>> iterate = () -> Stream.iterate(0, i -> i + 1).limit(n);
        for (int round = 0; round < 3; round++) {                   // rounds 0 and 1 are warm-up
            List<String> rows = List.of(
                row("ArrayList seq", array::stream), row("ArrayList par", array::parallelStream),
                row("LinkedList seq", linked::stream), row("LinkedList par", linked::parallelStream),
                row("iterate seq", iterate), row("iterate par", () -> iterate.get().parallel()));
            if (round == 2) rows.forEach(System.out::println);
        }

        // 3. Side effects: a plain ArrayList filled from a parallel forEach.
        for (int run = 0; run < 3; run++) {
            List<Integer> out = new ArrayList<>();
            String result;
            try { array.parallelStream().forEach(out::add); result = "size " + out.size(); }
            catch (Exception e) { result = e.getClass().getSimpleName(); }
            System.out.println("forEach into ArrayList: " + result + " (expected " + n + ")");
        }
        System.out.println("toList():               size " + array.parallelStream().toList().size());

        // 4. Order: forEach ignores encounter order, forEachOrdered keeps it.
        StringBuffer seen = new StringBuffer();                     // StringBuffer: synchronized appends
        IntStream.rangeClosed(1, 12).boxed().toList().parallelStream().forEach(i -> seen.append(i + " "));
        System.out.println("forEach:        " + seen);
        StringBuilder ordered = new StringBuilder();
        IntStream.rangeClosed(1, 12).boxed().toList().parallelStream().forEachOrdered(i -> ordered.append(i).append(' '));
        System.out.println("forEachOrdered: " + ordered);
    }
}
