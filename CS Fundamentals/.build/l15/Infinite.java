import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Infinite {
    public static void main(String[] args) {
        Stream<Long> powersOfTwo = Stream.iterate(1L, x -> x * 2);         // seed, then next
        System.out.println(powersOfTwo.limit(10).toList());

        // no yield in Java: carry the state (a, b) along as a tiny array
        System.out.println(Stream.iterate(new long[] {0, 1}, p -> new long[] {p[1], p[0] + p[1]})
            .map(p -> p[0]).limit(10).toList());
        System.out.println(Stream.iterate(new long[] {0, 1}, p -> new long[] {p[1], p[0] + p[1]})
            .map(p -> p[0]).filter(f -> f > 1_000).findFirst().orElseThrow());

        // Java 9+: iterate(seed, hasNext, next) is a lazy for loop
        System.out.println(Stream.iterate(5, n -> n >= 1, n -> n - 1).toList());

        System.out.println(Stream.generate(() -> "ha").limit(3).collect(Collectors.joining()));
    }
}
