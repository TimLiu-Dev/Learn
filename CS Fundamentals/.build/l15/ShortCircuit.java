import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ShortCircuit {
    public static void main(String[] args) {
        AtomicInteger visited = new AtomicInteger();      // a plain int can't be changed in a lambda

        int firstBig = IntStream.rangeClosed(1, 1_000).peek(n -> visited.incrementAndGet())
            .filter(n -> n * n > 50).findFirst().getAsInt();
        System.out.println("first square > 50: " + firstBig + ", visited " + visited);

        visited.set(0);
        boolean anyEven = IntStream.rangeClosed(1, 1_000).peek(n -> visited.incrementAndGet())
            .anyMatch(n -> n % 2 == 0);
        System.out.println("any even: " + anyEven + ", visited " + visited);

        visited.set(0);
        List<Integer> firstThree = IntStream.rangeClosed(1, 1_000).peek(n -> visited.incrementAndGet())
            .map(n -> n * 10).limit(3).boxed().toList();
        System.out.println("limit(3): " + firstThree + ", visited " + visited);

        visited.set(0);
        IntStream noTerminal = IntStream.rangeClosed(1, 1_000).peek(n -> visited.incrementAndGet())
            .map(n -> n * 10).filter(n -> n > 0);         // intermediate ops only
        System.out.println("no terminal op, visited " + visited);
    }
}
