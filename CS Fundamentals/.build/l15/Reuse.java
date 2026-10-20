import java.util.function.Supplier;
import java.util.stream.Stream;

public class Reuse {
    public static void main(String[] args) {
        Supplier<Stream<Integer>> fresh = () -> Stream.of(1, 2, 3, 4, 5, 6).filter(n -> n % 2 == 0);
        System.out.println(fresh.get().toList());         // the fix: ask for a NEW stream each time
        System.out.println(fresh.get().count());

        Stream<Integer> evens = Stream.of(1, 2, 3, 4, 5, 6).filter(n -> n % 2 == 0);
        System.out.println(evens.toList());
        System.out.println(evens.count());                // second terminal op on the same stream
    }
}
