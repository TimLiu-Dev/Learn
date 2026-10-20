import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class FirstHundred {
    public static void main(String[] args) {
        List<String> vocabulary = List.of("the", "the", "the", "cat", "cat", "mat", "a", "hat", "sat", "on");
        Random rnd = new Random(15);                                    // same seed, same text
        Stream<String> words = Stream.generate(() -> vocabulary.get(rnd.nextInt(vocabulary.size())));

        Map<String, Integer> seen = new HashMap<>();                    // state the lambda updates
        AtomicInteger read = new AtomicInteger();
        String winner = words
            .peek(w -> read.incrementAndGet())
            .filter(w -> seen.merge(w, 1, Integer::sum) == 100)         // stop at the 100th sighting
            .findFirst()
            .orElseThrow();

        System.out.println("'" + winner + "' reached 100 first, after reading " + read + " words");
        System.out.println("counts then: " + new TreeMap<>(seen));
    }
}
