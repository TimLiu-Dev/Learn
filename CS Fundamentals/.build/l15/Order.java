import java.util.List;
import java.util.stream.Stream;

public class Order {
    public static void main(String[] args) {
        List<String> words = List.of("kiwi", "fig", "banana", "plum", "cherry");

        System.out.println("== Stream (lazy)");
        Stream<String> pipeline = words.stream()
            .filter(w -> { System.out.println("  filter " + w); return w.length() > 3; })
            .map(w -> { System.out.println("  map " + w); return w.toUpperCase(); })
            .limit(2);
        System.out.println("pipeline built, nothing ran yet");
        System.out.println("result " + pipeline.toList());
    }
}
