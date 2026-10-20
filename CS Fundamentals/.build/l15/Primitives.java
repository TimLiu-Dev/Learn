import java.util.IntSummaryStatistics;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Primitives {
    public static void main(String[] args) {
        int a = IntStream.rangeClosed(1, 100).map(x -> x * x).sum();             // ints all the way
        int b = Stream.iterate(1, x -> x + 1).limit(100)                         // Stream<Integer>: boxed
            .mapToInt(x -> x * x).sum();                                         // unboxed from here
        System.out.println(a + " " + b);

        IntSummaryStatistics s = IntStream.of(4, 8, 15, 16, 23, 42).summaryStatistics();
        System.out.println("min=" + s.getMin() + " max=" + s.getMax() + " avg=" + s.getAverage());
    }
}
