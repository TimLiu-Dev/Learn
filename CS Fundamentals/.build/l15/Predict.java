import java.util.stream.Stream;

public class Predict {
    public static void main(String[] args) {
        int lazy = Stream.of(1, 2, 3, 4, 5, 6)
            .filter(n -> { System.out.print("f" + n + " "); return n % 2 == 0; })
            .map(n -> { System.out.print("m" + n + " "); return n * n; })
            .filter(n -> n > 10)
            .findFirst().orElseThrow();
        System.out.println("-> " + lazy);
    }
}
