import java.util.stream.Stream;

public class Stateful {
    public static void main(String[] args) {
        System.out.println("without sorted:");
        Stream.of(3, 1, 2)
            .map(n -> { System.out.println("  map " + n); return n * 10; })
            .forEach(n -> System.out.println("  got " + n));

        System.out.println("with sorted:");
        Stream.of(3, 1, 2)
            .map(n -> { System.out.println("  map " + n); return n * 10; })
            .sorted()                                    // stateful: must see every element first
            .forEach(n -> System.out.println("  got " + n));
    }
}
